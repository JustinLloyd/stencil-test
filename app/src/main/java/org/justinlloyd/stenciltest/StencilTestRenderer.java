package org.justinlloyd.stenciltest;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class StencilTestRenderer implements GLSurfaceView.Renderer
{
	private int m_programObject;

	private int m_positionLoc;

	private int m_colorLoc;

	private int m_width;
	private int m_height;

	private FloatBuffer m_vertices;
	private ShortBuffer m_indices;

	private final int RED_COMPONENT = 0;
	private final int GREEN_COMPONENT = 1;
	private final int BLUE_COMPONENT = 2;
	private final int ALPHA_COMPONENT = 3;

	private final int SOLID_RED = 0;
	private final int SOLID_GREEN = 1;
	private final int SOLID_BLUE = 2;
	private final int SOLID_YELLOW = 3;

	private float[][] m_colors = {
			{1.0f, 0.0f, 0.0f, 1.0f},
			{0.0f, 1.0f, 0.0f, 1.0f},
			{0.0f, 0.0f, 1.0f, 1.0f},
			{1.0f, 1.0f, 0.0f, 0.0f}
	};

	private final float[] m_verticesData =
			{
					-0.75f, 0.25f, 0.50f, // Quad #0
					-0.25f, 0.25f, 0.50f,
					-0.25f, 0.75f, 0.50f,
					-0.75f, 0.75f, 0.50f,
					0.25f, 0.25f, 0.90f, // Quad #1
					0.75f, 0.25f, 0.90f,
					0.75f, 0.75f, 0.90f,
					0.25f, 0.75f, 0.90f,
					-0.75f, -0.75f, 0.50f, // Quad #2
					-0.25f, -0.75f, 0.50f,
					-0.25f, -0.25f, 0.50f,
					-0.75f, -0.25f, 0.50f,
					0.25f, -0.75f, 0.50f, // Quad #3
					0.75f, -0.75f, 0.50f,
					0.75f, -0.25f, 0.50f,
					0.25f, -0.25f, 0.50f,
					-1.00f, -1.00f, 0.00f, // Big Quad
					1.00f, -1.00f, 0.00f,
					1.00f, 1.00f, 0.00f,
					-1.00f, 1.00f, 0.00f
			};

	private final short[] m_indicesData =
			{
					0, 1, 2, 0, 2, 3,  // Quad #0
					4, 5, 6, 4, 6, 7,  // Quad #1
					8, 9, 10, 8, 10, 11,  // Quad #2
					12, 13, 14, 12, 14, 15, // Quad #3
					16, 17, 18, 16, 18, 19  // Big Quad
			};

	public StencilTestRenderer(Context context)
	{
		m_vertices = ByteBuffer.allocateDirect(m_verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		m_vertices.put(m_verticesData).position(0);
		m_indices = ByteBuffer.allocateDirect(m_indicesData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		m_indices.put(m_indicesData).position(0);
	}

	private void setSolidRenderColour(int solidColour)
	{
		GLES30.glUniform4f(m_colorLoc, m_colors[solidColour][RED_COMPONENT], m_colors[solidColour][GREEN_COMPONENT], m_colors[solidColour][BLUE_COMPONENT], m_colors[solidColour][ALPHA_COMPONENT]);
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
	{
		String vShaderStr =
				"attribute vec4 a_position;   \n" +
						"void main()                  \n" +
						"{                            \n" +
						"   gl_Position = a_position; \n" +
						"}                            \n";

		String fShaderStr =
				"precision mediump float;  \n" +
						"uniform vec4  u_color;    \n" +
						"void main()               \n" +
						"{                         \n" +
						"  gl_FragColor = u_color; \n" +
						"}                         \n";


		m_programObject = ShaderUtils.loadProgram(vShaderStr, fShaderStr);
		m_positionLoc = GLES30.glGetAttribLocation(m_programObject, "a_position");
		m_colorLoc = GLES30.glGetUniformLocation(m_programObject, "u_color");

		GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES30.glClearStencil(0x0);
	}

	public void onDrawFrame(GL10 glUnused)
	{
		// enable writing to the stencil buffer
		// draw a small blue quad into the stencil buffer
		// disable writing to the stencil buffer
		// draw a large yellow quad covering the entire screen into the frame buffer
		// draw a small green quad in the top left into the frame buffer
		// draw a small red quad in the top right into the frame buffer
		// expected result: only the small red quad in the top right should be visible

		GLES30.glViewport(0, 0, m_width, m_height);
		GLES30.glUseProgram(m_programObject);
		GLES30.glVertexAttribPointer(m_positionLoc, 3, GLES30.GL_FLOAT, false, 0, m_vertices);
		GLES30.glEnableVertexAttribArray(m_positionLoc);
		GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

		/************************************************************************************/
		GLES30.glDisable(GLES30.GL_DEPTH_TEST);
		GLES30.glEnable(GLES30.GL_STENCIL_TEST);
		GLES30.glColorMask(true, true, true, true);
		GLES30.glDepthMask(false);
		GLES30.glStencilFunc(GLES30.GL_ALWAYS, 0x1, 0xFF);
		GLES30.glStencilOp(GLES30.GL_KEEP, GLES30.GL_KEEP, GLES30.GL_REPLACE);
		GLES30.glStencilMask(0xFF);
		GLES30.glClear(GLES30.GL_STENCIL_BUFFER_BIT);

		m_indices.position(6);
		setSolidRenderColour(SOLID_BLUE);
		GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, m_indices);


		/************************************************************************************/
		GLES30.glColorMask(true, true, true, true);
		GLES30.glDepthMask(false);
		GLES30.glStencilMask(0x00);
		GLES30.glStencilFunc(GLES30.GL_EQUAL, 0x1, 0xFF);


		// what the below code attempts to do i:
		//		draw a large yellow rectangle covering the entire screen
		// 		and draw a small green rectangle in the top left
		// 		and draw a small red rectangle in the top right
		// what should appear on screen is only a single red rectangle in the top right

		// draw a large yellow rectangle covering the entire screen
		m_indices.position(24);
		setSolidRenderColour(SOLID_YELLOW);
		GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, m_indices);

		// draw a small green rectangle in the top left
		m_indices.position(0);
		setSolidRenderColour(SOLID_GREEN);
		GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, m_indices);

		// draw a small red rectangle in the top right
		m_indices.position(6);
		setSolidRenderColour(SOLID_RED);
		GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, m_indices);

		GLES30.glDisable(GLES30.GL_STENCIL_TEST);

		// This code produces the expected result (red rectangle in top right corner of screen) on the following test devices:
		// Nexus 6P (SDK 24)
		// Pixel (SDK 25)
		// Samsung S7 (SDK 25)
	}

	public void onSurfaceChanged(GL10 glUnused, int width, int height)
	{
		m_width = width;
		m_height = height;
	}

}

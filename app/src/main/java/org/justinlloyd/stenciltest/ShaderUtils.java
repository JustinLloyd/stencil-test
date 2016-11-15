package org.justinlloyd.stenciltest;

import android.opengl.GLES30;
import android.util.Log;

public class ShaderUtils
{
	public static int loadShader(int type, String shaderSrc)
	{
		int shader;
		int[] compiled = new int[1];

		shader = GLES30.glCreateShader(type);

		if (shader == 0)
		{
			return 0;
		}

		GLES30.glShaderSource(shader, shaderSrc);

		GLES30.glCompileShader(shader);

		GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

		if (compiled[0] == 0)
		{
			Log.e("ShaderUtils", GLES30.glGetShaderInfoLog(shader));
			GLES30.glDeleteShader(shader);
			return 0;
		}
		return shader;
	}

	public static int loadProgram(String vertShaderSrc, String fragShaderSrc)
	{
		int vertexShader;
		int fragmentShader;
		int programObject;
		int[] linked = new int[1];

		vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertShaderSrc);
		if (vertexShader == 0)
		{
			return 0;
		}

		fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragShaderSrc);
		if (fragmentShader == 0)
		{
			GLES30.glDeleteShader(vertexShader);
			return 0;
		}

		programObject = GLES30.glCreateProgram();

		if (programObject == 0)
		{
			return 0;
		}

		GLES30.glAttachShader(programObject, vertexShader);
		GLES30.glAttachShader(programObject, fragmentShader);

		GLES30.glLinkProgram(programObject);

		GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

		if (linked[0] == 0)
		{
			Log.e("ShaderUtils", "Error linking program:");
			Log.e("ShaderUtils", GLES30.glGetProgramInfoLog(programObject));
			GLES30.glDeleteProgram(programObject);
			return 0;
		}

		GLES30.glDeleteShader(vertexShader);
		GLES30.glDeleteShader(fragmentShader);

		return programObject;
	}

}

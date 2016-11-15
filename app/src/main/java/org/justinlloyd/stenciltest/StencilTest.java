package org.justinlloyd.stenciltest;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;


public class StencilTest extends Activity
{
	private GLSurfaceView m_GLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		m_GLSurfaceView = new GLSurfaceView(this);
		if (detectOpenGLES30())
		{
			m_GLSurfaceView.setEGLContextClientVersion(2);
			m_GLSurfaceView.setEGLConfigChooser(8, 8, 8, 0, 8, 8);
			m_GLSurfaceView.setRenderer(new StencilTestRenderer(this));
		}
		else
		{
			Log.e("StencilTest", "OpenGL ES 3.0 not supported on device.  Exiting...");
			finish();

		}
		setContentView(m_GLSurfaceView);
	}

	private boolean detectOpenGLES30()
	{
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return (info.reqGlEsVersion >= 0x30000);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		m_GLSurfaceView.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		m_GLSurfaceView.onPause();
	}

}

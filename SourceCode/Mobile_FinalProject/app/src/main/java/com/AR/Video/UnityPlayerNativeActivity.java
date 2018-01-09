package com.AR.Video;

import com.unity3d.player.*;
import android.app.NativeActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

/**
 * @deprecated It's recommended that you base your code directly on UnityPlayerActivity or make your own NativeActitivty implementation.
 **/
public class UnityPlayerNativeActivity extends UnityPlayerActivity
{
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		Log.w("Unity", "UnityPlayerNativeActivity has been deprecated, please update your AndroidManifest to use UnityPlayerActivity instead");
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			onDestroy();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}

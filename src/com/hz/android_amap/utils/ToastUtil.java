package com.hz.android_amap.utils;

import com.amap.api.services.core.AMapException;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {

	private static Toast mToast;

	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			if (mToast != null) {
				mToast.cancel();
				mToast = null;
			}
		}
	};

	public static void showShortToast(Context context, String message) {
		TextView text = new TextView(context);// 显示的提示文字
		text.setText(message);
		text.setBackgroundColor(Color.BLACK);
		text.setPadding(10, 10, 10, 10);

		if (mToast != null) {//
			mHandler.postDelayed(r, 0);// 隐藏toast
		} else {
			mToast = new Toast(context);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.BOTTOM, 0, 150);
			mToast.setView(text);
		}

		mHandler.postDelayed(r, 1000);// 延迟1秒隐藏toast
		mToast.show();
	}

	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void showerror(Context context, int rCode) {
		try {
			switch (rCode) {
			case 13:
				throw new AMapException(AMapException.ERROR_USERID);
			case 15:
				throw new AMapException(AMapException.ERROR_UPLOADAUTO_STARTED);
			case 16:
				throw new AMapException(AMapException.ERROR_BINDER_KEY);
			case 21:
				throw new AMapException(AMapException.ERROR_IO);
			case 22:
				throw new AMapException(AMapException.ERROR_SOCKET);
			case 23:
				throw new AMapException(AMapException.ERROR_SOCKE_TIME_OUT);
			case 24:
				throw new AMapException(AMapException.ERROR_INVALID_PARAMETER);
			case 25:
				throw new AMapException(AMapException.ERROR_NULL_PARAMETER);
			case 26:
				throw new AMapException(AMapException.ERROR_URL);
			case 27:
				throw new AMapException(AMapException.ERROR_UNKNOW_HOST);
			case 28:
				throw new AMapException(AMapException.ERROR_UNKNOW_SERVICE);
			case 29:
				throw new AMapException(AMapException.ERROR_PROTOCOL);
			case 30:
				throw new AMapException(AMapException.ERROR_CONNECTION);
			case 31:
				throw new AMapException(AMapException.ERROR_UNKNOWN);
			case 32:
				throw new AMapException(AMapException.ERROR_FAILURE_AUTH);
			case 33:
				throw new AMapException(AMapException.ERROR_SERVICE);
			case 34:
				throw new AMapException(AMapException.ERROR_SERVER);
			case 35:
				throw new AMapException(AMapException.ERROR_QUOTA);
			case 36:
				throw new AMapException(AMapException.ERROR_REQUEST);
			case 37:
				throw new AMapException(
						AMapException.ERROR_SHARE_SEARCH_FAILURE);
			case 1901:
				throw new AMapException(AMapException.AMAP_LICENSE_IS_EXPIRED);
			case 39:
				throw new AMapException(
						AMapException.ERROR_USERKEY_PLAT_NOMATCH);
			case 1001:
				throw new AMapException(AMapException.AMAP_SIGNATURE_ERROR);
			case 43:
				throw new AMapException(AMapException.ERROR_ROUTE_FAILURE);
			case 44:
				throw new AMapException(
						AMapException.ERROR_OVER_DIRECTION_RANGE);
			case 45:
				throw new AMapException(AMapException.ERROR_OUT_OF_SERVICE);
			case 46:
				throw new AMapException(AMapException.ERROR_ID_NOT_FOUND);
			case 60:
				throw new AMapException(AMapException.ERROR_SCODE);
			case 2000:
				throw new AMapException(AMapException.AMAP_TABLEID_NOT_EXIST);
			case 2001:
				throw new AMapException(AMapException.AMAP_ID_NOT_EXIST);
			case 11:
				Toast.makeText(context, "两次单次上传的间隔低于 5 秒", Toast.LENGTH_LONG)
						.show();
				break;
			case 12:
				Toast.makeText(context, "Uploadinfo 对象为空", Toast.LENGTH_LONG)
						.show();
				break;
			case 14:
				Toast.makeText(context, "Point 为空，或与前次上传的相同", Toast.LENGTH_LONG)
						.show();
				break;
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}

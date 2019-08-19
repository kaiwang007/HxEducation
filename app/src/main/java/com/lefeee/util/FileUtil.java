package com.lefeee.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final File parentPath = Environment
			.getExternalStorageDirectory();
	private static String storagePath = "";
	private static final String DST_FOLDER_NAME = "HxEducate";
	public static String jpegName = "";

	/**
	 * @return
	 */
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**
	 * 保存照片到SD卡
	 * @param b
	 */
	public static void saveBitmap(Bitmap b) {

		String path = initPath();
		long dataTake = System.currentTimeMillis();
//		jpegName = path + "/" + dataTake + ".jpg";
		jpegName = path + "/" +  "rec.jpg";
//		Log.d(TAG, "saveBitmap:jpegName = " + jpegName);

		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
//			Log.i(TAG, "saveBitmap 成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			Log.i(TAG, "saveBitmap:失败");
			e.printStackTrace();
		}

	}

}

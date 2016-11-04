package marc.com.gaodedemo.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Broderick on 2016/11/1.
 */

public class ImageUtil {
	public static File createImageFile() throws IOException {
		// 定义图片名称
		String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileSuffix = ".jpg";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES + "/GDDemo");
		if (!storageDir.exists()) {
			storageDir.mkdirs();
		}
//        File image = File.createTempFile(
//                imageFileName,  /* 前缀 */
//                ".jpg",         /* 后缀 */
//                storageDir      /* 路径 */
//        );
		File image = new File(storageDir.getPath() + "/" + imageFileName + imageFileSuffix);
		return image;
	}
	/**
	 * 说 明 ： 扫描指定路径文件
	 * @param context
	 * @param photoPath
	 */
	public static void galleryAddPic(Context context, String photoPath) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(photoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}
	/**
	 * 获取自适应屏幕大小的Bitmap
	 * @param context 上下文
	 * @param filePath Bitmap文件路径
	 * @return 和屏幕大小适配的Bitmap
	 */
	public static Bitmap getBitmap(Context context, String filePath) {
		File f = new File(filePath);
		if (f != null) {
			if (!f.exists()) {
				Log.w("getBitmap", "No such file: " + filePath);
				return null;
			}
		}

		Bitmap bitmap = null;
		try {
			int targetWidth = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
			int targetHeight = context.getResources().getDisplayMetrics().heightPixels;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			int photoWidth = options.outWidth;
			int photoHeight = options.outHeight;

			//int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);
			//options.inSampleSize = scaleFactor;

			int inSampleSize = calculateInSampleSize(photoWidth, photoHeight, targetWidth, targetHeight);
			options.inSampleSize = inSampleSize;

			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		}

		return bitmap;
	}
	/**
	 * 计算图片的缩放比例
	 * @param originalWidth 原图宽度
	 * @param originalHeight 原图高度
	 * @param targetWidth 目标宽度
	 * @param targetHeight 目标高度
	 * @return
	 */
	private static int calculateInSampleSize(int originalWidth, int originalHeight, int targetWidth, int targetHeight) {
		int inSampleSize = 1;

		if (originalHeight > targetHeight || originalWidth > targetWidth) {
			while ((originalHeight / inSampleSize) > targetHeight && (originalWidth / inSampleSize) > targetWidth) {
				//设置inSampleSize为2的幂是因为解码器最终还是会对非2的幂的数进行向下处理，获取到最靠近2的幂的数。
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	/**
	 * 获取Uri的路径
	 * @param context Context
	 * @param uri Uri
	 * @return 路径
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = MediaStore.MediaColumns._ID + "=?";
				final String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	/**
	 * Get the value of the data column for this Uri . This is useful for
	 * MediaStore Uris , and other file - based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
	                                   String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = MediaStore.MediaColumns.DATA;
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
}

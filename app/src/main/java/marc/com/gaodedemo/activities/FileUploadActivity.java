package marc.com.gaodedemo.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import marc.com.gaodedemo.R;
import marc.com.gaodedemo.service.FileUploadService;
import marc.com.gaodedemo.util.ImageUtil;
import marc.com.gaodedemo.util.ServiceGenerator;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploadActivity extends Activity {
	private static final int REQUEST_PICK_IMAGE_KITKAT = 0;
	private static final int REQUEST_TAKE_PHOTO = 2;
	public static final String TAG = "FileUploadActivity";

	Uri file1Uri;//= ... // get it from a file chooser or a camera intent
	Uri file2Uri;//= ... // get it from a file chooser or a camera intent
	FileUploadService service;
	@Bind(R.id.takephoto)
	FloatingActionButton takephoto;
	@Bind(R.id.pickpic)
	FloatingActionButton pickpic;

	File mImageFile;
	String mImagePath;
	Bitmap mImage;
	@Bind(R.id.imageView)
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_upload);
		ButterKnife.bind(this);

		service = ServiceGenerator.createService(FileUploadService.class);
	}

	private void addPart() {
		MultipartBody.Part body1 = ServiceGenerator.prepareFilePart(FileUploadActivity.this, "video", mImageFile);

		RequestBody description = ServiceGenerator.createPartFromString("hello, this is description");

		Call<ResponseBody> call = service.uploadFile(description,body1);
		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				Log.i(TAG, "onResponse: "+response.toString());
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				Log.e(TAG, "onFailure: ", t);

			}
		});
	}

	@OnClick({R.id.takephoto, R.id.pickpic})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.takephoto:
				/*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File photoFile = null;
				try {
					photoFile = ImageUtil.createImageFile();
				} catch (IOException ex) {
					Log.e("打开相机错误=》", "拍照时创建新文件失败");
				}

				if (photoFile != null) {
					mImageFile = photoFile;
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
				}*/

				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(FileUploadActivity.this.getPackageManager()) != null) {
					File photoFile = null;
					try {
						photoFile = ImageUtil.createImageFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (photoFile != null && photoFile.exists()) {
                        /*获取当前系统的android版本号*/
						int currentapiVersion = android.os.Build.VERSION.SDK_INT;
						Log.e("currentapiVersion","currentapiVersion====>"+currentapiVersion);
						if (currentapiVersion<24){
							takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
							startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
						}else {
							ContentValues contentValues = new ContentValues(1);
							contentValues.put(MediaStore.Images.Media.DATA, photoFile.getAbsolutePath());
							Uri uri = FileUploadActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
							takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
						}
					} else {
						Toast.makeText(FileUploadActivity.this, "mis_error_image_not_exist", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(FileUploadActivity.this, "mis_msg_no_camera", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.pickpic:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_PICK_IMAGE_KITKAT);
				break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			Log.w(TAG, requestCode + "请求失败");
			if (requestCode == REQUEST_TAKE_PHOTO) {
				if (mImageFile != null) {
					mImageFile.delete();
				}
			}
//			this.finish();
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_PICK_IMAGE_KITKAT:

					if (data != null) {
						mImagePath = ImageUtil.getPath(this, data.getData());
						mImageFile = new File(mImagePath);
						if (mImage != null) {
							mImage.recycle();
						}
						mImage = ImageUtil.getBitmap(this, mImagePath);
						imageView.setImageBitmap(mImage);
						file1Uri = Uri.parse(mImagePath);
						addPart();
					}
					break;
				case REQUEST_TAKE_PHOTO:
					if (mImageFile != null) {
						mImagePath = mImageFile.getAbsolutePath();
						if (mImage != null) {
							mImage.recycle();
						}
						mImage = ImageUtil.getBitmap(this, mImagePath);
						if (mImage != null) {
							mImage.recycle();
						}
						mImage = ImageUtil.getBitmap(this, mImagePath);
						imageView.setImageBitmap(mImage);


					}
					break;

			}
//			this.finish();
		} else
			this.finish();
	}
}

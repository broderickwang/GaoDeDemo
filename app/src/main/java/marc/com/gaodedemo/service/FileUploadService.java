package marc.com.gaodedemo.service;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Broderick on 2016/11/1.
 */

public interface FileUploadService {



	// previous code for single file uploads
	@Multipart
	@POST("/servlet/UploadServlet"/*"/ServletDemo/servlet/UploadServlet"*/)
	Call<ResponseBody> uploadFile(
			@Part("description") RequestBody description,
			@Part MultipartBody.Part file);

	// new code for multiple files
	@Multipart
	@POST("/servlet/GDLngServlet")
	Call<ResponseBody> uploadMultipleFiles(
			@Part("description") RequestBody description,
			@Part MultipartBody.Part file1,
			@Part MultipartBody.Part file2);


}

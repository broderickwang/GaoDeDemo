package marc.com.gaodedemo.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Broderick on 2016/10/28.
 */

public class ServiceGenerator {
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";

	public static final String API_BASE_URL = /*"http://192.168.9.45:8080";*/"http://192.168.9.68:7001";
	//"http://192.168.9.45:8080";
	public static final String API_URL_RELEASE = "http://219.146.254.74:7007";

	private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

	private static Retrofit.Builder builder =
			new Retrofit.Builder()
					.baseUrl(API_BASE_URL)
					.addConverterFactory(GsonConverterFactory.create());

	public static <S> S createService(Class<S> serviceClass) {
		return createService(serviceClass, null, null);
	}

	public static <S> S createService(Class<S> serviceClass, String username, String password) {
		if (username != null && password != null) {
			String credentials = username + ":" + password;
			final String basic =
					"Basic " +credentials;

			httpClient.addInterceptor(new Interceptor() {
				@Override
				public Response intercept(Interceptor.Chain chain) throws IOException {
					Request original = chain.request();

					Request.Builder requestBuilder = original.newBuilder()
							.header("Authorization", basic)
							.header("Accept", "application/json")
							.method(original.method(), original.body());

					Request request = requestBuilder.build();
					return chain.proceed(request);
				}
			});
		}

		OkHttpClient client = httpClient.build();


		Retrofit retrofit = builder.client(client).build();
		return retrofit.create(serviceClass);
	}

	@NonNull
	public static RequestBody createPartFromString(String descriptionString) {
		return RequestBody.create(
				MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
	}

	@NonNull
	public static MultipartBody.Part prepareFilePart(Context context,String partName,File file) {
		// https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
		// use the FileUtils to get the actual file by uri
//		File file = FileUtils.getFile(context, fileUri);

		// create RequestBody instance from file
		RequestBody requestFile =
				RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

		// MultipartBody.Part is used to send also the actual file name
		return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
	}
}

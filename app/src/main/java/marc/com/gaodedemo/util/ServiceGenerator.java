package marc.com.gaodedemo.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Broderick on 2016/10/28.
 */

public class ServiceGenerator {
	public static final String API_BASE_URL = "http://192.168.9.45:8080";

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
}
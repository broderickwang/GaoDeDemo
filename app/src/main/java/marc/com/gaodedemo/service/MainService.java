package marc.com.gaodedemo.service;
import java.util.List;

import marc.com.gaodedemo.bean.ImageInfo;
import marc.com.gaodedemo.bean.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Broderick on 2016/10/27.
 */

public interface MainService {
	@GET("/api/4/start-image/{size}")
	Call<ImageInfo> getSplash(@Path("size")String size);

	@POST("/servlet/GDServlet")
	Call<User> basicLogin();

	@POST("/ServletDemo/servlet/GDServlet")
	Call<User> sendBody(@Body User user);

	@GET("/repos/{owner}/{repo}/contributors")
	Call<List<User>> contributors(
			@Path("owner") String owner,
			@Path("repo") String repo
	);

	@POST("/ServletDemo/servlet/GDServlet?")
	Call<ResponseBody> sendLng(@Query("latitude") String latitude, @Query("longitude")String longitude);

}

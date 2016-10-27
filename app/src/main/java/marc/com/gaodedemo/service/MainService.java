package marc.com.gaodedemo.service;
import java.util.List;

import marc.com.gaodedemo.bean.ImageInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
/**
 * Created by Broderick on 2016/10/27.
 */

public interface MainService {
	@GET("/api/4/start-image/{size}")
	Call<ImageInfo> getSplash(@Path("size")String size);


}

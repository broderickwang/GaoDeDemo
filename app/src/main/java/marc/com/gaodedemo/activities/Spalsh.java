package marc.com.gaodedemo.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import marc.com.gaodedemo.Main;
import marc.com.gaodedemo.R;
import marc.com.gaodedemo.bean.ImageInfo;
import marc.com.gaodedemo.bean.User;
import marc.com.gaodedemo.service.MainService;
import marc.com.gaodedemo.util.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Spalsh extends AppCompatActivity {
	static String BASE_URL = "http://news-at.zhihu.com";
	static String TEST_URL = "download/test.jpg";


	@Bind(R.id.spalsh)
	ImageView spalsh;
	@Bind(R.id.name)
	TextView name;
	@Bind(R.id.jump)
	Button jump;

	int isInto = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spalsh);
		ButterKnife.bind(this);
		ButterKnife.bind(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 设置状态栏透明
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
//		jump.setText(getStringJNI());
		jump.getBackground().setAlpha(100);
		loadD();
		getData();

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(1000);
		spalsh.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new Animation.AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				/*try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(SplashActivity.this, Main.class));
				SplashActivity.this.finish();*/
				new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

					@Override
					public void run() {
						// This method will be executed once the timer is over
						// Start your app main activity
						if(0 == isInto) {
							Intent i = new Intent(Spalsh.this, Main.class);
							startActivity(i);
							finish();
						}
					}
				}, 4000);
			}
		});
	}
	public void getData() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();


		MainService service = retrofit.create(MainService.class);
		Call<ImageInfo> call = service.getSplash("1080*1776");

		call.enqueue(new Callback<ImageInfo>() {
			@Override
			public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
				if (response.body() != null) {
					Glide.with(Spalsh.this)
							.load(response.body().getImg())
							.into(spalsh);
					name.setText(response.body().getText());
				}
			}

			@Override
			public void onFailure(Call<ImageInfo> call, Throwable t) {

			}
		});
	}
	@OnClick(R.id.jump)
	public void onClick() {
		isInto = 1;
		Intent i = new Intent(Spalsh.this, Main.class);
		startActivity(i);
//		downLoad();
		finish();
	}
	private void loadD() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://192.168.9.45:8080")
				.addConverterFactory(GsonConverterFactory.create())
				.build();


		MainService service = ServiceGenerator.createService(MainService.class);//retrofit.create(MainService.class);

		Call<ResponseBody> call = service.sendLng("120","36");
		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {

			}
		});
	}
}

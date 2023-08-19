package com.movieboxtv.app.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.TapsellPlusBannerType;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;
import com.movieboxtv.app.ui.Adapters.PosterHomeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.Actor;
import com.movieboxtv.app.entity.Poster;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ActorActivity extends AppCompatActivity {

    private LinearLayout linear_layout_activity_actor_movies;
    private TextView text_view_activity_actor_height;
    private TextView text_view_activity_actor_born;
    private TextView text_view_activity_actor_full_name;
    private ReadMoreTextView text_view_activity_actor_bio;
    private ImageView image_view_activity_actor_image;
    private ImageView image_view_activity_actor_background;
    private Actor actor;
    private TextView text_view_activity_actor_type;
    private String backable;
    private PosterHomeAdapter posterAdapter;
    private LinearLayoutManager linearLayoutManagerMoreMovies;
    private RecyclerView recycle_view_activity_activity_actor_movies;
    private LinearLayout linear_layout_activity_actor_height;
    private LinearLayout linear_layout_activity_actor_born;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        initView();
        initAction();
        getMovie();
        setActor();
        getRandomMovies();
        showAdsBanner();


    }

    private void setActor() {

        Picasso.with(this).load(actor.getImage()).into(image_view_activity_actor_image);
        final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //BlurImage.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(image_view_activity_actor_background);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) { }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        };
        Picasso.with(getApplicationContext()).load(actor.getImage()).into(target);
        image_view_activity_actor_background.setTag(target);

        ViewCompat.setTransitionName(image_view_activity_actor_image, "imageMain");

        text_view_activity_actor_full_name.setText(actor.getName());
        text_view_activity_actor_bio.setText(actor.getBio());
        text_view_activity_actor_bio.setTrimCollapsedText("بیشتر");
        text_view_activity_actor_bio.setTrimExpandedText("خلاصه");

        if (actor.getHeight()!=null){
            if (!actor.getHeight().isEmpty()){
                text_view_activity_actor_height.setText(actor.getHeight());
                linear_layout_activity_actor_height.setVisibility(View.VISIBLE);
            }
        }
        if (actor.getBorn()!=null){
            if (!actor.getBorn().isEmpty()){
                text_view_activity_actor_born.setText(actor.getBorn());
                linear_layout_activity_actor_born.setVisibility(View.VISIBLE);
            }
        }
        if (actor.getRole()!=null){
            if (!actor.getRole().isEmpty()){
                text_view_activity_actor_type.setText(" ( " + actor.getRole() + " ) ");
                text_view_activity_actor_type.setVisibility(View.VISIBLE);
            }
        }


    }
    private void getMovie() {
        actor = getIntent().getParcelableExtra("actor");
        backable = getIntent().getStringExtra("backable");
    }
    private void initAction() {

    }

    private void initView() {

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.image_view_activity_actor_background= findViewById(R.id.image_view_activity_actor_background);
        this.image_view_activity_actor_image= findViewById(R.id.image_view_activity_actor_image);
        this.text_view_activity_actor_type= findViewById(R.id.text_view_activity_actor_type);
        this.text_view_activity_actor_bio= findViewById(R.id.text_view_activity_actor_bio);
        this.text_view_activity_actor_born= findViewById(R.id.text_view_activity_actor_born);
        this.text_view_activity_actor_full_name= findViewById(R.id.text_view_activity_actor_full_name);
        this.text_view_activity_actor_height= findViewById(R.id.text_view_activity_actor_height);
        this.linear_layout_activity_actor_movies= findViewById(R.id.linear_layout_activity_actor_movies);
        this.recycle_view_activity_activity_actor_movies= findViewById(R.id.recycle_view_activity_activity_actor_movies);
        this.linear_layout_activity_actor_height= findViewById(R.id.linear_layout_activity_actor_height);
        this.linear_layout_activity_actor_born= findViewById(R.id.linear_layout_activity_actor_born);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRandomMovies() {

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Poster>> call = service.getPosterByActor(actor.getId());
        call.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0) {
                        linearLayoutManagerMoreMovies = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        posterAdapter  = new PosterHomeAdapter(response.body(), ActorActivity.this);
                        recycle_view_activity_activity_actor_movies.setHasFixedSize(true);
                        recycle_view_activity_activity_actor_movies.setAdapter(posterAdapter);
                        recycle_view_activity_activity_actor_movies.setLayoutManager(linearLayoutManagerMoreMovies);
                        linear_layout_activity_actor_movies.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) {
            }
        });
    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }
    public void showAdsBanner() {
        if (getString(R.string.AD_MOB_ENABLED_BANNER).equals("true")) {
            if (!checkSUBSCRIBED()) {
                PrefManager prefManager= new PrefManager(getApplicationContext());
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("FACEBOOK")){
                    showFbBanner();
                }
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("ADMOB")){
                    showAdmobBanner();
                }
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("BOTH")) {
                    if (prefManager.getString("Banner_Ads_display").equals("FACEBOOK")) {
                        prefManager.setString("Banner_Ads_display", "ADMOB");
                        showAdmobBanner();
                    } else {
                        prefManager.setString("Banner_Ads_display", "FACEBOOK");
                        showFbBanner();
                    }
                }
            }
        }
    }
    public void showAdmobBanner(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.linear_layout_ads);
        final AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        linear_layout_ads.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    String standardBannerResponseId;
    public void showFbBanner(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        TapsellPlus.requestStandardBannerAd(
                this, prefManager.getString("ADMIN_BANNER_FACEBOOK_ID"),
                TapsellPlusBannerType.BANNER_320x50,
                new AdRequestCallback() {
                    @Override
                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel);
                        //Ad is ready to show
                        //Put the ad's responseId to your responseId variable
                        standardBannerResponseId = tapsellPlusAdModel.getResponseId();
                        tapsellbanner();
                    }

                    @Override
                    public void error(@NonNull String message) {
                    }
                });
    }

    public void tapsellbanner(){

        TapsellPlus.showStandardBannerAd(this, standardBannerResponseId,
                findViewById(R.id.linear_layout_ads),
                new AdShowListener() {
                    @Override
                    public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.onOpened(tapsellPlusAdModel);
                    }

                    @Override
                    public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                        super.onError(tapsellPlusErrorModel);
                    }
                });
    }
}

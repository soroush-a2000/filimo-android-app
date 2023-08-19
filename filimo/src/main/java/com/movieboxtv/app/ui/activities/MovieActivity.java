package com.movieboxtv.app.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;
import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.Utils.FaNum;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.Actor;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.entity.Comment;
import com.movieboxtv.app.entity.DownloadItem;
import com.movieboxtv.app.entity.Language;
import com.movieboxtv.app.entity.Poster;
import com.movieboxtv.app.entity.Source;
import com.movieboxtv.app.entity.Subtitle;
import com.movieboxtv.app.event.CastSessionEndedEvent;
import com.movieboxtv.app.event.CastSessionStartedEvent;
import com.movieboxtv.app.services.DownloadService;
import com.movieboxtv.app.services.ToastMsg;
import com.movieboxtv.app.services.ToastService;
import com.movieboxtv.app.ui.Adapters.ActorAdapter2;
import com.movieboxtv.app.ui.Adapters.CommentAdapter;
import com.movieboxtv.app.ui.Adapters.GenreAdapter;
import com.movieboxtv.app.ui.Adapters.PosterHomeAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.TapsellPlusBannerType;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;
import ir.tapsell.sdk.TapsellShowOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieActivity extends AppCompatActivity {
    private static String TAG = "MovieActivity";
    private CastContext mCastContext;
    private SessionManager mSessionManager;
    private CastSession mCastSession;
    private final SessionManagerListener mSessionManagerListener = new SessionManagerListenerImpl();

    //views
    private ImageView image_view_activity_movie_background;
    private ImageView image_view_activity_movie_cover;
    private ImageView image_view_activity_movie_poster;
    private TextView text_view_activity_movie_title;
    private LinearLayout linear_layout_activity_movie_country;
    private ReadMoreTextView text_view_activity_movie_description;
    private TextView text_view_activity_movie_year;
    private TextView text_view_activity_movie_duration;
    private TextView text_view_activity_movie_classification;
    private RatingBar rating_bar_activity_movie_rating;
    private RecyclerView recycle_view_activity_movie_genres;
    private LinearLayout action_button_play;
    private TextView text_view_btn_play;
    private FloatingActionButton floating_action_button_activity_movie_comment;

    private LinearLayout linear_layout_activity_movie_cast;
    private RecyclerView recycle_view_activity_activity_movie_cast;
    private LinearLayoutManager linearLayoutManagerCast;
    private LinearLayout linear_layout_movie_activity_trailer;
    private LinearLayout linear_layout_movie_activity_rate;

    // lists
    private ArrayList<Comment> commentList = new ArrayList<>();
    private ArrayList<Subtitle> subtitlesForCast = new ArrayList<>();

    // movie
    private Poster poster;

    // layout manager
    private LinearLayoutManager linearLayoutManagerComments;
    private LinearLayoutManager linearLayoutManagerSources;
    private LinearLayoutManager linearLayoutManagerGenre;
    private LinearLayoutManager linearLayoutManagerMoreMovies;
    private LinearLayoutManager linearLayoutManagerDownloadSources;

    // adapters
    private GenreAdapter genreAdapter;
    private ActorAdapter2 actorAdapter;
    private CommentAdapter commentAdapter;
    private PosterHomeAdapter posterAdapter;


    private LinearLayout linear_layout_movie_activity_trailer_clicked;
    private RelativeLayout relative_layout_subtitles_loading;
    private RecyclerView recycle_view_activity_activity_movie_more_movies;
    private LinearLayout linear_layout_activity_movie_more_movies;
    private LinearLayout linear_layout_activity_movie_my_list;
    private ImageView image_view_activity_movie_my_list;
    private LinearLayout linear_layout_movie_activity_download;
    private List<Source> downloadableList = new ArrayList<>();
    private LinearLayout linear_layout_movie_activity_share;
    private String from;
    private int tryed = 0;
    private LinearLayout linear_layout_activity_movie_rating;
    private LinearLayout linear_layout_activity_movie_imdb;
    private TextView text_view_activity_movie_imdb_rating;
    private TextView text_view_activity_movie_country;

    private RewardedVideoAd mRewardedVideoAd;

    private int operationAfterAds = 0;

    private boolean autoDisplay = false;
    private PrefManager prefManager;


    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {
            Log.d(TAG, "onSessionStarting");
        }

        @Override
        public void onSessionStarted(Session session, String s) {
            Log.d(TAG, "onSessionStarted");
            invalidateOptionsMenu();
            EventBus.getDefault().post(new CastSessionStartedEvent());
        }

        @Override
        public void onSessionStartFailed(Session session, int i) {
            Log.d(TAG, "onSessionStartFailed");
        }

        @Override
        public void onSessionEnding(Session session) {
            Log.d(TAG, "onSessionEnding");
            EventBus.getDefault().post(new CastSessionEndedEvent(session.getSessionRemainingTimeMs()));
        }

        @Override
        public void onSessionEnded(Session session, int i) {
            Log.d(TAG, "onSessionEnded");
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            Log.d(TAG, "onSessionResuming");
        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            Log.d(TAG, "onSessionResumed");
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {
            Log.d(TAG, "onSessionResumeFailed");
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            Log.d(TAG, "onSessionSuspended");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        mCastContext = CastContext.getSharedInstance(this);
        prefManager = new PrefManager(getApplicationContext());



        initView();
        initAction();
        getMovie();
        setMovie();
        getPosterCastings();
        getRandomMovies();
        checkFavorite();
        setDownloadableList();

        showAdsBanner();
        initRewarded();


        TextView report1 = (TextView)findViewById(R.id.report1);
        report1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieActivity.this, SupportActivity.class);
                startActivity(intent);
            }
        });



    }

    private InterstitialAd admobInterstitialAd;
    private void requestAdmobInterstitial() {
        if (admobInterstitialAd==null){
            PrefManager prefManager= new PrefManager(MovieActivity.this);
            admobInterstitialAd = new InterstitialAd(MovieActivity.this.getApplicationContext());
            admobInterstitialAd.setAdUnitId(prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID"));
        }
        if (!admobInterstitialAd.isLoaded()){
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            admobInterstitialAd.loadAd(adRequest);
        }
    }

    public void loadRewardedVideoAd() {

        if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
            Toast.makeText(MovieActivity.this, "در حال بارگذاری ..", Toast.LENGTH_SHORT).show();
            requestAd();
        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
            Toast.makeText(MovieActivity.this, "در حال بارگذاری ..", Toast.LENGTH_SHORT).show();
            mRewardedVideoAd.loadAd(prefManager.getString("ADMIN_REWARDED_ADMOB_ID"),
                    new AdRequest.Builder().build());
        }else{
            new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.use_content_for_free));

            switch (operationAfterAds) {
                case 100:
                    poster.setDownloadas("1");
                    break;
                case 200:
                    poster.setPlayas("1");
                    break;
            }
        }

    }


    String rewardedResponseId;

    private void requestAd() {
        TapsellPlus.requestRewardedVideoAd(
                this,
                prefManager.getString("ADMIN_NATIVE_FACEBOOK_ID"),
                new AdRequestCallback() {
                    @Override
                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel);
                        rewardedResponseId = tapsellPlusAdModel.getResponseId();
                        showAd();
                    }

                    @Override
                    public void error(String message) {
                        Toast.makeText(MovieActivity.this, "بارگذاری تبلیغ با خطا مواجه شد", Toast.LENGTH_SHORT).show();

                        new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.use_content_for_free));
                        switch (operationAfterAds) {
                            case 100:
                                poster.setDownloadas("1");
                                break;
                            case 200:
                                poster.setPlayas("1");
                                break;
                        }
                    }
                });
    }

    private void showAd() {
        TapsellShowOptions show = new TapsellShowOptions();
        show.setBackDisabled(true);
        show.setShowDialog(true);

        TapsellPlus.showRewardedVideoAd(this, rewardedResponseId,
                new AdShowListener() {
                    @Override
                    public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.onOpened(tapsellPlusAdModel);

                        new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.ad_text));
                    }

                    @Override
                    public void onClosed(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.onClosed(tapsellPlusAdModel);

                    }

                    @Override
                    public void onRewarded(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.onRewarded(tapsellPlusAdModel);
                        new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.use_content_for_free));
                        switch (operationAfterAds) {
                            case 100:
                                poster.setDownloadas("1");
                                break;
                            case 200:
                                poster.setPlayas("1");
                                break;
                        }
                    }

                    @Override
                    public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                        super.onError(tapsellPlusErrorModel);
                        new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.use_content_for_free));
                        switch (operationAfterAds) {
                            case 100:
                                poster.setDownloadas("1");
                                break;
                            case 200:
                                poster.setPlayas("1");
                                break;
                        }
                    }

                });
    }


    public void initRewarded() {

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if (autoDisplay) {
                    autoDisplay = false;
                    mRewardedVideoAd.show();
                }
                Log.d("Rewarded", "onRewardedVideoAdLoaded ");

            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.d("Rewarded", "onRewardedVideoAdOpened ");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.d("Rewarded", "onRewardedVideoStarted ");

            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
                Log.d("Rewarded", "onRewardedVideoAdClosed ");

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.use_content_for_free));
                Log.d("Rewarded", "onRewarded ");
                switch (operationAfterAds) {
                    case 100:
                        poster.setDownloadas("1");
                        break;
                    case 200:
                        poster.setPlayas("1");
                        break;
                }
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.d("Rewarded", "onRewardedVideoAdLeftApplication ");

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.d("Rewarded", "onRewardedVideoAdFailedToLoad " + i);
                Toast.makeText(MovieActivity.this, "بارگذاری تبلیغ با خطا مواجه شد", Toast.LENGTH_SHORT).show();

                new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.use_content_for_free));
                switch (operationAfterAds) {
                    case 100:
                        poster.setDownloadas("1");
                        break;
                    case 200:
                        poster.setPlayas("1");
                        break;
                }
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });

    }


    private void setDownloadableList() {
        for (int i = 0; i < poster.getSources().size(); i++) {
            if (!poster.getSources().get(i).getType().equals("youtube") && !poster.getSources().get(i).getType().equals("embed")) {
                downloadableList.add(poster.getSources().get(i));
            }
        }
    }

    private void getRandomMovies() {
        String genres_list = "";
        for (int i = 0; i < poster.getGenres().size(); i++) {
            if (poster.getGenres().size() - 1 == i) {
                genres_list += poster.getGenres().get(i).getId();
            } else {
                genres_list += poster.getGenres().get(i).getId() + ",";
            }
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Poster>> call = service.getRandomMoivies(genres_list);
        call.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        List<Poster> posterList = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getId() != poster.getId())
                                posterList.add(response.body().get(i));
                        }
                        linearLayoutManagerMoreMovies = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        posterAdapter = new PosterHomeAdapter(posterList, MovieActivity.this);
                        recycle_view_activity_activity_movie_more_movies.setHasFixedSize(true);
                        recycle_view_activity_activity_movie_more_movies.setAdapter(posterAdapter);
                        recycle_view_activity_activity_movie_more_movies.setLayoutManager(linearLayoutManagerMoreMovies);
                        linear_layout_activity_movie_more_movies.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) {
            }
        });
    }

    private void getPosterCastings() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Actor>> call = service.getRolesByPoster(poster.getId());
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        linearLayoutManagerCast = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        actorAdapter = new ActorAdapter2(response.body(), MovieActivity.this);
                        recycle_view_activity_activity_movie_cast.setHasFixedSize(true);
                        recycle_view_activity_activity_movie_cast.setAdapter(actorAdapter);
                        recycle_view_activity_activity_movie_cast.setLayoutManager(linearLayoutManagerCast);
                        linear_layout_activity_movie_cast.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
            }
        });
    }

    private void getMovie() {
        poster = getIntent().getParcelableExtra("poster");
        from = getIntent().getStringExtra("from");
    }

    private void setMovie() {
        if (poster.getCover() != null) {
            Picasso.with(this).load(poster.getCover()).into(image_view_activity_movie_cover);
            final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //BlurImage.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(image_view_activity_movie_background);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.with(getApplicationContext()).load(poster.getImage()).into(target);
            image_view_activity_movie_background.setTag(target);

        }
        if (poster.getImage() != null) {
            Picasso.with(this).load(poster.getImage()).into(image_view_activity_movie_poster);
            ViewCompat.setTransitionName(image_view_activity_movie_poster, "imageMain");
        }


        text_view_activity_movie_title.setText(poster.getTitle());
        text_view_activity_movie_description.setText(poster.getDescription());
        text_view_activity_movie_description.setTrimCollapsedText("بیشتر");
        text_view_activity_movie_description.setTrimExpandedText("خلاصه");

        if (poster.getYear() != null) {
            if (!poster.getYear().isEmpty()) {
                text_view_activity_movie_year.setText(FaNum.convert(poster.getYear()));
                text_view_activity_movie_year.setVisibility(View.VISIBLE);
            }
        }

        if (poster.getClassification() != null) {
            if (!poster.getClassification().isEmpty()) {
                text_view_activity_movie_classification.setText(FaNum.convert(poster.getClassification()));
                text_view_activity_movie_classification.setVisibility(View.VISIBLE);
            }
        }

        if (poster.getDuration() != null) {
            if (!poster.getDuration().isEmpty()) {
                text_view_activity_movie_duration.setText(FaNum.convert(poster.getDuration()));
                text_view_activity_movie_duration.setVisibility(View.VISIBLE);
            }
        }
        if (poster.getImdb() != null) {
            if (!poster.getImdb().isEmpty()) {
                double d = Double.parseDouble(poster.getImdb());
                DecimalFormat f = new DecimalFormat("##.0");
                text_view_activity_movie_imdb_rating.setText(FaNum.convert("۱۰ / " + f.format(d)));
                linear_layout_activity_movie_imdb.setVisibility(View.VISIBLE);
            }
        }
        if (poster.getCountry() != null) {
            if (!poster.getCountry().isEmpty()) {
                text_view_activity_movie_country.setText("محصول کشور " + poster.getCountry().get(0).getTitle());
                linear_layout_activity_movie_country.setVisibility(View.VISIBLE);
            }
        }

        if (checkSUBSCRIBED()) {
            text_view_btn_play.setText("پخش");
        } else {
            if (poster.getPlayas() != null) {
                if (!poster.getPlayas().isEmpty()) {
                    if (poster.getPlayas().equals("2")) {
                        text_view_btn_play.setText("خرید اشتراک");
                    } else if (poster.getPlayas().equals("3")) {
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            // showDialog(true);
                            text_view_btn_play.setText("پخش");
                        }else {
                            text_view_btn_play.setText("ورود و تماشا");
                        }
                    }
                }
            }
        }

        rating_bar_activity_movie_rating.setRating(poster.getRating());
        linear_layout_activity_movie_rating.setVisibility(poster.getRating() == 0 ? View.GONE : View.VISIBLE);

        this.linearLayoutManagerGenre = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.genreAdapter = new GenreAdapter(poster.getGenres(), this);
        recycle_view_activity_movie_genres.setHasFixedSize(true);
        recycle_view_activity_movie_genres.setAdapter(genreAdapter);
        recycle_view_activity_movie_genres.setLayoutManager(linearLayoutManagerGenre);

        if (poster.getTrailer() != null) {
            linear_layout_movie_activity_trailer.setVisibility(View.VISIBLE);
        }
        if (poster.getComment()) {
            floating_action_button_activity_movie_comment.setVisibility(View.VISIBLE);
        } else {
            floating_action_button_activity_movie_comment.setVisibility(View.GONE);
        }
    }

    private void initAction() {
        linear_layout_movie_activity_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        linear_layout_activity_movie_my_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavotite();
            }
        });
        linear_layout_movie_activity_trailer_clicked.setOnClickListener(v -> {
            playTrailer();
        });
        action_button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSUBSCRIBED()) {
                    if (Config.PLAYERS_SUPPORT == 1){
                        playermetod();
                    }else {
                        showSourcesPlayDialog();
                    }
                } else {
                    if (poster.getPlayas().equals("2")) {
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            // showDialog(false);
                            Intent intent = new Intent(MovieActivity.this, PackageBuyActivity.class);
                            intent.putExtra("type_form", "play");
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else if (poster.getPlayas().equals("3")) {
                        operationAfterAds = 200;
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            // showDialog(true);

                                if (mRewardedVideoAd.isLoaded()) {
                                    mRewardedVideoAd.show();
                                } else {
                                    autoDisplay = true;
                                    loadRewardedVideoAd();
                                }

                        } else {
                            Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        if (Config.PLAYERS_SUPPORT == 1){
                            playermetod();
                        }else {
                            showSourcesPlayDialog();
                        }
                    }
                }
            }
        });
        linear_layout_movie_activity_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog();
            }
        });
        linear_layout_movie_activity_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Config.ADM_SUPPORT == 1){
                    downloaddialog();
                }else {
                    if (checkSUBSCRIBED()) {
                        showSourcesDownloadDialog();
                    } else {
                        if (poster.getDownloadas().equals("2")) {
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                //showDialog(false);
                                Intent intent = new Intent(MovieActivity.this, PackageBuyActivity.class);
                                intent.putExtra("type_form", "download");
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else if (poster.getDownloadas().equals("3")) {
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                // showDialog(true);
                                if (mRewardedVideoAd.isLoaded()) {
                                    mRewardedVideoAd.show();
                                } else {
                                    autoDisplay = true;
                                    loadRewardedVideoAd();
                                }
                            } else {
                                Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                            operationAfterAds = 100;
                        } else {
                            showSourcesDownloadDialog();
                        }
                    }
                }


            }
        });
        floating_action_button_activity_movie_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentsDialog();
            }
        });
    }

    public boolean checkSUBSCRIBED() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    public void playSource(int position) {
        addView();

        if (poster.getSources().get(position).getType().equals("youtube")) {
            Intent intent = new Intent(MovieActivity.this, YoutubeActivity.class);
            intent.putExtra("url", poster.getSources().get(position).getUrl());
            startActivity(intent);
            return;
        }
        if (poster.getSources().get(position).getType().equals("embed")) {
            Intent intent = new Intent(MovieActivity.this, EmbedActivity.class);
            intent.putExtra("url", poster.getSources().get(position).getUrl());
            startActivity(intent);
            return;
        }
        if (mCastSession == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            loadSubtitles(position);
        } else {
            Intent intent = new Intent(MovieActivity.this, PlayerActivity.class);
            intent.putExtra("id", poster.getId());
            intent.putExtra("url", poster.getSources().get(position).getUrl());
            intent.putExtra("type", poster.getSources().get(position).getType());
            intent.putExtra("image", poster.getImage());
            intent.putExtra("kind", "com");
            intent.putExtra("title", poster.getTitle());
            intent.putExtra("subtitle", poster.getTitle() + "(" + poster.getYear() + ")");
            startActivity(intent);
        }
    }

    public void playSource2(int position) {
        addView();

        if (poster.getSources().get(position).getType().equals("youtube")) {
            Toast.makeText(MovieActivity.this, "پخش این محتوا با MX Player امکان پذیر نیست.", Toast.LENGTH_SHORT).show();
        }
        if (poster.getSources().get(position).getType().equals("embed")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri videoUri = Uri.parse(poster.getSources().get(position).getUrl());
            intent.putExtra("title", poster.getTitle());
            intent.setDataAndType( videoUri, "application/x-mpegURL" );
            intent.setPackage( "com.mxtech.videoplayer.ad" );
            startActivity( intent );
        }
        if (mCastSession == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            loadSubtitles(position);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri videoUri = Uri.parse(poster.getSources().get(position).getUrl());
            intent.setDataAndType( videoUri, "application/x-mpegURL" );
            intent.putExtra("title", poster.getTitle());
            intent.setPackage( "com.mxtech.videoplayer.ad" );
            startActivity( intent );
        }
    }

    public void playSource3(int position) {
        addView();

        if (poster.getSources().get(position).getType().equals("youtube")) {
            Toast.makeText(MovieActivity.this, "پخش این محتوا با VLC امکان پذیر نیست.", Toast.LENGTH_SHORT).show();
        }
        if (poster.getSources().get(position).getType().equals("embed")) {
            int vlcRequestCode = 42;
            Uri uri = Uri.parse(poster.getSources().get(position).getUrl());
            Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
            vlcIntent.setPackage("org.videolan.vlc");
            vlcIntent.putExtra("title", poster.getTitle());
            vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
            vlcIntent.putExtra("from_start", false);
            startActivityForResult(vlcIntent, vlcRequestCode);
        }
        if (mCastSession == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            loadSubtitles(position);
        } else {
            int vlcRequestCode = 42;
            Uri uri = Uri.parse(poster.getSources().get(position).getUrl());
            Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
            vlcIntent.setPackage("org.videolan.vlc");
            vlcIntent.putExtra("title", poster.getTitle());
            vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
            vlcIntent.putExtra("from_start", false);
            startActivityForResult(vlcIntent, vlcRequestCode);
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public void playTrailer() {
        if (poster.getTrailer().getType().equals("youtube")) {
            Intent intent = new Intent(MovieActivity.this, YoutubeActivity.class);
            intent.putExtra("url", poster.getTrailer().getUrl());
            startActivity(intent);
            return;
        }
        if (poster.getTrailer().getType().equals("embed")) {
            Intent intent = new Intent(MovieActivity.this, EmbedActivity.class);
            intent.putExtra("url", poster.getTrailer().getUrl());
            startActivity(intent);
            return;
        }
        if (mCastSession == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            loadRemoteMedia(0, true);
        } else {
            Intent intent = new Intent(MovieActivity.this, PlayerActivity.class);
            intent.putExtra("url", poster.getTrailer().getUrl());
            intent.putExtra("type", poster.getTrailer().getType());
            intent.putExtra("image", poster.getImage());
            intent.putExtra("title", poster.getTitle());
            intent.putExtra("subtitle", poster.getTitle() + " تریلر ");
            startActivity(intent);
        }
    }

    public void rateDialog() {
        Dialog rateDialog = new Dialog(this,
                R.style.dialogman);
        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setCancelable(true);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        rateDialog.setContentView(R.layout.dialog_rate);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app = (AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final Button buttun_send = (Button) rateDialog.findViewById(R.id.buttun_send);
        final Button button_cancel = (Button) rateDialog.findViewById(R.id.button_cancel);
        final TextView text_view_rate_title = (TextView) rateDialog.findViewById(R.id.text_view_rate_title);
        text_view_rate_title.setText(getResources().getString(R.string.rate_this_movie));
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
            }
        });

        buttun_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prf = new PrefManager(getApplicationContext());
                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                    Integer id_user = Integer.parseInt(prf.getString("ID_USER"));
                    String key_user = prf.getString("TOKEN_USER");
                    Retrofit retrofit = apiClient.getClient();
                    apiRest service = retrofit.create(apiRest.class);
                    Call<ApiResponse> call = service.addPosterRate(id_user + "", key_user, poster.getId(), AppCompatRatingBar_dialog_rating_app.getRating());
                    call.enqueue(new retrofit2.Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getCode() == 200) {
                                    new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                                    if (response.body().getValues().size() > 0) {
                                        if (response.body().getValues().get(0).getName().equals("rate")) {
                                            linear_layout_activity_movie_rating.setVisibility(View.VISIBLE);
                                            rating_bar_activity_movie_rating.setRating(Float.parseFloat(response.body().getValues().get(0).getValue()));
                                        }
                                    }
                                } else {
                                    new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                                }
                            }
                            rateDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            rateDialog.dismiss();
                        }
                    });
                } else {
                    rateDialog.dismiss();
                    Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            }
        });
        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    rateDialog.dismiss();
                }
                return true;
            }
        });
        rateDialog.show();

    }

    public void showCommentsDialog() {

        Dialog dialog = new Dialog(this,
                R.style.dialogman);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.dialog_comment);
        TextView text_view_comment_dialog_count = dialog.findViewById(R.id.text_view_comment_dialog_count);
        ImageView image_view_comment_dialog_close = dialog.findViewById(R.id.image_view_comment_dialog_close);
        ImageView image_view_comment_dialog_empty = dialog.findViewById(R.id.image_view_comment_dialog_empty);
        ImageView image_view_comment_dialog_add_comment = dialog.findViewById(R.id.image_view_comment_dialog_add_comment);
        ProgressBar progress_bar_comment_dialog_comments = dialog.findViewById(R.id.progress_bar_comment_dialog_comments);
        ProgressBar progress_bar_comment_dialog_add_comment = dialog.findViewById(R.id.progress_bar_comment_dialog_add_comment);
        EditText edit_text_comment_dialog_add_comment = dialog.findViewById(R.id.edit_text_comment_dialog_add_comment);
        RecyclerView recycler_view_comment_dialog_comments = dialog.findViewById(R.id.recycler_view_comment_dialog_comments);

        image_view_comment_dialog_empty.setVisibility(View.GONE);
        recycler_view_comment_dialog_comments.setVisibility(View.GONE);
        progress_bar_comment_dialog_comments.setVisibility(View.VISIBLE);
        commentAdapter = new CommentAdapter(commentList, MovieActivity.this);
        linearLayoutManagerComments = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_view_comment_dialog_comments.setHasFixedSize(true);
        recycler_view_comment_dialog_comments.setAdapter(commentAdapter);
        recycler_view_comment_dialog_comments.setLayoutManager(linearLayoutManagerComments);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Comment>> call = service.getCommentsByPoster(poster.getId());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        commentList.clear();
                        for (int i = 0; i < response.body().size(); i++)
                            commentList.add(response.body().get(i));

                        commentAdapter.notifyDataSetChanged();

                        text_view_comment_dialog_count.setText(commentList.size() + " نظر ");
                        image_view_comment_dialog_empty.setVisibility(View.GONE);
                        recycler_view_comment_dialog_comments.setVisibility(View.VISIBLE);
                        progress_bar_comment_dialog_comments.setVisibility(View.GONE);
                        recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                        recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                    } else {
                        image_view_comment_dialog_empty.setVisibility(View.VISIBLE);
                        recycler_view_comment_dialog_comments.setVisibility(View.GONE);
                        progress_bar_comment_dialog_comments.setVisibility(View.GONE);
                    }
                } else {
                    image_view_comment_dialog_empty.setVisibility(View.VISIBLE);
                    recycler_view_comment_dialog_comments.setVisibility(View.GONE);
                    progress_bar_comment_dialog_comments.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                image_view_comment_dialog_empty.setVisibility(View.VISIBLE);
                recycler_view_comment_dialog_comments.setVisibility(View.GONE);
                progress_bar_comment_dialog_comments.setVisibility(View.GONE);
            }
        });

        image_view_comment_dialog_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_comment_dialog_add_comment.getText().length() > 0) {
                    PrefManager prf = new PrefManager(MovieActivity.this.getApplicationContext());
                    if (prf.getString("LOGGED").toString().equals("TRUE")) {
                        Integer id_user = Integer.parseInt(prf.getString("ID_USER"));
                        String key_user = prf.getString("TOKEN_USER");
                        byte[] data = new byte[0];
                        String comment_final = "";
                        try {
                            data = edit_text_comment_dialog_add_comment.getText().toString().getBytes("UTF-8");
                            comment_final = Base64.encodeToString(data, Base64.DEFAULT);
                        } catch (UnsupportedEncodingException e) {
                            comment_final = edit_text_comment_dialog_add_comment.getText().toString();
                            e.printStackTrace();
                        }
                        progress_bar_comment_dialog_add_comment.setVisibility(View.VISIBLE);
                        image_view_comment_dialog_add_comment.setVisibility(View.GONE);
                        Retrofit retrofit = apiClient.getClient();
                        apiRest service = retrofit.create(apiRest.class);
                        Call<ApiResponse> call = service.addPosterComment(id_user + "", key_user, poster.getId(), comment_final);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getCode() == 200) {
                                        recycler_view_comment_dialog_comments.setVisibility(View.VISIBLE);
                                        image_view_comment_dialog_empty.setVisibility(View.GONE);
                                        new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                                        edit_text_comment_dialog_add_comment.setText("");
                                        String id = "";
                                        String content = "";
                                        String user = "";
                                        String image = "";

                                        for (int i = 0; i < response.body().getValues().size(); i++) {
                                            if (response.body().getValues().get(i).getName().equals("id")) {
                                                id = response.body().getValues().get(i).getValue();
                                            }
                                            if (response.body().getValues().get(i).getName().equals("content")) {
                                                content = response.body().getValues().get(i).getValue();
                                            }
                                            if (response.body().getValues().get(i).getName().equals("user")) {
                                                user = response.body().getValues().get(i).getValue();
                                            }
                                            if (response.body().getValues().get(i).getName().equals("image")) {
                                                image = response.body().getValues().get(i).getValue();
                                            }
                                        }
                                        Comment comment = new Comment();
                                        comment.setId(Integer.parseInt(id));
                                        comment.setUser(user);
                                        comment.setContent(content);
                                        comment.setImage(image);
                                        comment.setEnabled(true);
                                        comment.setCreated(getResources().getString(R.string.now_time));
                                        commentList.add(comment);
                                        commentAdapter.notifyDataSetChanged();
                                        text_view_comment_dialog_count.setText(commentList.size() + " نظر ");

                                    } else {
                                        new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                                    }
                                }
                                recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                                recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                                commentAdapter.notifyDataSetChanged();
                                progress_bar_comment_dialog_add_comment.setVisibility(View.GONE);
                                image_view_comment_dialog_add_comment.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                progress_bar_comment_dialog_add_comment.setVisibility(View.GONE);
                                image_view_comment_dialog_add_comment.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            }
        });
        image_view_comment_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void downloaddialog() {

        Dialog dialog = new Dialog(this,
                R.style.dialogman);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.dialog_download2);
        ImageView image_view_comment_dialog_close1 = dialog.findViewById(R.id.image_view_comment_dialog_close1);
        TextView text_view_comment_dialog_count1 = dialog.findViewById(R.id.text_view_comment_dialog_count1);

        TextView image_view_comment_dialog_empty1 = dialog.findViewById(R.id.pish);
        TextView image_view_comment_dialog_add_comment1 = dialog.findViewById(R.id.adm);


        commentAdapter = new CommentAdapter(commentList, MovieActivity.this);
        linearLayoutManagerComments = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);


        image_view_comment_dialog_close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        image_view_comment_dialog_add_comment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkSUBSCRIBED()) {
                    boolean isAppInstalled = appInstalledOrNot("com.dv.adm");
                    if (isAppInstalled) {
                        showSourcesDownloadDialog2();
                    } else {
                        Toast.makeText(MovieActivity.this, "اپلیکیشن ADM ‌نصب نیست.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (poster.getDownloadas().equals("2")) {
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            //showDialog(false);
                            Intent intent = new Intent(MovieActivity.this, PackageBuyActivity.class);
                            intent.putExtra("type_form", "download");
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else if (poster.getDownloadas().equals("3")) {
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            // showDialog(true);
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                            } else {
                                autoDisplay = true;
                                loadRewardedVideoAd();
                            }

                        } else {
                            Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        operationAfterAds = 100;
                    } else {
                        boolean isAppInstalled = appInstalledOrNot("com.dv.adm");
                        if (isAppInstalled) {
                            showSourcesDownloadDialog2();
                        } else {
                            Toast.makeText(MovieActivity.this, "اپلیکیشن ADM ‌نصب نیست.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        image_view_comment_dialog_empty1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkSUBSCRIBED()) {
                    showSourcesDownloadDialog();
                } else {
                    if (poster.getDownloadas().equals("2")) {
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            //showDialog(false);
                            Intent intent = new Intent(MovieActivity.this, PackageBuyActivity.class);
                            intent.putExtra("type_form", "download");
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else if (poster.getDownloadas().equals("3")) {
                        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                            // showDialog(true);
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                            } else {
                                autoDisplay = true;
                                loadRewardedVideoAd();
                                Toast.makeText(MovieActivity.this, "در حال بارگذاری ..", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        operationAfterAds = 100;
                    } else {
                        showSourcesDownloadDialog();
                    }
                }

            }
        });

        dialog.show();
    }

    public void playermetod() {


        Dialog dialog = new Dialog(this,
                R.style.dialogman);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.dialog_play);
        ImageView image_view_comment_dialog_close2 = dialog.findViewById(R.id.image_view_comment_dialog_close1);
        TextView text_view_comment_dialog_count1 = dialog.findViewById(R.id.text_view_comment_dialog_count1);

        TextView image_view_comment_dialog_empty2 = dialog.findViewById(R.id.pishf);
        TextView image_view_comment_dialog_add_comment2 = dialog.findViewById(R.id.mxplayer);
        TextView image_view_comment_dialog_add_comment3 = dialog.findViewById(R.id.vlcplayer);


        commentAdapter = new CommentAdapter(commentList, MovieActivity.this);
        linearLayoutManagerComments = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);


        image_view_comment_dialog_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        image_view_comment_dialog_add_comment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                boolean isAppInstalled = appInstalledOrNot("com.mxtech.videoplayer.ad");
                if (isAppInstalled) {
                    showSourcesPlayDialog2();
                } else {
                    Toast.makeText(MovieActivity.this, "اپلیکیشن MX Player ‌نصب نیست.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        image_view_comment_dialog_add_comment3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                boolean isAppInstalled = appInstalledOrNot("org.videolan.vlc");
                if (isAppInstalled) {

                    showSourcesPlayDialog3();
                } else {
                    Toast.makeText(MovieActivity.this, "اپلیکیشن VLC ‌نصب نیست.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        image_view_comment_dialog_empty2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showSourcesPlayDialog();
            }
        });

        dialog.show();
    }
    public void showSourcesDownloadDialog() {
        if (ContextCompat.checkSelfPermission(MovieActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MovieActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        if (downloadableList.size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }
        // if (downloadableList.size()==1){
        //     DownloadSource(downloadableList.get(0));
        //     return;
        //  }

        Dialog download_source_dialog = new Dialog(this,
                R.style.dialogman);
        download_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        download_source_dialog.setCancelable(true);
        download_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = download_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        download_source_dialog.setContentView(R.layout.dialog_download);

        RelativeLayout relative_layout_dialog_source_close = download_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = download_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerDownloadSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        DownloadsAdapter sourceDownloadAdapter = new DownloadsAdapter();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceDownloadAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerDownloadSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download_source_dialog.dismiss();
            }
        });
        download_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    download_source_dialog.dismiss();
                }
                return true;
            }
        });
        download_source_dialog.show();


    }

    public void showSourcesDownloadDialog2() {
        if (ContextCompat.checkSelfPermission(MovieActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MovieActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        if (downloadableList.size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }

        Dialog download_source_dialog = new Dialog(this,
                R.style.dialogman);
        download_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        download_source_dialog.setCancelable(true);
        download_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = download_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        download_source_dialog.setContentView(R.layout.dialog_download);

        RelativeLayout relative_layout_dialog_source_close = download_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = download_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerDownloadSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        DownloadsAdapter2 sourceDownloadAdapter = new DownloadsAdapter2();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceDownloadAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerDownloadSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download_source_dialog.dismiss();
            }
        });
        download_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    download_source_dialog.dismiss();
                }
                return true;
            }
        });
        download_source_dialog.show();
    }

    public void showSourcesPlayDialog() {
        if (poster.getSources().size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }
        //  if (poster.getSources().size()==1){
        //    playSource(0);
        //     return;
        // }

        Dialog play_source_dialog = new Dialog(this,
                R.style.dialogman);
        play_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        play_source_dialog.setCancelable(true);
        play_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = play_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        play_source_dialog.setContentView(R.layout.dialog_sources);

        RelativeLayout relative_layout_dialog_source_close = play_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = play_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        SourceAdapter sourceAdapter = new SourceAdapter();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_source_dialog.dismiss();
            }
        });
        play_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    play_source_dialog.dismiss();
                }
                return true;
            }
        });
        play_source_dialog.show();


    }

    public void showSourcesPlayDialog2() {
        if (poster.getSources().size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }
        //  if (poster.getSources().size()==1){
        //    playSource(0);
        //     return;
        // }

        Dialog play_source_dialog = new Dialog(this,
                R.style.dialogman);
        play_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        play_source_dialog.setCancelable(true);
        play_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = play_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        play_source_dialog.setContentView(R.layout.dialog_sources);

        RelativeLayout relative_layout_dialog_source_close = play_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = play_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        SourceAdapter2 sourceAdapter = new SourceAdapter2();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_source_dialog.dismiss();
            }
        });
        play_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    play_source_dialog.dismiss();
                }
                return true;
            }
        });
        play_source_dialog.show();


    }

    public void showSourcesPlayDialog3() {
        if (poster.getSources().size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }
        //  if (poster.getSources().size()==1){
        //    playSource(0);
        //     return;
        // }

        Dialog play_source_dialog = new Dialog(this,
                R.style.dialogman);
        play_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        play_source_dialog.setCancelable(true);
        play_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = play_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        play_source_dialog.setContentView(R.layout.dialog_sources);

        RelativeLayout relative_layout_dialog_source_close = play_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = play_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        SourceAdapter3 sourceAdapter = new SourceAdapter3();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_source_dialog.dismiss();
            }
        });
        play_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    play_source_dialog.dismiss();
                }
                return true;
            }
        });
        play_source_dialog.show();


    }

    private void initView() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.text_view_activity_movie_imdb_rating = (TextView) findViewById(R.id.text_view_activity_movie_imdb_rating);
        this.text_view_activity_movie_country = (TextView) findViewById(R.id.text_view_activity_movie_country);
        this.linear_layout_activity_movie_rating = (LinearLayout) findViewById(R.id.linear_layout_activity_movie_rating);
        this.linear_layout_activity_movie_imdb = (LinearLayout) findViewById(R.id.linear_layout_activity_movie_imdb);
        this.linear_layout_movie_activity_share = (LinearLayout) findViewById(R.id.linear_layout_movie_activity_share);
        this.floating_action_button_activity_movie_comment = (FloatingActionButton) findViewById(R.id.floating_action_button_activity_movie_comment);
        this.relative_layout_subtitles_loading = (RelativeLayout) findViewById(R.id.relative_layout_subtitles_loading);
        this.action_button_play = (LinearLayout) findViewById(R.id.action_button_play);
        this.text_view_btn_play = (TextView) findViewById(R.id.text_view_btn_play);
        this.image_view_activity_movie_background = (ImageView) findViewById(R.id.image_view_activity_movie_background);
        this.image_view_activity_movie_cover = (ImageView) findViewById(R.id.image_view_activity_movie_cover);
        this.image_view_activity_movie_poster = (ImageView) findViewById(R.id.image_view_activity_movie_poster);
        this.text_view_activity_movie_title = (TextView) findViewById(R.id.text_view_activity_movie_title);
        this.linear_layout_activity_movie_country = (LinearLayout) findViewById(R.id.linear_layout_activity_movie_country);
        this.text_view_activity_movie_description = (ReadMoreTextView) findViewById(R.id.text_view_activity_movie_description);
        this.text_view_activity_movie_duration = (TextView) findViewById(R.id.text_view_activity_movie_duration);
        this.text_view_activity_movie_year = (TextView) findViewById(R.id.text_view_activity_movie_year);
        this.text_view_activity_movie_classification = (TextView) findViewById(R.id.text_view_activity_movie_classification);
        this.rating_bar_activity_movie_rating = (RatingBar) findViewById(R.id.rating_bar_activity_movie_rating);
        this.recycle_view_activity_movie_genres = (RecyclerView) findViewById(R.id.recycle_view_activity_movie_genres);
        this.recycle_view_activity_activity_movie_cast = (RecyclerView) findViewById(R.id.recycle_view_activity_activity_movie_cast);
        this.recycle_view_activity_activity_movie_more_movies = (RecyclerView) findViewById(R.id.recycle_view_activity_activity_movie_more_movies);
        this.linear_layout_activity_movie_cast = (LinearLayout) findViewById(R.id.linear_layout_activity_movie_cast);
        this.linear_layout_movie_activity_trailer = (LinearLayout) findViewById(R.id.linear_layout_movie_activity_trailer);
        this.linear_layout_movie_activity_trailer_clicked = (LinearLayout) findViewById(R.id.linear_layout_movie_activity_trailer_clicked);
        this.linear_layout_movie_activity_rate = (LinearLayout) findViewById(R.id.linear_layout_movie_activity_rate);
        this.linear_layout_activity_movie_more_movies = (LinearLayout) findViewById(R.id.linear_layout_activity_movie_more_movies);
        this.linear_layout_activity_movie_my_list = (LinearLayout) findViewById(R.id.linear_layout_activity_movie_my_list);
        this.linear_layout_movie_activity_download = (LinearLayout) findViewById(R.id.linear_layout_movie_activity_download);
        this.image_view_activity_movie_my_list = (ImageView) findViewById(R.id.image_view_activity_movie_my_list);
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
            mSessionManager.addSessionManagerListener(mSessionManagerListener);
            if (mCastSession == null) {
                mCastSession = mSessionManager.getCurrentCastSession();
            }
            playTrailer();
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Log.d(TAG, "onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {

                }
            }
        });
        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(getTrailerMediaInfos())
                .setAutoplay(autoPlay).build());
    }

    private void loadRemoteMediaSource(int position, boolean autoPlay) {
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
            mSessionManager.addSessionManagerListener(mSessionManagerListener);
            if (mCastSession == null) {
                mCastSession = mSessionManager.getCurrentCastSession();
            }
            tryed++;
            playSource(position);

            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Log.d(TAG, "onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {

                }
            }
        });
        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(getSourceMediaInfos(position))
                .setAutoplay(autoPlay).build());
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
    }

    @Override
    protected void onPause() {
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_cast, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }


    private MediaInfo getSourceMediaInfos(int position) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, poster.getTitle());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, poster.getTitle());

        movieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));
        movieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));
        List<MediaTrack> tracks = new ArrayList<>();

        for (int i = 0; i < subtitlesForCast.size(); i++) {
            tracks.add(buildTrack(i + 1, "text", "captions", subtitlesForCast.get(i).getUrl(), subtitlesForCast.get(i).getLanguage(), "en-US"));
        }

        MediaInfo mediaInfo = new MediaInfo.Builder(poster.getSources().get(position).getUrl())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .build();
        return mediaInfo;
    }

    private MediaInfo getTrailerMediaInfos() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, poster.getTitle());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, poster.getTitle() + " تریلر ");

        movieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));
        movieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));


        List<MediaTrack> tracks = new ArrayList<>();
        MediaInfo mediaInfo = new MediaInfo.Builder(poster.getTrailer().getUrl())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .build();

        return mediaInfo;
    }

    private static MediaTrack buildTrack(long id, String type, String subType, String contentId,
                                         String name, String language) {
        int trackType = MediaTrack.TYPE_UNKNOWN;
        if ("text".equals(type)) {
            trackType = MediaTrack.TYPE_TEXT;
        } else if ("video".equals(type)) {
            trackType = MediaTrack.TYPE_VIDEO;
        } else if ("audio".equals(type)) {
            trackType = MediaTrack.TYPE_AUDIO;
        }

        int trackSubType = MediaTrack.SUBTYPE_NONE;
        if (subType != null) {
            if ("captions".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_CAPTIONS;
            } else if ("subtitle".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_SUBTITLES;
            }
        }

        return new MediaTrack.Builder(id, trackType)
                .setContentType(MediaFormat.MIMETYPE_TEXT_VTT)
                .setName(name)
                .setSubtype(trackSubType)
                .setContentId(contentId)
                .setLanguage(language).build();
    }

    @Override
    public void onBackPressed() {
        if (from != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (from != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceHolder> {


        @Override
        public SourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_play, parent, false);
            SourceHolder mh = new SourceAdapter.SourceHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(SourceAdapter.SourceHolder holder, final int position) {
            //  holder.text_view_item_source_type.setText(poster.getSources().get(position).getType());
            holder.text_view_item_source_type.setText(poster.getSources().get(position).getQuality());
            switch (poster.getSources().get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
                case "youtube":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_youtube));
                    break;
                case "embed":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_embed_file));
                    break;
            }

            holder.text_view_item_source_source.setText(poster.getSources().get(position).getUrl());
            holder.image_view_item_source_type_play.setOnClickListener(v -> {
                playSource(position);

            });

        }

        @Override
        public int getItemCount() {
            return poster.getSources().size();
        }

        public class SourceHolder extends RecyclerView.ViewHolder {
            private final CardView image_view_item_source_type_play;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public SourceHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_play = (CardView) itemView.findViewById(R.id.image_view_item_source_type_play);
            }
        }
    }

    public class SourceAdapter2 extends RecyclerView.Adapter<SourceAdapter2.SourceHolder> {


        @Override
        public SourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_play, parent, false);
            SourceHolder mh = new SourceAdapter2.SourceHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(SourceAdapter2.SourceHolder holder, final int position) {
            //  holder.text_view_item_source_type.setText(poster.getSources().get(position).getType());
            holder.text_view_item_source_type.setText(poster.getSources().get(position).getQuality());
            switch (poster.getSources().get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
                case "youtube":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_youtube));
                    break;
                case "embed":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_embed_file));
                    break;
            }

            holder.text_view_item_source_source.setText(poster.getSources().get(position).getUrl());
            holder.image_view_item_source_type_play.setOnClickListener(v -> {
                playSource2(position);

            });

        }

        @Override
        public int getItemCount() {
            return poster.getSources().size();
        }

        public class SourceHolder extends RecyclerView.ViewHolder {
            private final CardView image_view_item_source_type_play;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public SourceHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_play = (CardView) itemView.findViewById(R.id.image_view_item_source_type_play);
            }
        }
    }

    public class SourceAdapter3 extends RecyclerView.Adapter<SourceAdapter3.SourceHolder> {


        @Override
        public SourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_play, parent, false);
            SourceHolder mh = new SourceAdapter3.SourceHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(SourceAdapter3.SourceHolder holder, final int position) {
            //  holder.text_view_item_source_type.setText(poster.getSources().get(position).getType());
            holder.text_view_item_source_type.setText(poster.getSources().get(position).getQuality());
            switch (poster.getSources().get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
                case "youtube":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_youtube));
                    break;
                case "embed":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_embed_file));
                    break;
            }

            holder.text_view_item_source_source.setText(poster.getSources().get(position).getUrl());
            holder.image_view_item_source_type_play.setOnClickListener(v -> {
                playSource3(position);

            });

        }

        @Override
        public int getItemCount() {
            return poster.getSources().size();
        }

        public class SourceHolder extends RecyclerView.ViewHolder {
            private final CardView image_view_item_source_type_play;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public SourceHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_play = (CardView) itemView.findViewById(R.id.image_view_item_source_type_play);
            }
        }
    }

    public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadHolder> {

        @Override
        public DownloadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_download, parent, false);
            DownloadHolder mh = new DownloadHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(DownloadHolder holder, final int position) {
            // holder.text_view_item_source_type.setText(downloadableList.get(position).getType());
            holder.text_view_item_source_type.setText(downloadableList.get(position).getQuality());
            switch (downloadableList.get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
            }
            holder.text_view_item_source_source.setText(downloadableList.get(position).getUrl());

            SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("url", downloadableList.get(position).getUrl());
            editor.apply();

            holder.image_view_item_source_type_download.setOnClickListener(v -> {
                DownloadSource(downloadableList.get(position));

            });
        }

        @Override
        public int getItemCount() {
            return downloadableList.size();
        }

        public class DownloadHolder extends RecyclerView.ViewHolder {
            private final CardView image_view_item_source_type_download;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public DownloadHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_download = (CardView) itemView.findViewById(R.id.image_view_item_source_type_download);
            }
        }
    }

    public class DownloadsAdapter2 extends RecyclerView.Adapter<DownloadsAdapter2.DownloadHolder> {

        @Override
        public DownloadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_download, parent, false);
            DownloadHolder mh = new DownloadHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(DownloadHolder holder, final int position) {
            // holder.text_view_item_source_type.setText(downloadableList.get(position).getType());
            holder.text_view_item_source_type.setText(downloadableList.get(position).getQuality());
            switch (downloadableList.get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
            }
            holder.text_view_item_source_source.setText(downloadableList.get(position).getUrl());

            SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("url", downloadableList.get(position).getUrl());
            editor.apply();
            holder.image_view_item_source_type_download.setOnClickListener(v -> {
                DownloadSource2(downloadableList.get(position));
            });
        }

        @Override
        public int getItemCount() {
            return downloadableList.size();
        }

        public class DownloadHolder extends RecyclerView.ViewHolder {
            private final CardView image_view_item_source_type_download;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public DownloadHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_download = (CardView) itemView.findViewById(R.id.image_view_item_source_type_download);
            }
        }
    }
    private void loadSubtitles(int position) {
        if (subtitlesForCast.size() > 0) {
            loadRemoteMediaSource(position, true);
            return;
        }
        relative_layout_subtitles_loading.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.getSubtitlesByPoster(poster.getId());
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                relative_layout_subtitles_loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        subtitlesForCast.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            for (int l = 0; l < response.body().get(i).getSubtitles().size(); l++) {
                                Subtitle subtitletocast = response.body().get(i).getSubtitles().get(l);
                                subtitletocast.setLanguage(response.body().get(i).getLanguage());
                                subtitlesForCast.add(subtitletocast);
                            }
                        }
                    }
                }
                loadRemoteMediaSource(position, true);
            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                relative_layout_subtitles_loading.setVisibility(View.GONE);
                loadRemoteMediaSource(position, true);
            }
        });
    }

    private void checkFavorite() {
        List<Poster> favorites_list = Hawk.get("my_list");
        Boolean exist = false;
        if (favorites_list == null) {
            favorites_list = new ArrayList<>();
        }

        for (int i = 0; i < favorites_list.size(); i++) {
            if (favorites_list.get(i).getId().equals(poster.getId())) {
                exist = true;
            }
        }
        if (exist) {
            image_view_activity_movie_my_list.setImageDrawable(getResources().getDrawable(R.drawable.heart2));
        } else {
            image_view_activity_movie_my_list.setImageDrawable(getResources().getDrawable(R.drawable.heart));
        }
    }

    private void addFavotite() {


        List<Poster> favorites_list = Hawk.get("my_list");
        Boolean exist = false;
        if (favorites_list == null) {
            favorites_list = new ArrayList<>();
        }
        int fav_position = -1;
        for (int i = 0; i < favorites_list.size(); i++) {
            if (favorites_list.get(i).getId().equals(poster.getId())) {
                exist = true;
                fav_position = i;
            }
        }
        if (exist == false) {
            favorites_list.add(poster);
            Hawk.put("my_list", favorites_list);
            image_view_activity_movie_my_list.setImageDrawable(getResources().getDrawable(R.drawable.heart2));
            Toasty.info(this, "این فیلم به لیست علاقه مندی اضافه شد", Toast.LENGTH_SHORT).show();
        } else {
            favorites_list.remove(fav_position);
            Hawk.put("my_list", favorites_list);
            image_view_activity_movie_my_list.setImageDrawable(getResources().getDrawable(R.drawable.heart));
            Toasty.warning(this, "این فیلم از لیست علاقه مندی حذف شد!", Toast.LENGTH_SHORT).show();
        }
    }

    public void share() {
        String shareBody = poster.getTitle() + "\n\n" + getResources().getString(R.string.get_this_movie_here) + "\n" + Config.API_URL.replace("api", "share") + poster.getId() + ".html";
        //String shareBody = poster.getTitle()+"\n\n"+getResources().getString(R.string.get_this_movie_here)+"\n"+ "https://admin.newhindivideosongs.in/share/"+ poster.getId()+".html";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        addShare();
    }

    public void addShare() {
        final PrefManager prefManager = new PrefManager(this);
        if (!prefManager.getString(poster.getId() + "_share").equals("true")) {
            prefManager.setString(poster.getId() + "_share", "true");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<Integer> call = service.addPosterShare(poster.getId());
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }
    }

    public void addView() {
        final PrefManager prefManager = new PrefManager(this);
        if (!prefManager.getString(poster.getId() + "_view").equals("true")) {
            prefManager.setString(poster.getId() + "_view", "true");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<Integer> call = service.addMovieView(poster.getId());
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void DownloadSource(Source source) {
        switch (source.getType()) {
            case "mov": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            case "webm": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "mkv": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "mp4": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "m3u8":
                if (!isMyServiceRunning(DownloadService.class)) {


                    Intent intent = new Intent(MovieActivity.this, DownloadService.class);
                    intent.putExtra("type", "com");
                    intent.putExtra("url", source.getUrl());
                    intent.putExtra("title", poster.getTitle());
                    intent.putExtra("image", poster.getImage());
                    intent.putExtra("id", source.getId());
                    intent.putExtra("element", poster.getId());

                    if (poster.getDuration() != null)
                        intent.putExtra("duration", poster.getDuration());
                    else
                        intent.putExtra("duration", "");

                    Toasty.info(this, "دانلود آغاز شد ...", Toast.LENGTH_LONG).show();
                    expandPanel(this);
                    startService(intent);
                } else {
                    Toasty.warning(MovieActivity.this, "باید دانلود قبلی کامل شود و سپس یک دانلود جدید را شروع نمایید!", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    public void DownloadSource2(Source source) {
        switch (source.getType()) {
            case "mov": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                } else {

                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                }
            }
            case "webm": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                } else {

                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                }
            }
            break;
            case "mkv": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                } else {

                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                }
            }
            break;
            case "mp4": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                } else {
                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);
                }
            }
            break;
            case "m3u8":
                if (!isMyServiceRunning(DownloadService.class)) {
                    Intent intent2 = new Intent("android.intent.action.MAIN");
                    intent2.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                    intent2.putExtra("com.dv.get.ACTION_LIST_ADD", source.getUrl());
                    intent2.putExtra("com.dv.get.ACTION_LIST_OPEN", true);
                    startActivity(intent2);

                } else {
                    Toasty.warning(MovieActivity.this, "باید دانلود قبلی کامل شود و سپس یک دانلود جدید را شروع نمایید!", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private static void expandPanel(Context _context) {
        try {
            @SuppressLint("WrongConstant") Object sbservice = _context.getSystemService("statusbar");
            Class<?> statusbarManager;
            statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }
            showsb.invoke(sbservice);
        } catch (ClassNotFoundException _e) {
            _e.printStackTrace();
        } catch (NoSuchMethodException _e) {
            _e.printStackTrace();
        } catch (IllegalArgumentException _e) {
            _e.printStackTrace();
        } catch (IllegalAccessException _e) {
            _e.printStackTrace();
        } catch (InvocationTargetException _e) {
            _e.printStackTrace();
        }
    }

    public void showDialog(Boolean withAds) {
        Dialog dialog = new Dialog(this, R.style.dialogman);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final PrefManager prf = new PrefManager(getApplicationContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscribe);
        RelativeLayout relative_layout_watch_ads = (RelativeLayout) dialog.findViewById(R.id.relative_layout_watch_ads);
        TextView text_view_watch_ads = (TextView) dialog.findViewById(R.id.text_view_watch_ads);
        if (withAds) {
            relative_layout_watch_ads.setVisibility(View.VISIBLE);
        } else {
            relative_layout_watch_ads.setVisibility(View.GONE);
        }
        relative_layout_watch_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                } else {
                    autoDisplay = true;
                    loadRewardedVideoAd();
                    text_view_watch_ads.setText("در حال بارگذاری ..");
                }
            }
        });

        TextView text_view_go_pro = (TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setText("تهیه اشتراک به مدت " + prf.getString("SUBSCRIBED_DAYS") + " روز ");
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Config.ADM_SUPPORT == 1){
                        downloaddialog();
                    }else {
                        if (checkSUBSCRIBED()) {
                            showSourcesDownloadDialog();
                        } else {
                            if (poster.getDownloadas().equals("2")) {
                                if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                    //showDialog(false);
                                    Intent intent = new Intent(MovieActivity.this, PackageBuyActivity.class);
                                    intent.putExtra("type_form", "download");
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                } else {
                                    Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }
                            } else if (poster.getDownloadas().equals("3")) {
                                if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                    // showDialog(true);
                                    if (mRewardedVideoAd.isLoaded()) {
                                        mRewardedVideoAd.show();
                                    } else {
                                        autoDisplay = true;
                                        loadRewardedVideoAd();
                                    }
                                } else {
                                    Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }
                                operationAfterAds = 100;
                            } else {
                                showSourcesDownloadDialog();
                            }
                        }
                    }
                }
                return;
            }
        }
    }

    public void showAdsBanner() {
        if (getString(R.string.AD_MOB_ENABLED_BANNER).equals("true")) {
            if (!checkSUBSCRIBED()) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("FACEBOOK")) {
                    showFbBanner();
                }
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("ADMOB")) {
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

    public void showAdmobBanner() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads = (LinearLayout) findViewById(R.id.linear_layout_ads);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void Download(Source source) {
        com.movieboxtv.app.Utils.Log.log("Android P<=");

        File fileName = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.download_foler) + "/", poster.getTitle().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "") + "_" + source.getId() + "." + source.getType());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source.getUrl()))
                .setTitle(poster.getTitle())// Title of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("در حال دانلود")// Description of the Download Notification
                .setVisibleInDownloadsUi(true)

                .setDestinationUri(Uri.fromFile(fileName));// Uri of the destination file
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        if (!isMyServiceRunning(SerieActivity.class)) {
            startService(new Intent(MovieActivity.this, ToastService.class));
        }
        Toasty.info(this, "دانلود آغاز شد ...", Toast.LENGTH_LONG).show();
        expandPanel(this);


        DownloadItem downloadItem = new DownloadItem(source.getId(), poster.getTitle(), "com", Uri.fromFile(fileName).getPath(), poster.getImage(), "", "", poster.getId(), downloadId);
        if (poster.getDuration() != null)
            downloadItem.setDuration(poster.getDuration());
        else
            downloadItem.setDuration("");

        List<DownloadItem> my_downloads_temp = Hawk.get("my_downloads_temp");
        if (my_downloads_temp == null) {
            my_downloads_temp = new ArrayList<>();
        }
        for (int i = 0; i < my_downloads_temp.size(); i++) {
            if (my_downloads_temp.get(i).getId().equals(source.getId())) {
                my_downloads_temp.remove(my_downloads_temp.get(i));
                Hawk.put("my_downloads_temp", my_downloads_temp);
            }
        }
        my_downloads_temp.add(downloadItem);
        Hawk.put("my_downloads_temp", my_downloads_temp);
    }

    public void DownloadQ(Source source) {
        com.movieboxtv.app.Utils.Log.log("Android Q");
        File fileName = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/", poster.getTitle().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "") + "_" + source.getId() + "." + source.getType());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source.getUrl()))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(poster.getTitle())// Title of the Download Notification
                .setDescription("در حال دانلود")// Description of the Download Notification
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, poster.getTitle().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "") + "_" + source.getId() + "." + source.getType());// Uri of the destination file
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        if (!isMyServiceRunning(SerieActivity.class)) {
            startService(new Intent(MovieActivity.this, ToastService.class));
        }
        Toasty.info(this, "دانلود آغاز شد ...", Toast.LENGTH_LONG).show();
        expandPanel(this);


        DownloadItem downloadItem = new DownloadItem(source.getId(), poster.getTitle(), "com", Uri.fromFile(fileName).getPath(), poster.getImage(), "", "", poster.getId(), downloadId);
        if (poster.getDuration() != null)
            downloadItem.setDuration(poster.getDuration());
        else
            downloadItem.setDuration("");

        List<DownloadItem> my_downloads_temp = Hawk.get("my_downloads_temp");
        if (my_downloads_temp == null) {
            my_downloads_temp = new ArrayList<>();
        }
        for (int i = 0; i < my_downloads_temp.size(); i++) {
            if (my_downloads_temp.get(i).getId().equals(source.getId())) {
                my_downloads_temp.remove(my_downloads_temp.get(i));
                Hawk.put("my_downloads_temp", my_downloads_temp);
            }
        }
        my_downloads_temp.add(downloadItem);
        Hawk.put("my_downloads_temp", my_downloads_temp);
    }
}

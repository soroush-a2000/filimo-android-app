package com.movieboxtv.app.ui.player;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.Language;
import com.movieboxtv.app.entity.Subtitle;
import com.movieboxtv.app.event.CastSessionEndedEvent;
import com.movieboxtv.app.event.CastSessionStartedEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import top.defaults.colorpicker.ColorPickerPopup;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.databinding.FragmentPlayerBinding;


import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class CustomPlayerFragment extends Fragment {

    private static String videoKind;
    private CustomPlayerViewModel mCustomPlayerViewModel;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private ImageView ic_media_stop;
    private RelativeLayout payer_pause_play;
    private View view;
    private Boolean done =  false;
    private Boolean isLive =  false;
    private TextView text_view_exo_player_live;
    private TextView exo_duration;
    private TextView exo_live;
    private ImageView image_view_exo_player_rotation;

    // new
    private AudioManager mAudioManager;
    private SeekBar  brightnessSeekbar;
    private int brightness;
    private ContentResolver cResolver;
    //Window object, that will store a reference to the current window
    private Window window;

    private Boolean isLandscape =  true;
    private ImageView image_view_exo_player_subtitles;
    private RelativeLayout relative_layout_subtitles_dialog;
    private ImageView image_view_dialog_source_close;

    // lists
    private ArrayList<Subtitle> subtitlesForCast= new ArrayList<>();
    private ArrayList<Subtitle> subtitles= new ArrayList<>();
    private ArrayList<Language> languages= new ArrayList<>();
    private RecyclerView recycler_view_comment_dialog_languages;
    private ProgressBar progress_bar_comment_dialog_subtitles;
    private LanguageAdapter languageAdapter;
    private LinearLayoutManager linearLayoutManagerLanguages;
    private LinearLayoutManager linearLayoutManagerSubtitles;
    private SubtitleAdapter subtitleAdapter;
    private RecyclerView recycler_view_comment_dialog_subtitles;
    private SwitchCompat switch_button_dialog_subtitle;
    private TextView text_view_dialog_subtitles_on_off;
    private RelativeLayout relative_layout_dialog_source_text_color_picker;
    private RelativeLayout relative_layout_dialog_source_background_color_picker;
    private PrefManager pref;
    private ImageView image_view_dialog_source_plus;
    private ImageView image_view_dialog_source_less;
    private TextView text_view_dialog_source_size_text;
    private Integer selectedLanguage = -1;
    private Integer SetedSelectedLanguage = -1;
    private static Integer videoId;
    private TextView text_view_exo_player_loading_subtitles;
    private ImageView image_view_exo_player_replay_10;
    private ImageView image_view_exo_player_forward_10;
    private ImageView image_view_exo_player_back;
    int maxBrightness;

    @Override
    public void onResume() {
        super.onResume();
        mCustomPlayerViewModel.play();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCustomPlayerViewModel.pause();
    }
    public static CustomPlayerFragment newInstance(String videoUrl, Boolean isLive, String videoType, String videoTitle, String videoSubTile, String videoImage, Integer videoId_, String _videoKind) {
        CustomPlayerFragment customPlayerFragment = new CustomPlayerFragment();
        Bundle args = new Bundle();
        args.putString("videoUrl", videoUrl);
        args.putString("videoType", videoType);
        args.putString("videoTitle", videoTitle);
        args.putString("videoSubTile", videoSubTile);
        args.putString("videoImage", videoImage);
        args.putBoolean("isLive", isLive);
        args.putString("videoKind", _videoKind);
        videoId = videoId_;
        videoKind = _videoKind;
        customPlayerFragment.setArguments(args);
        return customPlayerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPlayerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        view = (View) binding.getRoot();
        this.pref =new  PrefManager(getActivity());

        selectedLanguage = pref.getInt("subtitle_default_language");
        initView(binding);
        initAction();
        loadSubtitles();

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        cResolver = getActivity().getContentResolver();
        window = getActivity().getWindow();


        BoxedVertical volume1 = view.findViewById(R.id.volume);
        BoxedVertical brightness = view.findViewById(R.id.brightness);
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        if (mAudioManager != null) {
            int maximom = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume1.setMax(maximom*10);
            int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volume1.setValue(currentVolumn*10);
        }


        volume1.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                int s = Math.round(value/10);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, s, 0);
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {
            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
            }
        });

        maxBrightness = getMaxBrightness(getContext(), 1000);
        brightness.setMax(maxBrightness);
        brightness.setValue(getBrightness(getContext()));
        setBrightness(getContext(), getBrightness(getContext()));
        brightness.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                setBrightness(getContext(), boxedPoints.getValue());
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {

            }
        });

        return view;
    }
    int getMaxBrightness(Context context, int defaultValue){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            Field[] fields = powerManager.getClass().getDeclaredFields();
            for (Field field: fields) {
                if(field.getName().equals("BRIGHTNESS_ON")) {
                    field.setAccessible(true);
                    try {
                        return (int) field.get(powerManager);
                    } catch (IllegalAccessException e) {
                        return defaultValue;
                    }
                }
            }
        }
        return defaultValue;
    }

    public static int getBrightness(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(cResolver,  Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }

    public void setBrightness(Context context, int brightness){
        //ContentResolver cResolver = context.getContentResolver();
        //Settings.System.putInt(cResolver,  Settings.System.SCREEN_BRIGHTNESS,brightness);

        WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
        layout.screenBrightness = (float) brightness/maxBrightness;
        getActivity().getWindow().setAttributes(layout);

    }

    public void setSubtitleView(){
        int textColor =Color.WHITE;
        int backrgoundColor =Color.BLACK;
        int textSiz = 10;
        if (pref.getInt("subtitle_text_color") != 0){
            textColor = pref.getInt("subtitle_text_color");
        }
        if (pref.getInt("subtitle_background_color") != 0){
            backrgoundColor = pref.getInt("subtitle_background_color");
        }
        if (pref.getInt("subtitle_text_size") != 0){
            textSiz = pref.getInt("subtitle_text_size");
        }else{
            pref.setInt("subtitle_text_size",textSiz);
        }
        if (pref.getString("subtitle_enabled").equals("TRUE")) {
            switch_button_dialog_subtitle.setChecked(true);
            text_view_dialog_subtitles_on_off.setText(getActivity().getResources().getString(R.string.on));
        }else{
            switch_button_dialog_subtitle.setChecked(false);
            text_view_dialog_subtitles_on_off.setText(getActivity().getResources().getString(R.string.off));
        }
        mSimpleExoPlayerView.getSubtitleView().setApplyEmbeddedStyles(false);
        mSimpleExoPlayerView.getSubtitleView().setApplyEmbeddedFontSizes(false);
        mSimpleExoPlayerView.getSubtitleView().setStyle(new CaptionStyleCompat(textColor,Color.TRANSPARENT,backrgoundColor, CaptionStyleCompat.EDGE_TYPE_OUTLINE,Color.BLACK,null));
        mSimpleExoPlayerView.getSubtitleView().setFixedTextSize(TypedValue.COMPLEX_UNIT_PT, textSiz);
        relative_layout_dialog_source_background_color_picker.setBackgroundColor(backrgoundColor);
        relative_layout_dialog_source_text_color_picker.setBackgroundColor(textColor);
        text_view_dialog_source_size_text.setText("اندازه متن "+textSiz+" pt");
    }
    private void initView(FragmentPlayerBinding binding) {
        mCustomPlayerViewModel = new CustomPlayerViewModel(getActivity());

        image_view_exo_player_back = view.findViewById(R.id.image_view_exo_player_back);
        text_view_exo_player_loading_subtitles = view.findViewById(R.id.text_view_exo_player_loading_subtitles);
        image_view_exo_player_replay_10 = view.findViewById(R.id.image_view_exo_player_replay_10);
        image_view_exo_player_forward_10 = view.findViewById(R.id.image_view_exo_player_forward_10);
        payer_pause_play = view.findViewById(R.id.payer_pause_play);
        relative_layout_subtitles_dialog = view.findViewById(R.id.relative_layout_subtitles_dialog);
        text_view_exo_player_live = view.findViewById(R.id.text_view_exo_player_live);
        image_view_exo_player_rotation = view.findViewById(R.id.image_view_exo_player_rotation);
        image_view_exo_player_subtitles = view.findViewById(R.id.image_view_exo_player_subtitles);
        image_view_dialog_source_plus = view.findViewById(R.id.image_view_dialog_source_plus);
        image_view_dialog_source_less = view.findViewById(R.id.image_view_dialog_source_less);
        text_view_dialog_source_size_text = view.findViewById(R.id.text_view_dialog_source_size_text);
        image_view_dialog_source_close = view.findViewById(R.id.image_view_dialog_source_close);
        switch_button_dialog_subtitle = view.findViewById(R.id.switch_button_dialog_subtitle);
        text_view_dialog_subtitles_on_off = view.findViewById(R.id.text_view_dialog_subtitles_on_off);
        relative_layout_dialog_source_text_color_picker = view.findViewById(R.id.relative_layout_dialog_source_text_color_picker);
        relative_layout_dialog_source_background_color_picker = view.findViewById(R.id.relative_layout_dialog_source_background_color_picker);
        exo_duration = view.findViewById(com.google.android.exoplayer2.ui.R.id.exo_duration);
        exo_live = view.findViewById(R.id.exo_live);

        brightnessSeekbar = view.findViewById(R.id.brightnessSeekbar);

        isLive  =  getUrlExtra().getBoolean("isLive");
        mCustomPlayerViewModel.setPayerPausePlay(payer_pause_play);
        binding.setPlayerVm(mCustomPlayerViewModel);
        mSimpleExoPlayerView = binding.videoView;
        setSubtitleView();
        mSimpleExoPlayerView.setShutterBackgroundColor(Color.TRANSPARENT);
        if (isLive) {
            text_view_exo_player_live.setVisibility(View.VISIBLE);
            exo_duration.setVisibility(View.GONE);
            exo_live.setVisibility(View.VISIBLE);
        }else{
            text_view_exo_player_live.setVisibility(View.GONE);
            exo_duration.setVisibility(View.VISIBLE);
            exo_live.setVisibility(View.GONE);
        }
        recycler_view_comment_dialog_languages =  view.findViewById(R.id.recycler_view_comment_dialog_languages);
        recycler_view_comment_dialog_subtitles =  view.findViewById(R.id.recycler_view_comment_dialog_subtitles);
        progress_bar_comment_dialog_subtitles =  view.findViewById(R.id.progress_bar_comment_dialog_subtitles);

        languageAdapter =new LanguageAdapter();
        recycler_view_comment_dialog_languages.setHasFixedSize(true);
        recycler_view_comment_dialog_languages.setAdapter(languageAdapter);
        linearLayoutManagerLanguages =  new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recycler_view_comment_dialog_languages.setLayoutManager(linearLayoutManagerLanguages);

        subtitleAdapter =new SubtitleAdapter();
        recycler_view_comment_dialog_subtitles.setHasFixedSize(true);
        recycler_view_comment_dialog_subtitles.setAdapter(subtitleAdapter);
        linearLayoutManagerSubtitles =  new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        recycler_view_comment_dialog_subtitles.setLayoutManager(linearLayoutManagerSubtitles);


        //view.findViewById(R.id.view1).setOnTouchListener(new PlayerTouchListener(1) {
        //    @Override
        //    public void Top() {
        //        super.Top();
        //        mSimpleExoPlayerView.showController();
        //        mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        //        int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //        volumnSeekbar.setProgress(currentVolumn);
        //        volumnControlLayout.setVisibility(VISIBLE);
        //    }
//
        //    @Override
        //    public void Down() {
        //        super.Down();
        //        mSimpleExoPlayerView.showController();
        //        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        //        volumnControlLayout.setVisibility(VISIBLE);
        //        int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //        volumnSeekbar.setProgress(currentVolumn);
        //    }
//
        //    @Override
        //    public void DoubleTap() {
        //        super.DoubleTap();
        //        if (mCustomPlayerViewModel.mExoPlayer.getCurrentPosition()<10000) {
        //            mCustomPlayerViewModel.mExoPlayer.seekTo(0);
        //        }else{
        //            mCustomPlayerViewModel.mExoPlayer.seekTo(mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() - 10000);
        //        }
        //    }
//
        //    @Override
        //    public void OneTap() {
        //            mSimpleExoPlayerView.hideController();
        //    }
        //});
//
        //view.findViewById(R.id.view2).setOnTouchListener(new PlayerTouchListener(10) {
        //    @Override
        //    public void Top() {
        //        super.Top();
        //        mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        //        int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //        volumnSeekbar.setProgress(currentVolumn);
        //        volumnControlLayout.setVisibility(VISIBLE);
        //    }
//
        //    @Override
        //    public void Down() {
        //        super.Down();
        //        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        //        volumnControlLayout.setVisibility(VISIBLE);
        //        int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //        volumnSeekbar.setProgress(currentVolumn);
        //    }
//
        //    @Override
        //    public void DoubleTap() {
        //        super.DoubleTap();
        //        if ((mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() + 10000 ) > mCustomPlayerViewModel.mExoPlayer.getDuration() ) {
        //            mCustomPlayerViewModel.mExoPlayer.seekTo(mCustomPlayerViewModel.mExoPlayer.getDuration());
        //        }else{
        //            mCustomPlayerViewModel.mExoPlayer.seekTo(mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() + 10000);
        //        }
        //    }
//
        //    @Override
        //    public void OneTap() {
        //        if (mSimpleExoPlayerView.isControllerVisible())
        //            mSimpleExoPlayerView.hideController();
        //        else
        //            mSimpleExoPlayerView.showController();
        //    }
        //});
    }




    private void loadSubtitles() {
        if (videoKind==null)
            return;
        text_view_exo_player_loading_subtitles.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call;

        if (videoKind.equals("episode")){
            call = service.getSubtitlesByEpisode(videoId);
        }else{
            call = service.getSubtitlesByPoster(videoId);
        }
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                text_view_exo_player_loading_subtitles.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (response.body().size()>0) {
                        image_view_exo_player_subtitles.setVisibility(View.VISIBLE);
                        languages.clear();
                        for (int i = 0; i < response.body().size(); i++){
                            languages.add(response.body().get(i));
                            for (int l = 0; l < languages.get(i).getSubtitles().size(); l++) {
                                Subtitle subtitletocast =  languages.get(i).getSubtitles().get(l);
                                subtitletocast.setLanguage(languages.get(i).getLanguage());
                                subtitlesForCast.add(subtitletocast);
                            }
                            if (selectedLanguage == -1){
                                if (i == 0){
                                    languages.get(i).setSelected(true);
                                    subtitles.clear();
                                    for (int j = 0; j <  languages.get(i).getSubtitles().size(); j++) {
                                        subtitles.add(languages.get(i).getSubtitles().get(j));
                                    }
                                    subtitleAdapter.notifyDataSetChanged();
                                }else{
                                    languages.get(i).setSelected(false);
                                }
                            }else{
                                if (languages.get(i).getId() == selectedLanguage ){
                                    languages.get(i).setSelected(true);
                                    subtitles.clear();
                                    for (int j = 0; j <  languages.get(i).getSubtitles().size(); j++) {
                                        subtitles.add(languages.get(i).getSubtitles().get(j));
                                    }
                                    subtitleAdapter.notifyDataSetChanged();
                                }else{
                                    languages.get(i).setSelected(false);
                                }
                            }
                        }
                        languageAdapter.notifyDataSetChanged();
                        SelectCurrentSubtitle();
                        recycler_view_comment_dialog_languages.setVisibility(View.VISIBLE);
                        progress_bar_comment_dialog_subtitles.setVisibility(View.GONE);
                    }else{
                        recycler_view_comment_dialog_languages.setVisibility(View.GONE);
                        progress_bar_comment_dialog_subtitles.setVisibility(View.GONE);
                    }
                    mCustomPlayerViewModel.setSubtitilesList(subtitlesForCast);
                }else{
                    recycler_view_comment_dialog_languages.setVisibility(View.GONE);
                    progress_bar_comment_dialog_subtitles.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                text_view_exo_player_loading_subtitles.setVisibility(View.GONE);
                recycler_view_comment_dialog_languages.setVisibility(View.GONE);
                progress_bar_comment_dialog_subtitles.setVisibility(View.GONE);
            }
        });
    }
    @SuppressLint("SourceLockedOrientationActivity")
    private void initAction(){
        this.image_view_exo_player_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        this.image_view_dialog_source_plus.setOnClickListener(v->{
            if (pref.getInt("subtitle_text_size")<48) {
                pref.setInt("subtitle_text_size", pref.getInt("subtitle_text_size") + 1);
                setSubtitleView();
            }
        });
        this.image_view_exo_player_forward_10.setOnClickListener(v -> {
            if ((mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() + 10000 ) > mCustomPlayerViewModel.mExoPlayer.getDuration() ) {
                mCustomPlayerViewModel.mExoPlayer.seekTo(mCustomPlayerViewModel.mExoPlayer.getDuration());
            }else{
                mCustomPlayerViewModel.mExoPlayer.seekTo(mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() + 10000);
            }
        });
        this.image_view_exo_player_replay_10.setOnClickListener(v -> {
            if (mCustomPlayerViewModel.mExoPlayer.getCurrentPosition()<10000) {
                mCustomPlayerViewModel.mExoPlayer.seekTo(0);
            }else{
                mCustomPlayerViewModel.mExoPlayer.seekTo(mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() - 10000);
            }
        });
        this.image_view_dialog_source_less.setOnClickListener(v->{
            if (pref.getInt("subtitle_text_size")>4) {
                pref.setInt("subtitle_text_size", pref.getInt("subtitle_text_size") - 1);
                setSubtitleView();
            }
        });
        this.relative_layout_dialog_source_text_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(getActivity().getApplicationContext())
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("انتخاب")
                        .cancelTitle("لغو")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                v.setBackgroundColor(color);
                                pref.setInt("subtitle_text_color",color);
                                setSubtitleView();
                            }
                        });


            }
        });
        this.relative_layout_dialog_source_background_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(getActivity().getApplicationContext())
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("انتخاب")
                        .cancelTitle("لغو")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                v.setBackgroundColor(color);
                                pref.setInt("subtitle_background_color",color);
                                setSubtitleView();
                            }
                        });
            }
        });
        this.switch_button_dialog_subtitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                pref.setString("subtitle_enabled","TRUE");
                text_view_dialog_subtitles_on_off.setText(getActivity().getResources().getString(R.string.on));
                getCurrentSubtitle();
            }else{
                pref.setString("subtitle_enabled","FALSE");
                text_view_dialog_subtitles_on_off.setText(getActivity().getResources().getString(R.string.off));
                mCustomPlayerViewModel.preparePlayer(null,mCustomPlayerViewModel.mExoPlayer.getCurrentPosition()-1000);
            }
            relative_layout_subtitles_dialog.setVisibility(View.GONE);
        });
        this.image_view_exo_player_rotation.setOnClickListener(v->{
            if (isLandscape){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                isLandscape = false;
            }else{
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isLandscape = true;
            }
        });
        this.image_view_exo_player_subtitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomPlayerViewModel.pause();
                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);
            }
        });
        this.image_view_exo_player_subtitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomPlayerViewModel.pause();
                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                //mCustomPlayerViewModel.preparePlayer("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/tracks/GoogleIO-2014-CastingToTheFuture2-en.vtt");
            }
        });
        this.image_view_dialog_source_close.setOnClickListener(v->{
            mCustomPlayerViewModel.play();
            if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                relative_layout_subtitles_dialog.setVisibility(View.GONE);
            else
                relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);
        });
    }

    private void getCurrentSubtitle() {
        if (subtitles.size()>0){
            Subtitle currentSubtitle = subtitles.get(0);
            subtitles.get(0).setSelected(true);
            for (int i = 0; i < subtitles.size(); i++) {
                if(subtitles.get(i).getSelected()!=null) {
                    if (subtitles.get(i).getSelected() == true) {
                        currentSubtitle = subtitles.get(i);
                    }
                }
            }
            subtitleAdapter.notifyDataSetChanged();
            mCustomPlayerViewModel.preparePlayer(currentSubtitle,mCustomPlayerViewModel.mExoPlayer.getCurrentPosition()-1000);
        }else{
            mCustomPlayerViewModel.preparePlayer(null,mCustomPlayerViewModel.mExoPlayer.getCurrentPosition()-1000);
        }
    }
    private void SelectCurrentSubtitle() {
        if (pref.getString("subtitle_enabled").equals("TRUE")) {
            if (subtitles.size() > 0) {
                Subtitle currentSubtitle = subtitles.get(0);
                subtitles.get(0).setSelected(true);
                for (int i = 0; i < subtitles.size(); i++) {
                    if (subtitles.get(i).getSelected() != null) {
                        if (subtitles.get(i).getSelected() == true) {
                            currentSubtitle = subtitles.get(i);
                        }
                    }
                }
                subtitleAdapter.notifyDataSetChanged();
                mCustomPlayerViewModel.preparePlayer(currentSubtitle, mCustomPlayerViewModel.mExoPlayer.getCurrentPosition() - 1000);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCustomPlayerViewModel.onStart(mSimpleExoPlayerView, getUrlExtra());
        if (!done)
            setUpMediaRouteButton();
    }
    private void setUpMediaRouteButton() {
        androidx.mediarouter.app.MediaRouteButton mediaRouteButton = view.findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getActivity().getApplicationContext(),mediaRouteButton);
        done =  true;
    }

    public void setFull(){
        mCustomPlayerViewModel.setMediaFull();
    }
    public void setNormal(){
        mCustomPlayerViewModel.setMediaNormal();

    }

    public static SimpleExoPlayer player;

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }



        brightness =
                Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 0);
        brightnessSeekbar.setProgress(brightness);
        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(checkSystemWritePermission()) {
                    int screenBrightnessValue = progress*225/100;
                    Settings.System.putInt(getActivity().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onCastSessionStartedEvent(CastSessionStartedEvent event) {
        Log.v("V","onCastSessionStartedEvent");
        ExoPlayer mExoPlayer = mCustomPlayerViewModel.getExoPlayer();
        if (mExoPlayer != null) {

            long currentPosition = mExoPlayer.getCurrentPosition();
            if (currentPosition > 0) {
                int position = (int) currentPosition;
                mCustomPlayerViewModel.loadMedia(position, true);
            }
        }
    }

    @Subscribe
    public void onCastSessionEndedEvent(CastSessionEndedEvent event) {
        ExoPlayer mExoPlayer = mCustomPlayerViewModel.getExoPlayer();
        SimpleExoPlayerView mSimpleExoPlayerView = mCustomPlayerViewModel.getSimpleExoPlayerView();
        if (mExoPlayer != null) {
            long time = mExoPlayer.getDuration() - event.getSessionRemainingTime();
            if (time > 0) {
                mCustomPlayerViewModel.setIsInProgress(true);
                mExoPlayer.seekTo(time);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
        if (mSimpleExoPlayerView != null) {
            if (!mSimpleExoPlayerView.getUseController()) {
                mSimpleExoPlayerView.setUseController(true);
            }
        }
    }


    @Nullable
    private Bundle getUrlExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle;
        }
        return null;
    }
    public class LanguageAdapter extends  RecyclerView.Adapter<LanguageAdapter.LanguageHolder>{

        @Override
        public LanguageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language ,parent, false);
            LanguageHolder mh = new LanguageHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(LanguageHolder holder, final int position) {
            holder.text_view_item_language.setText(languages.get(position).getLanguage());
            Picasso.with(getActivity()).load(languages.get(position).getImage()).into(holder.image_view_item_language);
            if (languages.get(position).getSelected()==null){
                languages.get(position).setSelected(false);
            }
            /*if ( selectedLanguage == languages.get(position).getId()){
                for (int i = 0; i <languages.size(); i++) {
                    languages.get(i).setSelected(false);
                }
                languages.get(position).setSelected(true);
                subtitles.clear();
                for (int i = 0; i <  languages.get(position).getSubtitles().size(); i++) {
                    subtitles.add(languages.get(position).getSubtitles().get(i));
                }
               // subtitleAdapter.notifyDataSetChanged();
                //SelectCurrentSubtitle();
            }*/
            if (languages.get(position).getSelected()){
                holder.linear_layout_item_language.setBackgroundColor(Color.parseColor("#101e33"));
                holder.relative_layout_item_language_indector.setVisibility(View.VISIBLE);
            }else{
                holder.linear_layout_item_language.setBackgroundColor(Color.parseColor("#081528"));
                holder.relative_layout_item_language_indector.setVisibility(View.GONE);
            }
            holder.linear_layout_item_language.setOnClickListener(v->{
                for (int i = 0; i <languages.size(); i++) {
                    languages.get(i).setSelected(false);
                }
                languages.get(position).setSelected(true);
                notifyDataSetChanged();
                subtitles.clear();
                for (int i = 0; i <  languages.get(position).getSubtitles().size(); i++) {
                    subtitles.add(languages.get(position).getSubtitles().get(i));
                }
                selectedLanguage =languages.get(position).getId();
                subtitleAdapter.notifyDataSetChanged();
            });
        }
        @Override
        public int getItemCount() {
            return languages.size();
        }
        public class LanguageHolder extends RecyclerView.ViewHolder {
            private final ImageView image_view_item_language;
            private final TextView text_view_item_language;
            private final LinearLayout linear_layout_item_language;
            private final RelativeLayout relative_layout_item_language_indector;

            public LanguageHolder(View itemView) {
                super(itemView);
                this.text_view_item_language =  (TextView) itemView.findViewById(R.id.text_view_item_language);
                this.image_view_item_language =  (ImageView) itemView.findViewById(R.id.image_view_item_language);
                this.linear_layout_item_language =  (LinearLayout) itemView.findViewById(R.id.linear_layout_item_language);
                this.relative_layout_item_language_indector =  (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_language_indector);
            }
        }
    }
    public class SubtitleAdapter extends  RecyclerView.Adapter<SubtitleAdapter.SubtitleHolder>{

        @Override
        public SubtitleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtitle ,parent, false);
            SubtitleHolder mh = new SubtitleHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(SubtitleHolder holder, final int position) {
            holder.text_view_item_subtitle.setText("زیرنویس ("+(position+1)+")");
            if (subtitles.get(position).getSelected()==null){
                subtitles.get(position).setSelected(false);
            }
            if (subtitles.get(position).getSelected()){
                holder.text_view_item_subtitle.setTextColor(Color.parseColor("#081528"));
                holder.text_view_item_subtitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }else{
                holder.text_view_item_subtitle.setTextColor(Color.parseColor("#FFFFFF"));
                holder.text_view_item_subtitle.setBackgroundColor(Color.parseColor("#081528"));
            }
            holder.linear_layout_item_subtitle.setOnClickListener(v -> {
                for (int i = 0; i <subtitles.size(); i++) {
                    subtitles.get(i).setSelected(false);
                }
                mCustomPlayerViewModel.preparePlayer(subtitles.get(position),mCustomPlayerViewModel.mExoPlayer.getCurrentPosition()-1000);
                relative_layout_subtitles_dialog.setVisibility(View.GONE);
                subtitles.get(position).setSelected(true);
                pref.setInt("subtitle_default_language",selectedLanguage);
                notifyDataSetChanged();
            });
        }
        @Override
        public int getItemCount() {
            return subtitles.size();
        }
        public class SubtitleHolder extends RecyclerView.ViewHolder {
            private final TextView text_view_item_subtitle;
            private final LinearLayout linear_layout_item_subtitle;

            public SubtitleHolder(View itemView) {
                super(itemView);
                this.text_view_item_subtitle =  (TextView) itemView.findViewById(R.id.text_view_item_subtitle);
                this.linear_layout_item_subtitle =  (LinearLayout) itemView.findViewById(R.id.linear_layout_item_subtitle);
            }
        }
    }
    private boolean checkSystemWritePermission() {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(getActivity());
        } else {
            permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (!permission) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, 45638);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_SETTINGS}, 45638);
            }
        }
        return permission;
    }
}

package com.movieboxtv.app.ui.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import com.movieboxtv.app.config.Config;

import com.movieboxtv.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class YoutubeActivity extends YouTubeBaseActivity {

    private YouTubePlayerView video_youtube_player;
    private YouTubePlayer.OnInitializedListener onInitializedListinner;
    private  String youtubeUrl;
    private YouTubePlayer youTubePlayer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        Bundle bundle = getIntent().getExtras() ;
        youtubeUrl = bundle.getString("url");

        this.video_youtube_player =(YouTubePlayerView) findViewById(R.id.video_youtube_player);
        this.onInitializedListinner =  new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer_, boolean b) {
                youTubePlayer= youTubePlayer_;
                if (savedInstanceState!=null){
                    youTubePlayer.loadVideo(new YouTubeHelper().extractVideoIdFromUrl(youtubeUrl),savedInstanceState.getInt("current"));
                }else{
                    youTubePlayer.setFullscreen(true);
                    youTubePlayer.loadVideo(new YouTubeHelper().extractVideoIdFromUrl(youtubeUrl));
                }

                youTubePlayer.setOnFullscreenListener(v -> {
                    if (v){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }else{
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        video_youtube_player.initialize(Config.Youtube_Key,onInitializedListinner);
    }
    public class YouTubeHelper {

        final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

        public String extractVideoIdFromUrl(String url) {
            String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

            for(String regex : videoIdRegex) {
                Pattern compiledPattern = Pattern.compile(regex);
                Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

                if(matcher.find()){
                    return matcher.group(1);
                }
            }

            return null;
        }

        private String youTubeLinkWithoutProtocolAndDomain(String url) {
            Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
            Matcher matcher = compiledPattern.matcher(url);

            if(matcher.find()){
                return url.replace(matcher.group(), "");
            }
            return url;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        outState.putInt("current",youTubePlayer.getCurrentTimeMillis());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }
}

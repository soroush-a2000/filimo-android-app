package com.movieboxtv.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;

import com.movieboxtv.app.cast.ExpandedControlsActivity;
import com.movieboxtv.app.event.CastSessionEndedEvent;
import com.movieboxtv.app.event.CastSessionStartedEvent;
import com.movieboxtv.app.ui.player.CustomPlayerFragment;

import com.movieboxtv.app.R;

import org.greenrobot.eventbus.EventBus;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class PlayerActivity extends AppCompatActivity {

    private CastContext mCastContext;
    private SessionManager mSessionManager;
    private CastSession mCastSession;
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();
    private ScaleGestureDetector mScaleGestureDetector;
    private CustomPlayerFragment customPlayerFragment;
    private String videoUrl;
    private Boolean isLive = false;
    private String videoType;
    private String videoTitle;
    private String videoImage;
    private String videoSubTile;
    private int vodeoId ;
    private String videoKind;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            if (scaleGestureDetector.getScaleFactor()>1){
                CustomPlayerFragment myFragment = (CustomPlayerFragment)getSupportFragmentManager().findFragmentByTag("CustomPlayerFragment");
                myFragment.setFull();
            }
            if (scaleGestureDetector.getScaleFactor()<1){
                CustomPlayerFragment myFragment = (CustomPlayerFragment)getSupportFragmentManager().findFragmentByTag("CustomPlayerFragment");
                myFragment.setNormal();

            }
            return true;
        }
    }
    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {
            Log.d("MYAPP","onSessionStarting");
        }

        @Override
        public void onSessionStarted(Session session, String s) {
            Log.d("MYAPP","onSessionStarted");
            invalidateOptionsMenu();
            EventBus.getDefault().post(new CastSessionStartedEvent());
            startActivity(new Intent(PlayerActivity.this, ExpandedControlsActivity.class));
            finish();
        }

        @Override
        public void onSessionStartFailed(Session session, int i) {
            Log.d("MYAPP","onSessionStartFailed");
        }

        @Override
        public void onSessionEnding(Session session) {
            Log.d("MYAPP","onSessionEnding");
            EventBus.getDefault().post(new CastSessionEndedEvent(session.getSessionRemainingTimeMs()));
        }

        @Override
        public void onSessionEnded(Session session, int i) {
            Log.d("MYAPP","onSessionEnded");
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            Log.d("MYAPP","onSessionResuming");
        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            Log.d("MYAPP","onSessionResumed");
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {
            Log.d("MYAPP","onSessionResumeFailed");
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            Log.d("MYAPP","onSessionSuspended");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mCastContext = CastContext.getSharedInstance(this);
        Bundle bundle = getIntent().getExtras() ;
        vodeoId = bundle.getInt("id");
        videoUrl = bundle.getString("url");
        videoKind = bundle.getString("kind");
        isLive = bundle.getBoolean("isLive");
        videoType = bundle.getString("type");
        videoTitle = bundle.getString("title");
        videoSubTile = bundle.getString("subtitle");
        videoImage = bundle.getString("image");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState == null) {
            customPlayerFragment =
                    CustomPlayerFragment.newInstance(getVideoUrl(),isLive,videoType,videoTitle,videoSubTile,videoImage,vodeoId,videoKind);
            launchFragment(customPlayerFragment);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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

    private void launchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_fragment_container, fragment, "CustomPlayerFragment");
        fragmentTransaction.commit();


    }

    private String getVideoUrl() {
        return videoUrl;
    }

    public CastSession getCastSession() {
        return mCastSession;
    }

    public SessionManager getSessionManager() {
        return mSessionManager;
    }
}

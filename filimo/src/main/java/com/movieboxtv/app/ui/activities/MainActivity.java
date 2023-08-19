package com.movieboxtv.app.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.entity.Genre;

import org.jetbrains.annotations.NotNull;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;

import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.services.ToastMsg;
import com.movieboxtv.app.ui.fragments.DownloadsFragment;
import com.movieboxtv.app.ui.fragments.HomeFragment;
import com.movieboxtv.app.ui.fragments.MoviesFragment;
import com.movieboxtv.app.ui.fragments.SeriesFragment;
import com.movieboxtv.app.ui.fragments.TvFragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.TapsellPlusInitListener;
import ir.tapsell.plus.model.AdNetworkError;
import ir.tapsell.plus.model.AdNetworks;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private NavigationView navigationView;
    private TextView text_view_name_nave_header;
    private TextView text_view_expire_date_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;
    private ImageView image_view_profile_nav_header_bg;
    private Dialog rateDialog;
    private boolean FromLogin;
    private RelativeLayout relative_layout_home_activity_search_section;
    private EditText edit_text_home_activity_search;
    private ImageView image_view_activity_home_close_search;
    private ImageView image_view_activity_home_search;
    private ImageView image_view_activity_actors_back;
    private long back_pressed;
    ConsentForm form;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(getApplicationContext());
        TapsellPlus.initialize(this, prefManager.getString("ADMIN_NATIVE_ADMOB_ID"),
                new TapsellPlusInitListener() {
                    @Override
                    public void onInitializeSuccess(AdNetworks adNetworks) {
                        Log.d("onInitializeSuccess", adNetworks.name());
                    }

                    @Override
                    public void onInitializeFailed(AdNetworks adNetworks,
                                                   AdNetworkError adNetworkError) {
                        Log.e("onInitializeFailed", "ad network: " + adNetworks.name() + ", error: " +	adNetworkError.getErrorMessage());
                    }
                });


        if(Config.NAVIGATION_BAR == 2) {

            setContentView(R.layout.activity_mian2);
            if(Config.COMPULSORY_LOGIN) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (!prefManager.getString("LOGGED").toString().equals("TRUE")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            getGenreList();
            initViews();
            initActions();
            checkInstall();
            bottomNav2();

        }else if(Config.NAVIGATION_BAR == 1){

            setContentView(R.layout.activity_mian);
            if(Config.COMPULSORY_LOGIN) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (!prefManager.getString("LOGGED").toString().equals("TRUE")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            getGenreList();
            initViews();
            initActions();
            checkInstall();
        }
      //  initGDPR();

    }


    AnimatedBottomBar animatedBottomBar;

    private void bottomNav2(){
        animatedBottomBar = (AnimatedBottomBar) findViewById(R.id.animatedBottomBar);
        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabReselected(int position, @NotNull AnimatedBottomBar.Tab tab) {
                viewPager.setCurrentItem(position, true);
            }

            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NotNull AnimatedBottomBar.Tab newTab) {
                int tabId = newTab.getId();

                if (tabId == R.id.homeman) {
                    viewPager.setCurrentItem(4, true);
                } else if (tabId == R.id.film) {
                    viewPager.setCurrentItem(3, true);
                } else if (tabId == R.id.serial) {
                    viewPager.setCurrentItem(2, true);
                } else if (tabId == R.id.channel) {
                    viewPager.setCurrentItem(1, true);
                } else if (tabId == R.id.download) {
                    viewPager.setCurrentItem(0, true);
                }

            }
        });
    }


    private void checkInstall() {
        //PushPole.subscribe(getApplicationContext(), "filimo");
    }

    private void initActions() {
        image_view_activity_actors_back.setOnClickListener(v -> {
            relative_layout_home_activity_search_section.setVisibility(View.GONE);
            edit_text_home_activity_search.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(edit_text_home_activity_search.getApplicationWindowToken(), 0);
        });
        edit_text_home_activity_search.setOnEditorActionListener((v, actionId, event) -> {
            if (edit_text_home_activity_search.getText().length() > 0) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", edit_text_home_activity_search.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                relative_layout_home_activity_search_section.setVisibility(View.GONE);
                edit_text_home_activity_search.setText("");
            }
            return false;
        });
        image_view_activity_home_close_search.setOnClickListener(v -> {
            edit_text_home_activity_search.setText("");
        });
        image_view_activity_home_search.setOnClickListener(v -> {
            if (edit_text_home_activity_search.getText().length() > 0) {

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", edit_text_home_activity_search.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                relative_layout_home_activity_search_section.setVisibility(View.GONE);
                edit_text_home_activity_search.setText("");
            }
        });
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header = (TextView) headerview.findViewById(R.id.text_view_name_nave_header);
        this.text_view_expire_date_nave_header = (TextView) headerview.findViewById(R.id.text_view_subscribe_date_nave_header);
        this.circle_image_view_profile_nav_header = (CircleImageView) headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.image_view_profile_nav_header_bg = (ImageView) headerview.findViewById(R.id.image_view_profile_nav_header_bg);
        // init pager view
        PrefManager prf = new PrefManager(getApplicationContext());
        Menu nav_buy_Menu = navigationView.getMenu();
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            if (prf.getString("SUBSCRIBED").equals("TRUE")) {
                text_view_expire_date_nave_header.setText("اشتراک باقیمانده: " + " " + prf.getString("SUBSCRIBED_DAYS") + " " + "روز");
                text_view_expire_date_nave_header.setVisibility(View.VISIBLE);
                nav_buy_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.premium_title_extension));
            } else {
                nav_buy_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.subscribe));
                text_view_expire_date_nave_header.setVisibility(View.GONE);
            }
        } else {
            nav_buy_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.subscribe));
            text_view_expire_date_nave_header.setVisibility(View.GONE);
        }

        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DownloadsFragment());
        adapter.addFragment(new TvFragment());
        adapter.addFragment(new SeriesFragment());
        adapter.addFragment(new MoviesFragment());
        adapter.addFragment(new HomeFragment());


        viewPager.setAdapter(adapter);
        final BubbleNavigationConstraintView bubbleNavigationLinearView = findViewById(R.id.top_navigation_constraint);

        viewPager.setCurrentItem(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                if(Config.NAVIGATION_BAR == 2) {
                    animatedBottomBar.selectTabAt(i, true);
                }else if(Config.NAVIGATION_BAR == 1){
                    bubbleNavigationLinearView.setCurrentActiveItem(i);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bubbleNavigationLinearView.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/yekan.ttf"));
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });
        bubbleNavigationLinearView.setCurrentActiveItem(4);

        this.relative_layout_home_activity_search_section = (RelativeLayout) findViewById(R.id.relative_layout_home_activity_search_section);
        this.edit_text_home_activity_search = (EditText) findViewById(R.id.edit_text_home_activity_search);
        this.image_view_activity_home_close_search = (ImageView) findViewById(R.id.image_view_activity_home_close_search);
        this.image_view_activity_actors_back = (ImageView) findViewById(R.id.image_view_activity_actors_back);
        this.image_view_activity_home_search = (ImageView) findViewById(R.id.image_view_activity_home_search);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(4);
        } else if (id == R.id.login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

            FromLogin = true;

        } else if (id == R.id.nav_exit) {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
            }
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.my_profile) {
            PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("image", prf.getString("IMAGE_USER").toString());
                intent.putExtra("name", prf.getString("NAME_USER").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                FromLogin = true;
            }
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.nav_share) {

            final PrefManager prf = new PrefManager(getApplicationContext());
            final String appPackageName = getApplication().getPackageName();
            String shareBody1 = getResources().getString(R.string.download_more_from_link) + getResources().getString(R.string.link_bazzar) + appPackageName;
            String shareBody2 = getResources().getString(R.string.download_more_from_link) + getResources().getString(R.string.link_myket) + appPackageName;
            String shareBody3 = getResources().getString(R.string.download_more_from_link_direct) + prf.getString("APPLICATION_DOWNLOAD_LINK");
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            if (Config.PAYMENT_MOD == 1) {
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody1);
            } else if (Config.PAYMENT_MOD == 2) {
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody2);
            } else if (Config.PAYMENT_MOD == 3) {
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody3);
            }
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));

        } else if (id == R.id.nav_rate) {
            rateDialog(false);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.buy_now) {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                if (!prf.getString("SUBSCRIBED").equals("TRUE")) {
                    Intent intent = new Intent(getApplicationContext(), PackageBuyActivity.class);
                    intent.putExtra("type_form", "normal");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Intent intent = new Intent(getApplicationContext(), PackageBuyActivity.class);
                    intent.putExtra("type_form", "extension");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void logout() {

        PrefManager prf = new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.remove("LOGGED");
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Menu nav_buy_Menu = navigationView.getMenu();
                if (prf.getString("SUBSCRIBED").equals("TRUE")) {
                    text_view_expire_date_nave_header.setText("اشتراک باقیمانده: " + " " + prf.getString("SUBSCRIBED_DAYS") + " " + "روز");
                    text_view_expire_date_nave_header.setVisibility(View.VISIBLE);

                    nav_buy_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.premium_title_extension));

                } else {
                    nav_buy_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.subscribe));
                    text_view_expire_date_nave_header.setVisibility(View.GONE);
                }


            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")) {
            } else {
            }
        } else {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            text_view_expire_date_nave_header.setVisibility(View.GONE);
            nav_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.premium_title_extension));
            Picasso.with(getApplicationContext()).load(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        image_view_profile_nav_header_bg.setVisibility(View.GONE);
        Toasty.info(getApplicationContext(), getString(R.string.message_logout), Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this, SplashActivity.class));
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    private static final String TAG = "MainActivity ----- : ";

    private void initGDPR() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation =
                ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {getResources().getString(R.string.publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new
                ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                        Log.d(TAG, "onConsentInfoUpdated");
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Log.d(TAG, "PERSONALIZED");
                                ConsentInformation.getInstance(MainActivity.this)
                                        .setConsentStatus(ConsentStatus.PERSONALIZED);
                                break;
                            case NON_PERSONALIZED:
                                Log.d(TAG, "NON_PERSONALIZED");
                                ConsentInformation.getInstance(MainActivity.this)
                                        .setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                break;


                            case UNKNOWN:
                                Log.d(TAG, "UNKNOWN");
                                if
                                (ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown
                                        ()) {
                                    URL privacyUrl = null;
                                    try {
// TODO: Replace with your app's privacy policy URL.
                                        privacyUrl = new URL(Config.API_URL.replace("/api/", "/privacy_policy.html"));

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
// Handle error.

                                    }
                                    form = new ConsentForm.Builder(MainActivity.this,
                                            privacyUrl)
                                            .withListener(new ConsentFormListener() {
                                                @Override
                                                public void onConsentFormLoaded() {
                                                    Log.d(TAG, "onConsentFormLoaded");
                                                    showform();
                                                }

                                                @Override
                                                public void onConsentFormOpened() {
                                                    Log.d(TAG, "onConsentFormOpened");
                                                }

                                                @Override
                                                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                                    Log.d(TAG, "onConsentFormClosed");
                                                }

                                                @Override
                                                public void onConsentFormError(String errorDescription) {
                                                    Log.d(TAG, "onConsentFormError");
                                                    Log.d(TAG, errorDescription);
                                                }
                                            })
                                            .withPersonalizedAdsOption()
                                            .withNonPersonalizedAdsOption()
                                            .build();
                                    form.load();
                                } else {
                                    Log.d(TAG, "PERSONALIZED else");
                                    ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailedToUpdateConsentInfo(String errorDescription) {
// User's consent status failed to update.
                        Log.d(TAG, "onFailedToUpdateConsentInfo");
                        Log.d(TAG, errorDescription);
                    }
                });
    }

    private void showform() {
        if (form != null) {
            Log.d(TAG, "show ok");
            form.show();
        }
    }

    public void rateDialog(final boolean close) {
        this.rateDialog = new Dialog(this, R.style.Theme_Dialog);

        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setCancelable(true);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final PrefManager prf = new PrefManager(getApplicationContext());
        rateDialog.setCancelable(false);
        rateDialog.setContentView(R.layout.dialog_rating_app);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app = (AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final LinearLayout linear_layout_feedback = (LinearLayout) rateDialog.findViewById(R.id.linear_layout_feedback);
        final LinearLayout linear_layout_rate = (LinearLayout) rateDialog.findViewById(R.id.linear_layout_rate);
        final Button buttun_send_feedback = (Button) rateDialog.findViewById(R.id.buttun_send_feedback);
        final Button button_later = (Button) rateDialog.findViewById(R.id.button_later);
        final Button button_never = (Button) rateDialog.findViewById(R.id.button_never);
        final Button button_cancel = (Button) rateDialog.findViewById(R.id.button_cancel);
        button_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        final EditText edit_text_feed_back = (EditText) rateDialog.findViewById(R.id.edit_text_feed_back);
        buttun_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.addSupport("امتیاز برای اپلیکیشن", AppCompatRatingBar_dialog_rating_app.getRating() + " امتیاز ".toString(), edit_text_feed_back.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            new ToastMsg(getApplicationContext()).toastIconSuccess(getResources().getString(R.string.rating_done));
                        } else {

                            new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.error_server));
                        }
                        rateDialog.dismiss();

                        if (close)
                            finish();

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.error_server));
                        rateDialog.dismiss();

                        if (close)
                            finish();
                    }
                });
            }
        });

        AppCompatRatingBar_dialog_rating_app.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
               if (fromUser) {
                    if (Config.PAYMENT_MOD == 3) {
                        linear_layout_feedback.setVisibility(View.VISIBLE);
                        linear_layout_rate.setVisibility(View.GONE);
                    } else {
                        if (rating > 3) {
                            if (Config.PAYMENT_MOD == 1) {
                                try {
                                    final String appPackageName = getApplication().getPackageName();
                                    Intent intent = new Intent(Intent.ACTION_EDIT);
                                    intent.setData(Uri.parse("bazaar://details?id=" + appPackageName));
                                    intent.setPackage("com.farsitel.bazaar");
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "بازار نصب نمی باشد!", Toast.LENGTH_SHORT).show();
                                }
                            } else if (Config.PAYMENT_MOD == 2) {
                                try {
                                    final String appPackageName = getApplication().getPackageName();
                                    String url = "myket://comment?id=" + appPackageName;
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "مایکت نصب نمی باشد!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            prf.setString("NOT_RATE_APP", "TRUE");
                            rateDialog.dismiss();
                        } else {
                            linear_layout_feedback.setVisibility(View.VISIBLE);
                            linear_layout_rate.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    rateDialog.dismiss();
                    if (close)
                        finish();
                }
                return true;

            }
        });
        rateDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            edit_text_home_activity_search.requestFocus();
            relative_layout_home_activity_search_section.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();


        PrefManager prf = new PrefManager(getApplicationContext());

        Menu nav_Menu = navigationView.getMenu();


        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            nav_Menu.findItem(R.id.my_profile).setVisible(true);

            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);

            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
                if (prf.getString("SUBSCRIBED").equals("TRUE")) {
                    text_view_expire_date_nave_header.setText("اشتراک باقیمانده: " + " " + prf.getString("SUBSCRIBED_DAYS") + " " + "روز");
                    text_view_expire_date_nave_header.setVisibility(View.VISIBLE);
                    nav_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.premium_title_extension));

                } else {
                    text_view_expire_date_nave_header.setVisibility(View.GONE);
                    nav_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.subscribe));
                }

            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);

            final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //BlurImage.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(image_view_profile_nav_header_bg);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).into(target);
            image_view_profile_nav_header_bg.setTag(target);
            image_view_profile_nav_header_bg.setVisibility(View.VISIBLE);

        } else {
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            image_view_profile_nav_header_bg.setVisibility(View.GONE);
            nav_Menu.findItem(R.id.buy_now).setTitle(getString(R.string.subscribe));
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            text_view_expire_date_nave_header.setVisibility(View.GONE);
            Picasso.with(getApplicationContext()).load(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        if (FromLogin) {
            FromLogin = false;
        }

    }

    public void goToTV() {
        viewPager.setCurrentItem(1);
    }

    private void getGenreList() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Genre>> call = service.getGenreList();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {

            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(getBaseContext(), "برای خروج دوباره لمس کنید!", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
                }

            } else {
                rateDialog(true);
                return;
            }
        }

    }
}

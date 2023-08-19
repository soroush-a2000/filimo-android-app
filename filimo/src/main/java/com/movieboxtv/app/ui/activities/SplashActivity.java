package com.movieboxtv.app.ui.activities;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.pushpole.sdk.PushPole;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.Utils.FaNum;
import com.movieboxtv.app.Utils.HelperUtils;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.ApiResponse;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import com.movieboxtv.app.services.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.movieboxtv.app.config.Config.CHECK_VPN;


public class SplashActivity extends AppCompatActivity {


    private PrefManager prf;
    NumberProgressBar numberProgressBar;
    TextView tv_progres;
    Timer timer1;
    int progres=0;
    private boolean vpnStatus = false;
    private HelperUtils helperUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        vpnStatus = new HelperUtils(SplashActivity.this).isVpnConnectionAvailable();
        if (Config.SPLASH_SCREEN == 1){
            setContentView(R.layout.activity_splash);
            prf = new PrefManager(getApplicationContext());
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // If you want to modify a view in your Activity
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Config.SECURITY_QUESTION == true){
                                if ( !prf.getshowDialogSecurity()){
                                    showDialogSecurityQuestion();
                                } else {
                                    if (CHECK_VPN){
                                        if (vpnStatus){
                                            vpneror();
                                        }else{
                                            checkAccount();
                                        }
                                    }else{
                                        checkAccount();
                                    }
                                }
                            }else{
                                if (CHECK_VPN){
                                    if (vpnStatus){
                                        vpneror();
                                    }else{
                                        checkAccount();
                                    }
                                }else{
                                    checkAccount();
                                }
                            }
                        }
                    });
                }
            }, 3000);

            prf.setString("SUBSCRIBED", "FALSE");
            prf.setString("SUBSCRIBED_DAYS", "0");
            prf.setString("SUBSCRIBED_EXTENSION_PURCHASE", "0");

            prf.setString("ADMIN_BANNER_TYPE", "FALSE");
            prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID", "");
            prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID", "");
            prf.setString("ADMIN_INTERSTITIAL_TYPE", "FALSE");
            prf.setInt("ADMIN_INTERSTITIAL_CLICKS", 3);

            prf.setString("ADMIN_BANNER_ADMOB_ID", "");
            prf.setString("ADMIN_BANNER_FACEBOOK_ID", "");
            prf.setString("ADMIN_BANNER_TYPE", "FALSE");

            prf.setString("ADMIN_NATIVE_FACEBOOK_ID", "");
            prf.setString("ADMIN_NATIVE_ADMOB_ID", "");
            prf.setString("ADMIN_NATIVE_LINES", "6");
            prf.setString("ADMIN_NATIVE_TYPE", "FALSE");
            prf.setString("APPLICATION_DOWNLOAD_LINK", "");
            prf.setString("PAYMENT_GATEWAY", "");

            initBuy();
        }else if (Config.SPLASH_SCREEN == 2){
            setContentView(R.layout.activity_splash2);
            prf = new PrefManager(getApplicationContext());

            ImageView image_View_bg_top = (ImageView) findViewById(R.id.image_View_bg_top);
            ImageView splash_logo = (ImageView) findViewById(R.id.splash_logo);
            TextView txtyou = (TextView) findViewById(R.id.txtyou);
            TranslateAnimation animation = new TranslateAnimation(0.0f, 790.0f, 0.0f, 0.0f);
            animation.setDuration(12000);
            animation.setRepeatCount(10);
            animation.setRepeatMode(2);
            //animation.setFillAfter(true);
            image_View_bg_top.startAnimation(animation);
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_splash);
            Animation hyperspaceJumpAnimation2 = AnimationUtils.loadAnimation(this, R.anim.slide_down_splash);
            splash_logo.setVisibility(View.INVISIBLE);
            txtyou.setVisibility(View.INVISIBLE);
            Timer myTimer2 = new Timer();
            myTimer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    // If you want to modify a view in your Activity
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            splash_logo.setVisibility(View.VISIBLE);
                            splash_logo.setAnimation(hyperspaceJumpAnimation);
                        }
                    });
                }
            }, 800);
            Timer myTimer3 = new Timer();
            myTimer3.schedule(new TimerTask() {
                @Override
                public void run() {
                    // If you want to modify a view in your Activity
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtyou.setVisibility(View.VISIBLE);
                            txtyou.setAnimation(hyperspaceJumpAnimation2);
                        }
                    });
                }
            }, 1700);

            timerProgres();
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // If you want to modify a view in your Activity
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Config.SECURITY_QUESTION == true){
                                if ( !prf.getshowDialogSecurity()){
                                    showDialogSecurityQuestion();
                                } else {
                                    if (CHECK_VPN){
                                        if (vpnStatus){
                                            vpneror();
                                        }else{
                                            checkAccount();
                                        }
                                    }else{
                                        checkAccount();
                                    }
                                }
                            }else{
                                if (CHECK_VPN){
                                    if (vpnStatus){
                                        vpneror();
                                    }else{
                                        checkAccount();
                                    }
                                }else{
                                    checkAccount();
                                }
                            }

                        }
                    });
                }
            }, 3500);

            prf.setString("SUBSCRIBED", "FALSE");
            prf.setString("SUBSCRIBED_DAYS", "0");
            prf.setString("SUBSCRIBED_EXTENSION_PURCHASE", "0");

            prf.setString("ADMIN_BANNER_TYPE", "FALSE");
            prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID", "");
            prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID", "");
            prf.setString("ADMIN_INTERSTITIAL_TYPE", "FALSE");
            prf.setInt("ADMIN_INTERSTITIAL_CLICKS", 3);

            prf.setString("ADMIN_BANNER_ADMOB_ID", "");
            prf.setString("ADMIN_BANNER_FACEBOOK_ID", "");
            prf.setString("ADMIN_BANNER_TYPE", "FALSE");

            prf.setString("ADMIN_NATIVE_FACEBOOK_ID", "");
            prf.setString("ADMIN_NATIVE_ADMOB_ID", "");
            prf.setString("ADMIN_NATIVE_LINES", "6");
            prf.setString("ADMIN_NATIVE_TYPE", "FALSE");
            prf.setString("APPLICATION_DOWNLOAD_LINK", "");
            prf.setString("PAYMENT_GATEWAY", "");

            initBuy();
        }
    }

    private void showDialogSecurityQuestion(){
        Dialog dialog_select=new  Dialog(this, R.style.Theme_Dialog_write);
        dialog_select.setCancelable(false);
        dialog_select.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_select.getWindow() .setGravity(Gravity.TOP);
        dialog_select.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_select.setContentView(R.layout.dialog_securityquestion);
        dialog_select.show();
        ProgressBar progress = dialog_select.findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        Button btn_ok=dialog_select.findViewById(R.id.btn_ok);
        EditText ed_tehran=dialog_select.findViewById(R.id.ed_tehran);
        btn_ok.setOnClickListener(view -> {
            if (ed_tehran.getText().toString().trim().equals("تهران") || ed_tehran.getText().toString().trim().equals("طهران")){
                progress.setVisibility(View.VISIBLE);
                btn_ok.setVisibility(View.INVISIBLE);
                checkAccount();
                prf.setshowDialogSecurity(true);
            }else {
                Toast.makeText(this, "نام امنیتی وارد شده اشتباه می باشد", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void vpneror(){
        View v = (View) getLayoutInflater().inflate(R.layout.vpneror, null);
        TextView update_text_view_updates = (TextView) v.findViewById(R.id.update_text_view_updates);
        //  update_text_view_title.setText(title_update);
        update_text_view_updates.setText(R.string.novpn);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(" شناسایی شد vpn ")
                //.setMessage(response.body().getValue())
                .setView(v)
                .setPositiveButton(getResources().getString(R.string.go_out), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_vpn_key_24)
                .show();
    }

    private void timerProgres(){
        numberProgressBar=findViewById( R.id.progress_bar);
        tv_progres=findViewById( R.id.tv_progres);
        timer1=new Timer();

        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progres+=10;
                        if (progres>100){
                            progres=100;
                            timer1.cancel();
                        }
                        tv_progres.setText(FaNum.convert(progres+"%"));
                        numberProgressBar.setProgress(progres);

                    }
                });
            }
        },0,350);
    }

    String token_user;

    Integer id_user = 0;
    private void checkAccount() {
        Integer version = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version != -1) {

            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                id_user = Integer.parseInt(prf.getString("ID_USER"));
            }
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.check(version, id_user);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {

                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if (response.body().getValues().get(i).getName().equals("SUBSCRIBED")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("SUBSCRIBED", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("SUBSCRIBED_DAYS")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("SUBSCRIBED_DAYS", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_REWARDED_ADMOB_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_REWARDED_ADMOB_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_ADMOB_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_FACEBOOK_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_TYPE")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_INTERSTITIAL_TYPE", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_CLICKS")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setInt("ADMIN_INTERSTITIAL_CLICKS", Integer.parseInt(response.body().getValues().get(i).getValue()));
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_BANNER_ADMOB_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_BANNER_ADMOB_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_BANNER_FACEBOOK_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_BANNER_FACEBOOK_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_BANNER_TYPE")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_BANNER_TYPE", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_FACEBOOK_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_NATIVE_FACEBOOK_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_ADMOB_ID")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_NATIVE_ADMOB_ID", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_LINES")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_NATIVE_LINES", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_TYPE")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("ADMIN_NATIVE_TYPE", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("APPLICATION_DOWNLOAD_LINK")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    prf.setString("APPLICATION_DOWNLOAD_LINK", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("AUTO_SLIDER")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    Config.AUTO_CHANGED_SLIDER = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("TIME_SLIDER")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    Config.SLIDER_CHANGE_TIME = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("PAYMENT_GATEWAY")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                 prf.setString("PAYMENT_GATEWAY", response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("MERCHANT_CODE")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    Config.MERCHANT_CODE = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("PAYMENT_DESCRIPTION")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    Config.PAYMENT_DESCRIPTION = response.body().getValues().get(i).getValue();
                            }

                            if (Config.ACTIVE_MEETINGS){
                                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                                    if (response.body().getValues().get(i).getName().equals("TOKEN")) {
                                        token_user = response.body().getValues().get(i).getValue();
                                        if (!token_user.equals(PushPole.getId(getApplicationContext()))) {
                                            prf.remove("ID_USER");
                                            prf.remove("SALT_USER");
                                            prf.remove("TOKEN_USER");
                                            prf.remove("NAME_USER");
                                            prf.remove("TYPE_USER");
                                            prf.remove("USERN_USER");
                                            prf.remove("IMAGE_USER");
                                            prf.remove("LOGGED");
                                            new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.another_login));

                                        }
                                    }
                                }
                            }
                        }

                        initBuy();


                        if (response.body().getValues().get(1).getValue().equals("403")) {
                            prf.remove("ID_USER");
                            prf.remove("SALT_USER");
                            prf.remove("TOKEN_USER");
                            prf.remove("NAME_USER");
                            prf.remove("TYPE_USER");
                            prf.remove("USERN_USER");
                            prf.remove("IMAGE_USER");
                            prf.remove("LOGGED");
                            new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.account_disabled));
                        }


                        if (response.body().getCode().equals(200)) {

                            if (!prf.getString("first").equals("true")) {
                                Intent intent;
                                if (Config.ENABLE_INTRO) {
                                    intent = new Intent(SplashActivity.this, IntroActivity.class);
                                } else {
                                    intent = new Intent(SplashActivity.this, MainActivity.class);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                                prf.setString("first", "true");
                            } else {

                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }


                        } else if (response.body().getCode().equals(202)) {

                            String featurs_update = response.body().getMessage();
                            String clearDataEnabled = response.body().getValues().get(0).getValue();
                            View v = (View) getLayoutInflater().inflate(R.layout.update_message, null);
                            TextView update_text_view_title = (TextView) v.findViewById(R.id.update_text_view_title);
                            TextView update_text_view_updates = (TextView) v.findViewById(R.id.update_text_view_updates);
                            //  update_text_view_title.setText(title_update);
                            update_text_view_updates.setText(featurs_update);
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setTitle("آپدیت جدید")
                                    //.setMessage(response.body().getValue())
                                    .setView(v)
                                    .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Config.PAYMENT_MOD == 3) {
                                                final Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(prf.getString("APPLICATION_DOWNLOAD_LINK")));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                                startActivity(intent);
                                                if(clearDataEnabled.equals("1")){
                                                    clearAppData();
                                                }else{
                                                    finish();
                                                }
                                            } else {
                                                if (Config.PAYMENT_MOD == 1) {
                                                    try {
                                                        final String appPackageName = getApplication().getPackageName();
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse("bazaar://details?id=" + appPackageName));
                                                        intent.setPackage("com.farsitel.bazaar");
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        Toast.makeText(SplashActivity.this, "بازار نصب نمی باشد!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else if (Config.PAYMENT_MOD == 2) {
                                                    try {
                                                        final String appPackageName = getApplication().getPackageName();
                                                        String url = "myket://download/" + appPackageName;
                                                        Intent intent = new Intent();
                                                        intent.setAction(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse(url));
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        Toast.makeText(SplashActivity.this, "مایکت نصب نمی باشد!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                if(clearDataEnabled.equals("1")){
                                                    clearAppData();
                                                }else{
                                                    finish();
                                                }
                                            }
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(R.drawable.ic_update)
                                    .show();
                        } else {
                            if (!prf.getString("first").equals("true")) {
                                Intent intent;
                                if (Config.ENABLE_INTRO) {
                                    intent = new Intent(SplashActivity.this, IntroActivity.class);
                                } else {
                                    intent = new Intent(SplashActivity.this, MainActivity.class);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                                prf.setString("first", "true");
                            } else {

                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }
                        }
                    } else {
                        if (!prf.getString("first").equals("true")) {
                            Intent intent;
                            if (Config.ENABLE_INTRO) {
                                intent = new Intent(SplashActivity.this, IntroActivity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                            prf.setString("first", "true");
                        } else {
                            final PrefManager prf = new PrefManager(getApplicationContext());
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (!prf.getString("first").equals("true")) {
                        Intent intent;
                        if (Config.ENABLE_INTRO) {
                            intent = new Intent(SplashActivity.this, IntroActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                        prf.setString("first", "true");
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                }
            });
        } else {
            if (!prf.getString("first").equals("true")) {
                Intent intent;
                if (Config.ENABLE_INTRO) {
                    intent = new Intent(SplashActivity.this, IntroActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                prf.setString("first", "true");
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }

        }

    }


    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) Objects.requireNonNull(getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initBuy() {
        if (!prf.getString("SHOW_EXPIRE_MASSSAGE").equals("1")) {
            if (prf.getString("SUBSCRIBED_DAYS").equals("FINISH")) {
                new ToastMsg(getApplicationContext()).toastIconError("اشتراک شما به پایان رسیده!");
                prf.setString("SHOW_EXPIRE_MASSSAGE","1");
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}

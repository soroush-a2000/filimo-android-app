package com.movieboxtv.app.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.entity.Channel;
import com.movieboxtv.app.entity.Poster;
import com.movieboxtv.app.services.ToastMsg;

import com.movieboxtv.app.R;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoadActivity extends AppCompatActivity {

    private PrefManager prf;

    private Integer id;
    private String type;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        int time = 3000;
        Uri data = this.getIntent().getData();
        if (data == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                this.id = bundle.getInt("id");
                this.type = bundle.getString("type");
                time = 2000;
            }
        } else {
            if (data.getPath().contains("/c/share/")) {
                this.id = Integer.parseInt(data.getPath().replace("/c/share/", "").replace(".html", ""));
                this.type = "channel";
                time = 2000;
            } else {
                this.id = Integer.parseInt(data.getPath().replace("/share/", "").replace(".html", ""));
                this.type = "poster";
                time = 2000;
            }
        }


        prf = new PrefManager(getApplicationContext());

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                LoadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        checkAccount();

                    }
                });
            }
        }, time);

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


    private void checkAccount() {

        Integer version = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version != -1) {
            Integer id_user = 0;

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
                                    Config.AUTO_CHANGED_SLIDER = (response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("TIME_SLIDER")) {
                                if (response.body().getValues().get(i).getValue() != null)
                                    Config.SLIDER_CHANGE_TIME = (response.body().getValues().get(i).getValue());
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
                        if (id != null && type != null) {
                            if (type.equals("poster"))
                                getPoster();
                            if (type.equals("channel"))
                                getChannel();
                        } else {
                            if (response.body().getCode().equals(200)) {
                                if (!prf.getString("first").equals("true")) {
                                    Intent intent;
                                    if (Config.ENABLE_INTRO) {
                                        intent = new Intent(LoadActivity.this, IntroActivity.class);
                                    } else {
                                        intent = new Intent(LoadActivity.this, MainActivity.class);
                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                    prf.setString("first", "true");
                                } else {
                                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                }
                            } else if (response.body().getCode().equals(202)) {
                                String title_update = response.body().getValues().get(0).getValue();
                                String featurs_update = response.body().getMessage();
                                View v = (View) getLayoutInflater().inflate(R.layout.update_message, null);
                                TextView update_text_view_title = (TextView) v.findViewById(R.id.update_text_view_title);
                                TextView update_text_view_updates = (TextView) v.findViewById(R.id.update_text_view_updates);
                                //  update_text_view_title.setText(title_update);
                                update_text_view_updates.setText(featurs_update);
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(LoadActivity.this);
                                builder.setTitle("آپدیت جدید")
                                        //.setMessage(response.body().getValue())
                                        .setView(v)
                                        .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Config.PAYMENT_MOD == 3) {
                                                  //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prf.getString("APPLICATION_DOWNLOAD_LINK")));
                                                   // startActivity(browserIntent);
                                                   // finish();
                                                    final Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(prf.getString("APPLICATION_DOWNLOAD_LINK")));
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    if (Config.PAYMENT_MOD == 1) {
                                                        try {
                                                            final String appPackageName = getApplication().getPackageName();
                                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                                            intent.setData(Uri.parse("bazaar://details?id=" + appPackageName));
                                                            intent.setPackage("com.farsitel.bazaar");
                                                            startActivity(intent);
                                                        } catch (Exception e) {
                                                            Toast.makeText(LoadActivity.this, "بازار نصب نمی باشد!", Toast.LENGTH_SHORT).show();
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
                                                            Toast.makeText(LoadActivity.this, "مایکت نصب نمی باشد!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    finish();
                                                }
                                            }
                                        })
/*                                        .setNegativeButton(getResources().getString(R.string.skip), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (!prf.getString("first").equals("true")) {
                                                    Intent intent;
                                                    if (Config.ENABLE_INTRO) {
                                                        intent = new Intent(LoadActivity.this, IntroActivity.class);
                                                    } else {
                                                        intent = new Intent(LoadActivity.this, MainActivity.class);
                                                    }
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                                    finish();
                                                    prf.setString("first", "true");
                                                } else {
                                                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                                    finish();
                                                }
                                            }
                                        })*/
                                        .setCancelable(false)
                                        .setIcon(R.drawable.ic_update)
                                        .show();
                            } else {
                                if (!prf.getString("first").equals("true")) {
                                    Intent intent;
                                    if (Config.ENABLE_INTRO) {
                                        intent = new Intent(LoadActivity.this, IntroActivity.class);
                                    } else {
                                        intent = new Intent(LoadActivity.this, MainActivity.class);
                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                    prf.setString("first", "true");
                                } else {
                                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                }
                            }
                        }
                    } else {

                        if (id != null && type != null) {
                            if (type.equals("poster"))
                                getPoster();
                            if (type.equals("channel"))
                                getChannel();
                        } else {
                            if (!prf.getString("first").equals("true")) {
                                Intent intent;
                                if (Config.ENABLE_INTRO) {
                                    intent = new Intent(LoadActivity.this, IntroActivity.class);
                                } else {
                                    intent = new Intent(LoadActivity.this, MainActivity.class);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                                prf.setString("first", "true");
                            } else {
                                Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                    if (id != null && type != null) {
                        if (type.equals("poster"))
                            getPoster();
                        if (type.equals("channel"))
                            getChannel();
                    } else {
                        if (!prf.getString("first").equals("true")) {
                            Intent intent;
                            if (Config.ENABLE_INTRO) {
                                intent = new Intent(LoadActivity.this, IntroActivity.class);
                            } else {
                                intent = new Intent(LoadActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                            prf.setString("first", "true");
                        } else {
                            Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        }
                    }
                }
            });

        } else {

            if (id != null && type != null) {
                if (type.equals("poster"))
                    getPoster();
                if (type.equals("channel"))
                    getChannel();
            } else {
                if (!prf.getString("first").equals("true")) {
                    Intent intent;
                    if (Config.ENABLE_INTRO) {
                        intent = new Intent(LoadActivity.this, IntroActivity.class);
                    } else {
                        intent = new Intent(LoadActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                    prf.setString("first", "true");
                } else {
                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }
        }

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
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void getPoster() {

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Poster> call = service.getPosterById(id);
        call.enqueue(new retrofit2.Callback<Poster>() {
            @Override
            public void onResponse(Call<Poster> call, Response<Poster> response) {
                if (response.isSuccessful()) {
                    if (response.body().getType().equals("serie")) {
                        Intent in = new Intent(LoadActivity.this, SerieActivity.class);
                        in.putExtra("poster", response.body());
                        in.putExtra("from", "true");
                        startActivity(in);
                        finish();
                    }
                    if (response.body().getType().equals("com")) {
                        Intent in = new Intent(LoadActivity.this, MovieActivity.class);
                        in.putExtra("poster", response.body());
                        in.putExtra("from", "true");
                        startActivity(in);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Poster> call, Throwable t) {

            }
        });
    }

    public void getChannel() {

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Channel> call = service.geChannelById(id);
        call.enqueue(new retrofit2.Callback<Channel>() {
            @Override
            public void onResponse(Call<Channel> call, Response<Channel> response) {
                if (response.isSuccessful()) {
                    Intent in = new Intent(LoadActivity.this, ChannelActivity.class);
                    in.putExtra("channel", response.body());
                    in.putExtra("from", "true");
                    startActivity(in);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Channel> call, Throwable t) {

            }
        });
    }

}

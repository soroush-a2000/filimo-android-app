package com.movieboxtv.app.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.pushpole.sdk.PushPole;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.Utils.Utils;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.services.ToastMsg;

import com.movieboxtv.app.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {


    private ProgressDialog register_progress;
    private TextView text_view_btn_login;
    private TextView text_view_btn_register;
    private TextView text_view_activity_login_support , ghavanin;

    private LinearLayout linear_layout_activity_login_form_login;
    private LinearLayout linear_layout_activity_login_form_register;

    private EditText edit_text_phone_number_login_acitivty;
    private EditText edit_text_password_login_acitivty;
    private EditText edit_text_phone_number_register_acitivty;
    private EditText edit_text_password_confirm_login_acitivty_register;
    private EditText edit_text_password_login_acitivty_register;
    private EditText edit_text_phone_number_login_acitivty_for_reset_password;

    private LinearLayout linear_layout_activity_login_top_btn_register;
    private LinearLayout linear_layout_activity_login_top_btn_login;
    private PinView otp_edit_text_login_activity;
    private LinearLayout linear_layout_otp_confirm_login_activity;
    private LinearLayout linear_layout_progress_login_activity;
    private TextView text_view_show_timer;
    private LinearLayout linear_layout_box_timer;
    private TextView text_view_activity_login_reset_pass_btn;
    private LinearLayout linear_layout_activity_login_form_reset_password;
    private TextView text_view_btn_send_again_code;
    private TextView text_view_btn_change_phone_number;
    private LinearLayout linear_layout_activity_login_form_register_pasword;
    private TextView text_view_btn_confirm_code;
    private TextView text_view_btn_register_complete;
    private TextView text_view_btn_reset_password;

    private String phoneNum = "";
    private PrefManager prf;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        prf = new PrefManager(getApplicationContext());

        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        initView();
        initAction();
    }


    public void initView() {

        this.linear_layout_activity_login_top_btn_register = (LinearLayout) findViewById(R.id.linear_layout_activity_login_top_btn_register);
        this.linear_layout_activity_login_top_btn_login = (LinearLayout) findViewById(R.id.linear_layout_activity_login_top_btn_login);
        this.text_view_btn_login = (TextView) findViewById(R.id.text_view_btn_login);
        this.text_view_activity_login_support = (TextView) findViewById(R.id.text_view_activity_login_support);
        this.ghavanin = (TextView) findViewById(R.id.ghavanin);
        this.text_view_btn_register = (TextView) findViewById(R.id.text_view_btn_register);

        this.edit_text_phone_number_login_acitivty = (EditText) findViewById(R.id.edit_text_phone_number_login_acitivty);
        this.edit_text_phone_number_login_acitivty_for_reset_password = (EditText) findViewById(R.id.edit_text_phone_number_login_acitivty_for_reset_password);
        this.edit_text_phone_number_register_acitivty = (EditText) findViewById(R.id.edit_text_phone_number_register_acitivty);
        this.edit_text_password_confirm_login_acitivty_register = (EditText) findViewById(R.id.edit_text_password_confirm_login_acitivty_register);
        this.edit_text_password_login_acitivty_register = (EditText) findViewById(R.id.edit_text_password_login_acitivty_register);
        this.edit_text_password_login_acitivty = (EditText) findViewById(R.id.edit_text_password_login_acitivty);

        this.linear_layout_activity_login_form_login = (LinearLayout) findViewById(R.id.linear_layout_activity_login_form_login);
        this.linear_layout_activity_login_form_register = (LinearLayout) findViewById(R.id.linear_layout_activity_login_form_register);
        this.linear_layout_activity_login_form_register_pasword = (LinearLayout) findViewById(R.id.linear_layout_activity_login_form_register_pasword);
        this.otp_edit_text_login_activity = (PinView) findViewById(R.id.otp_edit_text_login_activity);
        this.linear_layout_otp_confirm_login_activity = (LinearLayout) findViewById(R.id.linear_layout_otp_confirm_login_activity);
        this.linear_layout_progress_login_activity = (LinearLayout) findViewById(R.id.linear_layout_progress_login_activity);
        this.text_view_activity_login_reset_pass_btn = (TextView) findViewById(R.id.text_view_activity_login_reset_pass_btn);
        this.linear_layout_activity_login_form_reset_password = (LinearLayout) findViewById(R.id.linear_layout_activity_login_form_reset_password);
        this.text_view_show_timer = (TextView) findViewById(R.id.text_view_show_timer);
        this.linear_layout_box_timer = (LinearLayout) findViewById(R.id.linear_layout_box_timer);
        this.text_view_btn_change_phone_number = (TextView) findViewById(R.id.text_view_btn_change_phone_number);
        this.text_view_btn_send_again_code = (TextView) findViewById(R.id.text_view_btn_send_again_code);

        this.text_view_btn_confirm_code = (TextView) findViewById(R.id.text_view_btn_confirm_code);
        this.text_view_btn_register_complete = (TextView) findViewById(R.id.text_view_btn_register_complete);
        this.text_view_btn_reset_password = (TextView) findViewById(R.id.text_view_btn_reset_password);


        ImageView image_View_bg_top = (ImageView) findViewById(R.id.image_View_bg_top);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 790.0f, 0.0f, 0.0f);
        animation.setDuration(16000);
        animation.setRepeatCount(10);
        animation.setRepeatMode(2);
        //animation.setFillAfter(true);
        image_View_bg_top.startAnimation(animation);


    }

    public void initAction() {


        linear_layout_activity_login_top_btn_register.setOnClickListener(v -> {

            if (linear_layout_activity_login_form_register.getVisibility() == View.VISIBLE) {
                return;
            }
            Utils.hideSoftKeyboard(this);
            showLoading(true);
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emptyEditTexts();
                            linear_layout_activity_login_top_btn_login.setBackgroundResource(0);
                            linear_layout_activity_login_top_btn_register.setBackground(getResources().getDrawable(R.drawable.bg_btn_register));
                            linear_layout_activity_login_form_login.setVisibility(View.GONE);
                            linear_layout_activity_login_form_reset_password.setVisibility(View.GONE);
                            linear_layout_activity_login_form_register.setVisibility(View.VISIBLE);
                            showLoading(false);

                        }
                    });
                }
            }, 2000);

        });

        linear_layout_activity_login_top_btn_login.setOnClickListener(v -> {
            if (linear_layout_activity_login_form_login.getVisibility() == View.VISIBLE) {
                return;
            }
            Utils.hideSoftKeyboard(this);
            showLoading(true);
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            emptyEditTexts();
                            linear_layout_activity_login_top_btn_register.setBackgroundResource(0);
                            linear_layout_activity_login_top_btn_login.setBackground(getResources().getDrawable(R.drawable.bg_btn_login));
                            linear_layout_activity_login_form_register.setVisibility(View.GONE);
                            linear_layout_activity_login_form_reset_password.setVisibility(View.GONE);
                            linear_layout_activity_login_form_register_pasword.setVisibility(View.GONE);
                            linear_layout_activity_login_form_login.setVisibility(View.VISIBLE);
                            showLoading(false);
                        }
                    });
                }
            }, 2000);
        });
        text_view_activity_login_reset_pass_btn.setOnClickListener(v -> {
            Utils.hideSoftKeyboard(this);
            showLoading(true);
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            emptyEditTexts();
                            linear_layout_activity_login_form_reset_password.setVisibility(View.VISIBLE);
                            linear_layout_activity_login_form_register.setVisibility(View.GONE);
                            linear_layout_activity_login_form_login.setVisibility(View.GONE);
                            linear_layout_activity_login_top_btn_register.setBackgroundResource(0);
                            linear_layout_activity_login_top_btn_login.setBackgroundResource(0);

                            showLoading(false);
                        }
                    });
                }
            }, 1500);

        });


        text_view_btn_login.setOnClickListener(v -> {
            if (edit_text_phone_number_login_acitivty.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "لطفا شماره موبایل خود را وارد نمایید!", Toast.LENGTH_SHORT).show();
            }
            if (edit_text_password_login_acitivty.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "لطفا رمز عبور خود را وارد نمایید!", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = PushPole.getId(getApplicationContext());
            login(edit_text_phone_number_login_acitivty.getText().toString(), edit_text_password_login_acitivty.getText().toString(), token);

        });

        text_view_btn_reset_password.setOnClickListener(v -> {
            if (edit_text_phone_number_login_acitivty_for_reset_password.getText().toString().isEmpty()) {
                new ToastMsg(getApplicationContext()).toastIconError("لطفا شماره موبایل را وارد نمایید");
                return;
            }
            resetPassword(edit_text_phone_number_login_acitivty_for_reset_password.getText().toString());

        });

        text_view_activity_login_support.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });

        ghavanin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });



        text_view_btn_register.setOnClickListener(v -> {
            phoneNum = edit_text_phone_number_register_acitivty.getText().toString();
            if (phoneNum.length() == 0) {
                Toast.makeText(LoginActivity.this, "لطفا شماره موبایل خود را وارد نمایید!", Toast.LENGTH_SHORT).show();
                return;
            } else if (phoneNum.length() < 8) {
                Toast.makeText(LoginActivity.this, "لطفا شماره موبایل وارد شده را برسی نمایید!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phoneNum.length() > 9) {
                final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_meat);
                if (checkBox.isChecked()) {
                    new AlertDialog.Builder(this)
                            .setTitle("ارسال کد تایید به شماره : ")
                            .setMessage(" \n" + phoneNum + " \n\n آیا شماره موبایل وارد شده را تایید میکنید یا می خواهید آن را ویرایش کنید؟")
                            .setPositiveButton("تایید",
                                    (dialog, which) -> {
                                        Utils.hideSoftKeyboard(this);
                                        verifyCodeSend(phoneNum);
                                    })
                            .setNegativeButton("ویرایش",
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                    }).show();
                }else {

                    Toast.makeText(LoginActivity.this, "لطفا قوانین و مقررات را بپذیرید!", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

        text_view_btn_send_again_code.setOnClickListener(v -> {

            verifyCodeSend(phoneNum);
        });

        text_view_btn_change_phone_number.setOnClickListener(v -> {
            linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
            linear_layout_activity_login_form_register.setVisibility(View.VISIBLE);

        });

        text_view_btn_confirm_code.setOnClickListener(v -> {
            if (Objects.requireNonNull(otp_edit_text_login_activity.getText()).toString().length() >= 5) {
                verifyCodeCheck(Objects.requireNonNull(otp_edit_text_login_activity.getText()).toString());
            } else {
                if (otp_edit_text_login_activity.getText().toString().isEmpty()) {

                    new ToastMsg(getApplicationContext()).toastIconError("کد را وارد نمایید");
                } else {
                    new ToastMsg(getApplicationContext()).toastIconError("کد 5 رقمی را کامل وارد نمایید!");
                }
            }
        });

        text_view_btn_register_complete.setOnClickListener(v -> {
            if (edit_text_password_confirm_login_acitivty_register.getText().toString().trim().length() < 6 || edit_text_password_login_acitivty_register.getText().toString().trim().length() < 6){

                new ToastMsg(getApplicationContext()).toastIconError("رمز عبور وارد شده کوتاه است!");
                return;
            }
            if (!edit_text_password_confirm_login_acitivty_register.getText().toString().trim().equals(edit_text_password_login_acitivty_register.getText().toString().trim())) {
                new ToastMsg(getApplicationContext()).toastIconError("رمز های عبور وارد شده مطابقت ندارند!");
                return;
            }
            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
            String token = PushPole.getId(getApplicationContext());
            signUp(phoneNum, edit_text_password_confirm_login_acitivty_register.getText().toString(), token, photo);
        });
    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            linear_layout_box_timer.setVisibility(View.GONE);
            text_view_btn_send_again_code.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;
            text_view_show_timer.setText("00:" + String.valueOf(time));
        }
    }

    public void emptyEditTexts() {
        edit_text_phone_number_login_acitivty.setText("");
        edit_text_password_login_acitivty.setText("");
        edit_text_phone_number_register_acitivty.setText("");
        edit_text_password_confirm_login_acitivty_register.setText("");
        edit_text_password_login_acitivty_register.setText("");
        edit_text_phone_number_login_acitivty_for_reset_password.setText("");
    }

    public void showLoading(Boolean status) {
        if (status) {
            linear_layout_progress_login_activity.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            linear_layout_progress_login_activity.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public void verifyCodeSend(String phone) {
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.operation_loading), true);
        register_progress.setCancelable(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.sendCode(phone);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {
                        register_progress.dismiss();

                        new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());

                        CountDownTimer cDownTimer = new MyCountDownTimer(60000, 1000);
                        cDownTimer.start();
                        text_view_btn_send_again_code.setVisibility(View.GONE);
                        linear_layout_box_timer.setVisibility(View.VISIBLE);
                        linear_layout_activity_login_form_register.setVisibility(View.GONE);
                        linear_layout_otp_confirm_login_activity.setVisibility(View.VISIBLE);

                    } else {
                        register_progress.dismiss();
                        new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));
            }
        });
    }

    public void resetPassword(String mobile) {
        Utils.hideSoftKeyboard(this);
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.operation_loading), true);
        register_progress.setCancelable(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.resetPassword(mobile);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {
                        register_progress.dismiss();

                        new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                        emptyEditTexts();
                        linear_layout_activity_login_form_login.setVisibility(View.VISIBLE);
                        linear_layout_activity_login_form_reset_password.setVisibility(View.GONE);
                        linear_layout_activity_login_top_btn_register.setBackgroundResource(0);
                        linear_layout_activity_login_top_btn_login.setBackground(getResources().getDrawable(R.drawable.bg_btn_login));
                        linear_layout_activity_login_form_register.setVisibility(View.GONE);
                        linear_layout_activity_login_form_reset_password.setVisibility(View.GONE);
                        linear_layout_activity_login_form_register_pasword.setVisibility(View.GONE);
                        linear_layout_activity_login_form_login.setVisibility(View.VISIBLE);

                    } else {
                        register_progress.dismiss();
                        new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));
            }
        });

    }

    public void verifyCodeCheck(String code) {
        Utils.hideSoftKeyboard(this);
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.operation_loading), true);
        register_progress.setCancelable(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.verifyCheckCode(code);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {
                        register_progress.dismiss();
                        Toasty.info(LoginActivity.this, "لطفا رمز عبوری را برای حساب خود تعیین کنید", Toast.LENGTH_SHORT).show();
                        linear_layout_activity_login_form_register_pasword.setVisibility(View.VISIBLE);
                        linear_layout_activity_login_form_register.setVisibility(View.GONE);
                        linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);

                    } else {
                        register_progress.dismiss();
                        new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));
            }
        });

    }

    public void login(String mobile, String password, String token) {
        showLoading(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.login(mobile, password, token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {

                        String id_user = "0";
                        String name_user = "x";
                        String username_user = "x";
                        String salt_user = "0";
                        String token_user = "0";
                        String type_user = "x";
                        String image_user = "x";
                        String enabled = "x";

                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if (response.body().getValues().get(i).getName().equals("salt")) {
                                salt_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")) {
                                token_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")) {
                                id_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")) {
                                name_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")) {
                                type_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")) {
                                username_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")) {
                                image_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")) {
                                enabled = response.body().getValues().get(i).getValue();
                            }
                        }
                        if (enabled.equals("true")) {
                            PrefManager prf = new PrefManager(getApplicationContext());
                            prf.setString("ID_USER", id_user);
                            prf.setString("SALT_USER", salt_user);
                            prf.setString("TOKEN_USER", token_user);
                            prf.setString("NAME_USER", name_user);
                            prf.setString("TYPE_USER", type_user);
                            prf.setString("USERN_USER", username_user);
                            prf.setString("IMAGE_USER", image_user);
                            prf.setString("LOGGED", "TRUE");

                            new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        } else {

                            new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.account_disabled));
                        }
                    }
                    if (response.body().getCode() == 500) {

                        new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                    }
                } else {
                    new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_progressـcancelled));
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));
                showLoading(false);
            }
        });
    }

    public void signUp(String username, String password, String token, String image) {
        Utils.hideSoftKeyboard(this);
        showLoading(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(username, password, token, image);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {

                        String id_user = "0";
                        String name_user = "x";
                        String username_user = "x";
                        String salt_user = "0";
                        String token_user = "0";
                        String type_user = "x";
                        String image_user = "x";
                        String enabled = "x";
                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if (response.body().getValues().get(i).getName().equals("salt")) {
                                salt_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")) {
                                token_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")) {
                                id_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")) {
                                name_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")) {
                                type_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")) {
                                username_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")) {
                                image_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")) {
                                enabled = response.body().getValues().get(i).getValue();
                            }
                        }
                        if (enabled.equals("true")) {
                            PrefManager prf = new PrefManager(getApplicationContext());
                            prf.setString("ID_USER", id_user);
                            prf.setString("SALT_USER", salt_user);
                            prf.setString("TOKEN_USER", token_user);
                            prf.setString("NAME_USER", name_user);
                            prf.setString("TYPE_USER", type_user);
                            prf.setString("USERN_USER", username_user);
                            prf.setString("IMAGE_USER", image_user);
                            prf.setString("LOGGED", "TRUE");

                            new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                            finish();


                        } else {
                            new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.account_disabled));
                        }
                    }
                    if (response.body().getCode() == 500) {
                        new ToastMsg(getApplicationContext()).toastIconError(response.body().getMessage());
                    }
                } else {
                    new ToastMsg(getApplicationContext()).toastIconError(getString(R.string.operation_progressـcancelled));
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                new ToastMsg(getApplicationContext()).toastIconError(getString(R.string.operation_no_internet));
                showLoading(false);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse);
        return;
    }

}


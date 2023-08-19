package com.movieboxtv.app.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.ApiResponse;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import com.movieboxtv.app.services.ToastMsg;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CBZarinPalActivity extends AppCompatActivity {

    public ProgressDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("در حال دریافت اطلاعات...");
        dialog.show();

        if (getIntent().getData() != null) {
            ZarinPal.getPurchase(this).verificationPayment(getIntent().getData(), new OnCallbackVerificationPaymentListener() {
                @Override
                public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
                    if (isPaymentSuccess) {
                        purchaseFinished();
                    } else {
                        new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.purchase_failed));
                        PrefManager prf = new PrefManager(getApplicationContext());
                        prf.remove("SUBSCRIBED_DAYS_PURCHASE");
                        finish();
                    }
                }
            });
        }


    }

    public void purchaseFinished() {
        PrefManager prf = new PrefManager(getApplicationContext());
        String id_user = prf.getString("ID_USER");
        String key_user = prf.getString("TOKEN_USER");
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.buySubscribe(id_user, key_user, prf.getString("SUBSCRIBED_DAYS_PURCHASE"), prf.getString("SUBSCRIBED_EXTENSION_PURCHASE"),"0");
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {

                        if (response.body().getValues().get(0).getName().equals("SUBSCRIBED")) {
                            if (response.body().getValues().get(0).getValue() != null)
                                prf.setString("SUBSCRIBED", response.body().getValues().get(0).getValue());
                        }
                        if (response.body().getValues().get(1).getName().equals("SUBSCRIBED_DAYS")) {
                            if (response.body().getValues().get(1).getValue() != null)
                                prf.setString("SUBSCRIBED_DAYS", response.body().getValues().get(1).getValue());
                        }


                        new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                        Intent intent = new Intent(CBZarinPalActivity.this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                        new ToastMsg(getApplicationContext()).toastIconError("با عرض پوزش مشکلی پیش آمد");
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}

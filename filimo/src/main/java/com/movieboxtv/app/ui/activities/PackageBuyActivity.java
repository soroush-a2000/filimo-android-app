package com.movieboxtv.app.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ir.cafebazaar.poolakey.Connection;
import ir.cafebazaar.poolakey.ConnectionState;
import ir.cafebazaar.poolakey.callback.ConnectionCallback;
import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.Utils.Utils;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.billing.IabHelper;
import com.movieboxtv.app.billing.IabResult;
import com.movieboxtv.app.billing.Inventory;
import com.movieboxtv.app.billing.Purchase;
import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.entity.Package;
import com.movieboxtv.app.services.ToastMsg;
import com.movieboxtv.app.ui.Adapters.PackageAdapter;
import com.movieboxtv.app.ui.views.RecyclerItemClickListener;
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.cafebazaar.poolakey.Payment;
import ir.cafebazaar.poolakey.callback.PurchaseCallback;
import ir.cafebazaar.poolakey.callback.PurchaseIntentCallback;
import ir.cafebazaar.poolakey.config.PaymentConfiguration;
import ir.cafebazaar.poolakey.config.SecurityCheck;
import ir.cafebazaar.poolakey.entity.PurchaseInfo;
import ir.cafebazaar.poolakey.request.PurchaseRequest;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PackageBuyActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe_refresh_layout_list_package;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_package;
    private TextView text_View_empty_list;
    private ImageView imageView_back;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout linear_layout_gone_if_extension_then;
    private PackageAdapter adapter;
    private TextView text_title_subscribe;
    private TextView text_view_later_buy;
    private TextView text_view_if_extension_then;
    private TextView text_view_activity_package_top_title;
    private PaymentConfiguration paymentConfiguration;
    private IabHelper iabHelper;
    private ProgressDialog loading_progress;
    private Connection paymentConnection;

    private Payment payment;
    static int RC_REQUEST = 1001;

    ArrayList<Package> packageArrayList = new ArrayList<>();
    private LinearLayout linear_layout_content_package_buy;

    private String from;
    private String type_form;

    private String sku;
    private String days;
    private PrefManager prf;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_buy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        loading_progress = new ProgressDialog(this);

        prf = new PrefManager(getApplicationContext());

        getIntentData();
        initView();
        initAction();
        loadPackeges();

        paymentConfiguration = new PaymentConfiguration(new SecurityCheck.Enable(Config.RSK_KEY), true);
        payment = new Payment(getApplicationContext(), paymentConfiguration);
        startPaymentConnection();

    }

    private void getIntentData() {
        type_form = getIntent().getStringExtra("type_form");
        from = getIntent().getStringExtra("from");
    }


    private void initView() {
        this.swipe_refresh_layout_list_package = findViewById(R.id.swipe_refresh_layout_list_package);
        button_try_again = findViewById(R.id.button_try_again);
        text_view_later_buy = findViewById(R.id.text_view_later_buy);
        text_title_subscribe = findViewById(R.id.text_title_subscribe);
        text_view_if_extension_then = findViewById(R.id.text_view_if_extension_then);
        linear_layout_gone_if_extension_then = findViewById(R.id.linear_layout_gone_if_extension_then);
        text_view_activity_package_top_title = findViewById(R.id.text_view_activity_package_top_title);
        linear_layout_content_package_buy = findViewById(R.id.linear_layout_content_package_buy);
        text_View_empty_list = findViewById(R.id.text_View_empty_list);
        imageView_back = findViewById(R.id.imageView_back);
        linear_layout_layout_error = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_package = findViewById(R.id.recycler_view_activity_package);
        adapter = new PackageAdapter(packageArrayList, this);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler_view_activity_package.setHasFixedSize(true);
        recycler_view_activity_package.setAdapter(adapter);
        recycler_view_activity_package.setLayoutManager(linearLayoutManager);


        if (type_form.equals("play")) {
            text_view_activity_package_top_title.setText(getString(R.string.premium_title_play));
            text_view_activity_package_top_title.setVisibility(View.VISIBLE);
            prf.setString("SUBSCRIBED_EXTENSION_PURCHASE", "0");
        } else if (type_form.equals("download")) {
            text_view_activity_package_top_title.setText(getString(R.string.premium_title_download));
            text_view_activity_package_top_title.setVisibility(View.VISIBLE);
            prf.setString("SUBSCRIBED_EXTENSION_PURCHASE", "0");
        } else if (type_form.equals("extension")) {
            prf.setString("SUBSCRIBED_EXTENSION_PURCHASE", "1");
            text_title_subscribe.setText(getString(R.string.premium_title_extension));
            linear_layout_gone_if_extension_then.setVisibility(View.GONE);
            text_view_if_extension_then.setText("مدت" + " " + "( " + prf.getString("SUBSCRIBED_DAYS") + " )" + " " + "روز از اشتراک شما باقیمانده و با خرید اشتراک جدید تعداد روز های پکیج خریداری شده به روز های فعلی شما افزوده میشود");
        } else {
            prf.setString("SUBSCRIBED_EXTENSION_PURCHASE", "0");
            text_view_activity_package_top_title.setVisibility(View.GONE);
        }
    }


    private void loadPackeges() {
        swipe_refresh_layout_list_package.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Package>> call = service.getPackageList();
        call.enqueue(new Callback<List<Package>>() {
            @Override
            public void onResponse(Call<List<Package>> call, final Response<List<Package>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            packageArrayList.add(response.body().get(i));
                        }
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_package.setVisibility(View.VISIBLE);
                        text_View_empty_list.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();

                    } else {
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_package.setVisibility(View.GONE);
                        linear_layout_content_package_buy.setVisibility(View.GONE);
                        text_View_empty_list.setVisibility(View.VISIBLE);

                    }
                } else {
                    linear_layout_layout_error.setVisibility(View.VISIBLE);
                    recycler_view_activity_package.setVisibility(View.GONE);
                    linear_layout_content_package_buy.setVisibility(View.GONE);
                    text_View_empty_list.setVisibility(View.GONE);
                }
                swipe_refresh_layout_list_package.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Package>> call, Throwable t) {
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_package.setVisibility(View.GONE);
                linear_layout_content_package_buy.setVisibility(View.GONE);
                text_View_empty_list.setVisibility(View.GONE);
                swipe_refresh_layout_list_package.setRefreshing(false);

                // swipe_refresh_layout_list_package.setVisibility(View.GONE);

            }
        });
    }

    private void startPaymentConnection() {

        paymentConnection = payment.connect(new Function1<ConnectionCallback, Unit>() {
            @Override
            public Unit invoke(ConnectionCallback connectionCallback) {
                connectionCallback.connectionSucceed(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        //Toast.makeText(PackageBuyActivity.this, "شد", Toast.LENGTH_LONG).show();

                        return null;
                    }
                });

                connectionCallback.connectionFailed(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        //Toast.makeText(PackageBuyActivity.this, "نشد", Toast.LENGTH_LONG).show();
                        return null;
                    }
                });

                connectionCallback.disconnected(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        //Toast.makeText(PackageBuyActivity.this, "خطا", Toast.LENGTH_LONG).show();
                        return null;
                    }
                });


                return null;
            }
        });

    }

    private void initAction() {
        swipe_refresh_layout_list_package.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                packageArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPackeges();
            }
        });

        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packageArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPackeges();
            }
        });

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        text_view_later_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recycler_view_activity_package.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        if (Config.PAYMENT_MOD == 1) {
                            if (Utils.isPackageInstalled("com.farsitel.bazaar", getPackageManager())) {
                                //purchaseSubscription();
                                sku = packageArrayList.get(position).getSKUKey();
                                days = String.valueOf(packageArrayList.get(position).getDays());

                                if (paymentConnection.getState() == ConnectionState.Connected.INSTANCE) {
                                    purchaseProduct(
                                            sku,
                                            1000,
                                            "",
                                            ""
                                    );
                                }

                            } else {
                                Toast.makeText(PackageBuyActivity.this, "لطفا بازار را نصب کنید", Toast.LENGTH_LONG).show();
                            }
                        } else if (Config.PAYMENT_MOD == 2) {
                            if (Utils.isPackageInstalled("ir.mservices.market", getPackageManager())) {
                                purchaseSubscription();
                                sku = packageArrayList.get(position).getSKUKey();
                                days = String.valueOf(packageArrayList.get(position).getDays());
                            } else {
                                Toast.makeText(PackageBuyActivity.this, "لطفا مایکت را نصب کنید", Toast.LENGTH_LONG).show();
                            }
                        } else if (Config.PAYMENT_MOD == 3) {
                            if (prf.getString("PAYMENT_GATEWAY").equals("pay")) {
                                prf.setString("SUBSCRIBED_DAYS_PURCHASE", String.valueOf(packageArrayList.get(position).getDays()));
                                if (!packageArrayList.get(position).getPriceOff().equals("0")) {
                                    purchaseByPay(packageArrayList.get(position).getPriceOff());
                                } else {
                                    purchaseByPay(packageArrayList.get(position).getPrice());
                                }
                            } else if (prf.getString("PAYMENT_GATEWAY").equals("zarinpal")) {
                                ProgressDialog operating_progress = new ProgressDialog(PackageBuyActivity.this);
                                operating_progress.setMessage("لطفا صبر کنید...");
                                operating_progress.setCancelable(false);
                                operating_progress.show();
                                PaymentRequest payment = ZarinPal.getPaymentRequest();
                                payment.setMerchantID(Config.MERCHANT_CODE);
                                if (!packageArrayList.get(position).getPriceOff().equals("0")) {
                                    payment.setAmount(Long.parseLong(packageArrayList.get(position).getPriceOff()));
                                } else {
                                    payment.setAmount(Long.parseLong(packageArrayList.get(position).getPrice()));
                                }
                                payment.setDescription(Config.PAYMENT_DESCRIPTION);
                                payment.setCallbackURL("flix://zarinpal");
                                payment.setMobile(prf.getString("USERN_USER"));
                                // payment.setEmail("imannamix@gmail.com");
                                //payment.isZarinGateEnable(false);

                                ZarinPal.getPurchase(getApplicationContext()).startPayment(payment, new OnCallbackRequestPaymentListener() {
                                    @Override
                                    public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                                        if (status == 100) {
                                            startActivity(intent);
                                            prf.setString("SUBSCRIBED_DAYS_PURCHASE", String.valueOf(packageArrayList.get(position).getDays()));
                                            operating_progress.dismiss();
                                        } else {
                                            new ToastMsg(getApplicationContext()).toastIconError("خطا در ایجاد درخواست پرداخت");
                                            operating_progress.dismiss();
                                        }

                                    }
                                });
                            } else {
                                ProgressDialog operating_progress = new ProgressDialog(PackageBuyActivity.this);
                                operating_progress.setMessage("لطفا صبر کنید...");
                                operating_progress.setCancelable(false);
                                operating_progress.show();
                                PaymentRequest payment = ZarinPal.getPaymentRequest();
                                payment.setMerchantID(Config.MERCHANT_CODE);
                                if (!packageArrayList.get(position).getPriceOff().equals("0")) {
                                    payment.setAmount(Long.parseLong(packageArrayList.get(position).getPriceOff()));
                                } else {
                                    payment.setAmount(Long.parseLong(packageArrayList.get(position).getPrice()));
                                }
                                payment.setDescription(Config.PAYMENT_DESCRIPTION);
                                payment.setCallbackURL("flix://zarinpal");
                                payment.setMobile(prf.getString("USERN_USER"));
                                // payment.setEmail("imannamix@gmail.com");
                                //payment.isZarinGateEnable(false);


                                ZarinPal.getPurchase(getApplicationContext()).startPayment(payment, new OnCallbackRequestPaymentListener() {
                                    @Override
                                    public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                                        if (status == 100) {
                                            startActivity(intent);
                                            prf.setString("SUBSCRIBED_DAYS_PURCHASE", String.valueOf(packageArrayList.get(position).getDays()));
                                            operating_progress.dismiss();
                                        } else {
                                            new ToastMsg(getApplicationContext()).toastIconError("خطا در ایجاد درخواست پرداخت");
                                            operating_progress.dismiss();
                                        }

                                    }
                                });
                            }

                        }
                    }
                })
        );

    }

    public void purchaseByPay(String amount) {
        ProgressDialog operating_progress = new ProgressDialog(PackageBuyActivity.this);
        operating_progress.setMessage("لطفا صبر کنید...");
        operating_progress.setCancelable(false);
        operating_progress.show();
        String id_user = prf.getString("ID_USER");
        String key_user = prf.getString("TOKEN_USER");
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.buyByPayGateway(id_user, key_user, amount);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {
                        operating_progress.dismiss();

                        if (response.body().getValues().get(0).getName().equals("PAY_URL")) {
                            if (!response.body().getValues().get(0).getValue().equals("empty")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().getValues().get(0).getValue()));
                                startActivity(browserIntent);
                            }
                        }

                    } else {
                        operating_progress.dismiss();
                        new ToastMsg(getApplicationContext()).toastIconError("با عرض پوزش مشکلی پیش آمد");                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                operating_progress.dismiss();
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));
            }
        });

    }

    private void purchaseSubscription() {
        loading_progress.setMessage("لطفا صبر کنید...");
        loading_progress.show();
        iabHelper = new IabHelper(this, Config.RSK_KEY);
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    Toast.makeText(PackageBuyActivity.this, getString(R.string.there_was_a_problem), Toast.LENGTH_LONG).show();
                    return;
                }

                if (iabHelper == null) return;

                iabHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }
    private void purchaseProduct(String productId, int requestCode, String payload, String dynamicPriceToken) {

        payment.purchaseProduct(this, new PurchaseRequest(productId, requestCode, payload, dynamicPriceToken), new Function1<PurchaseIntentCallback, Unit>() {
            @Override
            public Unit invoke(PurchaseIntentCallback purchaseIntentCallback) {

                purchaseIntentCallback.purchaseFlowBegan(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        //Toast.makeText(PackageBuyActivity.this, "خرید آغاز شد", Toast.LENGTH_LONG).show();
                        return null;
                    }
                });

                purchaseIntentCallback.failedToBeginFlow(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Toast.makeText(PackageBuyActivity.this, getString(R.string.there_was_a_problem), Toast.LENGTH_LONG).show();
                        return null;
                    }
                });

                return null;
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        payment.onActivityResult(requestCode, resultCode, data, new Function1<PurchaseCallback, Unit>() {
            @Override
            public Unit invoke(PurchaseCallback purchaseCallback) {
                purchaseCallback.purchaseSucceed(new Function1<PurchaseInfo, Unit>() {
                    @Override
                    public Unit invoke(PurchaseInfo purchaseInfo) {
                        //toast(R.string.general_purchase_succeed_message);
                        //iabHelper.handleActivityResult(requestCode, resultCode, data);
                        purchaseFinished();
                        new ToastMsg(getApplicationContext()).toastIconSuccess("خرید با موفقیت انجام شد");
                        return null;
                    }
                });

                purchaseCallback.purchaseCanceled(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        new ToastMsg(getApplicationContext()).toastIconError("خرید لغو شد.");
                        return null;
                    }
                });

                purchaseCallback.purchaseFailed(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        //toast(R.string.general_purchase_failed_message);
                        new ToastMsg(getApplicationContext()).toastIconError("خرید با خطا مواجه شد.");
                        return null;
                    }
                });
                return null;
            }
        });

    }


    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (iabHelper == null) return;
            if (result.isFailure()) {
                loading_progress.dismiss();
                Toast.makeText(PackageBuyActivity.this, getString(R.string.there_was_a_problem), Toast.LENGTH_LONG).show();
                return;
            }
            loading_progress.dismiss();
            iabHelper.launchPurchaseFlow(PackageBuyActivity.this, sku, RC_REQUEST, mPurchaseFinishedListener);

        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (iabHelper == null) return;
            if (result.isFailure()) {
                Toast.makeText(PackageBuyActivity.this, getString(R.string.purchase_failed), Toast.LENGTH_LONG).show();
                return;
            }
            if (result.isSuccess() && purchase != null) {
                iabHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (iabHelper == null) return;
            if (result.isSuccess() && purchase != null) {
                if (purchase.getSku().equals(sku)) {
                    purchaseFinished();
                }
            } else {
                Toast.makeText(PackageBuyActivity.this, getString(R.string.there_was_a_problem), Toast.LENGTH_LONG).show();
            }
        }
    };

    public void purchaseFinished() {
        ProgressDialog operating_progress = new ProgressDialog(PackageBuyActivity.this);
        operating_progress.setMessage("لطفا صبر کنید...");
        operating_progress.setCancelable(false);
        operating_progress.show();
        String id_user = prf.getString("ID_USER");
        String key_user = prf.getString("TOKEN_USER");
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.buySubscribe(id_user, key_user, days, prf.getString("SUBSCRIBED_EXTENSION_PURCHASE"), "0");
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {
                        operating_progress.dismiss();

                        if (response.body().getValues().get(0).getName().equals("SUBSCRIBED")) {
                            if (response.body().getValues().get(0).getValue() != null)
                                prf.setString("SUBSCRIBED", response.body().getValues().get(0).getValue());
                        }
                        if (response.body().getValues().get(1).getName().equals("SUBSCRIBED_DAYS")) {
                            if (response.body().getValues().get(1).getValue() != null)
                                prf.setString("SUBSCRIBED_DAYS", response.body().getValues().get(1).getValue());
                        }

                        new ToastMsg(getApplicationContext()).toastIconSuccess(response.body().getMessage());
                        Intent intent = new Intent(PackageBuyActivity.this, SplashActivity.class);// New activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        operating_progress.dismiss();
                        new ToastMsg(getApplicationContext()).toastIconError("با عرض پوزش مشکلی پیش آمد");

                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                operating_progress.dismiss();
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.operation_no_internet));            }
        });

    }

// @Override
// protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//     if (requestCode == RC_REQUEST) {
//         iabHelper.handleActivityResult(requestCode, resultCode, data);
//     } else {
//         super.onActivityResult(requestCode, resultCode, data);
//     }
// }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iabHelper != null)
            iabHelper.dispose();
        iabHelper = null;
    }


    @Override
    public void onBackPressed() {
        if (from != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
        return;
    }


}

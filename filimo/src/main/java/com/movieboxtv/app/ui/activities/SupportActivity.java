package com.movieboxtv.app.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.movieboxtv.app.services.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.ApiResponse;

import com.movieboxtv.app.R;

public class SupportActivity extends AppCompatActivity {

    private TextInputEditText support_input_email;
    private TextInputEditText support_input_message;
    private TextInputEditText support_input_name;
    private TextInputLayout support_input_layout_email;
    private TextInputLayout support_input_layout_message;
    private TextInputLayout support_input_layout_name;
    private RelativeLayout relative_layout_support_activity_send;
    private ProgressDialog register_progress;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    String[] web = Config.SUPPORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_dropdown_item_1line,web);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                support_input_name.setText(web[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("پشتیبانی");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initAction();
    }

    public void initView(){
        this.support_input_email=(TextInputEditText) findViewById(R.id.support_input_email);
        this.support_input_message=(TextInputEditText) findViewById(R.id.support_input_message);
        this.support_input_name=(TextInputEditText) findViewById(R.id.support_input_name);
        this.support_input_layout_email=(TextInputLayout) findViewById(R.id.support_input_layout_email);
        this.support_input_layout_message=(TextInputLayout) findViewById(R.id.support_input_layout_message);
        this.support_input_layout_name=(TextInputLayout) findViewById(R.id.support_input_layout_name);
        this.relative_layout_support_activity_send=(RelativeLayout) findViewById(R.id.relative_layout_support_activity_send);
    }

    public void initAction(){
        this.support_input_email.addTextChangedListener(new SupportTextWatcher(this.support_input_email));
        this.support_input_name.addTextChangedListener(new SupportTextWatcher(this.support_input_name));
        this.support_input_message.addTextChangedListener(new SupportTextWatcher(this.support_input_message));
        this.relative_layout_support_activity_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void submit(){
        if (!validateEmail()) {
            return;
        }
        if (!validatName()) {
            return;
        }
        if (!validatMessage()) {
            return;
        }
        register_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.addSupport(support_input_email.getText().toString(),support_input_name.getText().toString(),support_input_message.getText().toString().replace("\n","<br>"));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    new ToastMsg(getApplicationContext()).toastIconSuccess(getResources().getString(R.string.message_sended));
                    finish();
                }else{
                    new ToastMsg(getApplicationContext()).toastIconError(getString(R.string.error_server));
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.error_server));
            }
        });
    }


    private boolean validatName() {
        if (support_input_name.getText().toString().trim().isEmpty() || support_input_name.getText().length()  < 3 ) {
            support_input_layout_name.setError(getString(R.string.error_short_value));
            requestFocus(support_input_name);
            return false;
        } else {
            support_input_layout_name.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatMessage() {
        if (support_input_message.getText().toString().trim().isEmpty() || support_input_message.getText().length()  < 3 ) {
            support_input_layout_message.setError(getString(R.string.error_short_value));
            requestFocus(support_input_message);
            return false;
        } else {
            support_input_layout_message.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = support_input_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            support_input_layout_email.setError(getString(R.string.error_mail_valide));
            requestFocus(support_input_email);
            return false;
        } else {
            support_input_layout_email.setErrorEnabled(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    private class SupportTextWatcher implements TextWatcher {
        private View view;
        private SupportTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            int viewId = view.getId();
            if (viewId == R.id.support_input_email) {
                validateEmail();
            } else if (viewId == R.id.support_input_name) {
                validatName();
            } else if (viewId == R.id.support_input_message) {
                validatMessage();
            }
        }
    }
}

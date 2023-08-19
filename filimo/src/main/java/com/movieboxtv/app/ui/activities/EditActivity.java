package com.movieboxtv.app.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.api.ProgressRequestBody;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.entity.ApiResponse;
import com.movieboxtv.app.services.ToastMsg;

import com.movieboxtv.app.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks {

    private PrefManager prf;
    private CircleImageView image_view_edit_activity_user_profile;
    private TextView text_view_edit_activity_name_user;
    private ImageView image_view_edit_activity_name_edit_photo;
    private RelativeLayout relative_layout_edit_activity_save;
    private RelativeLayout relative_layout_edit_activity_change_image;

    private TextInputLayout text_input_layout_activity_edit_name;
    private TextInputEditText text_input_editor_text_activity_edit_name;

    private TextInputLayout text_input_layout_activity_edit_old_pass;
    private TextInputEditText text_input_editor_text_activity_edit_old_pass;
    private TextInputLayout text_input_layout_activity_edit_new_pass;
    private TextInputEditText text_input_editor_text_activity_edit_new_pass;
    private TextInputLayout text_input_layout_activity_edit_new_confirm_pass;
    private TextInputEditText text_input_editor_text_activity_edit_new_confirm_pass;
    private TextInputEditText text_view_edit_activity_status_subscribe;

    private RelativeLayout relative_layout_edit_activity_change_pass;
    private ProgressDialog login_progress;

    private int id;
    private String name;
    private String image;
    private int PICK_IMAGE = 1557;
    private String imageUrl;
    private ProgressDialog pd;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("پروفایل");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        this.id = bundle.getInt("id");
        this.name = bundle.getString("name");
        this.image = bundle.getString("image");
        this.prf = new PrefManager(getApplicationContext());
        initView();
        initAction();
        setUser();
    }

    private boolean validatName() {
        if (text_input_editor_text_activity_edit_name.getText().toString().trim().isEmpty() || text_input_editor_text_activity_edit_name.getText().length() < 3) {
            text_input_layout_activity_edit_name.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_edit_name);
            return false;
        } else {
            text_input_layout_activity_edit_name.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePassword(EditText et, TextInputLayout tIL) {
        if (et.getText().toString().trim().isEmpty() || et.getText().length()  < 6 ) {
            tIL.setError("رمز عبور کوتاه است!");
            requestFocus(et);
            return false;
        } else {
            tIL.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePasswordConfrom() {
        if (!text_input_editor_text_activity_edit_new_pass.getText().toString().equals(text_input_editor_text_activity_edit_new_confirm_pass.getText().toString())) {
            text_input_editor_text_activity_edit_new_confirm_pass.setError("رمز های عبور جدید مطابقت ندارند");
            requestFocus(text_input_editor_text_activity_edit_new_confirm_pass);
            return false;
        } else {
            text_input_layout_activity_edit_new_confirm_pass.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void initView() {



        this.image_view_edit_activity_user_profile = (CircleImageView) findViewById(R.id.image_view_edit_activity_user_profile);
        this.image_view_edit_activity_name_edit_photo = (ImageView) findViewById(R.id.image_view_edit_activity_name_edit_photo);
        this.relative_layout_edit_activity_change_image = (RelativeLayout) findViewById(R.id.relative_layout_edit_activity_change_image);
        this.text_view_edit_activity_name_user = (TextView) findViewById(R.id.text_view_edit_activity_name_user);
        this.relative_layout_edit_activity_save = (RelativeLayout) findViewById(R.id.relative_layout_edit_activity_save);
        this.text_input_editor_text_activity_edit_name = (TextInputEditText) findViewById(R.id.text_input_editor_text_activity_edit_name);
        this.text_input_layout_activity_edit_name = (TextInputLayout) findViewById(R.id.text_input_layout_activity_edit_name);
        this.text_input_layout_activity_edit_old_pass = (TextInputLayout) findViewById(R.id.text_input_layout_activity_edit_old_pass);
        this.text_input_editor_text_activity_edit_old_pass = (TextInputEditText) findViewById(R.id.text_input_editor_text_activity_edit_old_pass);
        this.text_input_layout_activity_edit_new_pass = (TextInputLayout) findViewById(R.id.text_input_layout_activity_edit_new_pass);
        this.text_input_editor_text_activity_edit_new_pass = (TextInputEditText) findViewById(R.id.text_input_editor_text_activity_edit_new_pass);
        this.text_input_layout_activity_edit_new_confirm_pass = (TextInputLayout) findViewById(R.id.text_input_layout_activity_edit_new_confirm_pass);
        this.text_input_editor_text_activity_edit_new_confirm_pass = (TextInputEditText) findViewById(R.id.text_input_editor_text_activity_edit_new_confirm_pass);
        this.relative_layout_edit_activity_change_pass = (RelativeLayout) findViewById(R.id.relative_layout_edit_activity_change_pass);

        if (Config.IMAGE_PROFILE_EDIT) {
            this.relative_layout_edit_activity_change_image.setVisibility(View.VISIBLE);
        } else {
            this.relative_layout_edit_activity_change_image.setVisibility(View.GONE);
        }
        if (Config.NAME_PROFILE_EDIT) {
            this.text_input_layout_activity_edit_name.setVisibility(View.VISIBLE);
        } else {
            this.text_input_layout_activity_edit_name.setVisibility(View.GONE);
        }

        TextView text_view_edit_activity_status_subscribe =(TextView) findViewById(R.id.text_view_edit_activity_status_subscribe);
        if (prf.getString("SUBSCRIBED").equals("TRUE")) {
            text_view_edit_activity_status_subscribe.setText("اشتراک باقیمانده: " + " " + prf.getString("SUBSCRIBED_DAYS") + " " + "روز");
            text_view_edit_activity_status_subscribe.setVisibility(View.VISIBLE);
            text_view_edit_activity_status_subscribe.setTextColor(Color.parseColor("#47e280"));
        } else {
            if (prf.getString("SUBSCRIBED_DAYS").equals("FINISH")) {
                text_view_edit_activity_status_subscribe.setTextColor(Color.parseColor("#FF5E5E"));
                text_view_edit_activity_status_subscribe.setText("اشتراک شما به پایان رسیده شما میتونید دوباره اشتراک تهیه نمایید");
                text_view_edit_activity_status_subscribe.setVisibility(View.VISIBLE);
            }else{
                text_view_edit_activity_status_subscribe.setVisibility(View.GONE);
            }
        }

        RelativeLayout relative_layout_btn_edit_top = (RelativeLayout) findViewById(R.id.relative_layout_btn_edit_top);
        if(!Config.IMAGE_PROFILE_EDIT && !Config.NAME_PROFILE_EDIT){
            relative_layout_btn_edit_top.setVisibility(View.GONE);
        }

        pd = new ProgressDialog(EditActivity.this);
        pd.setMessage("بروزرسانی اطلاعات ...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
    }

    private void initAction() {
        this.relative_layout_edit_activity_save.setOnClickListener(v -> {
            submit();
        });
        this.relative_layout_edit_activity_change_pass.setOnClickListener(v -> {
            changePassword();
        });

        this.image_view_edit_activity_name_edit_photo.setOnClickListener(v -> {
            SelectImage();
        });
    }
    private void changePassword() {
        if (!validatePassword(text_input_editor_text_activity_edit_new_pass,text_input_layout_activity_edit_new_pass))
            return;
        if (!validatePassword(text_input_editor_text_activity_edit_new_confirm_pass,text_input_layout_activity_edit_new_confirm_pass))
            return;
        if (!validatePasswordConfrom())
            return;

        changePasswordSubmit();
    }

    private void submit() {
        if (!validatName())
            return;
        edit();
    }

    private void setUser() {
        this.text_input_editor_text_activity_edit_name.setText(name);
        this.text_view_edit_activity_name_user.setText(name);
        Picasso.with(this)
                .load(image)
                .error(R.drawable.placeholder_profile)
                .placeholder(R.drawable.placeholder_profile)
                .into(image_view_edit_activity_user_profile);
    }

    private void SelectImage() {
        if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            startActivityForResult(i, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage();
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {


            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            imageUrl = picturePath;

            File file = new File(imageUrl);
            Picasso.with(this)
                    .load(file)
                    .error(R.drawable.placeholder_profile)
                    .placeholder(R.drawable.placeholder_profile)
                    .into(image_view_edit_activity_user_profile);
        } else {

            Log.i("SonaSys", "resultCode: " + resultCode);
            switch (resultCode) {
                case 0:
                    Log.i("SonaSys", "User cancelled");
                    break;
                case -1:
                    break;
            }
        }
    }

    private void changePasswordSubmit() {

        login_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));

        PrefManager prf= new PrefManager(getApplicationContext());
        String id_ser=  prf.getString("ID_USER");

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.changePassword(id_ser,text_input_editor_text_activity_edit_old_pass.getText().toString(),text_input_editor_text_activity_edit_new_pass.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200){
                        String salt_user="0";
                        String token_user="0";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }

                        }
                        PrefManager prf= new PrefManager(getApplicationContext());

                        prf.setString("SALT_USER",salt_user);
                        prf.setString("TOKEN_USER",token_user);

                        new ToastMsg(getApplicationContext()).toastIconSuccess(message);
                        finish();
                        login_progress.dismiss();
                    } else if (code == 500) {
                        text_input_layout_activity_edit_old_pass.setError(response.body().getMessage().toString());
                        requestFocus(text_input_editor_text_activity_edit_old_pass);
                        login_progress.dismiss();
                    }
                }else{
                    new ToastMsg(getApplicationContext()).toastIconError(getString(R.string.operation_no_internet));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                new ToastMsg(getApplicationContext()).toastIconError(getString(R.string.operation_no_internet));
                login_progress.dismiss();

            }
        });
    }

    public void edit() {
        pd.show();

        PrefManager prf = new PrefManager(getApplicationContext());

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        MultipartBody.Part body = null;
        if (imageUrl != null) {
            File file1 = new File(imageUrl);
            int file_size = Integer.parseInt(String.valueOf(file1.length() / 1024 / 1024));
            if (file_size > 20) {

                new ToastMsg(getApplicationContext()).toastIconError("حداکثر اندازه پرونده 20M باید باشد");
            }
            Log.v("SIZE", file1.getName() + "");


            final File file = new File(imageUrl);


            ProgressRequestBody requestFile = new ProgressRequestBody(file, EditActivity.this);

            body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
        }
        String id_ser = prf.getString("ID_USER");
        String key_ser = prf.getString("TOKEN_USER");

        Call<ApiResponse> request = service.editProfile(body, Integer.parseInt(id_ser), key_ser, text_input_editor_text_activity_edit_name.getText().toString().trim());

        request.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful()) {
                    new ToastMsg(getApplicationContext()).toastIconSuccess(getResources().getString(R.string.infos_updated_successfully));
                    for (int i = 0; i < response.body().getValues().size(); i++) {
                        if (response.body().getValues().get(i).getName().equals("name")) {
                            String Newname = response.body().getValues().get(i).getValue();
                            if (Newname != null) {
                                if (!Newname.isEmpty()) {
                                    prf.setString("NAME_USER", Newname);

                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("url")) {
                            String NewImage = response.body().getValues().get(i).getValue();
                            if (NewImage != null) {
                                if (!NewImage.isEmpty()) {
                                    prf.setString("IMAGE_USER", NewImage);
                                }
                            }
                        }
                    }
                    finish();
                } else {
                    new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.error_server));
                }
                pd.dismiss();
                pd.cancel();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.error_server));
                pd.dismiss();
                pd.cancel();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse);
        return;
    }
    @Override
    public void onProgressUpdate(int percentage) {
        pd.setProgress(percentage);
    }

    @Override
    public void onError() {
        pd.dismiss();
        pd.cancel();
    }

    @Override
    public void onFinish() {
        pd.dismiss();
        pd.cancel();

    }
}

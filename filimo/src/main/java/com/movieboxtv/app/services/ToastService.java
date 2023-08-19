package com.movieboxtv.app.services;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;


import com.orhanobut.hawk.Hawk;
import com.movieboxtv.app.R;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.DownloadItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
//import wseemann.media.FFmpegMediaMetadataRetriever;

public class ToastService extends IntentService {
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;


    public ToastService() {
        super("Service");
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {


        IntentFilter intentFilter =new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long DownloadedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (getDownoadStatus(DownloadedId) == DownloadManager.STATUS_SUCCESSFUL) {

                    new ToastMsg(getApplicationContext()).toastIconSuccess(getString(R.string.file_has_been_downloaded));
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    final int id =(int) DownloadedId; // [0, 60] + 20 => [20, 80]

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

                        notificationChannel.setSound(null, null);
                        notificationChannel.enableLights(false);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.enableVibration(false);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    DownloadItem downloadItem = null;
                    List<DownloadItem> my_downloads_temp =Hawk.get("my_downloads_temp");
                    if (my_downloads_temp == null) {
                        my_downloads_temp = new ArrayList<>();
                    }

                    for (int i = 0; i < my_downloads_temp.size(); i++) {
                        if (my_downloads_temp.get(i).getDownloadid() == id ) {
                            downloadItem = my_downloads_temp.get(i);
                            my_downloads_temp.remove(my_downloads_temp.get(i));
                            Hawk.put("my_downloads_temp",my_downloads_temp);
                        }
                    }

                    List<DownloadItem> my_downloads_list =Hawk.get("my_downloads_list");
                    if (my_downloads_list == null) {
                        my_downloads_list = new ArrayList<>();
                    }

                    if (downloadItem == null){
                        Log.v("MYDOWNLOADLIST_TOAST","the downloaded  element not  exist on temp list");
                    }else{
                        for (int i = 0; i < my_downloads_list.size(); i++) {
                            if (my_downloads_list.get(i).getId() == downloadItem.getId() ) {

                                String path = my_downloads_list.get(i).getPath();

                                File file = new File(path);
                                if (file.exists()){
                                    file.delete();
                                }

                                my_downloads_list.remove(my_downloads_list.get(i));
                                Hawk.put("my_downloads_list",my_downloads_list);
                            }
                        }
                        File file = new File(downloadItem.getPath());
                        String size = "" ;
                        if (file.exists()) {
                            size = getStringSizeLengthFile(file.length());
                        }



                        downloadItem.setSize(size);


                        my_downloads_list.add(downloadItem);
                        Hawk.put("my_downloads_list",my_downloads_list);

                        if (downloadItem.getType().equals("episode")){
                            addEpisodeDownload(downloadItem.getElement());
                        }
                        if (downloadItem.getType().equals("com")){
                            addMovieDownload(downloadItem.getElement());
                        }

                    }
                    /*
                    notificationBuilder = new NotificationCompat.Builder(ToastService.this, "id");
                    notificationBuilder.setSmallIcon(R.drawable.ic_file_download);
                    notificationManager.cancel(id);
                    notificationBuilder.setOngoing(false);
                    notificationBuilder.setProgress(0, 0, false);
                    notificationBuilder.setContentTitle(getString(R.string.Downloaded));
                    notificationBuilder.setContentText(getResources().getString(R.string.file_has_been_downloaded));
                    notificationManager.notify(id, notificationBuilder.build());
*/

                }
            }
        },intentFilter);
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private int getDownoadStatus(long id){
        DownloadManager.Query  query =  new DownloadManager.Query();
        query.setFilterById(id);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor =  downloadManager.query(query);
        if (cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            return status;
        }
        return DownloadManager.ERROR_UNKNOWN;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }
    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if(size < sizeMb)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }
    public static String formatDuration(final long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "" : hours < 10 ? String.valueOf("0" + hours) :
                String.valueOf(hours));
        if(hours!=0)
            b.append("h");
        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("0" + minutes) :
                String.valueOf(minutes));
        b.append("min");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        b.append("s");

        return b.toString();
    }
    public void  addMovieDownload(Integer id){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.addMovieDownload(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    public void addEpisodeDownload(Integer id){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.addEpisodeDownload(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

    }
}

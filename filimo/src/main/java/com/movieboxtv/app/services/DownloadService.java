package com.movieboxtv.app.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.movieboxtv.app.R;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.crypto.PlaylistDownloader;
import com.movieboxtv.app.entity.DownloadItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.core.app.NotificationCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DownloadService  extends IntentService  implements PlaylistDownloader.DownloadListener {

    private String title ="";
    private String playlistUrl ="";
    private Integer id ;
    private String path;
    private Integer element;
    private String image;
    private String type;
    private boolean downloaded = false;
    private String duration;

    public DownloadService() {
        super("Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        playlistUrl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        type = intent.getStringExtra("type");
        id = intent.getIntExtra("id",0);
        element = intent.getIntExtra("element",0);
        duration = intent.getStringExtra("duration");

        playlistUrl = intent.getStringExtra("url");
        id = intent.getIntExtra("id",0);
        Log.d("MY SERVICE DATA", "url ="+playlistUrl+";id = "+id);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(title)
                .setContentText("Downloading")
                .setDefaults(0)
                .setOngoing(true)
                .setAutoCancel(false);
        notificationManager.notify(id, notificationBuilder.build());

        try {
            PlaylistDownloader downloader =
                    new PlaylistDownloader(playlistUrl,this);
            final int min = 100;
            final int max = 999;
            final int random = new Random().nextInt((max - min) + 1) + min;
            this.path =  Environment.getExternalStorageDirectory()+"/"+getResources().getString(R.string.download_foler)+"/"+ title.replace(" ","_").replaceAll("[^\\.A-Za-z0-9_]", "")+"_"+id+"_"+random+".mp4";
            downloader.download(path);

        } catch (java.io.IOException e) {
            Toast.makeText(this, "آدرس مسیر صحیح نیست", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }



    private void updateNotification(int currentProgress) {


        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("" + currentProgress + "%");
        notificationManager.notify(id, notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete) {
       /* Intent intent = new Intent(MainActivity.PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);*/
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
    private void onDownloadComplete(boolean downloadComplete) {
        sendProgressUpdate(downloadComplete);
        notificationBuilder.setSmallIcon(R.drawable.ic_file_download);
        notificationManager.cancel(id);
        notificationBuilder.setOngoing(false);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("فایل با موفقیت دانلود شد");
        notificationManager.notify(id, notificationBuilder.build());


        DownloadItem downloadItem = new DownloadItem();

        downloadItem.setId(id);
        downloadItem.setElement(element);
        downloadItem.setImage(image);
        downloadItem.setPath(path);
        downloadItem.setType(type);
        downloadItem.setTitle(title);


        Log.v("MYDOWNLOADLIST_TOAST", downloadItem.getPath());
        File file = new File(downloadItem.getPath());

        String size = "" ;
        if (file.exists()) {
            size = getStringSizeLengthFile(file.length());
        }



        downloadItem.setDuration(duration);
        downloadItem.setSize(size);


        List<DownloadItem> my_downloads_list =Hawk.get("my_downloads_list");
        if (my_downloads_list == null) {
            my_downloads_list = new ArrayList<>();
        }


        if (downloadItem != null){
            for (int i = 0; i < my_downloads_list.size(); i++) {
                if (my_downloads_list.get(i).getId() == downloadItem.getId()) {
                    String path = my_downloads_list.get(i).getPath();
                    File file1 = new File(path);
                    if (file1.exists()) {
                        file1.delete();
                    }
                    my_downloads_list.remove(my_downloads_list.get(i));
                    Hawk.put("my_downloads_list", my_downloads_list);
                }
            }
        }
        my_downloads_list.add(downloadItem);
        Hawk.put("my_downloads_list", my_downloads_list);

        if (type.equals("episode")){
            addEpisodeDownload(element);
        }
        if (type.equals("com")){
            addMovieDownload(element);
        }
        this.stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(id);
    }

    @Override
    public void onProgressUpdate(int progress) {
        updateNotification(progress);
    }

    @Override
    public void onStartDownload(String url) {

    }

    @Override
    public void OnDownloadCompleted() {
        downloaded = true;
        onDownloadComplete(true);
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

    @Override
    public void onDestroy() {
        if (downloaded){
            new ToastMsg(getApplicationContext()).toastIconSuccess(getResources().getString(R.string.file_has_been_downloaded));
        }
        super.onDestroy();

    }

}

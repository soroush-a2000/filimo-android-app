package com.movieboxtv.app.ui.Adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.movieboxtv.app.R;
import com.movieboxtv.app.Utils.Log;
import com.movieboxtv.app.entity.DownloadItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadedAdapter extends   RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final DownloadListener downloadListener;
    private List<DownloadItem> downloadItemList;
    private Activity activity;
    public DownloadedAdapter(List<DownloadItem> downloadItemList, Activity activity,DownloadListener downloadListener) {
        this.downloadItemList = downloadItemList;
        this.activity = activity;
        this.downloadListener = downloadListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_downloaded,null);
                viewHolder = new DownloadedAdapter.DownloadedHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new DownloadedAdapter.EmptyHolder(v2);
                break;
            }

        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 1:
                DownloadedHolder downloadedHolder =  (DownloadedHolder) holder;
                downloadedHolder.text_view_item_download_title.setText(downloadItemList.get(position).getTitle());
                Picasso.with(activity).load(downloadItemList.get(position).getImage()).into(downloadedHolder.image_view_item_download_image);
                downloadedHolder.text_view_item_download_duration.setText(downloadItemList.get(position).getDuration());
                downloadedHolder.text_view_item_download_size.setText(downloadItemList.get(position).getSize());
                Log.log(downloadItemList.get(position).getId()+"");
                downloadedHolder.image_view_item_download_delete.setOnClickListener(v->{
                    List<DownloadItem> my_downloads_list =Hawk.get("my_downloads_list");
                    if (my_downloads_list == null) {
                        my_downloads_list = new ArrayList<>();
                    }
                    for (int i = 0; i < my_downloads_list.size(); i++) {
                        if (my_downloads_list.get(i).getId().equals(downloadItemList.get(position).getId()) ) {
                            String path = downloadItemList.get(position).getPath();

                            my_downloads_list.remove(my_downloads_list.get(i));
                            Hawk.put("my_downloads_list",my_downloads_list);
                            File file = new File(path);
                            if (file.exists()){
                                Log.log( "EXISTR");
                                try {
                                    Uri imageUri = FileProvider.getUriForFile(activity,
                                            activity.getApplicationContext()
                                                    .getPackageName() + ".provider", file);
                                    ContentResolver contentResolver = activity.getContentResolver();
                                    int deletefile = contentResolver.delete(imageUri, null, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                file.delete();
                            }
                        }
                    }
                    downloadListener.OnUpdated();

                });
                downloadedHolder.image_view_item_download_finder.setOnClickListener(v->{

                    String path = downloadItemList.get(position).getPath();

                    Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));

                    Intent intent = new Intent();
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("file/*");
                    intent.setData(imageUri);
                    activity.startActivity(intent);
                });
                downloadedHolder.relative_layout_item_download.setOnClickListener(v->{
                    downloadListener.OnPlay(downloadItemList.get(position));
                });
                downloadedHolder.image_view_item_download_play.setOnClickListener(v->{
                    downloadListener.OnPlay(downloadItemList.get(position));

                });
                break;
            case 2:

                break;
        }
    }

    @Override
    public int getItemCount() {

        return downloadItemList.size();
    }

    public class DownloadedHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout relative_layout_item_download;
        private final ImageView image_view_item_download_delete;
        private final ImageView image_view_item_download_play;
        private final ImageView image_view_item_download_image;
        private final ImageView image_view_item_download_finder;
        private final TextView text_view_item_download_title;
        private final TextView text_view_item_download_size;
        private final TextView text_view_item_download_duration;

        public DownloadedHolder(@NonNull View itemView) {
            super(itemView);
            this.relative_layout_item_download=(RelativeLayout) itemView.findViewById(R.id.relative_layout_item_download);
            this.image_view_item_download_delete=(ImageView) itemView.findViewById(R.id.image_view_item_download_delete);
            this.image_view_item_download_image=(ImageView) itemView.findViewById(R.id.image_view_item_download_image);
            this.image_view_item_download_play=(ImageView) itemView.findViewById(R.id.image_view_item_download_play);
            this.image_view_item_download_finder=(ImageView) itemView.findViewById(R.id.image_view_item_download_finder);
            this.text_view_item_download_duration=(TextView) itemView.findViewById(R.id.text_view_item_download_duration);
            this.text_view_item_download_size=(TextView) itemView.findViewById(R.id.text_view_item_download_size);
            this.text_view_item_download_title=(TextView) itemView.findViewById(R.id.text_view_item_download_title);
        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return downloadItemList.get(position).getTypeView();
    }
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
    public interface DownloadListener{
        void OnUpdated();
        void OnPlay(DownloadItem downloadItem);
    }
}

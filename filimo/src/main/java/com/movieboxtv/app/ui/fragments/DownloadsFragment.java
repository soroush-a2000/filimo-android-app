package com.movieboxtv.app.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orhanobut.hawk.Hawk;
import com.movieboxtv.app.Provider.AndroidWebServer;
import com.movieboxtv.app.R;
import com.movieboxtv.app.entity.DownloadItem;
import com.movieboxtv.app.ui.Adapters.DownloadedAdapter;
import com.movieboxtv.app.ui.activities.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadsFragment extends Fragment  implements DownloadedAdapter.DownloadListener {

    private static final int DEFAULT_PORT = 8589;

    // INSTANCE OF ANDROID WEB SERVER
    private AndroidWebServer androidWebServer;
    private BroadcastReceiver broadcastReceiverNetworkState;
    private static boolean isStarted = false;

    private View view;
    private SwipeRefreshLayout swipe_refresh_layout_downloads_fragment;
    private LinearLayout linear_layout_load_downloads_fragment;
    private ImageView image_view_empty_list;
    private RecyclerView recycler_view_downloads_fragment;
    private List<DownloadItem> downloadItemArrayList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private DownloadedAdapter downloadedAdapter;
    private DownloadItem downloadItem;

    public DownloadsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_downloads, container, false);
        initView();
        initAction();
        loadDownloadsList();
        initBroadcastReceiverNetworkStateChanged();
        return view;
    }

    private void loadDownloadsList() {
        downloadItemArrayList.clear();
        linear_layout_load_downloads_fragment.setVisibility(View.VISIBLE);
        recycler_view_downloads_fragment.setVisibility(View.GONE);
        image_view_empty_list.setVisibility(View.GONE);
        List<DownloadItem> my_downloads_list =Hawk.get("my_downloads_list");
        if (my_downloads_list == null) {
            my_downloads_list = new ArrayList<>();
        }
        downloadItemArrayList.add(new DownloadItem().setTypeView(2));
        for (int i = 0; i < my_downloads_list.size(); i++) {
            downloadItemArrayList.add(my_downloads_list.get(i));
        }
        if (my_downloads_list.size()==0){
            linear_layout_load_downloads_fragment.setVisibility(View.GONE);
            recycler_view_downloads_fragment.setVisibility(View.GONE);
            image_view_empty_list.setVisibility(View.VISIBLE);
        }else{
            linear_layout_load_downloads_fragment.setVisibility(View.GONE);
            recycler_view_downloads_fragment.setVisibility(View.VISIBLE);
            image_view_empty_list.setVisibility(View.GONE);
        }
        downloadedAdapter.notifyDataSetChanged();
    }

    private void initAction() {
            swipe_refresh_layout_downloads_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadDownloadsList();
                    swipe_refresh_layout_downloads_fragment.setRefreshing(false);
                }
            });
    }

    private void initView() {
        this.swipe_refresh_layout_downloads_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_downloads_fragment);
        this.linear_layout_load_downloads_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_downloads_fragment);
        this.image_view_empty_list = (ImageView) view.findViewById(R.id.image_view_empty_list);
        this.recycler_view_downloads_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_downloads_fragment);

        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,RecyclerView.VERTICAL,false);


        this.downloadedAdapter =new DownloadedAdapter(downloadItemArrayList,getActivity(),this);
        recycler_view_downloads_fragment.setHasFixedSize(true);
        recycler_view_downloads_fragment.setAdapter(downloadedAdapter);
        recycler_view_downloads_fragment.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void OnUpdated() {
        loadDownloadsList();
    }
    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":";
    }

    private int getPortFromEditText() {
        return  DEFAULT_PORT;
    }

    public boolean isConnectedInWifi() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        NetworkInfo networkInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }

    private boolean startAndroidWebServer(String url) {
        if (!isStarted) {
            int port = getPortFromEditText();
            try {
                if (port == 0) {
                    throw new Exception();
                }
                androidWebServer = new AndroidWebServer(port,url);
                androidWebServer.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean stopAndroidWebServer() {
        if (isStarted && androidWebServer != null) {
            androidWebServer.stop();
            return true;
        }
        return false;
    }
    private void initBroadcastReceiverNetworkStateChanged() {
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        broadcastReceiverNetworkState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        getActivity().registerReceiver(broadcastReceiverNetworkState, filters);
    }
    @Override
    public void OnPlay(DownloadItem downloadItem) {
        this.downloadItem =downloadItem;

        if (isConnectedInWifi()) {
            if (!isStarted && startAndroidWebServer(downloadItem.getPath())) {
                String type = "video/mp4";
                if (downloadItem.getType().equals("mov"))
                    type = "video/quicktime";
                Intent intent1 = new Intent(getActivity(), PlayerActivity.class);
                intent1.putExtra("id",downloadItem.getElement());
                intent1.putExtra("url",getIpAccess()+getPortFromEditText());
                intent1.putExtra("type",type);
                intent1.putExtra("kind",downloadItem.getType());
                intent1.putExtra("image",downloadItem.getImage());
                intent1.putExtra("title",downloadItem.getTitle());
                intent1.putExtra("subtitle",downloadItem.getTitle());
                getActivity().startActivity(intent1);
                isStarted = true;
            } else if (stopAndroidWebServer()) {
                isStarted = false;
            }
        } else {
            String type = "video/mp4";
            if (downloadItem.getType().equals("mov"))
                type = "video/quicktime";
            Intent intent = new Intent(getActivity(),PlayerActivity.class);
            intent.putExtra("id",downloadItem.getElement());
            intent.putExtra("url",downloadItem.getPath());
            intent.putExtra("type",type);
            intent.putExtra("kind",downloadItem.getType());
            intent.putExtra("image",downloadItem.getImage());
            intent.putExtra("title",downloadItem.getTitle());
            intent.putExtra("subtitle",downloadItem.getTitle());
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        stopAndroidWebServer();
        isStarted = false;
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiverNetworkState);

        super.onDestroy();
    }
}

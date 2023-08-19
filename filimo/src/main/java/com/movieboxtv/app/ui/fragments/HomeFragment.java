package com.movieboxtv.app.ui.fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.movieboxtv.app.ui.Adapters.HomeAdapter;
import com.movieboxtv.app.ui.Adapters.HomeAdapter2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.orhanobut.hawk.Hawk;
import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.R;
import com.movieboxtv.app.api.apiClient;
import com.movieboxtv.app.api.apiRest;
import com.movieboxtv.app.entity.Data;
import com.movieboxtv.app.entity.Genre;
import com.movieboxtv.app.entity.Poster;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment {


    private View view;
    private SwipeRefreshLayout swipe_refresh_layout_home_fragment;
    private LinearLayout linear_layout_load_home_fragment;
    private LinearLayout linear_layout_page_error_home_fragment;
    private RecyclerView recycler_view_home_fragment;
    private RelativeLayout relative_layout_load_more_home_fragment;
    private HomeAdapter homeAdapter;
    private HomeAdapter2 homeAdapter2;


    private Genre my_genre_list;
    private List<Data> dataList=new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private Button button_try_again;


    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;
    private Integer item = 0 ;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view=  inflater.inflate(R.layout.fragment_home, container, false);
        prefManager= new PrefManager(getApplicationContext());

        initViews();
        initActions();
        loadMyList();
        loadData();
        return view;
    }

    private void loadData() {

        showLoadingView();
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Data> call = service.homeData();
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()){
                    dataList.clear();
                    dataList.add(new Data().setViewType(0));
                    if (response.body().getSlides().size()>0){
                        Data sliodeData =  new Data();
                        sliodeData.setSlides(response.body().getSlides());
                        dataList.add(sliodeData);
                    }
                    if (response.body().getChannels().size()>0){
                       Data channelData = new Data();
                       channelData.setChannels(response.body().getChannels());
                        dataList.add(channelData);
                    }
                    if (response.body().getActors().size()>0){
                        Data actorsData = new Data();
                        actorsData.setActors(response.body().getActors());
                        dataList.add(actorsData);
                    }
                    if (response.body().getCountries().size()>0){
                        Data countriesData = new Data();
                        countriesData.setCountries(response.body().getCountries());
                        dataList.add(countriesData);
                    }
                    if (response.body().getGenres().size()>0){
                        if (my_genre_list!=null){
                            Data genreDataMyList = new Data();
                            genreDataMyList.setGenre(my_genre_list);
                            dataList.add(genreDataMyList);
                        }
                        for (int i = 0; i < response.body().getGenres().size(); i++) {
                            if(response.body().getGenres().get(i).getPosters().size()>0) {

                                Data genreData = new Data();
                                genreData.setGenre(response.body().getGenres().get(i));
                                dataList.add(genreData);

                                if (native_ads_enabled) {
                                    item++;
                                    if (item == lines_beetween_ads) {
                                        item = 0;
                                        if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                            dataList.add(new Data().setViewType(6));
                                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                            dataList.add(new Data().setViewType(7));
                                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                            if (type_ads == 0) {
                                                dataList.add(new Data().setViewType(6));
                                                type_ads = 1;
                                            } else if (type_ads == 1) {
                                                dataList.add(new Data().setViewType(7));
                                                type_ads = 0;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    showListView();
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        homeAdapter2.notifyDataSetChanged();
                    }else {

                        homeAdapter.notifyDataSetChanged();
                    }
                }else{
                    showErrorView();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                showErrorView();
            }
        });
    }
   private void showLoadingView(){
       linear_layout_load_home_fragment.setVisibility(View.VISIBLE);
       linear_layout_page_error_home_fragment.setVisibility(View.GONE);
       recycler_view_home_fragment.setVisibility(View.GONE);
   }
    private void showListView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.GONE);
        recycler_view_home_fragment.setVisibility(View.VISIBLE);
    }
    private void showErrorView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.VISIBLE);
        recycler_view_home_fragment.setVisibility(View.GONE);
    }
    private void initActions() {
        swipe_refresh_layout_home_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMyList();
                loadData();
                swipe_refresh_layout_home_fragment.setRefreshing(false);
            }
        });
        button_try_again.setOnClickListener(v->{
            loadMyList();
            loadData();
        });
    }
    public boolean checkSUBSCRIBED(){
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }
    private void initViews() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")){
            native_ads_enabled=true;
            if (tabletSize) {
                lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }else{
                lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled=false;
        }
        this.swipe_refresh_layout_home_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        this.linear_layout_load_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_home_fragment);
        this.linear_layout_page_error_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_home_fragment);
        this.recycler_view_home_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_home_fragment);
        this.relative_layout_load_more_home_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_home_fragment);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,RecyclerView.VERTICAL,false);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.homeAdapter2 =new HomeAdapter2(dataList,getActivity());
            recycler_view_home_fragment.setHasFixedSize(true);
            recycler_view_home_fragment.setAdapter(homeAdapter2);
            recycler_view_home_fragment.setLayoutManager(gridLayoutManager);
        }else {

            this.homeAdapter =new HomeAdapter(dataList,getActivity());
            recycler_view_home_fragment.setHasFixedSize(true);
            recycler_view_home_fragment.setAdapter(homeAdapter);
            recycler_view_home_fragment.setLayoutManager(gridLayoutManager);
        }
    }
    private void loadMyList(){
        List<Poster> my_list  = new ArrayList<>();
        try {
            my_list = Hawk.get("my_list");
            if (my_list.size()!=0) {

            }
        }catch (NullPointerException e){
            my_list  = new ArrayList<>();
        }
        if (my_list.size()>0){
            my_genre_list = new Genre();
            my_genre_list.setId(-3);
            my_genre_list.setTitle("لیست مورد علاقه من");
            my_genre_list.setPosters(my_list);
        }else{
            my_genre_list=null;
        }
    }

    @Override
    public void onResume() {
        if (my_genre_list!=null) {
            List<Poster> my_current_list = Hawk.get("my_list");
            boolean changed = false;
            if (my_current_list.size() != my_genre_list.getPosters().size()) {
                changed = true;
            } else {
                for (int i = 0; i < my_genre_list.getPosters().size(); i++) {
                    if (my_current_list.get(i).getId() != my_genre_list.getPosters().get(i).getId()) {
                        changed = true;
                    }
                }
            }
            if (changed) {
                loadMyList();
                loadData();
            }
        }else{
            List<Poster> my_current_list = Hawk.get("my_list");
            if (my_current_list==null)
                my_current_list  = new ArrayList<>();

            if(my_current_list.size()>0){
                loadMyList();
                loadData();
            }
        }
        super.onResume();
    }
}

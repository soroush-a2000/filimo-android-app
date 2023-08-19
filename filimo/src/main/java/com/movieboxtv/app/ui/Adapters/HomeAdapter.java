package com.movieboxtv.app.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import com.movieboxtv.app.Provider.PrefManager;
import com.movieboxtv.app.Utils.CBR;
import com.movieboxtv.app.entity.Data;
import com.movieboxtv.app.ui.activities.MainActivity;

import com.movieboxtv.app.R;

import com.movieboxtv.app.config.Config;
import com.movieboxtv.app.ui.activities.ActorsActivity;
import com.movieboxtv.app.ui.activities.GenreActivity;
import com.movieboxtv.app.ui.activities.MyListActivity;
import com.movieboxtv.app.ui.activities.TopActivity;
import com.movieboxtv.app.ui.views.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity activity;
    private List<Data> dataList = new ArrayList<>();
    private SlideAdapter slide_adapter;
    private ChannelHomeAdapter channelAdapter;
    private LinearLayoutManager linearLayoutManagerChannelAdapter;
    private ActorAdapter actorAdapter;
    private CountryAdapter countryAdapter;
    private LinearLayoutManager linearLayoutManagerActorAdapter;
    private LinearLayoutManager linearLayoutManagerGenreAdapter;
    private LinearLayoutManager linearLayoutManagerCountryAdapter;
    private PosterHomeAdapter posterAdapter;


    private Context context;
    // private Timer mTimer;
    public HomeAdapter(List<Data> dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View v0 = inflater.inflate(R.layout.item_hometop, parent, false);
                viewHolder = new EmptyHolder(v0);
                break;
            }
            case 1: {
                View v1 = inflater.inflate(R.layout.item_slides, parent, false);
                viewHolder = new SlideHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_channels, parent, false);
                viewHolder = new ChannelHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_actors, parent, false);
                viewHolder = new ActorHolder(v3);
                break;
            }
            case 4: {
                View v4 = inflater.inflate(R.layout.item_countries, parent, false);
                viewHolder = new CountryHolder(v4);
                break;
            }
            case 5: {
                View v5 = inflater.inflate(R.layout.item_genres, parent, false);
                viewHolder = new GenreHolder(v5);
                break;
            }
            case 6: {
                View v6 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                viewHolder = new FacebookNativeHolder(v6);
                break;
            }
           // case 7: {
           //     View v7 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
           //     viewHolder = new AdmobNativeHolder(v7);
           //     break;
           // }
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)) {
            case 1:
                final SlideHolder holder = (SlideHolder) holder_parent;
                slide_adapter = new SlideAdapter(activity, dataList.get(position).getSlides());
                holder.view_pager_slide.setAdapter(slide_adapter);
                holder.view_pager_slide.setOffscreenPageLimit(1);
                holder.view_pager_slide.setClipToPadding(false);
                holder.view_pager_slide.setPageMargin(0);
                if (Config.AUTO_CHANGED_SLIDER.equals("TRUE")) {
                    holder.view_pager_slide.startAutoScroll();
                } else {
                    holder.view_pager_slide.stopAutoScroll();
                }
                holder.view_pager_slide.setInterval(Integer.parseInt(Config.SLIDER_CHANGE_TIME + "000"));
                holder.view_pager_slide.setCycle(true);
                holder.view_pager_slide.setStopScrollWhenTouch(true);

                holder.view_pager_indicator.setupWithViewPager(holder.view_pager_slide);
                // holder.view_pager_slide.setCurrentItem(dataList.get(position).getSlides().size() / 2); //for set center slide
                holder.view_pager_slide.setCurrentItem(dataList.get(position).getSlides().size() - 1);
                slide_adapter.notifyDataSetChanged();
                break;
            case 2:
                final ChannelHolder holder_channel = (ChannelHolder) holder_parent;
                this.linearLayoutManagerChannelAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.channelAdapter = new ChannelHomeAdapter(dataList.get(position).getChannels(), activity);
                holder_channel.recycle_view_channels_item.setHasFixedSize(true);
                holder_channel.recycle_view_channels_item.setAdapter(channelAdapter);
                holder_channel.recycle_view_channels_item.setLayoutManager(linearLayoutManagerChannelAdapter);
                channelAdapter.notifyDataSetChanged();
                holder_channel.item_home_more_container.setOnClickListener(v -> {
                    ((MainActivity) activity).goToTV();
                });
                break;
            case 3:
                final ActorHolder holder_actor = (ActorHolder) holder_parent;
                this.linearLayoutManagerActorAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.actorAdapter = new ActorAdapter(dataList.get(position).getActors(), activity);
                holder_actor.recycle_view_actors_item_actors.setHasFixedSize(true);
                holder_actor.recycle_view_actors_item_actors.setAdapter(actorAdapter);
                holder_actor.recycle_view_actors_item_actors.setLayoutManager(linearLayoutManagerActorAdapter);
                actorAdapter.notifyDataSetChanged();
                holder_actor.item_home_more_actors_container.setOnClickListener(v -> {
                    Intent intent = new Intent(activity.getApplicationContext(), ActorsActivity.class);
                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                });
                break;
            case 4:
                final CountryHolder holder_country = (CountryHolder) holder_parent;
                this.linearLayoutManagerCountryAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.countryAdapter = new CountryAdapter(dataList.get(position).getCountries(), activity);
                holder_country.recycle_view_countries_item_countries.setHasFixedSize(true);
                holder_country.recycle_view_countries_item_countries.setAdapter(countryAdapter);
                holder_country.recycle_view_countries_item_countries.setLayoutManager(linearLayoutManagerCountryAdapter);
                countryAdapter.notifyDataSetChanged();
/*                holder_country.item_home_more_countries_container.setOnClickListener(v -> {
                    Intent intent = new Intent(activity.getApplicationContext(), CountryActivity.class);
                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                });*/
                break;
            case 5:
                final GenreHolder holder_genres = (GenreHolder) holder_parent;
                holder_genres.text_view_item_genre_title.setText(dataList.get(position).getGenre().getTitle());
                holder_genres.item_home_more_genre_container.setOnClickListener(v -> {
                    if (dataList.get(position).getGenre().getId() == -1) {
                        Intent intent = new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "rating");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    } else if (dataList.get(position).getGenre().getId() == -2) {
                        Intent intent = new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "created");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    } else if (dataList.get(position).getGenre().getId() == 0) {
                        Intent intent = new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "views");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    } else if (dataList.get(position).getGenre().getId() == -3) {
                        Intent intent = new Intent(activity.getApplicationContext(), MyListActivity.class);
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    } else {
                        Intent intent = new Intent(activity.getApplicationContext(), GenreActivity.class);
                        intent.putExtra("genre", dataList.get(position).getGenre());
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }


                });
                this.linearLayoutManagerGenreAdapter = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                if (dataList.get(position).getGenre().getId() == -3)
                    this.posterAdapter = new PosterHomeAdapter(dataList.get(position).getGenre().getPosters(), activity, true, new CBR() {
                        @Override
                        public void refreshAdapter() {
                            dataList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                else
                    this.posterAdapter = new PosterHomeAdapter(dataList.get(position).getGenre().getPosters(), activity);

                holder_genres.recycle_view_posters_item_genre.setHasFixedSize(true);
                holder_genres.recycle_view_posters_item_genre.setAdapter(posterAdapter);
                holder_genres.recycle_view_posters_item_genre.setLayoutManager(linearLayoutManagerGenreAdapter);
                posterAdapter.notifyDataSetChanged();

                break;
          // case 7: {
          //     final AdmobNativeHolder holder_admob = (AdmobNativeHolder) holder_parent;

          //     holder_admob.adLoader.loadAd(new AdRequest.Builder().build());

          //     break;
          // }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (dataList.get(position).getSlides() != null) {
            type = 1;
        }
        if (dataList.get(position).getChannels() != null) {
            type = 2;
        }
        if (dataList.get(position).getActors() != null) {
            type = 3;
        }
        if (dataList.get(position).getCountries() != null) {
            type = 4;
        }
        if (dataList.get(position).getGenre() != null) {
            type = 5;
        }
        if (dataList.get(position).getViewType() == 6) {
            type = 6;

        }
      //  if (dataList.get(position).getViewType() == 7) {
      //      type = 7;
//
      //  }
        return type;
    }

    private class SlideHolder extends RecyclerView.ViewHolder {

        private final ViewPagerIndicator view_pager_indicator;
        private final AutoScrollViewPager view_pager_slide;

        public SlideHolder(View itemView) {
            super(itemView);
            this.view_pager_indicator = (ViewPagerIndicator) itemView.findViewById(R.id.view_pager_indicator);
            this.view_pager_slide = (AutoScrollViewPager) itemView.findViewById(R.id.view_pager_slide);

        }
    }

    private class ChannelHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_channels_item;
        private final LinearLayout item_home_more_container;

        public ChannelHolder(View itemView) {
            super(itemView);
            this.recycle_view_channels_item = (RecyclerView) itemView.findViewById(R.id.recycle_view_channels_item);
            this.item_home_more_container = (LinearLayout) itemView.findViewById(R.id.item_home_more_container);

        }
    }

    private class ActorHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_actors_item_actors;
        private final LinearLayout item_home_more_actors_container;

        public ActorHolder(View itemView) {
            super(itemView);
            this.recycle_view_actors_item_actors = (RecyclerView) itemView.findViewById(R.id.recycle_view_actors_item_actors);
            this.item_home_more_actors_container = (LinearLayout) itemView.findViewById(R.id.item_home_more_actors_container);
        }
    }

    private class CountryHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_countries_item_countries;
        // private final LinearLayout item_home_more_countries_container;

        public CountryHolder(View itemView) {
            super(itemView);

            this.recycle_view_countries_item_countries = (RecyclerView) itemView.findViewById(R.id.recycle_view_countries_item_countries);
            //this.item_home_more_countries_container = (LinearLayout) itemView.findViewById(R.id.item_home_more_countries_container);
        }
    }

    private class GenreHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_posters_item_genre;
        private final TextView text_view_item_genre_title;
        private final LinearLayout item_home_more_genre_container;

        public GenreHolder(View itemView) {
            super(itemView);
            this.recycle_view_posters_item_genre = (RecyclerView) itemView.findViewById(R.id.recycle_view_posters_item_genre);
            this.text_view_item_genre_title = (TextView) itemView.findViewById(R.id.text_view_item_genre_title);
            this.item_home_more_genre_container = (LinearLayout) itemView.findViewById(R.id.item_home_more_genre_container);
        }
    }

    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }

    public class FacebookNativeHolder extends RecyclerView.ViewHolder {
        private final String TAG = "WALLPAPERADAPTER";
        private LinearLayout nativeAdContainer;
        private LinearLayout adView;
        private NativeAd nativeAd;

        public FacebookNativeHolder(View view) {
            super(view);
            loadNativeAd(view);
        }

        private void loadNativeAd(final View view) {
            PrefManager prefManager = new PrefManager(activity);

            nativeAd = new NativeAd(activity, prefManager.getString("ADMIN_NATIVE_FACEBOOK_ID"));
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                    // Race condition, load() called again before last ad was displayed
                    if (nativeAd == null || nativeAd != ad) {
                        return;
                    }
                   /* NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark))
                            .setTitleTextColor(Color.WHITE)
                            .setDescriptionTextColor(Color.WHITE)
                            .setButtonColor(Color.WHITE);

                    View adView = NativeAdView.render(activity, nativeAd, NativeAdView.Type.HEIGHT_300, viewAttributes);

                    LinearLayout nativeAdContainer = (LinearLayout) view.findViewById(R.id.native_ad_container);
                    nativeAdContainer.addView(adView);*/
                    // Inflate Native Ad into Container
                    inflateAd(nativeAd, view);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d(TAG, "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d(TAG, "Native ad impression logged!");
                }
            });

            // Request an ad
            nativeAd.loadAd();
        }

        private void inflateAd(NativeAd nativeAd, View view) {

            LayoutInflater inflater = LayoutInflater.from(activity);
            nativeAd.unregisterView();

            // Add the Ad view into the ad container.
            nativeAdContainer = view.findViewById(R.id.native_ad_container);
            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
            adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdContainer, false);
            nativeAdContainer.addView(adView);

            // Add the AdChoices icon
            LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
            AdChoicesView adChoicesView = new AdChoicesView(activity, nativeAd, true);
            adChoicesContainer.addView(adChoicesView, 0);

            // Create native UI using the ad metadata.
            AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdTitle.setText(nativeAd.getAdvertiserName());
            nativeAdBody.setText(nativeAd.getAdBodyText());
            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                    adView,
                    nativeAdMedia,
                    nativeAdIcon,
                    clickableViews);
        }

    }

    public class AdmobNativeHolder extends RecyclerView.ViewHolder {
        private final AdLoader adLoader;
        private UnifiedNativeAd nativeAd;
        private FrameLayout frameLayout;

        public AdmobNativeHolder(@NonNull View itemView) {
            super(itemView);

            PrefManager prefManager = new PrefManager(activity);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_adplaceholder);
            AdLoader.Builder builder = new AdLoader.Builder(activity, prefManager.getString("ADMIN_NATIVE_ADMOB_ID"));

            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                // OnUnifiedNativeAdLoadedListener implementation.
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    if (nativeAd != null) {
                        nativeAd.destroy();
                    }
                    nativeAd = unifiedNativeAd;

                    UnifiedNativeAdView adView = (UnifiedNativeAdView) activity.getLayoutInflater()
                            .inflate(R.layout.ad_unified, null);
                    populateUnifiedNativeAdView(unifiedNativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }

            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            this.adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {


                }
            }).build();

        }
    }

    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.ad_media);

        mediaView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        } else {

        }
    }
}

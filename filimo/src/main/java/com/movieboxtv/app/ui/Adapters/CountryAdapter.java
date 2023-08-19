package com.movieboxtv.app.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import com.movieboxtv.app.entity.Country;

import com.movieboxtv.app.R;

import com.movieboxtv.app.ui.activities.CountryActivity;

import java.util.List;

public class CountryAdapter extends  RecyclerView.Adapter<CountryAdapter.CountryHolder>{
        private List<Country> countryList;
        private Activity activity;
        public CountryAdapter(List<Country> countryList, Activity activity) {
            this.countryList = countryList;
            this.activity = activity;
        }
        @Override
        public CountryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country , null);
            CountryHolder mh = new CountryHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(CountryHolder holder, final int position) {
            Picasso.with(activity).load(countryList.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.image_view_item_country);
            holder.text_view_item_country.setText(countryList.get(position).getTitle());

            holder.linear_layout_item_country.setOnClickListener(v->{
               // ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,holder.linear_layout_item_country,"imageMain");
                Intent in = new Intent(activity, CountryActivity.class);
                in.putExtra("country", countryList.get(holder.getAdapterPosition()));
                activity.startActivity(in);
          //      activity.startActivity(in,activityOptionsCompat.toBundle());
            });
        }

        @Override
        public int getItemCount() {
            return countryList.size();
        }

        public class CountryHolder extends RecyclerView.ViewHolder {
            private final TextView text_view_item_country;
            private final ImageView image_view_item_country;
            private final LinearLayout linear_layout_item_country;

            public CountryHolder(View itemView) {
                super(itemView);
                this.image_view_item_country =  (ImageView) itemView.findViewById(R.id.image_view_item_country);
                this.text_view_item_country =  (TextView) itemView.findViewById(R.id.text_view_item_country);
                this.linear_layout_item_country =  (LinearLayout) itemView.findViewById(R.id.linear_layout_item_country);
            }
        }
    }

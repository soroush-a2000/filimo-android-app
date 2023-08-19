package com.movieboxtv.app.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.movieboxtv.app.R;
import com.movieboxtv.app.entity.Actor;
import com.movieboxtv.app.ui.activities.ActorActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ActorAdapter  extends  RecyclerView.Adapter<ActorAdapter.ActorHolder>{
        private List<Actor> actorList;
        private Activity activity;
        public ActorAdapter(List<Actor> actorList, Activity activity) {
            this.actorList = actorList;
            this.activity = activity;
        }
        @Override
        public ActorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor , null);
            ActorHolder mh = new ActorHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(ActorHolder holder, final int position) {
            Picasso.with(activity).load(actorList.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.circle_image_view_item_actor);
            holder.text_view_item_actor_name.setText(actorList.get(position).getName());
            if (actorList.get(position).getRole() != null) {
                holder.text_view_item_actor_cast.setText(actorList.get(position).getRole());
                holder.text_view_item_actor_cast.setVisibility(View.VISIBLE);
            }

            holder.linear_layout_item_actor.setOnClickListener(v->{
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,holder.linear_layout_item_actor,"imageMain");
                Intent in = new Intent(activity, ActorActivity.class);
                in.putExtra("actor", actorList.get(holder.getAdapterPosition()));
                activity.startActivity(in,activityOptionsCompat.toBundle());
            });
        }
        @Override
        public int getItemCount() {
            return actorList.size();
        }
        public class ActorHolder extends RecyclerView.ViewHolder {
            private final TextView text_view_item_actor_name;
            private final TextView text_view_item_actor_cast;
            private final ImageView circle_image_view_item_actor;
            private final LinearLayout linear_layout_item_actor;

            public ActorHolder(View itemView) {
                super(itemView);
                this.circle_image_view_item_actor =  (ImageView) itemView.findViewById(R.id.circle_image_view_item_actor);
                this.text_view_item_actor_cast =  (TextView) itemView.findViewById(R.id.text_view_item_actor_cast);
                this.text_view_item_actor_name =  (TextView) itemView.findViewById(R.id.text_view_item_actor_name);
                this.linear_layout_item_actor =  (LinearLayout) itemView.findViewById(R.id.linear_layout_item_actor);
            }
        }
    }

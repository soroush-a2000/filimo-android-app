package com.movieboxtv.app.ui.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movieboxtv.app.R;
import com.movieboxtv.app.entity.Category;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{
    private List<Category> categoriesList;
    private Activity activity;
    public CategoryAdapter(List<Category> categoriesList, Activity activity) {
        this.categoriesList = categoriesList;
        this.activity = activity;
    }
    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catergory , null);
        CategoryHolder mh = new CategoryHolder(v);
        return mh;
    }
    @Override
    public void onBindViewHolder(CategoryHolder holder, final int position) {
        holder.text_view_item_category_title.setText(categoriesList.get(position).getTitle());
    }
    @Override
    public int getItemCount() {
        return categoriesList.size();
    }
    public class CategoryHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_category_title;
        public CategoryHolder(View itemView) {
            super(itemView);
            this.text_view_item_category_title =  (TextView) itemView.findViewById(R.id.text_view_item_category_title);
        }
    }
}

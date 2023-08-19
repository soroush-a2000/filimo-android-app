package com.movieboxtv.app.ui.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.movieboxtv.app.entity.Package;

import com.movieboxtv.app.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageHolder> {
    private List<Package> packageList;
    private Activity activity;

    public PackageAdapter(List<Package> packageList, Activity activity) {
        this.packageList = packageList;
        this.activity = activity;
    }

    @Override
    public PackageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package, null);
        PackageHolder mh = new PackageHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(PackageHolder holder, final int position) {

        holder.text_view_item_package_title.setText(packageList.get(position).getTitle());

        if (packageList.get(position).getPriceOff().equals("0")) {
            holder.linear_layout_off_package.setVisibility(View.GONE);
            holder.text_view_item_package_price.setText(priceFormat(packageList.get(position).getPrice()));
            holder.image_view_item_pack.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_basket));
            holder.relative_layout_line_on_price.setBackgroundResource(0);
            holder.text_view_item_package_price.setTextColor(activity.getResources().getColor(R.color.green));
        } else {

            holder.text_view_item_package_price.setText(priceFormat(packageList.get(position).getPrice()));
            holder.text_view_item_package_off.setText("%" + priceOffFormat(packageList.get(position).getPrice(), packageList.get(position).getPriceOff()) + " تخفیف ");
            holder.text_view_item_package_price.setTextColor(activity.getResources().getColor(R.color.secondary_text));
            holder.text_view_item_package_price_off.setText(priceFormat(packageList.get(position).getPriceOff()));
            holder.relative_layout_line_on_price.setBackground(activity.getResources().getDrawable(R.drawable.line_on_price));
            holder.image_view_item_pack.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_coupon));
        }

        holder.linear_layout_item_package.setOnClickListener(v -> {

        });
    }

    public static String priceFormat(String number) {

        return String.format(Locale.getDefault(), "%,d", Integer.parseInt(number)).concat(" تومان ");

    }

    public static String priceOffFormat(String price, String price_off) {
        DecimalFormat df = new DecimalFormat("##0");
        float result = (Float.parseFloat(price) - Float.parseFloat(price_off)) / Float.parseFloat(price) * 100;
        return String.valueOf(df.format((Math.round(result * 100.0) / 100.0)));
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public class PackageHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_package_title;
        private final TextView text_view_item_package_price;
        private final TextView text_view_item_package_price_off;
        private final TextView text_view_item_package_off;
        private final LinearLayout linear_layout_item_package;
        private final LinearLayout linear_layout_off_package;
        private final RelativeLayout relative_layout_line_on_price;
        private final ImageView image_view_item_pack;

        public PackageHolder(View itemView) {
            super(itemView);
            this.text_view_item_package_title = (TextView) itemView.findViewById(R.id.text_view_item_package_title);
            this.text_view_item_package_price = (TextView) itemView.findViewById(R.id.text_view_item_package_price);
            this.text_view_item_package_price_off = (TextView) itemView.findViewById(R.id.text_view_item_package_price_off);
            this.text_view_item_package_off = (TextView) itemView.findViewById(R.id.text_view_item_package_off);
            this.linear_layout_item_package = (LinearLayout) itemView.findViewById(R.id.linear_layout_item_package);
            this.linear_layout_off_package = (LinearLayout) itemView.findViewById(R.id.linear_layout_off_package);
            this.relative_layout_line_on_price = (RelativeLayout) itemView.findViewById(R.id.relative_layout_line_on_price);
            this.image_view_item_pack = (ImageView) itemView.findViewById(R.id.image_view_item_pack);


        }
    }
}

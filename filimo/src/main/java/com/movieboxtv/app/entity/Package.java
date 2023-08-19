package com.movieboxtv.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Package implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("days")
    @Expose
    private Integer days;

    @SerializedName("skukey")
    @Expose
    private String sku_key;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("priceoff")
    @Expose
    private String price_off;

    public Package() {
    }

    protected Package(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        days = in.readInt();
        price = in.readString();
        price_off = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeInt(days);
        dest.writeString(price);
        dest.writeString(price_off);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getSKUKey() {
        return sku_key;
    }

    public void setSKUKey(String sku_key) {
        this.sku_key = sku_key;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceOff() {
        return price_off;
    }

    public void setPriceOff(String price_off) {
        this.price_off = price_off;
    }

}

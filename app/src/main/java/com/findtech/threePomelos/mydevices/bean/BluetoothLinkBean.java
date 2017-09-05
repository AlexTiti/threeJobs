package com.findtech.threePomelos.mydevices.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/06
 *   desc     :
 *   version  :   V 1.0.5
 */
public class BluetoothLinkBean implements Parcelable {
    String name;
    int state;
    String adresss;
    String type;
    String deviceIndentifier;
    String company;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setAdresss(String adresss) {
        this.adresss = adresss;
    }

    public String getName() {
        return name;
    }

    public int getState() {
        return state;
    }

    public String getAdresss() {
        return adresss;
    }

    public BluetoothLinkBean(){

    }

    public String getDeviceIndentifier() {
        return deviceIndentifier;
    }

    public void setDeviceIndentifier(String deviceIndentifier) {
        this.deviceIndentifier = deviceIndentifier;
    }

    public BluetoothLinkBean(String name, int state, String adresss) {
        this.name = name;
        this.state = state;
        this.adresss = adresss;
    }

    protected BluetoothLinkBean(Parcel in) {
        name = in.readString();
        state = in.readInt();
        adresss = in.readString();
        type = in.readString();
        deviceIndentifier = in.readString();
    }

    public static final Creator<BluetoothLinkBean> CREATOR = new Creator<BluetoothLinkBean>() {
        @Override
        public BluetoothLinkBean createFromParcel(Parcel in) {
            return new BluetoothLinkBean(in);
        }

        @Override
        public BluetoothLinkBean[] newArray(int size) {
            return new BluetoothLinkBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(state);
        dest.writeString(adresss);
        dest.writeString(type);
        dest.writeString(deviceIndentifier);
    }
}

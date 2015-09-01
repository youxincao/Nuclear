package com.nuclear.monitor.model;

import com.google.api.client.util.Key;

/**
 * Created by weilun on 2015/9/1.
 */
public class DeviceInfo {

    @Key
    private String time;

    @Key
    private boolean alert;

    @Key
    private String der;

    @Key
    private String de;

    @Key
    private String _id;

    @Key
    private int __v;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public String getDer() {
        return der;
    }

    public void setDer(String der) {
        this.der = der;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "time='" + time + '\'' +
                ", alert=" + alert +
                ", der='" + der + '\'' +
                ", de='" + de + '\'' +
                ", _id='" + _id + '\'' +
                ", __v=" + __v +
                '}';
    }
}

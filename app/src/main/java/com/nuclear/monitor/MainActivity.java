package com.nuclear.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nuclear.monitor.model.DeviceInfo;
import com.nuclear.monitor.net.request.DeviceInfoRequest;
import com.nuclear.monitor.net.service.HttpNuclearService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends Activity {


    private static final String[] mDeviceUrls = {"http://182.92.80.116:3000/data/HT000RG10020140101000002",
            "http://182.92.80.116:3000/data/HT000RG10000130137"};
    private static final String TAG = "MainActivity";

    private static final int MSG_TYPE_QUERY_DEVICE_INFOS = 0x1;
    private static final int DEVICEINFO_QUERY_INTERVAL = 5000;

    public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String LOCAL_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final SpiceManager mSpiceManager = new SpiceManager(HttpNuclearService.class);
    private SimpleAdapter mAdapter = null;
    private List<Map<String, Object>> mAdapterData = null;
    private ListView mListView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_TYPE_QUERY_DEVICE_INFOS:
                    requestDevicesInfo();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void requestDevicesInfo() {
        for (int i = 0; i < mDeviceUrls.length; i++) {
            requestDeviceInfoData(mDeviceUrls[i]);
        }
        mHandler.sendEmptyMessageDelayed(MSG_TYPE_QUERY_DEVICE_INFOS, DEVICEINFO_QUERY_INTERVAL);
    }

    @Override
    protected void onStart() {
        mSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler.sendEmptyMessage(MSG_TYPE_QUERY_DEVICE_INFOS);
        initAdapter();
        mListView = (ListView) findViewById(R.id.lv_devices);
        mListView.setAdapter(mAdapter);
    }

    private void initAdapter() {
        mAdapterData = new ArrayList<>();
        mAdapter = new SimpleAdapter(this, mAdapterData, R.layout.lv_item_device,
                new String[]{"id", "time", "der", "de", "alert"},
                new int[]{R.id.et_device_id, R.id.et_device_time, R.id.et_device_der, R.id.et_device_de, R.id.et_device_alert});
    }

    @Override
    protected void onStop() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onStop();
    }

    public void requestDeviceInfoData(final String urlString) {
        DeviceInfoRequest request = new DeviceInfoRequest();
        request.setUrl(urlString);
        mSpiceManager.execute(request, new RequestListener() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                // Log.e(TAG, "onRequestFailure[" + urlString + "]");

            }

            @Override
            public void onRequestSuccess(Object o) {
                // Log.e(TAG, "onRequestSuccess[" + urlString + "]");
                String deviceId = getDeviceID(urlString);
                if (deviceId != null) {
                    DeviceInfo[] deviceInfos = null;
                    try {
                        deviceInfos = (DeviceInfo[]) o;
                        for (int i = 0; i < deviceInfos.length; i++) {
                            Log.e(TAG, "Url[" + urlString + "] Device : " + deviceInfos[i]);
                            Map<String, Object> map = getDeviceData(deviceId);
                            String date =
                                    utc2Local(deviceInfos[i].getTime(), UTC_PATTERN, LOCAL_PATTERN);
                            if (date == null) {
                                date = deviceInfos[i].getTime();
                            }
                            map.put("time", date);
                            map.put("de", deviceInfos[i].getDe());
                            map.put("der", deviceInfos[i].getDer());
                            map.put("alert", deviceInfos[i].isAlert() ? getResources().getString(R.string.string_yes)
                                    : getResources().getString(R.string.string_no));
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String getDeviceID(String urlString) {
        if (urlString != null && urlString.length() < 6) {
            return null;
        }
        return urlString.substring(urlString.length() - 6);
    }

    private Map<String, Object> getDeviceData(String deviceID) {
        Map<String, Object> result = null;
        for (Map deviceMap : mAdapterData) {
            if (deviceMap.containsKey("id")) {
                String tmp = (String) deviceMap.get("id");
                if (tmp != null && tmp.equals(deviceID)) {
                    result = deviceMap;
                    break;
                }
            }
        }

        if (result == null) {
            result = new HashMap();
            result.put("id", deviceID);
            mAdapterData.add(result);
        }
        return result;
    }

    public static String utc2Local(String utcTime, String utcTimePatten,
                                   String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

}

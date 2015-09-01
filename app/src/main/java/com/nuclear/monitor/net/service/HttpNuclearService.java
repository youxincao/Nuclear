package com.nuclear.monitor.net.service;

import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;

/**
 * Created by weilun on 2015/9/1.
 */
public class HttpNuclearService extends Jackson2GoogleHttpClientSpiceService {

    @Override
    public int getThreadCount() {
        return 1;
    }
}

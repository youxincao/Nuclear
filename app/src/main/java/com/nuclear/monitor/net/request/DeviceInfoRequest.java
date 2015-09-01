package com.nuclear.monitor.net.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nuclear.monitor.model.DeviceInfo;

/**
 * Created by weilun on 2015/9/1.
 */
public class DeviceInfoRequest extends BaseRequest<DeviceInfo[]> {

    public DeviceInfoRequest() {
        super(DeviceInfo[].class);
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        HttpRequest request = null;
        GenericUrl genericUrl = new GenericUrl(url);

        if (postParameters == null) {
            request = getHttpRequestFactory().buildGetRequest(genericUrl);
        } else {
            HttpContent content = new UrlEncodedContent(postParameters);
            request = buildPostRequest(genericUrl, content);
        }

        request.setParser(new JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}

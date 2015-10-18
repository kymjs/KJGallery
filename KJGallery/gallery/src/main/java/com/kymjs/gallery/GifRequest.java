/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kymjs.gallery;

import org.kymjs.kjframe.bitmap.Persistence;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpHeaderParser;
import org.kymjs.kjframe.http.KJHttpException;
import org.kymjs.kjframe.http.NetworkResponse;
import org.kymjs.kjframe.http.Request;
import org.kymjs.kjframe.http.Response;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.HashMap;
import java.util.Map;

/**
 * 让KJBitmap兼容GifView
 *
 * @author kymjs (http://www.kymjs.com/) on 10/13/15.
 */
public class GifRequest extends Request<byte[]> implements Persistence {

    // 用来保证当前对象只有一个线程在访问
    private static final Object sDecodeLock = new Object();

    private final Map<String, String> mHeaders = new HashMap<>();

    public GifRequest(String url, HttpCallBack callback) {
        super(HttpMethod.GET, url, callback);
        mHeaders.put("cookie", HttpConfig.sCookie);
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    public String getCacheKey() {
        return getUrl();
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        synchronized (sDecodeLock) {
            try {
                return doParse(response);
            } catch (OutOfMemoryError e) {
                KJLoger.debug("Caught OOM for %d byte image, url=%s",
                        response.data.length, getUrl());
                return Response.error(new KJHttpException(e));
            }
        }
    }

    private Response<byte[]> doParse(NetworkResponse response) {
        if (response.data == null) {
            return Response.error(new KJHttpException(response));
        } else {
            return Response.success(response.data, response.headers,
                    HttpHeaderParser.parseCacheHeaders(mConfig, response));
        }
    }

    @Override
    protected void deliverResponse(Map<String, String> header, byte[] response) {
        if (mCallback != null) {
            mCallback.onSuccess(response);
        }
    }
}

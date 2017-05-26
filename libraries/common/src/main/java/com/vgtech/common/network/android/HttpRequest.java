package com.vgtech.common.network.android;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.utils.ACache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhangshaofang on 2015/8/7.
 */

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class HttpRequest extends Request<String> {
    private HttpListener mListener;
    private int mCallbackId;
    private NetworkPath mPath;
    private ACache mCache;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public HttpRequest(int method, String url, HttpListener listener,
                       HttpListener errorListener, int callbackId, NetworkPath path, ACache aCache) {
        super(method, url, errorListener);
        mListener = listener;
        mCallbackId = callbackId;
        mPath = path;
        mCache = aCache;
    }


    @Override
    protected void deliverResponse(String response) {
        //   mListener.onResponse(response);
        RootData rootData = null;
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (mPath.isVantop()) {
                rootData = new RootData();
            } else {
                rootData = JsonDataFactory.getData(jsonObject);
            }
            rootData.setJson(jsonObject);
        } catch (Exception e) {
            rootData = new RootData();
            if (mPath.getType() == NetworkPath.TYPE_JSONARRAY) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    success = true;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            rootData.message = e.getMessage();
            rootData.responce = response;
        } finally {
            if (mCache != null && (rootData.isSuccess() || success) && mPath.isCache()) {
                if (mPath.getType() == NetworkPath.TYPE_JSONARRAY) {
                    mCache.put(mPath.getPath(), response);
                } else {
                    mCache.put(mPath.getPath(), rootData.getJson());
                }
            }
            mListener.dataLoaded(mCallbackId, mPath, rootData);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        //  super.deliverError(error);
        NetworkResponse response = error.networkResponse;
//       String errmsg =  new String(error.networkResponse.data);
//       Log.e("swj",new String (response.data));
        RootData rootData = new RootData();
        rootData.message = TextUtils.isEmpty(error.getMessage()) ? error.getCause() != null ? error.getCause().toString()
                : "" : error.getMessage();
        if (response != null) {
            String data = new String(response.data);
            rootData.message = data;
        }
        mListener.dataLoaded(mCallbackId, mPath, rootData);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
//            if(response.headers!=null)
//            {
//                String cookie = response.headers.get("Set-Cookie");
//                if(!TextUtils.isEmpty(cookie))
//                {
//                    String ystid = cookie.subSequence(cookie.indexOf("=")+1, cookie.indexOf(";")).toString();
//                    if(mPath!=null&&mPath.getPostValues()!=null)
//                        mPath.getPostValues().put("cookie",ystid);
//                }
//            }
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}

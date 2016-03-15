package com.ali.address911;

import android.content.Context;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SvcReq extends Address911Request {

    public String mRequestId = "";
    public String mAppToken = "";
    public ArrayList<UserDetail> mUserDetailList;

    public SvcReq(Context context) {

    }

    public SvcReq(ArrayList<UserDetail> userDetailList, String token) {
        mUserDetailList = userDetailList;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        mRequestId = sdf.format(new Date());
        mAppToken = token;

    }

    //TODO: AppToken, RequestIid not setup content resolver;
    public SvcReq(String requestId, String appToken, ArrayList<UserDetail> userDetailList) {
        mRequestId = requestId;
        mAppToken = appToken;
        mUserDetailList = userDetailList;
    }

    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, "SvcReq");
        serializer.startTag(null, "requestId");
        if (!mRequestId.isEmpty()) {
            serializer.text(mRequestId);
        }
        serializer.endTag(null, "requestId");
        /*
        serializer.startTag(null, "appToken");
        if (mAppToken != null && !mAppToken.isEmpty()) {
            serializer.text(mAppToken);
        }
        serializer.endTag(null, "appToken");*/
        serializer.startTag(null, "userDetailList");
        if (mUserDetailList != null) {
            for (UserDetail detail : mUserDetailList) {
                detail.serialize(serializer);
            }
        }

        serializer.endTag(null, "userDetailList");
        serializer.endTag(null, "SvcReq");
    }
}

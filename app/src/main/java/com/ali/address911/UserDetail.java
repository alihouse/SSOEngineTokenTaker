package com.ali.address911;

import android.content.Context;
import android.nfc.Tag;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ali.sso.SSOtoken;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class UserDetail extends Address911Request {

    public String mReqType = "";
    public String mMdn = "";
    public String mImei = "";
    public String mUUID = "";
    public Address mAddress = null;
    public Context mContext = null;
    private final static String TAG = SSOtoken.TAG;
    public UserDetail(Address address, String reqType, Context context){
        mReqType = reqType;
        mAddress = address ;
        /** NOTICE: getLine1Number should be push apk to /system/priv-app/ and get uses-permission READ_PRIVILEGED_PHONE_STATE**/
//        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        mMdn = tMgr.getLine1Number();
        /*For Test hard code MDN number*/
        mMdn = "8623090552";
        Log.i(TAG, "[UserDetail][MDN] is >>>>> : "+mMdn);
//        mMdn = android.telephony.TelephonyManager.getDefault().getLine1Number();
    }

    public UserDetail(Address address, String reqType, String mdn, String imei, String uuid, Context context) {
        mReqType = reqType;
        mMdn = mdn;
        mImei = imei;
        mUUID = uuid;
        mAddress = address;
        mContext = context;
    }

    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, "UserDetail");
        serializer.startTag(null, "reqType");
        serializer.text(mReqType);
        serializer.endTag(null, "reqType");
        serializer.startTag(null, "mdn");
        if(mMdn != null && !mMdn.isEmpty()) {
            serializer.text(mMdn);
        }
        serializer.endTag(null, "mdn");
        serializer.startTag(null, "imei");
        serializer.text("VOWIFI");
        serializer.endTag(null, "imei");
//        serializer.startTag(null, "uuid");
//        serializer.text(mUUID);
//        serializer.endTag(null, "uuid");
        if(mAddress != null){
            mAddress.serialize(serializer);
        }
        serializer.endTag(null, "UserDetail");
    }
}

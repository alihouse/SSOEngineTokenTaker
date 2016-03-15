package com.ali.address911;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

public class SSOAppTokenVZW {
    private HandlerThread mHanlderTread = null;
    private Handler mHandler = null;
    private ContentObserverHandler mNonUiHandler = null;
    private ContentObserver mObserver = null;
    private final String SILENTURI = "content://com.verizon.loginclient/token/silent";
    private Context mContext = null;
    public static final String CONTENT_URI = "content://com.verizon.loginclient/token";
    public static final String CONTENT_URI_SILENT = "content://com.verizon.loginclient/token/silent";
    public static final String TOKEN = "token";
    //public static final String ESTABLISH_TOKEN = "com.motricity.verizon.ssoengine.ESTABLISH_TOKEN";

    private static final String TAG = "SSOAppTokenVZW";
    private static final int SET_TOKEN = 2001;
    private static final int WAIT_60_SECOND = 2002;
    private ContentResolver mContentResolver = null;
    private OnQueryTokenCompleteListener mOnQueryTokenCompleteListener = null;

    public interface OnQueryTokenCompleteListener{
        public void onComplete(String token);
    }

    public SSOAppTokenVZW(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mHanlderTread = new HandlerThread("GetSSOToken");
        mHanlderTread.start();
        mNonUiHandler = new ContentObserverHandler(mHanlderTread.getLooper());
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case SET_TOKEN:{
                        mOnQueryTokenCompleteListener.onComplete((String) msg.obj);
                    }

                }
                // A notification of change has arrived from the Content Provider. Call again to getToken

            }
        };
    }

    public void setOnQueryTokenCompleteListener(OnQueryTokenCompleteListener listener){
        mOnQueryTokenCompleteListener = listener;
    }
    public void getAPPTOKEN() {
        if (mNonUiHandler != null) {
            mNonUiHandler.sendEmptyMessage(0);
        }
    }

    private final class ContentObserverHandler extends Handler {
        public ContentObserverHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            // A notification of change has arrived from the Content Provider. Call again to getToken
            Log.i(TAG, "[ContentObserverHandler]GetToken");
            getToken();
        }
    }

    private void getToken() {
        Log.i(TAG, "[getToken] getToken start +");
        /**
         * The apps shall check the device type to determine the authentication method to use.
         * The apps shall only use SSO if the device is a type of 4G/LTE.
         * The app shall use AppAuth if it's a type of 3G. The following logic may be used to
         * determine the device type:
         */

        if(mContentResolver == null || mContext == null) return;
        PackageManager pm = mContext.getPackageManager();

        boolean useSSO = pm.hasSystemFeature("com.verizon.hardware.telephony.lte") ||
                pm.hasSystemFeature("com.verizon.hardware.telephony.ehrpd") ||
                pm.hasSystemFeature("com.vzw.telephony.lte") ||
                pm.hasSystemFeature("com.vzw.telephony.ehrpd");

        if (!useSSO) {
            // Device is not LTE or eHRPD capable, so use AAA based AppAuth
            // Display message below and exit
            // “This device is a 3G device and can not be use SSO.”
            return;
        }

        if(pm.resolveContentProvider("com.verizon.loginclient", 0) == null){
            return;
        }

        String packageName = pm.resolveContentProvider("com.verizon.loginclient", 0).packageName;
        if (null == packageName) {
            // SSOEngine not installed.   This should not occur on non-rooted commercial devices, as it comes as a preloaded system app
            return;
        } else if (packageName.equals("com.motricity.verizon.ssodownloadable") || packageName.equals("com.motricity.verizon.ssoengine")) {
            // Official SSO Client is installed, continue
        } else {
            // If you got here then the SSO Clients official Content Provider is registered with a rogue application.
            // Verizon can dictate what action the 3rd party app should take in this case, but you should not continue to make the query to get a Token.
            return;
        }

        Uri uri = Uri.parse(SILENTURI);

        if (mObserver != null) {
            mContentResolver.unregisterContentObserver(mObserver);
        }
        String appToken = "";
        Cursor cursor = mContentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.i(TAG, "[getToken]Cursor count:" + cursor);

            if (cursor.getString(cursor.getColumnIndex(TOKEN)) != null) {
                String tokenValue = cursor.getString(cursor.getColumnIndex(TOKEN));
                appToken = Base64.encodeToString(tokenValue.getBytes(), Base64.DEFAULT);
            } else {
                mObserver = new SSOTokenContentObserver(mNonUiHandler);
                mContentResolver.registerContentObserver(uri, false, mObserver);
            }
        }
        Log.i(TAG, "[getToken]appToken is : " + appToken);


        if (cursor != null)
            cursor.close();

        if(mHandler != null) {
            Message m = mHandler.obtainMessage(SET_TOKEN);
            m.obj = appToken;
            mHandler.sendMessage(m);
        }

        Log.i(TAG, "[getToken] getToken start -");
    }

    class SSOTokenContentObserver extends ContentObserver {
        private Handler mHandler;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SSOTokenContentObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        public void onChange(boolean selfChange) {
            Log.i(TAG, "[SSOTokenContentObserver]onChange: selfChange "+selfChange);
            mContentResolver.unregisterContentObserver(this);
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
        }

    }

    public void unregisterContentObserver(){
        if(mContentResolver != null && mObserver != null){
            mContentResolver.unregisterContentObserver(mObserver);
        }
    }
}

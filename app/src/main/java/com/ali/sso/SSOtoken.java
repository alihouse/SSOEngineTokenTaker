package com.ali.sso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.UserDictionary;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ali.address911.Address;
import com.ali.address911.Address911Constant;
import com.ali.address911.Address911Find;
import com.ali.address911.Address911FindResult;
import com.ali.address911Utils.Address911Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SSOtoken extends Activity {

    private HandlerThread mHanlderTread = null;
    private ContentObserverHandler mNonUiHandler = null;
    private ContentObserver mObserver = null;
    private Cursor mCursor = null;
    private final String SILENTURI = "content://com.verizon.loginclient/token/silent";

    public static final String CONTENT_URI = "content://com.verizon.loginclient/token";
    public static final String CONTENT_URI_SILENT = "content://com.verizon.loginclient/token/silent";
    public static final String TOKEN = "token";
    public static final String ESTABLISH_TOKEN = "com.motricity.verizon.ssoengine.ESTABLISH_TOKEN";
    public static final String TAG = "HTCSSOTOKEN";
    private TextView mText = null;
    private TextView mTitle = null;
    private Button mBtn = null;
    private static final int SEND_NONUI_MESSAGE = 12;
    private static final int UPDATE_UI = 14;
    private OnQueryTokenCompleteListener mOnQueryTokenCompleteListener = null;
    private String mAPPtoken = "";
    private ProgressDialog mProgressDialog = null;
    private FindAddressCompletedListener mFind911AddressCompleteListener = null;
    private Context mContext = null;
    private boolean mIsProgressingQuery = false;
    private ArrayList<String> mTestArr = new ArrayList<>();
    private Address911Find mAddress911Find;
    private RegistrationHanlder mRegistrationHanlder = null;

    private static final int VOWIFI_OPT_IN_ON_SUCCESS = 120001;
    private static final int VOWIFI_OPT_IN_ON_NETWORK_ERROR = 120002;
    private static final int VOWIFI_OPT_IN_ON_CANCEL = 120003;

    public interface OnQueryTokenCompleteListener {
        public void onComplete(String token);
    }

    public void setOnQueryTokenCompleteListener(OnQueryTokenCompleteListener listener) {
        mOnQueryTokenCompleteListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssotoken);
        mText = (TextView) findViewById(R.id.txt_token);
        mTitle = (TextView) findViewById(R.id.token_title);
        mBtn = (Button) findViewById(R.id.btn_click);
        mContext = this;
        mText.setMovementMethod(new ScrollingMovementMethod());

        mAddress911Find = new Address911Find(mContext);

        mTitle.setText("Query Uri : " + SILENTURI);
        mHanlderTread = new HandlerThread("GetSSOToken");
        mHanlderTread.start();
        mNonUiHandler = new ContentObserverHandler(mHanlderTread.getLooper());
        mProgressDialog = new ProgressDialog(this);
        mFind911AddressCompleteListener = new FindAddressCompletedListener();

        mRegistrationHanlder = new RegistrationHanlder();
        Address911Utils.getInstance().registerForVoWifiOptInOnCancel(mRegistrationHanlder, VOWIFI_OPT_IN_ON_CANCEL, null);
        Address911Utils.getInstance().registerForVoWifiOptInOnNetworkError(mRegistrationHanlder, VOWIFI_OPT_IN_ON_NETWORK_ERROR, null);
        Address911Utils.getInstance().registerForVoWifiOptInOnSuccess(mRegistrationHanlder, VOWIFI_OPT_IN_ON_SUCCESS, null);
        //Intent intent = new Intent(ESTABLISH_TOKEN);
        //sendBroadcast(intent, "com.verizon.ACCESS_VZW_ACCOUNT");
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button click");

                Address911Utils.getInstance().getVoWifiOptInOnCancelRegistrations().notifyRegistrants();
                if(!mIsProgressingQuery) {
                    mHandler.sendEmptyMessage(SEND_NONUI_MESSAGE);
                }
            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_NONUI_MESSAGE: {
                    Intent intent = new Intent();
                    intent.setClassName("com.ali.sso", "com.ali.sso.SSOtoken");
                    intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    mNonUiHandler.sendEmptyMessage(0);
                    break;
                }

                case UPDATE_UI: {
                    updateUI((String) msg.obj);
//                    mProgressDialog.dismiss();
                    Log.i(TAG, "[UPDATE_UI] token : " + (String) msg.obj);

                }

            }
            // A notification of change has arrived from the Content Provider. Call again to getToken

        }
    };


    private class RegistrationHanlder extends Handler{
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case VOWIFI_OPT_IN_ON_SUCCESS:
                    Log.d("Alii", "We got callBack~!!!! VOWIFI_OPT_IN_ON_SUCCESS");
                    break;
                case VOWIFI_OPT_IN_ON_NETWORK_ERROR:
                    Log.d("Alii", "We got callBack~!!!! VOWIFI_OPT_IN_ON_NETWORK_ERROR");
                    break;
                case VOWIFI_OPT_IN_ON_CANCEL:
                    Log.d("Alii", "We got callBack~!!!! VOWIFI_OPT_IN_ON_CANCEL");
                    break;
            }
        }
    }

    private void updateUI(String txt) {
        mIsProgressingQuery = false;
        mText.setText("Query finish.", TextView.BufferType.EDITABLE);

        for(String s : mTestArr){
            mText.append(s);
        }
        if (txt == null || txt.isEmpty()) {
            mText.append("\nAPP Token is null...");
            mText.append("\nToken is null . STOP");
            /** For Test **/
//            Address address = new Address("1","Verizon way","NW","basking ridge","NJ","07920","US");
//            Address911Find address911Find = new Address911Find(mContext);
//            address911Find.setFind911AddressCompleteListener(mFind911AddressCompleteListener);
//            address911Find.findAddress911(address, Address911Constant.ReqType.QUERY, txt);
//            Address911Find address911Find = new Address911Find(mContext);
            mAddress911Find.setFind911AddressCompleteListener(mFind911AddressCompleteListener);
            mAddress911Find.findAddress911(null, Address911Constant.ReqType.QUERY, txt);
        } else {
            mText.append("\nApp Token is : " + txt);
            mText.append("\nContinue to Post XML to SPG server");

//            Address911Find address911Find = new Address911Find(mContext);
            mAddress911Find.setFind911AddressCompleteListener(mFind911AddressCompleteListener);
            mAddress911Find.findAddress911(null, Address911Constant.ReqType.QUERY, txt);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
//        mTestArr.add(0,"FirstAddTo0");
//        for(int i = 0; i<=10; i++) {
//            Double d = Math.random();
//            String addString = d.toString();
//            mTestArr.add(addString);
//        }

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mHanlderTread.getLooper().quit();
        mHanlderTread = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ssotoken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            mHanlder.sendEmptyMessage(SEND_NONUI_MESSAGE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final class ContentObserverHandler extends Handler {
        public ContentObserverHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            // A notification of change has arrived from the Content Provider. Call again to getToken
            Log.i(TAG, "[ContentObserverHandler]GetToken");
            mIsProgressingQuery = true;
            getToken();
//            getContent();
        }
    }

    private void getToken() {
        Log.i(TAG, "[getToken] start +");
        /**
         * The apps shall check the device type to determine the authentication method to use.
         * The apps shall only use SSO if the device is a type of 4G/LTE.
         * The app shall use AppAuth if it's a type of 3G. The following logic may be used to
         * determine the device type:
         */
        PackageManager pm = getApplication().getPackageManager();

        boolean useSSO = pm.hasSystemFeature("com.verizon.hardware.telephony.lte") ||
                pm.hasSystemFeature("com.verizon.hardware.telephony.ehrpd") ||
                pm.hasSystemFeature("com.vzw.telephony.lte") ||
                pm.hasSystemFeature("com.vzw.telephony.ehrpd");
/*
        if (!useSSO) {
            // Device is not LTE or eHRPD capable, so use AAA based AppAuth
            // Display message below and exit
            // “This device is a 3G device and can not be use SSO.”
//            mHandler.sendEmptyMessage(UPDATE_UI);
            return;
        }

        if (pm.resolveContentProvider("com.verizon.loginclient", 0) == null) {
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
*/
        Uri uri = null;
        // Device is LTE or eHRPD capable, so use SSO
        // Interactive URI – Spinner dialog shown to user.  Used by 3rd Party Applications when subscriber needs to be notified/blocked from interaction
//         uri = Uri.parse("com.motricity.verizon.ssoengine.SSOContentProviderConstants.CONTENT_URI");
        // Silent URI – no user interaction. Typically used by Setup Wizard
        uri = Uri.parse(SILENTURI);

        Log.i(TAG, "[getToken] start Query uri " + uri.toString());

        if (mObserver != null) {
            getContentResolver().unregisterContentObserver(mObserver);
        }

        Log.i(TAG, "[getToken] start Query");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        Log.i(TAG, "[getToken] Query END");
        if (cursor == null) {
            Log.i(TAG, "[getToken] cursor is null");
        }

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getString(cursor.getColumnIndex(TOKEN)) != null) {
                String tokenValue = cursor.getString(cursor.getColumnIndex(TOKEN));
                mAPPtoken = Base64.encodeToString(tokenValue.getBytes(), Base64.DEFAULT);
            } else {
                mObserver = new MyContentObserver(mNonUiHandler);
                getContentResolver().registerContentObserver(uri, false, mObserver);
            }
        }
        Log.i(TAG, "[getToken] APP token : " + mAPPtoken);
        if (mHandler != null) {
            Message m = mHandler.obtainMessage(UPDATE_UI);
            m.obj = mAPPtoken;
            mHandler.sendMessage(m);
        }

        if (cursor != null)
            cursor.close();

        Log.i(TAG, "[getToken] start -");
    }

    class MyContentObserver extends ContentObserver {
        private Handler nhandler;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
            nhandler = handler;
        }

        public void onChange(boolean selfChange) {
            Log.i(TAG, "[MyContentObserver]ContentObserver onChange call back " + selfChange);
            getContentResolver().unregisterContentObserver(mObserver);
            if (nhandler != null) {
                Message msg = nhandler.obtainMessage();
                nhandler.sendMessage(msg);
            }
        }

    }

    class FindAddressCompletedListener implements Address911Find.OnFind911AddressCompleteListener {
        @Override
        public void onCompleted(Address911FindResult addressFindResult, String responseCode) {
            String resulteString = addressFindResult.getCompleteCode();
            if (resulteString.equals(Address911Constant.CompleteCode.COMPLETE_CODE_SUCCESS)) {
                mText.append("\nCOMPLETE_CODE_SUCCESS");
                Log.w(TAG, "[FindAddressCompletedListener]onCompleted COMPLETE_CODE_SUCCESS");
                if (null != mHandler) {
//                    mHandler.sendMessage(mHandler.obtainMessage(EVENT_STATUS_COMPLETE_SUCCESS));
                }
            } else {
                mText.append("\nonCompleted addressFindResult fail");
                Log.w(TAG, "[FindAddressCompletedListener]onCompleted addressFindResult fail ");
                if (null != mHandler) {
//                    mHandler.sendMessage(mHandler.obtainMessage(EVENT_STATUS_COMPLETE_FAIL));
                }
            }
        }

        @Override
        public void onCancel(int tag) {
            Log.w(TAG, "[FindAddressCompletedListener]onCancel() tag = " + tag);

            switch (tag) {
                case Address911Constant.CancelCode.NETWORK_ERROR:
//                    mText.append("\nNETWORK_ERROR");
                    Log.w(TAG, "[FindAddressCompletedListener]onCancel() NETWORK_ERROR");
                    if (null != mHandler) {
//                        mHandler.sendMessage(mHandler.obtainMessage(EVENT_CANCEL_NETWORK_ERROR));
                    }
                    break;
                case Address911Constant.CancelCode.TOKEN_IS_EMPTY:
                    mText.append("\nTOKEN_IS_EMPTY");
                    Log.w(TAG, "[FindAddressCompletedListener]onCancel() TOKEN_IS_EMPTY");
                    if (null != mHandler) {
//                        mHandler.sendMessage(mHandler.obtainMessage(EVENT_CANCEL_TOKEN_IS_EMPTY));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

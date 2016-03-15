package com.ali.address911;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Telephony;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import android.os.Message;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.widget.ProgressBar;

import com.ali.address911Utils.Address911Utils;
import com.ali.sso.R;
import com.ali.sso.SSOtoken;
//import android.os.SystemProperties;

public class Address911Find {
    private OnFind911AddressCompleteListener mFind911AddressCompleteListener = null;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int QUERY_APP_TOKEN_TIMEOUT = 60000;
    private static final int START_WAITING_DIALOG = 3001;
    private static final int TERMINATE_WAITING_DIALOG = 4001;
    private static final int TERMINATE_WAITING_DIALOG_DELAY60 = 5001;

//    private static final String URL = "http://www.google.com/";
    private String URL = "http://spg-esit.vzw.com/sit01/SSFGateway/e911Location/changeAddress";
    private String URL04 = "http://spg-esit.vzw.com/sit04/SSFGateway/e911Location/changeAddress";
    private XmlParser mXmlParser = null;
    private static final String TAG = SSOtoken.TAG;
    private Context mContext = null;
    private Address mRequestAddress = null;
    private String mRequestType = null;
    private boolean mIsProcessingTask = false;
    public QueryTokenCompleteListener mOnQeuryTokenCompleteListener = null;
    public SSOAppTokenVZW mSSOAppTokenVZW = null;
    public Handler mHandler = null;
    private String mToken = null;
    private ProgressDialog mNetWorkCheckLoadingDialog = null;
    private String URL_USER_CHANGE = "gsm.vowifi.changeurl";
    private String EMPTY_TOKEN_PROP = "ali.vowifi.emptytoken";
    public interface OnFind911AddressCompleteListener {
        public void onCompleted(Address911FindResult lyricFindResult, String responseCode);

        public void onCancel(int tag);
    }

    public Address911Find(Context context) {
        mContext = context;
        mXmlParser = new XmlParser();
        mHandler = new CallBackhandler();
    }

    public void setFind911AddressCompleteListener(OnFind911AddressCompleteListener find911AddressCompleteListener) {
        if (find911AddressCompleteListener != null) {
            mFind911AddressCompleteListener = find911AddressCompleteListener;
        }
    }

    public void findAddress911(Address address, String requestType, String token) {
//        String url_change = SystemProperties.get(URL_USER_CHANGE, "0");
//        Log.d(TAG, "[findAddress911][Alii]: Systemproperty ali.fake.changeurl value" + url_change);
//        if(!url_change.equals("0")){
//            URL = url_change;
//            Log.d(TAG, "[findAddress911][Alii]: Changed URL" + URL);
//            SystemProperties.set(URL_USER_CHANGE, "0");
//        }
//        String emptyToken = SystemProperties.getProperty(EMPTY_TOKEN_PROP,"0");
//        Log.d(TAG, "[QueryTokenCompleteListener][Alii]: Systemproperty gsm.vowifi.emptytoken value" + emptyToken);
//        if(emptyToken.equals("1")){
//            if (mHandler != null) {
//                mHandler.removeMessages(TERMINATE_WAITING_DIALOG);
//                mHandler.removeMessages(TERMINATE_WAITING_DIALOG_DELAY60);
//            }
//            executeFindAddress911Task("");
//            return;
//        }
        mRequestAddress = address;
        mRequestType = requestType;
        token = "MTQ0NTAwOTM5MzQ3MUBEQThCRUJBNzEzQUI4NEY0QkVCN0Q4OEFEMzlGMTcyOEQ4REFBRkM5";
        Log.i(TAG, "[findAddress911][token]: " + token);

        if (token != null || !token.isEmpty()) {
            executeFindAddress911Task(token);
            mNetWorkCheckLoadingDialog = new ProgressDialog(mContext);
            mNetWorkCheckLoadingDialog.setMessage(mContext.getResources().getText(R.string.hello_world));
            mNetWorkCheckLoadingDialog.setIndeterminate(true);
            mNetWorkCheckLoadingDialog.show();
        } else {
            Log.i(TAG, "[findAddress911][token]: token is null .DO NOTHING!");
        }
    }

    public DialogInterface.OnCancelListener mWaitingDialogCancellistener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            Log.d(TAG, "Waiting dialog onCancel");
            finish(null);
        }
    };

    //For query again API.Using previous data.
    private void executeFindAddress911Task(){
        if(URL.equals(URL04.toString())){
            Log.d(TAG, "Already changed.... but still fail");
            mIsProcessingTask = false;
            finish(null);
            return;
        }
        URL = URL04;

        executeFindAddress911Task(mToken);
    }
    private void executeFindAddress911Task(String token) {
        Log.d(TAG, "[executeFindAddress911Task] +");
        String postXML = "";

        if (mRequestType.equals(Address911Constant.ReqType.ADD) && mRequestAddress != null) {
            AddAddress911Request add = new AddAddress911Request(mRequestAddress, mContext, token);
            postXML = add.toXML();
        } else if (mRequestType.equals(Address911Constant.ReqType.QUERY)) {
            QueryAddress911Request query = new QueryAddress911Request(mContext, token);
            postXML = query.toXML();
        }

        if (!mIsProcessingTask) {
            mIsProcessingTask = true;
            AsyncFind911AddressTask addressTask = new AsyncFind911AddressTask(postXML);
            addressTask.execute((String) null);

        } else {
            Log.i(TAG, "Processing Task , wait...mIsProcessingTask: " + mIsProcessingTask);
        }

        Log.d(TAG, "[executeFindAddress911Task] -");
    }

    private class AsyncFind911AddressTask extends AsyncTask<String, Integer, Address911FindResult> {
        private String mPostXML = "";

        AsyncFind911AddressTask(String postXML) {
            mPostXML = postXML;
        }

        @Override
        protected Address911FindResult doInBackground(String... params) {

            Address911FindResult findResult = null;
            URL url = null;
            HttpURLConnection conn = null;
            Log.d(TAG, "[Address911Find]Start post XML to " + URL + "POST XML :" + mPostXML);
            try {
                //Create connection
                url = new URL(URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setReadTimeout(CONNECTION_TIMEOUT);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setRequestProperty("Content-Length", Integer.toString(mPostXML.getBytes().length));
                conn.connect();
//                conn.setFixedLengthStreamingMode(mPostXML.length());
                //Send request
                OutputStream output = new BufferedOutputStream(conn.getOutputStream());
                output.write(mPostXML.getBytes());
                output.flush();
                output.close();
            } catch (IOException e) {
                Log.w(TAG, "[Address911Find] Connection IOException");
                e.printStackTrace();
                conn.disconnect();
                if (mFind911AddressCompleteListener != null) {
                    Address911Utils.getInstance().getVoWifiOptInOnNetworkErrorInRegistrations().notifyRegistrants();
                    mFind911AddressCompleteListener.onCancel(Address911Constant.CancelCode.NETWORK_ERROR);
                }

                return findResult;

            }

            //TODO: Need to do real test.
            InputStream inputStream = null;
            int httpResponseCode = 0;
            try {
                Log.i(TAG, "[Address911Find] : getInputStream");
                inputStream = conn.getInputStream();
            } catch (IOException ioex) {
                Log.i(TAG, "[Address911Find] : IOException in getInputStream ioex = " + ioex);
                return findResult;
            } finally {
                try {
                    httpResponseCode = conn.getResponseCode();
                } catch (IOException ex) {
                    Log.i(TAG, "[Address911Find] : IOException in getResponseCode ioex = " + ex);
                    ex.printStackTrace();
                }

                Log.i(TAG, "[Address911Find] : httpResponseCode = " + httpResponseCode);
            }

            if (inputStream != null) {
                Log.i(TAG, "[Address911Find] : Alll start parse input stream");
//                showInputStream(inputStream);
                Address911FindResult result = mXmlParser.getAddress911FindResult(showInputStream(inputStream));
                if (result != null) {
                    findResult = result;
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.i(TAG, "[Address911Find]  : close inputStream fail : " + e);
                }
            } else {
                Log.i(TAG, "[Address911Find]  : inputStream is null");
            }

            if (findResult != null) {
                Log.d(TAG, "[Address911Find] AddressFindResult: " + findResult.toString());
            }


            conn.disconnect();
            Log.i(TAG, "[Address911Find] return AddressFind Result ");
            return findResult;
        }

        @Override
        protected void onPostExecute(Address911FindResult result) {
            Activity a = (Activity)mContext;
            Log.d(TAG, "[onPostExecute] mIsProcessingTask :" + mIsProcessingTask + " Alii Context "+ a.isFinishing());
            //If result is Empty, Change URL to sit04 and query again.
//            if(true) {
//                Log.d(TAG, " Result is Empty... Change URL and Query again. Fail URL is : " + URL);
//                mIsProcessingTask = false;
//                executeFindAddress911Task();
//            }else{
                mIsProcessingTask = false;
                mNetWorkCheckLoadingDialog.cancel();
                finish(result);
//            }

        }
    }

    private void finish(Address911FindResult result) {
        if (result != null) {
            if (mFind911AddressCompleteListener != null) {
                if (result.getCompleteCode().equals(Address911Constant.CompleteCode.COMPLETE_CODE_SUCCESS)) {
                    Address911Utils.getInstance().getVoWifiOptInOnSuccessRegistrations().notifyRegistrants();
                    mFind911AddressCompleteListener.onCompleted(result, Address911Constant.CompleteCode.COMPLETE_CODE_SUCCESS);
                } else if (result.hasAlternateAddressList()) {
                    mFind911AddressCompleteListener.onCompleted(result, Address911Constant.CompleteCode.COMPLETE_CODE_INVALID_ALTERNATE_ADDRESS_FOUND);
                } else if (result.getCompleteCode().equals(Address911Constant.CompleteCode.COMPLETE_CODE_INVALID_NO_ADDRESS_FOUND)) {
                    mFind911AddressCompleteListener.onCompleted(result, Address911Constant.CompleteCode.COMPLETE_CODE_INVALID_NO_ADDRESS_FOUND);
                } else if (result.getCompleteCode().equals(Address911Constant.CompleteCode.COMPLETE_CODE_INVALID_ADDRESS)) {
                    mFind911AddressCompleteListener.onCompleted(result, Address911Constant.CompleteCode.COMPLETE_CODE_INVALID_ADDRESS);
                } else {
                    Address911Utils.getInstance().getVoWifiOptInOnCancelRegistrations().notifyRegistrants();
                    mFind911AddressCompleteListener.onCompleted(result, Address911Constant.CompleteCode.COMPLETE_CODE_INVALID);
                }
            }
        }

        if (mSSOAppTokenVZW != null) {
            mSSOAppTokenVZW.unregisterContentObserver();
        }

        if (mFind911AddressCompleteListener != null) {
            mFind911AddressCompleteListener = null;
        }

        mNetWorkCheckLoadingDialog.cancel();
    }

    class QueryTokenCompleteListener implements SSOAppTokenVZW.OnQueryTokenCompleteListener {

        @Override
        public void onComplete(String token) {

//            if (token == null || token.isEmpty()) {
//                //APP token is null
//                Log.i(TAG, "[QueryTokenCompleteListener][SSOAppTokenVZW] APP token is null. Wait time out or Observer notify.");
//            } else {
//            token = "MTQ0NTAwOTM5MzQ3MUBEQThCRUJBNzEzQUI4NEY0QkVCN0Q4OEFEMzlGMTcyOEQ4REFBRkM5";
//            Log.i(TAG, "[QueryTokenCompleteListener][token]: " + token);
//            executeFindAddress911Task(token);
//            }
        }
    }

    private String getPostXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<E911LocationManagementSvc xmlns:ns2='http://ssf.vzw.com/common.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>");
        sb.append("<ns2:SvcHdr>");
        sb.append("<ns2:SvcInfo><svcNv>addAddress</svcNv><subSvcNm>W91</subSvcNm></ns2:SvcInfo></ns2:SvcHdr><SvcBdy><SvcReq><userDetailList>");
        sb.append("<UserDetail><reqType>QUERY</reqType><imei>VOWIFI</imei><mdn>6692351759</mdn></UserDetail></userDetailList></SvcReq></SvcBdy></E911LocationManagementSvc>");
       return sb.toString();
        /*
        <?xml version='1.0' encoding='UTF-8'?>
        <E911LocationManagementSvc xmlns:ns2="http://ssf.vzw.com/common.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <ns2:SvcHdr>
        <ns2:SvcInfo>
        <svcNv>addAddress</svcNv>
        <subSvcNm>W91</subSvcNm>
        </ns2:SvcInfo>
        </ns2:SvcHdr>
        <SvcBdy>
        <SvcReq>
        <userDetailList>
        <UserDetail>
        <reqType>QUERY</reqType>
        <imei>VOWIFI</imei>
        <mdn>6692351759</mdn>
        </UserDetail>
        </userDetailList>
        </SvcReq>
        </SvcBdy>
        </E911LocationManagementSvc>
        **/
    }

    private InputStream showInputStream(InputStream in){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Fake code simulating the copy
        // You can generally do better with nio if you need...
        // And please, unlike me, do something about the Exceptions :D
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = in.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }

            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream input1 = new ByteArrayInputStream(baos.toByteArray());
        InputStreamReader rd = new InputStreamReader(input1);
        int ch = 0;
        StringBuilder sb = new StringBuilder();
        try {
            while ((ch = rd.read()) != -1) {
                sb.append((char) ch);
//                        System.out.print("_"+(char)ch);
            }
            Log.i(TAG, "[Address911Find] Show inputStream : " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static final int VOWIFI_OPT_IN_CHANGED = 1111;
    private static final int VOWIFI_OPT_IN_NOCHANGED = 1112;

    private class CallBackhandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case VOWIFI_OPT_IN_CHANGED:
                    Log.d("Alii", "We got callBack~!!!! VOWIFI_OPT_IN_CHANGED");
                    break;
                case VOWIFI_OPT_IN_NOCHANGED:
                    Log.d("Alii", "We got callBack~!!!! VOWIFI_OPT_IN_NOCHANGED");
                    break;
            }
        }
    }

}

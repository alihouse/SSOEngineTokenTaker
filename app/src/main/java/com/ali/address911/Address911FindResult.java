package com.ali.address911;

import java.util.ArrayList;

public class Address911FindResult {
    static final int ERROR_UNKNOWN = -1;
    private int mResponseCode = ERROR_UNKNOWN;
    private Address mUserDetailAddress = null;
    private String mReqType = "";
    private String mReqId = "";
    private ArrayList<Address> mAddressList = null;
    private Address UserDetailAddress = null;
    private String mErrorCode = "";
    private String mErrorMessage = "";
    private boolean mHasAlternateAddressList = false;

    public void setResponseCode(int responseCode) {
        mResponseCode = responseCode;
    }

    public void setUserDetailAddress(Address ad) {
        mUserDetailAddress = ad;
    }

    public Address getUserDetailAddress() {
        return mUserDetailAddress;
    }

    public void setHasAlternateAddressList(boolean has) {
        mHasAlternateAddressList = has;
    }

    public boolean hasAlternateAddressList() {
        return mHasAlternateAddressList;
    }

    public void setAddressList(ArrayList<Address> addressList) {
        mAddressList = addressList;
    }

    public ArrayList<Address> getAddressList() {
        return mAddressList;
    }

    public void setReqType(String type) {
        mReqType = type;
    }

    public String getReqType() {
        return mReqType;
    }

    public void setReqId(String reqId) {
        mReqId = reqId;
    }

    public String getReqId() {
        return mReqId;
    }

    public void setCompleteCode(String errorCode) {
        mErrorCode = errorCode;
    }

    public String getCompleteCode() {
        return mErrorCode;
    }

    public void setCompleteMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public String getCompleteMessage() {
        return mErrorMessage;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("mReqType : ")
                .append(mReqType).append(" mReqId ")
                .append(mReqId)
                .append(" mHasAlternateAddressList :")
                .append(mHasAlternateAddressList)
                .append(" mErrorCode : ")
                .append(mErrorCode)
                .append(" mErrorMessage : ")
                .append(mErrorMessage).toString();
    }

}

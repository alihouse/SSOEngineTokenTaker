package com.ali.address911Utils;

import android.os.Handler;

public class Address911Utils {
    private static final Address911Utils INSTANCE = new Address911Utils();
    private AddressRegistrantList mVoWifiOptInOnNetworkErrorRegistrations = new AddressRegistrantList();
    private AddressRegistrantList mVoWifiOptInOnSuccessRegistrations = new AddressRegistrantList();
    private AddressRegistrantList mVoWifiOptInOnCancelRegistrations = new AddressRegistrantList();

    private Address911Utils(){

    }

    public static Address911Utils getInstance(){
        return INSTANCE;
    }

    public void registerForVoWifiOptInOnSuccess(Handler h, int what, Object obj) {
        AddressRegistrant r = new AddressRegistrant(h, what, obj);
        mVoWifiOptInOnSuccessRegistrations.addUnique(h, what, obj);
    }

    public void unregisterForVoWifiOptInOnSuccess(Handler h) {
        mVoWifiOptInOnSuccessRegistrations.remove(h);
    }

    public AddressRegistrantList getVoWifiOptInOnSuccessRegistrations() {
        return mVoWifiOptInOnSuccessRegistrations;
    }

    public void registerForVoWifiOptInOnNetworkError(Handler h, int what, Object obj) {
        AddressRegistrant r = new AddressRegistrant(h, what, obj);
        mVoWifiOptInOnNetworkErrorRegistrations.addUnique(h, what, obj);
    }

    public void unregisterForVoWifiOptInOnNetworkError(Handler h) {
        mVoWifiOptInOnNetworkErrorRegistrations.remove(h);
    }

    public AddressRegistrantList getVoWifiOptInOnNetworkErrorInRegistrations() {
        return mVoWifiOptInOnNetworkErrorRegistrations;
    }
    public void registerForVoWifiOptInOnCancel(Handler h, int what, Object obj) {
        AddressRegistrant r = new AddressRegistrant(h, what, obj);
        mVoWifiOptInOnCancelRegistrations.addUnique(h, what, obj);
    }

    public void unregisterForVoWifiOptInOnCancel(Handler h) {
        mVoWifiOptInOnCancelRegistrations.remove(h);
    }

    public AddressRegistrantList getVoWifiOptInOnCancelRegistrations() {
        return mVoWifiOptInOnCancelRegistrations;
    }

}

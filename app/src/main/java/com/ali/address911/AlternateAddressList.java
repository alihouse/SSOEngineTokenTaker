package com.ali.address911;

import java.util.ArrayList;

public class AlternateAddressList {

    private ArrayList<Address> mAltAddressDetailArrayList = new ArrayList<>();
    public AlternateAddressList(){}

    public void addAltAddressDetai(Address a){
        mAltAddressDetailArrayList.add(a);
    }

    public ArrayList<Address> getAlternateAddressList(){
        return mAltAddressDetailArrayList;
    }
}

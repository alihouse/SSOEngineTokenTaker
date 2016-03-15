package com.ali.address911;

public class AltAddressDetail {

    private Address mAddress = null;
    public AltAddressDetail(Address address){
        mAddress = address;
    }

    public Address getAddress(){
        return mAddress;
    }

}

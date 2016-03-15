package com.ali.address911;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class Address extends Address911Request implements Cloneable{

    public String mHouseNumber = "";
    public String mRoad = "";
    public String mState = "";
    public String mLocation = "";
    public String mCity = "";
    public String mZip = "";
    public String mCountry = "";

    public Address() {
    }

    public Address(String houseNumber, String road, String location,
                   String city, String state, String zip, String country) {

        mHouseNumber = houseNumber;
        mRoad = road;
        mLocation = location;
        mCity = city;
        mState = state;
        mZip = zip;
        mCountry = country;

    }

    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, Address911Constant.SvcBdyTag.ADDRESS);
        serializer.startTag(null, Address911Constant.Address.HOUSENUMBER);
        serializer.text(mHouseNumber);
        serializer.endTag(null, Address911Constant.Address.HOUSENUMBER);
        serializer.startTag(null, Address911Constant.Address.ROAD);
        serializer.text(mRoad);
        serializer.endTag(null, Address911Constant.Address.ROAD);
        serializer.startTag(null, Address911Constant.Address.LOCATION);
        serializer.text(mLocation);
        serializer.endTag(null, Address911Constant.Address.LOCATION);
        serializer.startTag(null, Address911Constant.Address.CITY);
        serializer.text(mCity);
        serializer.endTag(null, Address911Constant.Address.CITY);
        serializer.startTag(null, Address911Constant.Address.STATE);
        serializer.text(mState);
        serializer.endTag(null, Address911Constant.Address.STATE);
        serializer.startTag(null, Address911Constant.Address.ZIP);
        serializer.text(mZip);
        serializer.endTag(null, Address911Constant.Address.ZIP);
        serializer.startTag(null, Address911Constant.Address.COUNTRY);
        serializer.text(mCountry);
        serializer.endTag(null,  Address911Constant.Address.COUNTRY);
        serializer.endTag(null, Address911Constant.SvcBdyTag.ADDRESS);
    }

    public void setHouseNumber(String houseNumber) {
        mHouseNumber = houseNumber;
    }

    public String getHouseNumber() {
        return mHouseNumber;
    }

    public void setRoad(String road) {
        mRoad = road;
    }

    public String getRoad() {
        return mRoad;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCity() {
        return mCity;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getState() {
        return mState;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    public String getZip() {
        return mZip;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void clean() {
        mHouseNumber = "";
        mRoad = "";
        mState = "";
        mLocation = "";
        mCity = "";
        mZip = "";
        mCountry = "";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

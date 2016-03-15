package com.ali.address911;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class DvcInfo extends  Address911Request{

    public String mDvcName = "";
    public String mOsType = "";
    public DvcInfo(){
        mDvcName = "HTC M10";
        mOsType = "Sense8.0";
    }
    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag("http://ssf.vzw.com/common.xsd", "DvcInfo");
        serializer.startTag("http://ssf.vzw.com/common.xsd", "dvcName");
        serializer.text(mDvcName);
        serializer.endTag("http://ssf.vzw.com/common.xsd", "dvcName");
        serializer.startTag("http://ssf.vzw.com/common.xsd", "osType");
        serializer.text(mOsType);
        serializer.endTag("http://ssf.vzw.com/common.xsd", "osType");
        serializer.endTag("http://ssf.vzw.com/common.xsd", "DvcInfo");
    }
}

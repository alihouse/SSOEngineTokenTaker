package com.ali.address911;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class SvcInfo extends  Address911Request {

    private String mSubSvcNm = "";
    private String mSvcNm = "E911Address";
    public SvcInfo(String subSvcNm){
        mSubSvcNm = subSvcNm;
    }
    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag("http://ssf.vzw.com/common.xsd", "SvcInfo");
        serializer.startTag("http://ssf.vzw.com/common.xsd", "svcNm");
        serializer.text(mSvcNm);
        serializer.endTag("http://ssf.vzw.com/common.xsd", "svcNm");
        serializer.startTag("http://ssf.vzw.com/common.xsd", "subSvcNm");
        serializer.text(mSubSvcNm);
        serializer.endTag("http://ssf.vzw.com/common.xsd", "subSvcNm");
        serializer.endTag("http://ssf.vzw.com/common.xsd", "SvcInfo");
    }
}

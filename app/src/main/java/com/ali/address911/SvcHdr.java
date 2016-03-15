package com.ali.address911;

import android.content.Context;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class SvcHdr extends  Address911Request {

    public String mSubSvcNm = "";
    public Context mContext = null;
    public SvcHdr(String subsvcNm, Context context){
        mSubSvcNm = subsvcNm;
        mContext = context;

    }
    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        SvcInfo svcInfo = new SvcInfo(mSubSvcNm);
        TransInfo transInfo = new TransInfo(mContext);
        DvcInfo dvcInfo = new DvcInfo();
        serializer.startTag("http://ssf.vzw.com/common.xsd", "SvcHdr");
        svcInfo.serialize(serializer);
        transInfo.serialize(serializer);
        dvcInfo.serialize(serializer);
        serializer.endTag("http://ssf.vzw.com/common.xsd", "SvcHdr");
    }
}

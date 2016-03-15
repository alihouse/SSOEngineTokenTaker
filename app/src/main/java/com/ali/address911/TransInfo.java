package com.ali.address911;

import android.content.Context;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransInfo extends  Address911Request{

    public String timeStamp = "";
    public TransInfo(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        timeStamp = sdf.format(new Date());
    }
    @Override
    public void serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag("http://ssf.vzw.com/common.xsd", "TransInfo");
        serializer.startTag("http://ssf.vzw.com/common.xsd", "timeStamp");
        serializer.text(timeStamp);
        serializer.endTag("http://ssf.vzw.com/common.xsd", "timeStamp");
        serializer.endTag("http://ssf.vzw.com/common.xsd", "TransInfo");
    }
}

package com.ali.address911;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public abstract class Address911Request implements IRequest {

    public String toXML() {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", null);
            serializer.setPrefix("ssf", "http://ssf.vzw.com/common.xsd");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag(null, "E911LocationManagementSvc");
            serializer.attribute("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation", "Address.xsd");
            serialize(serializer);
            serializer.endTag(null, "E911LocationManagementSvc");
            serializer.endDocument();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}

package com.ali.address911;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public interface IRequest {
    public void serialize(XmlSerializer serializer)  throws IllegalArgumentException, IllegalStateException, IOException;
}

package io.gripxtech.locationjobdispatcher.webservice.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.util.List;

import io.gripxtech.locationjobdispatcher.helper.LocationData;

/**
 * 获取具体信息需要传的参数
 * Created by Jeeson on 16/7/15.
 */

public class RequestModel {
    @Attribute(name = "xmlns")
    public String cityNameAttribute;

    @Element(name = "Tracklog", required = false)
    public List<LocationData> locationData;     //城市名字

}

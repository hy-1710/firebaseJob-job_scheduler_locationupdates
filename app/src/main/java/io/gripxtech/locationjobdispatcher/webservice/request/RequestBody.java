package io.gripxtech.locationjobdispatcher.webservice.request;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 用户角色返回body
 * Created by Jeeson on 16/7/15.
 */
@Root(name = "soap:Body", strict = false)
public class RequestBody {

    @Element(name = "SyncTracklog", required = false)
    public RequestModel getWeatherbyCityName;
}

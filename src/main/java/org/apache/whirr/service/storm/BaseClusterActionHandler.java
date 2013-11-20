package org.apache.whirr.service.storm;

import org.apache.whirr.service.ClusterActionHandlerSupport;

public class BaseClusterActionHandler extends ClusterActionHandlerSupport {
    @Override
    public String getRole() {
        return "default";
    }
}

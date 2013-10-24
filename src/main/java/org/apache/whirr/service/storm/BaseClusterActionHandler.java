package org.apache.whirr.service.storm;

import org.apache.whirr.service.ClusterActionHandlerSupport;

/**
 * Created with IntelliJ IDEA.
 * User: tgoetz
 * Date: 10/19/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseClusterActionHandler extends ClusterActionHandlerSupport {
    @Override
    public String getRole() {
        return "default";
    }
}

package org.apache.whirr.service.storm;


import org.apache.whirr.service.ClusterActionEvent;

import java.io.IOException;

import static org.jclouds.scriptbuilder.domain.Statements.call;

public class StormSupervisorClusterActionHandler extends StormClusterActionHandler {
    public static final String ROLE = "storm-supervisor";

    @Override
    public String getRole() {
        return ROLE;
    }

    @Override
    protected void beforeConfigure(ClusterActionEvent event) throws IOException, InterruptedException {
        super.beforeConfigure(event);
        addStatement(event, call("configure_supervisord", "supervisor"));
    }
}

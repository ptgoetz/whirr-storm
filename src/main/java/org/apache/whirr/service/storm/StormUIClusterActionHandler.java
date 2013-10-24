package org.apache.whirr.service.storm;


import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.FirewallManager;

import java.io.IOException;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

public class StormUIClusterActionHandler extends StormClusterActionHandler {
    public static final String ROLE = "storm-ui";

    @Override
    public String getRole() {
        return ROLE;
    }

    @Override
    protected void beforeConfigure(ClusterActionEvent event) throws IOException, InterruptedException {
        super.beforeConfigure(event);

        addStatement(event, call("configure_supervisord", "ui"));

        event.getFirewallManager().addRule(
                FirewallManager.Rule.create().destination(role(ROLE)).port(8080)
        );

        handleFirewallRules(event);
    }
}

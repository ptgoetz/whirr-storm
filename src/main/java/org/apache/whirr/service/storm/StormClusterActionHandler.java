package org.apache.whirr.service.storm;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.configuration.Configuration;
import org.apache.whirr.Cluster;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

public abstract class StormClusterActionHandler extends ClusterActionHandlerSupport {

    @Override
    protected void beforeBootstrap(ClusterActionEvent event) throws IOException {
        ClusterSpec clusterSpec = event.getClusterSpec();
        Configuration conf = clusterSpec.getConfiguration();


        addStatement(event, call("retry_helpers"));
        addStatement(event, call("configure_hostnames"));

        addStatement(event, call(getInstallFunction(conf, "java", "install_openjdk")));

        addStatement(event, call("install_supervisord"));
        addStatement(event, call("install_storm"));


    }

    @Override
    protected void beforeConfigure(ClusterActionEvent event)
            throws IOException, InterruptedException {
        Cluster cluster = event.getCluster();

        Set<Cluster.Instance> ensemble = cluster.getInstancesMatching(role("zookeeper"));
        List<String> zks = getPublicIps(ensemble);
        String zkServers = Joiner.on(' ').join(zks);

        addStatement(event, call("configure_storm_zk", zkServers));

        Cluster.Instance nimbusInstance = cluster.getInstanceMatching(role(StormNimbusClusterActionHandler.ROLE));
        addStatement(event, call("configure_storm_nimbus", nimbusInstance.getPublicHostName()));

        Set<Cluster.Instance> all = cluster.getInstancesMatching(role("storm-nimbus"));
        all.addAll(cluster.getInstancesMatching(role("storm-supervisor")));

        for(Cluster.Instance instance : all){
            event.getFirewallManager().addRule(
                    FirewallManager.Rule.create()
                            .source(instance.getPublicAddress().getHostAddress())
                            .destination(cluster.getInstancesMatching(role("zookeeper")))
                            .port(2181)
            );
        }

        Set<Cluster.Instance> nimbusComms = cluster.getInstancesMatching(role("storm-supervisor"));
        nimbusComms.addAll(cluster.getInstancesMatching(role("storm-ui")));

        for(Cluster.Instance instance : nimbusComms){
            event.getFirewallManager().addRule(
                    FirewallManager.Rule.create()
                        .source(instance.getPublicAddress().getHostAddress())
                        .destination(cluster.getInstancesMatching(role("storm-nimbus")))
                        .port(6627)
            );
        }
    }


    private List<String> getPublicIps(Set<Cluster.Instance> instances) {
        return Lists.transform(Lists.newArrayList(instances),
                new Function<Cluster.Instance, String>() {
                    @Override
                    public String apply(Cluster.Instance instance) {
                        return instance.getPublicIp();
                    }
                });
    }


    @Override
    protected void beforeStart(ClusterActionEvent event) {
        addStatement(event, call("start_storm"));
    }

    @Override
    protected void beforeStop(ClusterActionEvent event) {
        addStatement(event, call("stop_storm"));
    }

}

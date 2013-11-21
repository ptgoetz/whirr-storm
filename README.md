# About

Whirr-Storm provides an [Apache Whirr](http://whirr.apache.org) service implementation for configuring storm clusters for cloud providers such
as Amazon EC2 and Rackspace.

Whirr-Storm will install all Storm's dependencies and setup the necessary firewall rules.


# Quickstart

Install Apache Whirr:

```
wget http://www.apache.org/dist/whirr/whirr-0.8.2/whirr-0.8.2.tar.gz
tar -zxf whirr-0.8.2.tar.gz
cd whirr-0.8.2
```

Generate an SSH key for Whirr:

```
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa_whirr
```

Set up your Amazon EC2 credentials:

```
mkdir ~/.whirr
echo "PROVIDER=aws-ec2" > ~/.whirr/credentials
echo "IDENTITY=[your EC2 Access Key ID]" >> ~/.whirr/credentials
echo "CREDENTIAL=[your EC2 Secret Access Key]" >> ~/.whirr/credentials
```

Add Whirr-Storm service JAR to Whirr's lib directory:

```
wget http://repo1.maven.org/maven2/com/github/ptgoetz/whirr-storm/0.1.0/whirr-storm-0.1.0.jar -P lib
```

Create a `storm.properties` cluster configuration file:

```
whirr.cluster-name=storm

# Cluster with 1 nimbus/storm-ui node, 3 supervisor nodes, and 1 zookeeper node:
whirr.instance-templates=1 storm-nimbus+storm-ui,3 storm-supervisor, 1 zookeeper

# Pseudo-Cluster with everything running on one node:
#whirr.instance-template=1 storm-nimbus+storm-ui+storm-supervisor+zookeeper

whirr.private-key-file=${sys:user.home}/.ssh/id_rsa_whirr
whirr.public-key-file=${whirr.private-key-file}.pub

# EC2 image
whirr.image-id=us-east-1/ami-55dc0b3c
# EC2 hardware type
whirr.hardware-id=t1.micro


# REQUIRED: Where to download the storm distribution
# Storm 0.9.0-rc3
whirr.storm.zip.url=https://dl.dropboxusercontent.com/s/t8m516l2kadt7c6/storm-0.9.0-rc3.zip

# Storm 0.9.0-rc2
#whirr.storm.zip.url=https://dl.dropboxusercontent.com/s/p5wf0hsdab5n9kn/storm-0.9.0-rc2.zip

# Advanced
# Storm-specific config. Anything prefixed with "whirr-storm" will end up in the storm.yaml file.
# "nimbus.host" and "storm.zookeeper.servers" setting are handled automatically and don't need to be set.
#whirr-storm.supervisor.slots.ports=6700,6701,6702,6703

# Netty transport configuration (REQUIRED -- 0mq is not currently supported)
whirr-storm.storm.messaging.transport="backtype.storm.messaging.netty.Context"
whirr-storm.storm.messaging.netty.server_worker_threads=1
whirr-storm.storm.messaging.netty.client_worker_threads=1
whirr-storm.storm.messaging.netty.buffer_size=5242880
whirr-storm.storm.messaging.netty.max_retries=100
whirr-storm.storm.messaging.netty.max_wait_ms=1000
whirr-storm.storm.messaging.netty.min_wait_ms=100

```

Launch the cluster:

```
./bin/whirr launch-cluster --config storm.properties
```

Once the cluster has launched, Whirr will output ssh commands for connecting to each node in the cluster. Find the
IP address corresponding to nimbus/storm-ui and open the following url in a browser:

```
http://[nimbus IP]:8080
```

Deploy a Storm topology:

```
storm jar myTopology.jar com.example.StormTopology test-topology -c nimbus-host=[nimbus IP]
```

When you are finished with the cluster, shut it down:

```
./bin/whirr destroy-cluster --config storm.properties
```


# TODO

 * Add support for logviewer and DRPC daemons.
 * Add support for role-based firewall rules (e.g. open ports between supervisors and cassandra nodes if there are
 "cassandra" roles in the cluster.
 * Add support for 0mq.
 * Add OSGi support.
 * More documentation.

function configure_storm_zk(){

    echo "storm.zookeeper.servers:" | sudo tee -a /etc/storm/storm.yaml
    for server in "$@"; do
      echo "     - \"${server}\"" | sudo tee -a /etc/storm/storm.yaml
    done

}
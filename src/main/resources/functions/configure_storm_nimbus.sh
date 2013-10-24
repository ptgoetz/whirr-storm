function configure_storm_nimbus(){
    echo "nimbus.host: \"$1\"" | sudo tee -a /etc/storm/storm.yaml
}
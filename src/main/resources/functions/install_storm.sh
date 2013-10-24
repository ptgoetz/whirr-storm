function install_storm(){
if [ ! -e "/etc/storm/storm.yaml" ]
then
    retry_apt_get install -y unzip

    sudo groupadd storm
    sudo useradd --gid storm --home-dir /home/storm --create-home --shell /bin/bash storm

    wget -N https://dl.dropboxusercontent.com/s/p5wf0hsdab5n9kn/storm-0.9.0-rc2.zip -P /tmp
    sudo unzip -o /tmp/storm-0.9.0-rc2.zip -d /usr/share/
    sudo chown -R storm:storm /usr/share/storm-0.9.0-rc2
    sudo ln -s /usr/share/storm-0.9.0-rc2 /usr/share/storm
    sudo ln -s /usr/share/storm/bin/storm /usr/bin/storm
    sudo rm /tmp/storm-0.9.0-rc2.zip

    sudo mkdir /etc/storm
    sudo chown storm:storm /etc/storm
    sudo mv /usr/share/storm/conf/storm.yaml /etc/storm/
    sudo ln -s /etc/storm/storm.yaml /usr/share/storm/conf/storm.yaml

    echo "storm.messaging.transport: \"backtype.storm.messaging.netty.Context\"" | sudo tee -a /etc/storm/storm.yaml
    echo "storm.messaging.netty.buffer_size: 16384" | sudo tee -a /etc/storm/storm.yaml
    echo "storm.messaging.netty.max_retries: 10" | sudo tee -a /etc/storm/storm.yaml
    echo "storm.messaging.netty.min_wait_ms: 1000" | sudo tee -a /etc/storm/storm.yaml
    echo "storm.messaging.netty.max_wait_ms: 5000" | sudo tee -a /etc/storm/storm.yaml
fi
}
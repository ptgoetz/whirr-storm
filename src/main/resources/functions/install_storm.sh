function install_storm(){
if [ ! -e "/etc/storm/storm.yaml" ]
then
    retry_apt_get install -y unzip

    sudo groupadd storm
    sudo useradd --gid storm --home-dir /home/storm --create-home --shell /bin/bash storm

    URL=$1
    ZIPFILE=${URL##*/}
    VERSION=${ZIPFILE%.*}


    wget -N $URL -P /tmp
    sudo unzip -o /tmp/$ZIPFILE -d /usr/share/
    sudo chown -R storm:storm /usr/share/$VERSION
    sudo ln -s /usr/share/$VERSION /usr/share/storm
    sudo ln -s /usr/share/storm/bin/storm /usr/bin/storm
    sudo rm /tmp/$ZIPFILE

    sudo mkdir /etc/storm
    sudo chown storm:storm /etc/storm
    #sudo mv /usr/share/storm/conf/storm.yaml /etc/storm/
    sudo touch /etc/storm/storm.yaml
    sudo rm /usr/share/storm/conf/storm.yaml
    sudo ln -s /etc/storm/storm.yaml /usr/share/storm/conf/storm.yaml

    sudo mkdir /var/log/storm
    sudo chown storm:storm /var/log/storm

    sudo sed -i 's/${storm.home}\/logs/\/var\/log\/storm/g' /usr/share/storm/logback/cluster.xml

fi
}
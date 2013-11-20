
function install_supervisord() {
    retry_apt_get install -y supervisor
    /etc/init.d/supervisor stop
}
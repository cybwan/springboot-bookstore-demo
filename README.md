# Dubbo Demo

```bash
apt install -y openjdk-8-jre-headless
git clone https://github.com/cybwan/springboot-bookstore-demo.git -b demo
tar zxvf apache-zookeeper-3.6.2-bin.tar.gz
mv apache-zookeeper-3.6.2-bin/conf/zoo_sample.cfg apache-zookeeper-3.6.2-bin/conf/zoo.cfg

wget https://github.com/cybwan/springboot-bookstore-demo/raw/refs/heads/dist/httpbin-dubbo.jar
wget https://github.com/cybwan/springboot-bookstore-demo/raw/refs/heads/dist/curl-dubbo.jar

sudo ip netns add s1
sudo ip link add cni1 type veth peer name eth0 netns s1
sudo ip link set cni1 up
sudo ip addr add 10.0.0.1/24 dev cni1
sudo ip -n s1 link set eth0 up
sudo ip netns exec s1 ifconfig eth0 10.0.0.2/24 up
sudo ip netns exec s1 ip route add default via 10.0.0.1
sudo ip netns exec s1 ifconfig lo up

apache-zookeeper-3.6.2-bin/bin/zkServer.sh start
apache-zookeeper-3.6.2-bin/bin/zkServer.sh status

java -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev
ip netns exec v1 java -jar curl-dubbo.jar --spring.profiles.active=dubbo,dev
ip netns exec v1 curl 10.0.0.2:14001
```

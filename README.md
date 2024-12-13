# Dubbo Demo

```bash
#安装 jdk
apt install -y openjdk-8-jre-headless

git clone https://github.com/cybwan/springboot-bookstore-demo.git -b demo
cd springboot-bookstore-demo
tar zxvf apache-zookeeper-3.6.2-bin.tar.gz
mv apache-zookeeper-3.6.2-bin/conf/zoo_sample.cfg apache-zookeeper-3.6.2-bin/conf/zoo.cfg

sudo ip netns add s1
sudo ip link add cni1 type veth peer name eth0 netns s1
sudo ip link set cni1 up
sudo ip addr add 10.0.0.1/24 dev cni1
sudo ip -n s1 link set eth0 up
sudo ip netns exec s1 ifconfig eth0 10.0.0.2/24 up
sudo ip netns exec s1 ip route add default via 10.0.0.1
sudo ip netns exec s1 ifconfig lo up

#启动 zk 服务
apache-zookeeper-3.6.2-bin/bin/zkServer.sh start
#确认 zk 服务
apache-zookeeper-3.6.2-bin/bin/zkServer.sh status | grep 2181

nohup ip netns exec s1 java -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &

nohup java -jar curl-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.curl.out 2>&1 &

curl 10.0.0.1:14001 -I
curl -s 10.0.0.1:14001
echo $(curl -s 10.0.0.1:14001)

#确认调用正常后, kill 掉 s1 下的 httpbin java 进程, 用下面命令重启
nohup ip netns exec s1 java -DDUBBO_IP_TO_REGISTRY=10.0.0.1 -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &

#pipy dubbo proxy 运行在 10.0.0.1:6666 即可,
#请求转发给 10.0.0.2:6666
```

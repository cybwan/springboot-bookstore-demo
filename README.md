# Dubbo Proxy Demo

```bash
#安装 jdk
sudo apt update
sudo apt install -y openjdk-8-jre-headless

git clone https://github.com/cybwan/springboot-bookstore-demo.git -b demo
tar zxvf springboot-bookstore-demo/apache-zookeeper-3.6.2-bin.tar.gz
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
apache-zookeeper-3.6.2-bin/bin/zkServer.sh stop
apache-zookeeper-3.6.2-bin/bin/zkServer.sh start
#确认 zk 服务
apache-zookeeper-3.6.2-bin/bin/zkServer.sh status | grep 2181


cd springboot-bookstore-demo
#启动 httpbin provider 服务
#nohup sudo ip netns exec s1 java -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &

nohup sudo ip netns exec s1 java -Xms512M -Xmx512M -DDUBBO_IP_TO_REGISTRY=10.0.0.1 -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &


#启动 TCP 代理服务
#pipy springboot-bookstore-demo/dubbo-proxy.js --admin-port=6060 

#启动 fgw dubbo 代理服务
git clone https://github.com/flomesh-io/fgw.git
#pipy --log-level=debug fgw/src/main.js --args --config springboot-bookstore-demo/config.yaml
pipy fgw/src/main.js --args --config springboot-bookstore-demo/fgw.config.yaml

pipy --log-level=debug fgw/src/main.js --args --config springboot-bookstore-demo/dubbo-http-config.yaml


#启动 curl 客户端服务
export SPRING_CONFIG_LOCATION=file:///root/springboot-bookstore-demo/config/

nohup java -Xms512M -Xmx512M -jar springboot-bookstore-demo/curl-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.curl.out 2>&1 &

#测试
echo $(curl -s 10.0.0.1:14001/hostname)
echo $(curl -s 10.0.0.1:14001?count=1)
echo $(curl -s 10.0.0.1:14001/meter?n=1\&c=1)
#十万次请求
echo $(curl -s 10.0.0.1:14001?count=100000)
echo $(curl -s 10.0.0.1:14001/meter?n=100000\&c=10)
#百万次请求
echo $(curl -s 10.0.0.1:14001?count=1000000)


sudo java -Xms16G -Xmx16G -Xss256k -DDUBBO_IP_TO_REGISTRY=172.22.1.11  -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev

sudo java -Xms8G -Xmx8G -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=8 -XX:+UseG1GC -Ddubbo.netty.preferDirect=true -Ddubbo.memory.watermark.high=0.7 -jar curl-dubbo.jar --spring.profiles.active=dubbo,dev 

echo $(curl -s localhost:14001/meter?n=1000000\&c=512)

echo $(curl -s localhost:14001/meter?n=100000\&c=32)
curl -s localhost:14001/meter?n=10\&c=512


sudo java  -Xms4G -Xmx4G -Xss256k -DDUBBO_IP_TO_REGISTRY=192.168.226.5  -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev


pipy --admin-port=6060  fgw/src/main.js --reuse-port --threads=max --args --config dubbo-route.yaml

pipy 'pipy.listen(6666, $=>$.connect("10.0.0.2:6666"))' --admin-port=6060 --threads=4 --reuse-ports

curl localhost:6060/dump/inbound+outbound

sudo ip netns exec s1 java  -Xms16G -Xmx16G -Xss256k -DDUBBO_IP_TO_REGISTRY=172.22.1.11  -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev

sudo java  -Xms16G -Xmx16G -Xss256k -DDUBBO_IP_TO_REGISTRY=172.22.1.11  -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev

pipy 'pipy.listen(6666, $=>$.connect("10.0.0.2:6666"))' --admin-port=6060 --threads=4 --reuse-port
```

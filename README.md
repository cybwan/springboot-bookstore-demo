# Dubbo Proxy Demo

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
apache-zookeeper-3.6.2-bin/bin/zkServer.sh stop
apache-zookeeper-3.6.2-bin/bin/zkServer.sh start
#确认 zk 服务
apache-zookeeper-3.6.2-bin/bin/zkServer.sh status | grep 2181

#启动 httpbin provider 服务
#nohup sudo ip netns exec s1 java -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &

nohup sudo ip netns exec s1 java -Xms512M -Xmx512M -DDUBBO_IP_TO_REGISTRY=10.0.0.1 -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &

#启动 dubbo 代理服务
pipy dubbo-proxy.js --admin-port=6060 

git clone https://github.com/flomesh-io/fgw.git
#pipy --log-level=debug fgw/src/main.js --args --config config.yaml
pipy fgw/src/main.js --args --config config.yaml


#启动 curl 客户端服务
# curl-dubbo.1.jar 十万次请求
# curl-dubbo.jar 百万次请求
nohup java -Xms512M -Xmx512M -jar curl-dubbo.1.jar --spring.profiles.active=dubbo,dev >nohup.curl.out 2>&1 &

#测试
curl 10.0.0.1:14001 -I
curl -s 10.0.0.1:14001
echo $(curl -s 10.0.0.1:14001)
```

## Ref Commands

```bash
#确认调用正常后, kill 掉 s1 下的 httpbin java 进程, 用下面命令重启
#nohup ip netns exec s1 java -DDUBBO_IP_TO_REGISTRY=10.0.0.1 -DDUBBO_PORT_TO_REGISTRY=6666 -jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev >nohup.httpbin.out 2>&1 &

#pipy dubbo proxy 运行在 10.0.0.1:6666 即可,
#请求转发给 10.0.0.2:6666
#pipy 'pipy.listen(6666, $=>$.dump(">>>").connect("10.0.0.2:6666").dump("<<<"))' --log-level=debug:dump
#pipy 'pipy.listen(6666, $=>$.dump().connect("10.0.0.2:6666"))'

#pipy dubbo-proxy.js --admin-port=6060 
#curl localhost:6060/metrics 查询 metrics

endTime:847743039620  -  startTime:816926671097=30816368523
endTime:1657876107191 - startTime:1627374465979=30501641212
endTime:1907307705895 - startTime:1883426785401=23880920494



Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.apache.dubbo.rpc.RpcException: Failed to invoke the method hostname in the service io.flomesh.demo.api.HttpbinService. Tried 3 times of the providers [10.0.0.1:6666] (1/1) from the registry 10.0.0.1:2181 on the consumer 10.0.0.1 using the dubbo version 2.7.23. Last error is: Invoke remote method timeout. method: hostname, provider: 

dubbo://10.0.0.1:6666/io.flomesh.demo.api.HttpbinService?anyhost=true&application=curl&check=false&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&init=false&interface=io.flomesh.demo.api.HttpbinService&metadata-type=remote&methods=hostname&pid=1967&qos.enable=false&register.ip=10.0.0.1&release=2.7.23&remote.application=curl&revision=0.0.1-SNAPSHOT&service.name=ServiceBean:/io.flomesh.demo.api.HttpbinService:v1&side=consumer&sticky=false&timestamp=1739792202604&version=v1, cause: org.apache.dubbo.remoting.TimeoutException: Waiting server-side response timeout by scan timer. start time: 2025-02-17 12:07:10.204, end time: 2025-02-17 12:07:11.222, client elapsed: 0 ms, server elapsed: 1018 ms, timeout: 1000 ms, request: Request [id=632751, version=2.0.2, twoway=true, event=false, broken=false, data=null], channel: /10.0.0.1:60034 -> /10.0.0.1:6666] with root cause
```


## 服务部署

```bash
kubectl apply -f manifests/fgw-dubbo-demo-svcs.yaml

# 部署 server
kubectl apply -f manifests/fgw-dubbo-demo-deploy-server.yaml

# 部署 fgw
export fgw_dubbo_server_ip="$(kubectl get pod --selector app=fgw-dubbo-server -o jsonpath='{.items[0]..status.podIP}')"
echo fgw_dubbo_server_ip $fgw_dubbo_server_ip

sed -i "s/127.0.0.1/$fgw_dubbo_server_ip/g" manifests/fgw-dubbo-demo-deploy-route.yaml

kubectl apply -f manifests/fgw-dubbo-demo-deploy-route.yaml

# 部署 client
export fgw_dubbo_route_ip="$(kubectl get pod --selector app=fgw-dubbo-route -o jsonpath='{.items[0]..status.podIP}')"
echo fgw_dubbo_route_ip $fgw_dubbo_route_ip

sed -i "s/localhost/$fgw_dubbo_route_ip/g" manifests/fgw-dubbo-demo-deploy-client.yaml

kubectl apply -f manifests/fgw-dubbo-demo-deploy-client.yaml
```

## 延迟测试

```bash
export fgw_dubbo_client_ip="$(kubectl get pod --selector app=fgw-dubbo-client -o jsonpath='{.items[0]..status.podIP}')"
echo fgw_dubbo_client_ip $fgw_dubbo_client_ip

curl http://$fgw_dubbo_client_ip:8080/\?concurrency\=8\&duration\=10\&rate\=10000
```

## 服务卸载

```bash
kubectl delete -f manifests/fgw-dubbo-demo-deploy-client.yaml
kubectl delete -f manifests/fgw-dubbo-demo-deploy-route.yaml
kubectl delete -f manifests/fgw-dubbo-demo-deploy-server.yaml
kubectl delete -f manifests/fgw-dubbo-demo-svcs.yaml
```


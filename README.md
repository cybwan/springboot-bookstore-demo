## 服务部署

```bash
kubectl create namespace flomesh-demo

kubectl apply -n flomesh-demo -f manifests/fgw-dubbo-demo-svcs.yaml

# 部署 server
kubectl apply -n flomesh-demo -f manifests/fgw-dubbo-demo-deploy-server.yaml

# 部署 fgw
kubectl apply -n flomesh-demo -f manifests/fgw-dubbo-demo-deploy-route.yaml
```

## 测试

```bash
export fgw_dubbo_server_pod_name="$(kubectl get pod -n flomesh-demo --selector app=fgw-dubbo-server -o jsonpath='{.items[0].metadata.name}')"
echo fgw_dubbo_server_pod_name $fgw_dubbo_server_pod_name

kubectl exec -it -n flomesh-demo $fgw_dubbo_server_pod_name -- curl -H 'Host: test.com' -d @data.json http://fgw-dubbo-route:6868/user

#10k DTO
kubectl exec -it -n flomesh-demo $fgw_dubbo_server_pod_name -- curl -H 'Host: test.com' -d @data.10k.json http://fgw-dubbo-route:6868/user
```

## 服务卸载

```bash
kubectl delete -f manifests/fgw-dubbo-demo-deploy-route.yaml
kubectl delete -f manifests/fgw-dubbo-demo-deploy-server.yaml
kubectl delete -f manifests/fgw-dubbo-demo-svcs.yaml
```


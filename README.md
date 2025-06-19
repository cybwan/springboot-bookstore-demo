

```
kubectl apply -f manifests/fgw-dubbo-demo.yaml

#kubectl rollout restart deployment fgw-dubbo-demo
```



```bash
export fgw_dubbo_pod_ip="$(kubectl get pod --selector app=fgw-dubbo-demo -o jsonpath='{.items[0]..status.podIP}')"
echo fgw_dubbo_pod_ip $fgw_dubbo_pod_ip

curl http://127.0.0.1:8080/\?concurrency\=8\&duration\=30
Total requests: 32971 in 30s
Request rate: 1099.033333333333req/s
Avg. latency: 0.184083163993ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=8&duration=30
Total requests: 32009 in 30s
Request rate: 1066.966666666667req/s
Avg. latency: 0.337296229186ms
```


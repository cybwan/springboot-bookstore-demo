

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

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=16&duration=30
Total requests: 30161 in 30.001s
Request rate: 1005.333155561481req/s
Avg. latency: 0.318523556912ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=32&duration=30
Total requests: 31656 in 30s
Request rate: 1055.2req/s
Avg. latency: 0.326391616122ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=64&duration=30
Total requests: 29197 in 30.001s
Request rate: 973.200893303557req/s
Avg. latency: 0.311433982944ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=128&duration=30
Total requests: 30891 in 30s
Request rate: 1029.7req/s
Avg. latency: 0.319371532162ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=256&duration=30
Total requests: 28919 in 30.001s
Request rate: 963.934535515483req/s
Avg. latency: 0.310229641412ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=512&duration=30
Total requests: 27923 in 30.001s
Request rate: 930.735642145262req/s
Avg. latency: 0.303522150199ms
```


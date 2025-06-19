

```
kubectl apply -f manifests/fgw-dubbo-demo.yaml
```



```bash
export fgw_dubbo_pod_ip="$(kubectl get pod --selector app=fgw-dubbo-demo -o jsonpath='{.items[0]..status.podIP}')"
echo fgw_dubbo_pod_ip $fgw_dubbo_pod_ip


curl http://$fgw_dubbo_pod_ip:8080/?concurrency=512&duration=30

Total requests: 19633 in 30.001s
Request rate: 654.411519616013req/s
Avg. latency: 0.33953751337ms
```


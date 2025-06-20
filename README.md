

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
#8c
Total requests: 32009 in 30s
Request rate: 1066.966666666667req/s
Avg. latency: 0.337296229186ms

#4c
Total requests: 28591 in 30s
Request rate: 953.033333333333req/s
Avg. latency: 0.313453429401ms

#4c cpu:1 mem:1G
Total requests: 29756 in 30.001s
Request rate: 991.833605546482req/s
Avg. latency: 0.322660807904ms

#4c cpu:2 mem:2G
Total requests: 29604 in 30.001s
Request rate: 986.767107763074req/s
Avg. latency: 0.318596135657ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=16&duration=30
#8c
Total requests: 30161 in 30.001s
Request rate: 1005.333155561481req/s
Avg. latency: 0.318523556912ms

#4c
Total requests: 28122 in 30.001s
Request rate: 937.368754374854req/s
Avg. latency: 0.30535370884ms

#4c cpu:1 mem:1G
Total requests: 30415 in 30.001s
Request rate: 1013.799540015333req/s
Avg. latency: 0.319520236725ms

#4c cpu:2 mem:2G
Total requests: 29966 in 30.001s
Request rate: 998.833372220926req/s
Avg. latency: 0.321367950344ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=32&duration=30
#8c
Total requests: 31656 in 30s
Request rate: 1055.2req/s
Avg. latency: 0.326391616122ms

#4c
Total requests: 27795 in 30s
Request rate: 926.5req/s
Avg. latency: 0.302485986688ms

#4c cpu:1 mem:1G
Total requests: 30925 in 30s
Request rate: 1030.833333333333req/s
Avg. latency: 0.323086693614ms

#4c cpu:2 mem:2G
Total requests: 30853 in 30s
Request rate: 1028.433333333333req/s
Avg. latency: 0.326809710563ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=64&duration=30
#8c
Total requests: 29197 in 30.001s
Request rate: 973.200893303557req/s
Avg. latency: 0.311433982944ms

#4c
Total requests: 26626 in 30.001s
Request rate: 887.503749875004req/s
Avg. latency: 0.294918388042ms

#4c cpu:1 mem:1G
Total requests: 27832 in 30.001s
Request rate: 927.702409919669req/s
Avg. latency: 0.307276192871ms

#4c cpu:2 mem:2G
Total requests: 28251 in 30.001s
Request rate: 941.668611046298req/s
Avg. latency: 0.307135605819ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=128&duration=30
#8c
Total requests: 30891 in 30s
Request rate: 1029.7req/s
Avg. latency: 0.319371532162ms

#4c
Total requests: 25786 in 30s
Request rate: 859.533333333333req/s
Avg. latency: 0.282771969286ms

#4c cpu:1 mem:1G
Total requests: 27525 in 30.001s
Request rate: 917.469417686077req/s
Avg. latency: 0.305437965486ms

#4c cpu:2 mem:2G
Total requests: 28394 in 30s
Request rate: 946.466666666667req/s
Avg. latency: 0.308485701204ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=256&duration=30
#8c
Total requests: 28919 in 30.001s
Request rate: 963.934535515483req/s
Avg. latency: 0.310229641412ms

#4c
Total requests: 24901 in 30s
Request rate: 830.033333333333req/s
Avg. latency: 0.275555319064ms

#4c cpu:1 mem:1G
Total requests: 25983 in 30s
Request rate: 866.1req/s
Avg. latency: 0.298977177385ms

#4c cpu:2 mem:2G
Total requests: 25710 in 30s
Request rate: 857req/s
Avg. latency: 0.290026098794ms

curl http://$fgw_dubbo_pod_ip:8080/?concurrency=512&duration=30
#8c
Total requests: 27923 in 30.001s
Request rate: 930.735642145262req/s
Avg. latency: 0.303522150199ms

#4c
Total requests: 25148 in 30.001s
Request rate: 838.238725375821req/s
Avg. latency: 0.287305272785ms

#4c cpu:1 mem:1G
Total requests: 28069 in 30s
Request rate: 935.633333333333req/s
Avg. latency: 0.313683850511ms

#4c cpu:2 mem:2G
Total requests: 25665 in 30s
Request rate: 855.5req/s
Avg. latency: 0.291898383012ms
```


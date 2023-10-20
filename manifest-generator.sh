#!/usr/bin/env bash

rm -f manifests/*/book*.yaml

generate_yaml() {
    project_name=$(echo $1 | cut -d "-" -f 1)
cat <<EOF
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $1
  labels:
    version: ${2:-v1}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: $1
      version: ${2:-v1}
  template:
    metadata:
      labels:
        app: $1
        version: ${2:-v1}
      annotations:
          prometheus.io/scrape: "true"
          prometheus.io/path: "/actuator/prometheus"
          prometheus.io/port: "14001"
    spec:
      containers:
        - name: $1
          image: addozhang/${project_name}-$3:latest
          ports:
            - containerPort: 14001
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: '$3,prod'
            - name: IDENTITY
              value: $1
            - name: SPRING_CLOUD_CONSUL_DISCOVERY_TAGS
              value: "version=${2:-v1}"
#            - name: SPRING_CLOUD_CONSUL_HOST
#              value: 'consul.default'
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 14001
            initialDelaySeconds: 5
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 14001
            initialDelaySeconds: 60
            periodSeconds: 30
EOF
}

for module in bookwarehouse bookstore bookbuyer bookthief; do
  generate_yaml $module "" consul > ./manifests/consul/$module-consul.yaml
  generate_yaml $module "" eureka > ./manifests/eureka/$module-eureka.yaml
done

for module in bookstore-v2; do
  generate_yaml $module v2 consul > ./manifests/consul/$module-consul.yaml
  generate_yaml $module v2 eureka > ./manifests/eureka/$module-eureka.yaml
done

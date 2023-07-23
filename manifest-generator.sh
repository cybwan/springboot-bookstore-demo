#!/usr/bin/env bash

generate_yaml() {
cat <<EOF
apiVersion: v1
kind: ServiceAccount
metadata:
  name: $1
---
apiVersion: v1
kind: Service
metadata:
  name: $1
spec:
  selector:
    app: $1
  ports:
    - protocol: TCP
      port: 14001
      targetPort: 14001
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $1
spec:
  replicas: 1
  selector:
    matchLabels:
      app: $1
  template:
    metadata:
      labels:
        app: $1
    spec:
      serviceAccountName: $1
      containers:
        - name: $1
          image: addozhang/${1}:latest
          ports:
            - containerPort: 14001
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: 'consul,prod'
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
  generate_yaml $module > ./manifests/$module.yaml
done

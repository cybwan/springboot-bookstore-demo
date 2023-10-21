#!/usr/bin/env bash

generate_yaml() {
    local module=$1
    local version=${2:-v1}
    local registry_type=$3

    # Determine the deployment name and app label
    local deploy_name=$module
    local app_label=$module

    if [[ $module == "bookstore-v2" ]]; then
        app_label="bookstore"
    fi

    cat <<EOF > manifests/$registry_type/$deploy_name-$registry_type.yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $deploy_name
  labels:
    version: $version
spec:
  replicas: 1
  selector:
    matchLabels:
      app: $app_label
      version: $version
  template:
    metadata:
      labels:
        app: $app_label
        version: $version
      annotations:
          prometheus.io/scrape: "true"
          prometheus.io/path: "/actuator/prometheus"
          prometheus.io/port: "14001"
    spec:
      containers:
        - name: $app_label
          image: addozhang/${app_label}-$registry_type:latest
          ports:
            - containerPort: 14001
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: '$registry_type,prod'
            - name: IDENTITY
              value: $deploy_name
            - name: SPRING_CLOUD_CONSUL_DISCOVERY_TAGS
              value: "version=$version"
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

# Remove existing manifests
rm -f manifests/*/book*.yaml

# For each module, generate the yaml
for module in bookwarehouse bookstore bookbuyer bookthief; do
    generate_yaml "$module" v1 consul
    generate_yaml "$module" v1 eureka
done

generate_yaml "bookstore-v2" v2 consul
generate_yaml "bookstore-v2" v2 eureka
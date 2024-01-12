#!/usr/bin/env bash

generate_yaml() {
    local module=$1
    local version=${2:-v1}
    local registry_type=$3
    local tag=$4

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
    spec:
      containers:
        - name: $app_label
          image: addozhang/${app_label}-$registry_type:$tag
          imagePullPolicy: Always
          ports:
            - containerPort: 20880
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: '$registry_type,prod'
            - name: IDENTITY
              value: $deploy_name
#            - name: DUBBO_REGISTRY_ADDRESS
#              value: 'zookeeper.default'
EOF
}

# Remove existing manifests
rm -f manifests/*/book*.yaml

# For each module, generate the yaml
for module in bookwarehouse bookstore bookbuyer bookthief; do
    generate_yaml "$module" v1 dubbo 0.3
done

generate_yaml "bookstore-v2" v2 dubbo 0.3

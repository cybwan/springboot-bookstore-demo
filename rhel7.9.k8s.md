## init

```bash
sed -i 's/IPV6_AUTOCONF=yes/IPV6_AUTOCONF=no/g' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i 's/IPV6_DEFROUTE=yes/IPV6_DEFROUTE=no/g' /etc/sysconfig/network-scripts/ifcfg-ens33

sed -i 's/IPV6_AUTOCONF=yes/IPV6_AUTOCONF=no/g' /etc/sysconfig/network-scripts/ifcfg-ens34
sed -i 's/IPV6_DEFROUTE=yes/IPV6_DEFROUTE=no/g' /etc/sysconfig/network-scripts/ifcfg-ens34

cat > /etc/hosts <<EOF
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
192.168.127.13 rhel master
EOF

sudo systemctl stop swap.target
sudo systemctl disable --now swap.target
sudo systemctl mask swap.target
sudo sed -i '/.*swap/d' /etc/fstab

sudo systemctl stop firewalld
sudo systemctl disable firewalld

mkdir /iso
cat >> /etc/fstab <<EOF
/dev/sr0 /iso iso9660 defaults 0 0
EOF

sed -i 's/enabled=1/enabled=0/' /etc/yum/pluginconf.d/subscription-manager.conf

cat > /etc/yum.repos.d/redhat.repo <<EOF
[RHEL-CDROM]
name=RHEL 7 CDROM
baseurl=file:///iso
enabled=1
gpgcheck=1
gpgkey=file:///iso/RPM-GPG-KEY-redhat-release
EOF

cat > /etc/yum.repos.d/epel.repo <<EOF
[epel]
name=Extra Packages for CentOS 7
baseurl=https://mirrors.aliyun.com/epel/7/x86_64/
enabled=1
gpgcheck=0
EOF

cat > /etc/yum.repos.d/docker-ce.repo <<EOF
[docker-ce-stable]
name=Docker CE Stable - \$basearch
baseurl=https://mirrors.aliyun.com/docker-ce/linux/centos/7.9/\$basearch/stable
enabled=1
gpgcheck=1
gpgkey=https://mirrors.aliyun.com/docker-ce/linux/centos/gpg
EOF

cat > /etc/yum.repos.d/kubernetes.repo <<EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
EOF

if [ ! -f /etc/sysctl.d/k8s.conf ]; then
sudo tee /etc/sysctl.d/k8s.conf <<EOF
# ipv4配置
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1
net.ipv4.conf.all.rp_filter = 0
net.ipv4.conf.all.forwarding = 1
EOF
fi

if [ ! -f /etc/sysctl.d/fs.conf ]; then
sudo tee /etc/sysctl.d/fs.conf <<EOF
fs.file-max=655360
fs.inotify.max_user_watches = 655350
fs.inotify.max_user_instances = 1024
EOF
fi

cat >> /etc/security/limits.conf <<EOF
* soft nofile 655350
* hard nofile 655350
EOF

if [ ! -f /etc/modules-load.d/k8s.conf ]; then
sudo tee /etc/modules-load.d/k8s.conf <<EOF
overlay
br_netfilter
EOF
sudo modprobe overlay
sudo modprobe br_netfilter
fi

sudo sysctl --system

HTTP_PROXY="http://192.168.127.91:7890"
HTTPS_PROXY="http://192.168.127.91:7890"
ALL_PROXY="socks5://192.168.127.91:7890"
NO_PROXY="localhost,127.0.0.1,.localdomain.com,127.0.0.0/8,192.168.0.0/16,10.244.0.0/16,10.96.0.0/12"

export http_proxy=${HTTP_PROXY} https_proxy=${HTTPS_PROXY} all_proxy=${ALL_PROXY}

yum --showduplicates list kubeadm | grep 1.28

cd /tmp
sudo yum install -y wget policycoreutils-python libseccomp
wget https://mirrors.aliyun.com/centos/7/extras/x86_64/Packages/container-selinux-2.107-1.el7_6.noarch.rpm
sudo rpm -i container-selinux-2.107-1.el7_6.noarch.rpm

wget https://mirrors.aliyun.com/centos/7/extras/x86_64/Packages/slirp4netns-0.4.3-4.el7_8.x86_64.rpm
sudo rpm -i slirp4netns-0.4.3-4.el7_8.x86_64.rpm

wget https://mirrors.aliyun.com/centos/7/extras/x86_64/Packages/fuse3-libs-3.6.1-4.el7.x86_64.rpm
sudo rpm -i fuse3-libs-3.6.1-4.el7.x86_64.rpm

wget https://mirrors.aliyun.com/centos/7/extras/x86_64/Packages/fuse-overlayfs-0.7.2-6.el7_8.x86_64.rpm
sudo rpm -i fuse-overlayfs-0.7.2-6.el7_8.x86_64.rpm

yum install -y bc jq docker-ce kubeadm-1.28.2 kubelet-1.28.2 kubectl-1.28.2

wget https://github.com/Mirantis/cri-dockerd/releases/download/v0.3.3/cri-dockerd-0.3.3-3.el7.x86_64.rpm
cri-dockerd-0.3.3-3.el7.x86_64.rpm

sed -i "/\[Service\]/aEnvironment=\"NO_PROXY=${NO_PROXY}\"" /usr/lib/systemd/system/docker.service
sed -i "/\Service\]/aEnvironment=\"HTTPS_PROXY=${HTTPS_PROXY}\"" /usr/lib/systemd/system/docker.service
sed -i "/\[Service\]/aEnvironment=\"HTTP_PROXY=${HTTP_PROXY}\"" /usr/lib/systemd/system/docker.service

cat /usr/lib/systemd/system/docker.service

if [ ! -f /etc/docker/daemon.json ]; then
sudo tee /etc/docker/daemon.json <<EOF
{
  "insecure-registries": [
    "192.168.0.0/16"
  ],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "50m",
    "max-file": "1"
  },
  "exec-opts": [
    "native.cgroupdriver=systemd"
  ]
}
EOF
fi

mkdir -p /opt/cni

sed -i 's#ExecStart=.*#ExecStart=/usr/bin/cri-dockerd --container-runtime-endpoint fd:// --pod-infra-container-image registry.k8s.io/pause:3.9 --ipv6-dual-stack#g' /usr/lib/systemd/system/cri-docker.service

sudo systemctl daemon-reload
sudo systemctl enable docker.service
sudo systemctl enable cri-docker.service
sudo systemctl enable cri-docker.socket
sudo systemctl enable kubelet.service

sudo yum install -y bash-completion

cat >> ~/.bashrc <<EOF
#Enabling tab-completion
complete -cf sudo
complete -cf man
source <(kubectl completion bash)
source <(kubeadm completion bash)
alias wk="watch -n 2 kubectl get pods -A -o wide"
alias k=kubectl
EOF

cat >> /etc/inputrc <<EOF
# do not show hidden files in the list
set match-hidden-files off
# auto complete ignoring case
set show-all-if-ambiguous on
set completion-ignore-case on
"\e[A": history-search-backward
"\e[B": history-search-forward
EOF

sudo tee >> ~/.bashrc <<EOF
alias km="export http_proxy=${HTTP_PROXY} https_proxy=${HTTPS_PROXY} all_proxy=${ALL_PROXY}"
EOF

sudo systemctl reboot
```

## master

```bash
hostnamectl set-hostname master

sed -i 's/192.168.127.13/192.168.127.141/g' /etc/sysconfig/network-scripts/ifcfg-ens33
sed -i 's/192.168.226.13/192.168.226.141/g' /etc/sysconfig/network-scripts/ifcfg-ens34

cat > /etc/hosts <<EOF
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
192.168.127.141 master
192.168.127.142 worker1
EOF

sudo systemctl reboot

cat > /opt/cni/kubeadm.yaml <<EOF
apiVersion: kubeadm.k8s.io/v1beta3
bootstrapTokens: # 可以指定bootstrapToken，默认24小过期自动删除
  - groups:
      - system:bootstrappers:kubeadm:default-node-token
    token: abcdef.0123456789abcdef
    ttl: 24h0m0s
    usages:
      - signing
      - authentication
kind: InitConfiguration # 初始Master节点的私有配置
localAPIEndpoint:
  advertiseAddress: 192.168.127.141 # master节点ip
  bindPort: 6443
nodeRegistration:
  criSocket: unix:///var/run/cri-dockerd.sock
  imagePullPolicy: IfNotPresent
  name: master
  taints: null
  kubeletExtraArgs:
    node-ip: "192.168.127.141"    # master节点ip
---
apiServer:
  timeoutForControlPlane: 4m0s
  certSANs:
    - "master"
    - "192.168.127.141"
  extraArgs:
    service-cluster-ip-range: 10.96.0.0/18
    bind-address: "0.0.0.0"
    secure-port: "6443"
apiVersion: kubeadm.k8s.io/v1beta3
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controlPlaneEndpoint: "master:6443"    # api-server地址,建议用域名
controllerManager:
  extraArgs:
    bind-address: "0.0.0.0"
dns: {}
etcd:
  local:
    dataDir: /var/lib/etcd
imageRepository: registry.k8s.io
kind: ClusterConfiguration # 所有Master节点的公共配置
kubernetesVersion: 1.28.0
networking:
  dnsDomain: cluster.local
  podSubnet: 10.244.0.0/16    # ipv4放在前面，那么kubectl get node时显示的是ipv4地址
  serviceSubnet: 10.96.0.0/18    # ipv4放在前面，那么kubectl get service时显示的是ipv4地址
scheduler:
  extraArgs:
    bind-address: "0.0.0.0"
---
apiVersion: kubelet.config.k8s.io/v1beta1
kind: KubeletConfiguration
failSwapOn: false
cgroupDriver: systemd
healthzBindAddress: "127.0.0.1"
---
apiVersion: kubeproxy.config.k8s.io/v1alpha1
kind: KubeProxyConfiguration
clusterCIDR: "10.244.0.0/16"    # Pod的地址范围
mode: "iptables"
EOF

mkdir -p /etc/cni/net.d
cat > /etc/cni/net.d/10-macvlan.conf <<EOF
{
  "name": "macvlannet",
  "type": "macvlan",
  "master": "ens33",              // 替换为你的主机网络接口
  "mode": "bridge",
  "ipam": {
    "type": "host-local",
    "subnet": "192.168.127.0/24", // 替换为你的子网
    "rangeStart": "192.168.127.40",
    "rangeEnd": "192.168.127.60",
    "gateway": "192.168.127.1",
    "routes": [{ "dst": "0.0.0.0/0" }]
  }
}
EOF

kubeadm init --config /opt/cni/kubeadm.yaml --ignore-preflight-errors=SystemVerification --upload-certs -v 5

mkdir -p ~/.kube
sudo cp -i /etc/kubernetes/admin.conf ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config

kubectl taint nodes --all node-role.kubernetes.io/control-plane-
kubectl label node ${HOSTNAME} node-role.kubernetes.io/worker=worker --overwrite
```

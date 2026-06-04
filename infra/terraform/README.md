# PulseOps Terraform Skeleton

This folder shows how PulseOps can be installed into an existing Kubernetes cluster with Terraform and Helm.

It is intentionally provider-light: point `kubeconfig_path` at a local Kind, Minikube, Docker Desktop, or managed Kubernetes cluster, then Terraform renders the Helm chart in `../../charts/pulseops`.

```bash
terraform init
terraform plan -var="kubeconfig_path=$HOME/.kube/config"
terraform apply -var="kubeconfig_path=$HOME/.kube/config"
```

Use real secret management for production environments. The default values are demo credentials for local portfolio deployment only.

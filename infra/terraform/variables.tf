variable "kubeconfig_path" {
  description = "Path to a kubeconfig file for the target cluster."
  type        = string
  default     = "~/.kube/config"
}

variable "namespace" {
  description = "Kubernetes namespace where PulseOps will be installed."
  type        = string
  default     = "pulseops"
}

variable "backend_image" {
  description = "Backend image repository."
  type        = string
  default     = "pulseops-backend"
}

variable "frontend_image" {
  description = "Frontend image repository."
  type        = string
  default     = "pulseops-frontend"
}

variable "image_tag" {
  description = "Image tag to deploy for backend and frontend."
  type        = string
  default     = "latest"
}

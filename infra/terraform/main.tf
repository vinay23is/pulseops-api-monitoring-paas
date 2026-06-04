resource "kubernetes_namespace" "pulseops" {
  metadata {
    name = var.namespace
  }
}

resource "helm_release" "pulseops" {
  name       = "pulseops"
  namespace  = kubernetes_namespace.pulseops.metadata[0].name
  chart      = "${path.module}/../../charts/pulseops"
  wait       = true
  timeout    = 600

  set {
    name  = "backend.image.repository"
    value = var.backend_image
  }

  set {
    name  = "frontend.image.repository"
    value = var.frontend_image
  }

  set {
    name  = "backend.image.tag"
    value = var.image_tag
  }

  set {
    name  = "frontend.image.tag"
    value = var.image_tag
  }
}

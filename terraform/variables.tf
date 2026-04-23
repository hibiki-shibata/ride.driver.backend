variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "region" {
  description = "GCP Region"
  type        = string
  default     = "us-central1"
}

variable "app_name" {
  description = "Application name"
  type        = string
}

variable "base_docler_image_url" {
  description = "Base Docker image URL for Cloud Run service"
  type        = string
}
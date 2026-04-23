terraform {
  required_version = ">= 1.5"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }

#   # Store state remotely in GCS (recommended)
#   backend "gcs" {
#     bucket = "your-tf-state-bucket"
#     prefix = "terraform/state"
#   }
}

provider "google" {
  project = var.project_id
  region  = var.region
}
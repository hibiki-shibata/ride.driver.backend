terraform {
  required_version = ">= 1.4"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 7.2"
    }
  }

#   # Store state remotely in GCS
#   backend "gcs" {
#     bucket = "ride-tf-state-bucket"
#     prefix = "terraform/state"
#   }
}

provider "google" {
  project = var.project_id
  region  = var.region
}
resource "google_cloud_run_v2_service" "cloud_run_service" {
  depends_on = [
    google_vpc_access_connector.vpc_connector,
    google_project_iam_member.cloud_run_sa_roles,
    google_project_service.apis
  ] 
  deletion_protection = false
  name     = "${var.app_name}-run-service"
  location = var.region

  ingress = "INGRESS_TRAFFIC_ALL"

  template {
    service_account = google_service_account.cloud_run_sa.email

    # --add-cloudsql-instances
    volumes {
      name = "cloudsql"
      cloud_sql_instance {
        instances = [
          "${google_sql_database_instance.postgres_instance.connection_name}"
        ]
      }
    }

    # --vpc-connector
    vpc_access {
      connector = google_vpc_access_connector.vpc_connector.id 
      egress    = "PRIVATE_RANGES_ONLY"
    }

    scaling {
      min_instance_count = 0
      max_instance_count = 2
    }

    containers {
      image = "us-docker.pkg.dev/cloudrun/container/hello:latest"  #place holder

      resources {
        limits = {
          memory = "512Mi"
          cpu    = "1"
        }
        cpu_idle = true  # --cpu-throttling
      }
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
      env {
        name  = "DB_PORT"
        value = "5432"
      }
      # secrets
      env {
        name = "DB_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = "db-password"
            version = "latest"
          }
        }
      }
      env {
        name = "DB_USERNAME"
        value_source {
          secret_key_ref {
            secret  = "db-username"
            version = "latest"
          }
        }
      }
      env {
        name = "DB_HOST"
        value_source {
          secret_key_ref {
            secret  = "db-host"
            version = "latest"
          }
        }
      }
      env {
        name = "DB_NAME"
        value_source {
          secret_key_ref {
            secret  = "db-name"
            version = "latest"
          }
        }
      }
      env {
        name = "CLOUD_SQL_CONNECTION_NAME"
        value_source {
          secret_key_ref {
            secret  = "cloud-sql-connection-name"
            version = "latest"
          }
        }
      }
      env {
        name = "JWT_SECRET_STRING"
        value_source {
          secret_key_ref {
            secret  = "jwt-secret-string"
            version = "latest"
          }
        }
      }
      volume_mounts {
        name       = "cloudsql"
        mount_path = "/cloudsql"
      }
    }
  }
}

# --allow-unauthenticated
resource "google_cloud_run_v2_service_iam_member" "cloud_run_service_iam" {
  project  = var.project_id
  location = var.region
  name     = google_cloud_run_v2_service.cloud_run_service.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
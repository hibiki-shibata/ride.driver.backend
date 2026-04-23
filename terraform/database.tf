resource "google_sql_database_instance" "postgres_instance" {
  name             = "${var.app_name}-postgres-instance"
  database_version = "POSTGRES_15"
  region           = var.region

  settings {
    tier = "db-f1-micro" # Shared CPU, 0.6 GB RAM - Cheapest

    ip_configuration {
      ipv4_enabled                                  = false  # Private IP only
      private_network                               = data.google_compute_network.default.id
      enable_private_path_for_google_cloud_services = true
    }
  }

  deletion_protection = false
  depends_on          = [google_service_networking_connection.private_vpc_connection]
}

resource "google_sql_database" "postgres_db" {
  name     = "${var.app_name}-db"
  instance = google_sql_database_instance.postgres_instance.connection_name
}

# Create a user and credential for the database manually!!!
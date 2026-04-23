# Checked
resource "google_secret_manager_secret" "db_password" {
  secret_id = "db-password"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret" "db_host" {
  secret_id = "db-host"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret" "db_name" {
  secret_id = "db-name"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret" "db_username" {
  secret_id = "db-username"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret" "cloud_sql_connection_name" {
  secret_id = "cloud-sql-connection-name"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret" "jwt_secret_string" {
  secret_id = "jwt-secret-string"
  replication {
    auto {}
  }
}

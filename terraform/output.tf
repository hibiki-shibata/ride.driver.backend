# Checked
output "cloud_run_url" {
  value = google_cloud_run_v2_service.cloud_run_service.uri
}

output "db_private_ip" {
  value     = google_sql_database_instance.postgres-instance.private_ip_address
  sensitive = true
}
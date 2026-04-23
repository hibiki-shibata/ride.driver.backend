resource "google_vpc_access_connector" "vpc_connector" {
  name          = "${var.app_name}-connector"
  region        = var.region
  network       = "default"
  min_instances = 2
  max_instances = 3
  ip_cidr_range = "10.8.0.0/28"
  depends_on    = [google_project_service.apis]
}
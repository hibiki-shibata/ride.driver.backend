# network.tf
data "google_compute_network" "default" {
  name = "default"
}

# Reserve IP range (For later use with VPC peering for Cloud SQL private IP)
resource "google_compute_global_address" "private_ip_range" {
  name          = "${var.app_name}-private-ip-range"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       =  data.google_compute_network.default.id
  depends_on    = [google_project_service.apis]
}

# Create peering connection between VPC and Cloud SQL
resource "google_service_networking_connection" "private_vpc_connection" {
  network                 =  data.google_compute_network.default.id 
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip_range.name]
}
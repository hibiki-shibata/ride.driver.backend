resource "google_artifact_registry_repository" "ride_artifact_repository" {
  repository_id = "${var.app_name}-artifact-repository"
  location      = var.region
  format        = "DOCKER"
  description   = "Docker repository for ride backend"

  depends_on = [google_project_service.apis]
}
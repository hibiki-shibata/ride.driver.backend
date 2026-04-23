resource "google_artifact_registry_repository" "ride-artifact-repository" {
  location      = var.region
  repository_id = "ride-artifact-repository"
  format        = "DOCKER"
  description   = "Docker repository for ride driver backend"

  depends_on = [google_project_service.apis]
}
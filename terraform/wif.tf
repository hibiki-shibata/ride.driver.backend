# 1. Create the pool
resource "google_iam_workload_identity_pool" "github_wif_pool" {
  project                   = var.project_id
  workload_identity_pool_id = "github-pool"
  display_name              = "GitHub Actions Pool"
}

# gcloud iam workload-identity-pools create-github-provider [PROVIDER_NAME] --workload-identity-pool=[POOL_NAME] --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" --attribute-condition="assertion.repository == 'YOUR_GITHUB_USERNAME/ride.driver.backend'" --issuer-uri="https://token.actions.githubusercontent.com"
resource "google_iam_workload_identity_pool_provider" "github_wif_provider" {
  project                            = var.project_id
  workload_identity_pool_id          = google_iam_workload_identity_pool.github_wif_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = "github-provider"

  attribute_mapping = {
    "google.subject"       = "assertion.sub"
    "attribute.actor"      = "assertion.actor"
    "attribute.repository" = "assertion.repository"
  }

  attribute_condition = "assertion.repository == 'hibiki-shibata/ride.driver.backend'"

  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }
}

# gcloud iam service-accounts add-iam-policy-binding [SA_NAME] --role=roles/iam.workloadIdentityUser --member="principalSet://iam.googleapis.com/[POOL_RESOURCE_ID]/attribute.repository/YOUR_GITHUB_USERNAME/ride.driver.backend"
resource "google_service_account_iam_member" "wif_binding" {
  service_account_id = google_service_account.cloud_run_sa.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.github_wif_pool.name}/attribute.repository/hibiki-shibata/ride.driver.backend"
}
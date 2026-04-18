# Deploy Note

Google Cloud Run is used for deployment. The deployment process is automated using Github Actions.

### Major flow
1. Either push or merge to main branch
2. Github Actions as CI/CD will be triggered
3. Build the Docker image and push it to Google Container Registry (GCR)
4. Deploy the new image to Google Cloud Run


### Setup Google Cloud Run
1. gcloud auth login
2. gcloud config set project [PROJECT_ID] (e.g. [PROJECT_NAME])
3. Enable Cloud Run API: 
    - gcloud services enable run.googleapis.com
    - gcloud services enable artifactregistry.googleapis.com
    - gcloud services enable iamcredentials.googleapis.com
    - gcloud services enable cloudresourcemanager.googleapis.com
4. Create Artifact Registry: 
    - gcloud artifacts repositories create [REPOSITORY_NAME] --repository-format=docker --location=[REGION] (e.g. gcr-repo, us-central1)
5. Create Service Accounts:
    - gcloud iam service-accounts create [SERVICE_ACCOUNT_NAME] --display-name="[DISPLAY_NAME]" (e.g. cloud-run-sa, Cloud Run Service Account)
    - gcloud iam service-accounts create [RUNTIME_SERVICE_ACCOUNT_NAME] --display-name="[DISPLAY_NAME]" (e.g. runtime-sa, Runtime Service Account)
6. Grant permissions to Service Account:
    - gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/run.admin"
    - gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
    - gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/artifactregistry.writer"


7. Get Project ID
    - gcloud projects describe [PROJECT_NAME]
8. Create Identity Pool
    - gcloud iam workload-identity-pools create [POOL_NAME] --location="global" --display-name="[DISPLAY_NAME]" (e.g. github-pool, Github Pool)
9. Create Identity Provider
    - gcloud iam workload-identity-pools providers create-oidc [PROVIDER_NAME] --location="global" --workload-identity-pool="[POOL_NAME]" --display-name="[DISPLAY_NAME]" --issuer-uri="https://token.actions.githubusercontent.com" --allowed-audiences="[AUDIENCE]" (e.g. github-provider, Github Provider, [PROJECT_ID].svc.id.goog[WORKLOAD_IDENTITY_POOL_NAME]/[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com)
10. Let github provider use deployer account
    - gcloud iam service-accounts add-iam-policy-binding [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com --member="principalSet://iam.googleapis.com/projects/[PROJECT_NUMBER]/locations/global/workloadIdentityPools/[POOL_NAME]/attribute.repository/[GITHUB_REPOSITORY]" --role="roles/iam.workloadIdentityUser" (e.g. cloud-run-sa, [PROJECT_NAME], github-pool, hibikishibata/ride-driver-backend)
11. Let Deployer Use Runtime Account
    - gcloud iam service-accounts add-iam-policy-binding [RUNTIME_SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser" (e.g. runtime-sa, cloud-run-sa, [PROJECT_NAME])
12. Github Secrets Setup
    - WIF_PROVIDER: projects/[PROJECT_NUMBER]/locations/global/workloadIdentityPools/[POOL_NAME]/providers/[PROVIDER_NAME] (e.g. projects/123456789012/locations/global/workloadIdentityPools/github-pool/providers/github-provider)
    - GCP_SERVICE_ACCOUNT: [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com (e.g. cloud-run-sa, [PROJECT_NAME])
13. Prepare deploy file:

```yaml
name: CI/CD to Cloud Run

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest

    permissions:
      contents: read
      id-token: write

    steps:
      - uses: actions/checkout@v4

      - uses: google-github-actions/auth@v2
        with:
          workload_identity_provider: ${{ secrets.WIF_PROVIDER }}
          service_account: ${{ secrets.GCP_SERVICE_ACCOUNT }}

      - uses: google-github-actions/setup-gcloud@v2

      - run: gcloud auth configure-docker us-central1-docker.pkg.dev --quiet

      - name: Build image
        run: |
          docker build -t us-central1-docker.pkg.dev/[PROJECT_NAME]/[ARTIFACT_REGISTRY_NAME]/app:${{ github.sha }} .

      - name: Push image
        run: |
          docker push us-central1-docker.pkg.dev/[PROJECT_NAME]/[ARTIFACT_REGISTRY_NAME]/app:${{ github.sha }}

      - name: Deploy Cloud Run
        run: |
          gcloud run deploy my-backend \
            --image us-central1-docker.pkg.dev/[PROJECT_NAME]/[ARTIFACT_REGISTRY_NAME]/app:${{ github.sha }} \
            --region us-central1 \
            --platform managed \
            --allow-unauthenticated \
            --service-account [SERVICE_ACCOUNT_NAME]@[PROJECT_NAME].iam.gserviceaccount.com
```            

Utility commands
- Check logs: gcloud run logs read [SERVICE_NAME] --region [REGION] (e.g. my-backend, us-central1)
- Check deployed services: gcloud run services list --region [REGION] (e.g. us-central1)
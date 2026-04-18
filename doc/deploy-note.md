# Deploy Note

Google Cloud Run is used for deployment. The deployment process is automated using Github Actions.

### Major flow
1. Either push or merge to main branch
2. Github Actions as CI/CD will be triggered
3. Build the Docker image and push it to Google Container Registry (GCR)
4. Deploy the new image to Google Cloud Run


### Setup Google Cloud Run
1. gcloud auth login

2. gcloud config set project [PROJECT_ID] (e.g. hibiki-portfolio)

3. Enable Cloud Run API: 
```sh
[PROJECT_NAME]gcloud services enable run.googleapis.com
[PROJECT_NAME]gcloud services enable artifactregistry.googleapis.com
[PROJECT_NAME]gcloud services enable iamcredentials.googleapis.com
[PROJECT_NAME]gcloud services enable cloudresourcemanager.googleapis.com
```

4. Create Artifact Registry: 
```sh
[PROJECT_NAME]gcloud artifacts repositories create [REPOSITORY_NAME] --repository-format=docker --location=[REGION] 
    (e.g. gcr-repo, us-central1)
```

5. Create Service Accounts:
```sh    
[PROJECT_NAME]gcloud iam service-accounts create [SERVICE_ACCOUNT_NAME] --display-name="[DISPLAY_NAME]" 
    (e.g. cloud-run-sa, Deployer Service Account)
[PROJECT_NAME]gcloud iam service-accounts create [RUNTIME_SERVICE_ACCOUNT_NAME] --display-name="[DISPLAY_NAME]"
    (e.g. runtime-sa, Runtime Service Account)
```

6. Grant Permissions To Service Account:
```sh
[PROJECT_NAME]gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/run.admin"
[PROJECT_NAME]gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
[PROJECT_NAME]gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/artifactregistry.writer"
```

7. Get Project ID:
```sh
[PROJECT_NAME]gcloud projects describe [PROJECT_NAME]
```

8. Create Identity Pool. 
This pool is a gate for external services and GCP
```sh
[PROJECT_NAME]gcloud iam workload-identity-pools create [POOL_NAME] --location="global" --display-name="[DISPLAY_NAME]" 
    (e.g. github-pool, Github Pool)
```

9. Create Identity Provider to trust request(token) from Github
Token is issued when Github Actions is triggered and used for authentication to GCP
```sh
[PROJECT_NAME]gcloud iam workload-identity-pools providers create-oidc [PROVIDER_NAME] --location="global" --workload-identity-pool="[POOL_NAME]" --display-name="[DISPLAY_NAME]" --issuer-uri="https://token.actions.githubusercontent.com" --allowed-audiences="[AUDIENCE]"
     (e.g. github-provider, Github Provider, github-audience)
```

10. Allow the deployer service account to be impersonated by Github Actions via Workload Identity Federations
```sh
[PROJECT_NAME]gcloud iam service-accounts add-iam-policy-binding [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com --member="principalSet://iam.googleapis.com/projects/[PROJECT_NUMBER]/locations/global/workloadIdentityPools/[POOL_NAME]/attribute.repository/[GITHUB_REPOSITORY]" --role="roles/iam.workloadIdentityUser" 
    (e.g. cloud-run-sa, [PROJECT_NAME], github-pool, hibikishibata/ride-driver-backend)
```

11. Let Deployer Use Runtime Service Account
```sh
[PROJECT_NAME]gcloud iam service-accounts add-iam-policy-binding [RUNTIME_SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser" (e.g. runtime-sa, cloud-run-sa, [PROJECT_NAME])
```

12. Github Secrets Setup
```sh
[PROJECT_NAME]WIF_PROVIDER: projects/[PROJECT_NUMBER]/locations/global/workloadIdentityPools/[POOL_NAME]/providers/[PROVIDER_NAME] 
    (e.g. projects/123456789012/locations/global/workloadIdentityPools/github-pool/providers/github-provider)
[PROJECT_NAME]GCP_SERVICE_ACCOUNT: [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com (e.g. cloud-run-sa, [PROJECT_NAME])
```

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
      id-token: write # To get OIDC token

    steps:
  [PROJECT_NAME]uses: actions/checkout@v4 # 

  [PROJECT_NAME]uses: google-github-actions/auth@v2 # To authenticate to Google Cloud using the OIDC token from Github Actions
        with:
          workload_identity_provider: ${{ secrets.WIF_PROVIDER }}
          service_account: ${{ secrets.GCP_SERVICE_ACCOUNT }}

  [PROJECT_NAME]uses: google-github-actions/setup-gcloud@v2 # To set up gcloud CLI
 
  [PROJECT_NAME]run: gcloud auth configure-docker us-central1-docker.pkg.dev --quiet # 

  [PROJECT_NAME]name: Build image
        run: |
          docker build -t us-central1-docker.pkg.dev/[PROJECT_NAME]/[ARTIFACT_REGISTRY_NAME]/app:${{ github.sha }} .

  [PROJECT_NAME]name: Push image
        run: |
          docker push us-central1-docker.pkg.dev/[PROJECT_NAME]/[ARTIFACT_REGISTRY_NAME]/app:${{ github.sha }}

  [PROJECT_NAME]name: Deploy Cloud Run
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
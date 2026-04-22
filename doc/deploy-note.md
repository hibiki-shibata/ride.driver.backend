# Deploy Note

Google Cloud Run is used for deployment. The deployment process is automated using Github Actions.

### Major flow
1. Authenticate gcloud CLI
2. Enable required API in GCP to use its services
3. Create Artifact Registry for images
4. Create Secret Manager for secrets
5. Create Cloud SQL instance for database
6. Create VPC connector for private connection between Cloud Run and Cloud SQL
7. Create Service Account to let Github Actions to deploy to GCP
8. Bind necessary permissions to the service accounts within the project
9. Create Identity Pool and Provider to allow Github Actions to authenticate to GCP using OIDC token
10. Configure Github Secrets to store sensitive information such as service account email and OIDC provider details
11. Prepare Github Actions workflow file to automate the build and deploy process

### Setup Google Cloud Run
1. gcloud auth login

2. gcloud config set project [PROJECT_ID] (e.g. hibiki-portfolio)

3. Enable required API: 
```sh
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com # For Artifact Registry
gcloud services enable iamcredentials.googleapis.com 
gcloud services enable cloudresourcemanager.googleapis.com
gcloud services enable sqladmin.googleapis.com # For Cloud SQL
```

4. Create Artifact Registry: 
```sh
gcloud artifacts repositories create [REPOSITORY_NAME] --repository-format=docker --location=[REGION] 
    (e.g. gcr-repo, us-central1)
```

5. Create Service Accounts:
```sh    
gcloud iam service-accounts create [SERVICE_ACCOUNT_NAME] --display-name="[DISPLAY_NAME]" 
    (e.g. cloud-run-sa, Deployer Service Account)
gcloud iam service-accounts create [RUNTIME_SERVICE_ACCOUNT_NAME] --display-name="[DISPLAY_NAME]"
    (e.g. runtime-sa, Runtime Service Account)
```

6. Configure project to provide necessary permissions to the service account:
```sh
gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/run.admin"
gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/artifactregistry.writer"
# To use secret manager 
gcloud projects add-iam-policy-binding [PROJECT_ID] --member="serviceAccount:[RUNTIME_SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/secretmanager.secretAccessor"
```

7. Create Identity Pool:
This pool is a gate for external services and GCP
```sh
gcloud iam workload-identity-pools create [POOL_NAME] --location="global" --display-name="[DISPLAY_NAME]" 
    (e.g. github-pool, Github Pool)
```

8. Create Identity Provider to trust request(token) from Github
Token is issued when Github Actions is triggered and used for authentication to GCP
```sh
gcloud iam workload-identity-pools providers create-oidc github-identity-provider \
  --location="global" \
  --workload-identity-pool="github-identity-pool" \
  --issuer-uri="https://token.actions.githubusercontent.com" \
  --attribute-mapping="google.subject=assertion.sub,attribute.repository=assertion.repository" \
  --attribute-condition="assertion.repository=='[GITHUB_USERNAME]/[GITHUB_REPOSITORY_NAME]'" \
  --display-name="Github Identity Provider"
```
(e.g. github-provider, Github Provider, github-audience)

9. Configure the service account to trust the Identity Pool and allow it to impersonate the service account
```sh
gcloud iam service-accounts add-iam-policy-binding [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com --member="principalSet://iam.googleapis.com/projects/[PROJECT_NUMBER]/locations/global/workloadIdentityPools/[POOL_NAME]/attribute.repository/[GITHUB_USERNAME]/[GITHUB_REPOSITORY_NAME]" --role="roles/iam.workloadIdentityUser" 
```
(e.g. cloud-run-sa, [PROJECT_NAME], github-pool, hibikishibata/ride-driver-backend)
*Project policy roles ≠ Service account policy roles

10. Configure so that Deployer Service Account can use Runtime Service Account to run the Cloud Run service
```sh
gcloud iam service-accounts add-iam-policy-binding [RUNTIME_SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com --member="serviceAccount:[SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
```
(e.g. runtime-sa, cloud-run-sa, [PROJECT_NAME])

11. Github Secrets Setup
```sh
WIF_PROVIDER: projects/[PROJECT_NUMBER]/locations/global/workloadIdentityPools/[POOL_NAME]/providers/[PROVIDER_NAME] 
    (e.g. projects/123456789012/locations/global/workloadIdentityPools/github-pool/providers/github-provider)
GCP_SERVICE_ACCOUNT: [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com 
```
(e.g. cloud-run-sa, [PROJECT_NAME])

12. Prepare deploy file:
(Google Github Action Doc)[https://github.com/google-github-actions/setup-gcloud]
```yaml
name: CI/CD to Google Cloud Run

on:
  push:
    branches: [master]

jobs:
  deploy:
    runs-on: ubuntu-latest

    permissions:
      contents: read
      id-token: write # To get OIDC token

    steps:
      - uses: actions/checkout@v6.0.0

      - id: 'auth'
        name: 'Authenticate to Google Cloud'
        uses: google-github-actions/auth@v3 # To authenticate to Google Cloud using the OIDC token from Github Actions
        with:
          workload_identity_provider: ${{ secrets.WIF_PROVIDER }}
          service_account: ${{ secrets.GCP_SERVICE_ACCOUNT }}

      - name: 'Set up Cloud SDK for Docker authentication'
        id: 'setup-gcloud'
        uses: 'google-github-actions/setup-gcloud@v3'
        with:
          version: '>= 363.0.0'

      - name: Authenticate Docker to Artifact Registry
        run: |
          gcloud auth configure-docker us-central1-docker.pkg.dev --quiet          

      - name: Build image
        run: |
          docker build -t us-central1-docker.pkg.dev/ride-backend-portfolio/ride-artifact-repository/ride-driver-backend:${{ github.sha }} .

      - name: Push image to Google Artifact Registry
        run: |
          docker push us-central1-docker.pkg.dev/ride-backend-portfolio/ride-artifact-repository/ride-driver-backend:${{ github.sha }}

      - name: 'Deploy to Cloud Run'
        id: 'deploy'
        uses: 'google-github-actions/deploy-cloudrun@v3'
        with: 
          service: 'ride-driver-backend'
          image: 'us-central1-docker.pkg.dev/ride-backend-portfolio/ride-artifact-repository/ride-driver-backend:${{ github.sha }}'
          region: 'us-central1'
          flags: |-
            --allow-unauthenticated
            --service-account=${{ secrets.GCP_SERVICE_ACCOUNT }}
            --add-cloudsql-instances=ride-backend-portfolio:us-central1:ride-postgres-instance
            --vpc-connector=ride-vpc-connector
          env_vars: |-
             SPRING_PROFILES_ACTIVE=prod
             DB_PORT=5432
          secrets: |-
            DB_PASSWORD=db-password:latest
            DB_USERNAME=db-username:latest
            DB_HOST=db-host:latest
            DB_NAME=db-name:latest
            CLOUD_SQL_CONNECTION_NAME=cloud-sql-connection-name:latest
            JWT_SECRET_STRING=jwt-secret-string:latest
            --min-instances=0
            --max-instances=2
            --memory=512Mi
            --cpu=1
            --cpu-throttling
          env_vars_update_strategy: overwrite
          secrets_update_strategy: overwrite
```            

GCP Concepts:
- Service Account: 
An identity used by non-human like applications or services to interact with GCP resources.
The permissions/roles - what the service account can do - are defined by resources where its used.
Need to be configured to allow external services to impersonate it and access GCP resources.

Identity Pool: 
- A pool that defines a set of identities from external identity providers (e.g. Github, AWS, Azure). It acts as a gate for external services to access GCP resources.

Identity Provider: 
- A provider that defines the trust relationship between GCP and an external identity provider. 
It specifies the "issuer URI" and "allowed audiences" for authentication.


# Setup Cloud SQL connection for Cloud Run with Private IP
https://docs.cloud.google.com/sql/docs/postgres/connect-instance-cloud-run

### Setup Private IP for Cloud Run to connect to Cloud SQL
https://codelabs.developers.google.com/connecting-to-private-cloudsql-from-cloud-run#3
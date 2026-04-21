# Production Development Note

https://docs.cloud.google.com/sql/docs/postgres/connect-instance-cloud-run

1. Create Cloud SQL instance for production environment. 
```sh
 gcloud sql instances create ride-postgres-instance \
  --database-version=POSTGRES_17 \
  --tier=db-f1-micro \
  --region=us-central1
ERROR: (gcloud.sql.instances.create) HTTPError 400: Invalid request: Invalid Tier (db-f1-micro) for (ENTERPRISE_PLUS) Edition. Use a predefined Tier like db-perf-optimized-N-* instead. Learn more at https://cloud.google.com/sql/docs/postgres/create-instance#machine-types.
```
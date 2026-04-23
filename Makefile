SHELL = /bin/zsh # 

.PHONY: run
run:
	./gradlew clean && ./gradlew bootRun

.PHONY: test
test:
	./gradlew clean && ./gradlew test

.PHONY: clean
clean:
	./gradlew clean

.PHONY: postgres
postgres:
	nerdctl container start hibiki-postgr
	@echo "ATTENTION: PostgreSQL password is krakenkey"
	psql -h localhost -p 5432 -U krakenadmin -d krakendb

.PHONY: prodrun
prodrun:
	./gradlew clean && ./gradlew build && java -jar build/libs/app.jar --spring.profiles.active=prod

.PHONY: terraform import
terraform-import:
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_service_account.cloud_run_sa projects/ride-app-project-id/serviceAccounts/ride-app-run-sa@ride-app-project-id.iam.gserviceaccount.com ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_vpc_access_connector.vpc_connector projects/ride-app-project-id/locations/us-central1/connectors/ride-app-vpc-connector ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_compute_global_address.private_ip_range projects/ride-app-project-id/global/addresses/ride-app-private-ip-range ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_artifact_registry_repository.ride_artifact_repository projects/ride-app-project-id/locations/us-central1/repositories/ride-app-artifact-repository ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_secret_manager_secret.db_name projects/ride-app-project-id/secrets/db-name ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_secret_manager_secret.db_host projects/ride-app-project-id/secrets/db-host ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_secret_manager_secret.db_username projects/ride-app-project-id/secrets/db-username ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_secret_manager_secret.db_password projects/ride-app-project-id/secrets/db-password ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_secret_manager_secret.cloud_sql_connection_name projects/ride-app-project-id/secrets/cloud-sql-connection-name ||
	terraform -chdir=terraform import  -var-file=environments/production.tfvars google_secret_manager_secret.jwt_secret_string projects/ride-app-project-id/secrets/jwt-secret-string ||

.PHONY: help
help:
	@echo "Available commands:"
	@echo "  run       - Start the application"
	@echo "  test      - Run tests"
	@echo "  clean     - Clean build artifacts"
	@echo "  Postgres  - Start PostgreSQL and connect to it"
	@echo "  help      - Show this help message"
	@echo "  prodrun   - Build and run the application in production mode"


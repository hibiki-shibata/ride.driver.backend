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




.PHONEY: postgres
postgres:
	nerdctl container start hibiki-postgr
	@echo "ATTENTION: PostgreSQL password is krakenkey"
	psql -h localhost -p 5432 -U krakenadmin -d krakendb


.PHONEY: prodrun
prodrun:
	./gradlew clean && ./gradlew build && java -jar build/libs/app.jar --spring.profiles.active=prod



.PHONY: help
help:
	@echo "Available commands:"
	@echo "  run       - Start the application"
	@echo "  test      - Run tests"
	@echo "  clean     - Clean build artifacts"
	@echo "  Postgres  - Start PostgreSQL and connect to it"
	@echo "  help      - Show this help message"
	@echo "  prodrun   - Build and run the application in production mode"
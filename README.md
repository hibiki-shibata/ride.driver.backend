This is a backend for Ride Driver App

You can use command `make help` to learn simplified CLI ops.

### Manual Initiations For Mac/Linux
1. `./gradlew bootRun` - Run with Spring plugin (Recommended)
2. `./gradlew run` - Compile and Run Script (Enable "application" plugin)
3. `./gradle build` - Compile jar file with required classpath & MainClass spec
4. `java -jar ./app/build/libs/*OT.jar` - Run compiled jar file
- Spring Plug in(@SpringBootApplication) helps automatically find the mainClass to include it in manifest
- <file_name>.plane.jar is complied file without mainClass
- <file_name>.jar is complied file with mainClass


### Initiate Postgers Container:
`
docker run -p 5432:5432 -d \
    --name demo-postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_DB=postgres \
    postgres
`


### Flyway control
- Flyway is responsible only for DB migration - Create tables or stuffs.
- Spring JPA is reading @Entity or @Table stuffs, to validate and use ORM
- Data type of columns in SQL have to be corresponding to the data type of model Entities - JPA valdate it.
This is a backend for Ride Driver App

You can use `make run` to see simplify your CLI ops.

For Mac/Linux
1. `./gradlew bootRun` - Run with Spring plugin (Recommended)
2. `./gradlew run` - Compile and Run Script (Enable "application" plugin)
3. `./gradle build` - Compile jar file with required classpath & MainClass spec
4. `java -jar ./app/build/libs/*OT.jar` - Run compiled jar file
- Spring Plug in(@SpringBootApplication) helps automatically find the mainClass to include it in manifest
- <file_name>.plane.jar is complied file without mainClass
- <file_name>.jar is complied file with mainClass


Prepare
`
docker run -p 5432:5432 -d \
    --name demo-postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_DB=postgres \
    postgres
`

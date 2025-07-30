This is a backend for Ride Driver App

For Mac/Linux
1. `./gradlew run` - Compile and Run Script
3. `./gradlew bootRun` - Run with Spring plugin
2. `./gradle build` - Compile jar file with required classpath & MainClass spec
4. `java -jar ./app/build/libs/*OT.jar` - Run compiled jar file
- Spring Plug in(@SpringBootApplication) helps automatically find the mainClass to include it in manifest
- <file_name>.plane.jar is complied file without mainClass
- <file_name>.jar is complied file with mainClass


val springBootFrameworkVersion = "3.5.6"
val springDependencyManagementVersion = "1.1.7"
val flywayDependencyVersion = "11.14.1"
val postgresqlDependencyVersion = "42.7.8"
val checkstyleDependencyVersion = "12.1.0"
val jacksonDatatypeDependencyVersion = "2.20.0"

plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "kdodds"
version = "0.0.1-SNAPSHOT"
description = "Local user service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

checkstyle {
    toolVersion = checkstyleDependencyVersion
    configFile = rootProject.file("/config/checkstyle/checkstyle.xml") // Path to your Checkstyle rules file
    isIgnoreFailures = false // Set to true to allow the build to pass even with violations
    maxWarnings = 0 // Fail the build if any warnings are found
    maxErrors = 0 // Fail the build if any errors are found
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // implementations
    implementation("org.springframework.boot:spring-boot-starter:${springBootFrameworkVersion}")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootFrameworkVersion}")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator:${springBootFrameworkVersion}")
    // https://mvnrepository.com/artifact/com.puppycrawl.tools/checkstyle
    implementation("com.puppycrawl.tools:checkstyle:${checkstyleDependencyVersion}")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonDatatypeDependencyVersion}")
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    implementation("org.flywaydb:flyway-core:${flywayDependencyVersion}")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${springBootFrameworkVersion}")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:${postgresqlDependencyVersion}")
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-hateoas
    implementation("org.springframework.boot:spring-boot-starter-hateoas:${springBootFrameworkVersion}")

    // https://mvnrepository.com/artifact/org.flywaydb/flyway-database-postgresql
    runtimeOnly("org.flywaydb:flyway-database-postgresql:${flywayDependencyVersion}")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools:${springBootFrameworkVersion}")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootFrameworkVersion}")
    // https://mvnrepository.com/artifact/com.h2database/h2
    testImplementation("com.h2database:h2:2.4.240")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}

// ensure checkstyle tasks get the suppressions property
tasks.withType<Checkstyle>().configureEach {
    configProperties?.set("checkstyle.suppressions.file",
        file("$rootDir/config/checkstyle/suppressions.xml").absolutePath
    )
}

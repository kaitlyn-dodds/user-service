plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "kdodds"
version = "0.0.1-SNAPSHOT"
description = "user_service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

checkstyle {
    toolVersion = "11.0.0"
    configFile = rootProject.file("/config/checkstyle/checkstyle.xml") // Path to your Checkstyle rules file
    isIgnoreFailures = false // Set to true to allow the build to pass even with violations
    maxWarnings = 0 // Fail the build if any warnings are found
    maxErrors = 0 // Fail the build if any errors are found
}

// ensure checkstyle tasks get the suppressions property
tasks.withType<Checkstyle>().configureEach {
    configProperties?.set("checkstyle.suppressions.file",
        file("$rootDir/config/checkstyle/suppressions.xml").absolutePath
    )
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

    implementation("org.springframework.boot:spring-boot-starter")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // https://mvnrepository.com/artifact/com.puppycrawl.tools/checkstyle
    implementation("com.puppycrawl.tools:checkstyle:11.0.0")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

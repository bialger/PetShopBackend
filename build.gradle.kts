plugins {
    id("java")
    id("org.flywaydb.flyway") version "11.7.2"
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    application
}

group = "org.bialger"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

repositories {
    mavenCentral()
}

val mapstructVersion = "1.5.5.Final"
val lombokVersion = "1.18.38"
val springdocVersion = "2.3.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.3")
    }
    dependencies {
        dependency("org.mapstruct:mapstruct:${mapstructVersion}")
        dependency("org.mapstruct:mapstruct-processor:${mapstructVersion}")
        dependency("org.projectlombok:lombok:${lombokVersion}")
        dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocVersion}")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    implementation("org.mapstruct:mapstruct")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("org.bialger.gateway.MicroservicePetShopApp")
}

tasks.register<JavaExec>("runGatewayService") {
    group = "application"
    description = "Run Gateway Service"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.bialger.gateway.MicroservicePetShopApp")
    systemProperty("spring.config.location", "classpath:/")
    systemProperty("spring.config.name", "application-gateway")
}

tasks.register<JavaExec>("runOwnersService") {
    group = "application"
    description = "Run Owners Service"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.bialger.owners.OwnerMicroservice")
    systemProperty("spring.config.location", "classpath:/")
    systemProperty("spring.config.name", "application-owners")
}

tasks.register<JavaExec>("runPetsService") {
    group = "application"
    description = "Run Pets Service"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.bialger.pets.PetMicroservice")
    systemProperty("spring.config.location", "classpath:/")
    systemProperty("spring.config.name", "application-pets")
}

tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).apply {
            addBooleanOption("html5", true)
            addStringOption("Xdoclint:none", "-quiet")
            windowTitle = "Bank App API Documentation"
            encoding = "UTF-8"
            docEncoding = "UTF-8"
            charSet = "UTF-8"
        }
    }

    dependsOn(tasks.compileJava)
}

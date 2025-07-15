val kotlinVersion: String by project
val jvmVersion: String by project
val postgresqlVersion: String by project
val jpaPluginVersion: String by project

//Spring
val springBootVersion: String by project
val springPluginVersion: String by project
val springDependencyManagementVersion: String by project

//OpenSearch
val opensearchJavaClientVersion: String by project
val opensearchRestClientVersion: String by project
val apacheHttpAsyncClientVersion: String by project
val apacheHttpComponent: String by project

//AWS SDK
val amazonSdkVersion: String by project

//Untitled
val jakartaValidationVersion: String by project
val redisClientVersion: String by project
val mailjetClientVersion: String by project
val twilioSdkVersion: String by project

//JSON
val jsonwebtokenVersion: String by project
val jacksonmoduleKotlinVersion: String by project
val jsonwebtokenImplVersion: String by project
val jsonwebtokenJacksonVersion: String by project
val jwtJacksonVersion: String by project

//Testing
val mockVersion: String by project
val ninjaVersion: String by project
val mockkVersion: String by project

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.chatsphere"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(jvmVersion)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-groovy-templates")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtokenImplVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("redis.clients:jedis:$redisClientVersion")
    implementation("com.mailjet:mailjet-client:$mailjetClientVersion")
    implementation("com.twilio.sdk:twilio:$twilioSdkVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.apache.httpcomponents.core5:httpcore5:$apacheHttpComponent")
    implementation("org.opensearch.client:opensearch-java:$opensearchJavaClientVersion")
    implementation("org.opensearch.client:opensearch-rest-client:$opensearchRestClientVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jsonwebtokenImplVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jsonwebtokenImplVersion")
    implementation(platform("software.amazon.awssdk:bom:$amazonSdkVersion"))
    implementation("software.amazon.awssdk:s3")
    implementation("com.ninja-squad:springmockk:$ninjaVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:$mockkVersion")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}

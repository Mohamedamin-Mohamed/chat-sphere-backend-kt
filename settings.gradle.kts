pluginManagement{
    val kotlinVersion: String by settings
    val jvmVersion: String by settings
    val springPluginVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val jpaPluginVersion: String by settings
    val springBootVersion: String by settings

    plugins{
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version springPluginVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        kotlin("plugin.jpa") version jpaPluginVersion
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "chatspherekotlin"

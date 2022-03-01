plugins {
    java
    kotlin("jvm") version "1.6.10"
    application
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.springframework:spring-context:5.3.15")
    implementation("net.lingala.zip4j:zip4j:2.9.1")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jgrapht:jgrapht-core:1.4.0")
    testImplementation("org.jgrapht:jgrapht-io:1.4.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.easymock:easymock:3.1")
}

group = "com.rustleund"
version = "1.1.0"
description = "fighting-fantasy"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

application {
    mainClass.set("rustleund.fightingfantasy.main.Main")
    applicationDefaultJvmArgs = listOf("--add-opens java.base/java.lang=ALL-UNNAMED")
}


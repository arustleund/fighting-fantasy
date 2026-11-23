plugins {
    java
    kotlin("jvm") version "2.4.21"
    groovy
    application
    id("jacoco")
    id("org.sonarqube") version "7.6.0.18749"
}

repositories {
    mavenLocal()
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation("commons-beanutils:commons-beanutils:1.11.0")
    implementation("commons-logging:commons-logging:1.4.0")
    implementation("com.google.guava:guava:33.8.0-jre")
    implementation("org.springframework:spring-context:7.1.0")
    implementation("net.lingala.zip4j:zip4j:2.11.6")
    implementation("com.google.code.gson:gson:2.15.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.notkamui.libs:keval:2.1.0")
    implementation("jaco:jaco-mp3-player:0.10.2")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.easymock:easymock:5.7.0")
    testImplementation("org.spockframework:spock-core:2.5-groovy-6.0")
    testImplementation("org.apache.groovy:groovy:6.0.0")
    testImplementation("io.strikt:strikt-core:0.35.1")
    testImplementation("io.kotest:kotest-runner-junit5:6.2.7")
}

group = "com.rustleund"
version = "1.1.1"
description = "fighting-fantasy"

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("rustleund.fightingfantasy.main.Main")
}

jacoco {
    toolVersion = "0.8.16"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

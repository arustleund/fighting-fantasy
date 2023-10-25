plugins {
    java
    kotlin("jvm") version "1.8.22"
    groovy
    application
    id ("jacoco")
    id("org.sonarqube") version "4.2.1.3168"
}

repositories {
    mavenLocal()
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.springframework:spring-context:5.3.15")
    implementation("net.lingala.zip4j:zip4j:2.9.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.notkamui.libs:keval:0.8.0")
    implementation("jaco:jaco-mp3-player:0.10.2")

    testImplementation("org.easymock:easymock:4.3")
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.10")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-framework-datatest:5.7.2")
}

group = "com.rustleund"
version = "1.1.1"
description = "fighting-fantasy"

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("rustleund.fightingfantasy.main.Main")
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

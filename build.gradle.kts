plugins {
    java
    kotlin("jvm") version "1.6.10"
    groovy
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.springframework:spring-context:5.3.15")
    implementation("net.lingala.zip4j:zip4j:2.9.1")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.notkamui.libs:keval:0.8.0")

    testImplementation("org.jgrapht:jgrapht-core:1.5.1")
    testImplementation("org.jgrapht:jgrapht-io:1.5.1")
    testImplementation("org.easymock:easymock:4.3")
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.10")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
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
}

tasks {
    test {
        useJUnitPlatform()
    }
}

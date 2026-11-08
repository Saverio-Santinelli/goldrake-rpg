plugins {
    application
}

application {
    mainClass.set("it.unicam.cs.mpgc.rpg129548.DuelloConsole")
}

tasks.run {
    standardInput = System.`in`
}

group = "it.unicam.cs.mpgc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

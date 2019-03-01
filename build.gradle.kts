buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.11")
    }
}

plugins {
    id("org.jetbrains.intellij") version "0.4.4"
}

apply(plugin = "java")
apply(plugin = "kotlin")

version = "1.13"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.11")
    testImplementation("junit:junit:4.12")
}


intellij {
    pluginName = "Gradle Dependencies Helper"
    version = "IC-2018.3.5"
    updateSinceUntilBuild = false
    setPlugins("Groovy", "gradle")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

tasks {
    wrapper {
        gradleVersion = "5.2.1"
    }
}

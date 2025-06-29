plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.krishnasony"
version = "1.0.4"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin stdlib is automatically provided by IntelliJ Platform
    // implementation("org.jetbrains.kotlin:kotlin-stdlib") - removed per plugin recommendations
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.apache.commons:commons-compress:1.26.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2024.1.4")
    type.set("IC") // IntelliJ IDEA Community Edition
    
    plugins.set(listOf(
        "java",
        "gradle",
        "org.jetbrains.plugins.gradle"
    ))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("251.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

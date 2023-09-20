plugins {
    id 'java'
    id 'org.jetbrains.kotlin.jvm' version '1.9.20-Beta'
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = 'com.th7bo'
version = '1.0'

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = "https://repo.papermc.io/repository/maven-public/"
    }
    maven {
        name = "sonatype"
        url = "https://oss.sonatype.org/content/groups/public/"
    }
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.1")
    }
}

dependencies {
    compileOnly "io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT"
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"

    compileOnly 'org.projectlombok:lombok:1.18.28'
    annotationProcessor 'org.projectlombok:lombok:1.18.28'

    testCompileOnly 'org.projectlombok:lombok:1.18.28'
    testAnnotationProcessor 'org.projectlombok:lombok:1.18.28'

    implementation "com.google.code.gson:gson:2.8.0"
    implementation "com.google.guava:guava:11.0.2"
    api "org.mongodb:mongodb-driver:3.12.11"
    api "org.apache.commons:commons-lang3:3.5"
    api "org.apache.commons:commons-io:1.3.2"

}

def targetJavaVersion = 17
java {
    def javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType(JavaCompile).configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release = targetJavaVersion
    }
}

processResources {
    def props = [version: version]
    inputs.properties props
    filteringCharset 'UTF-8'
    filesMatching('plugin.yml') {
        expand props
    }
}
kotlin {
    jvmToolchain(17)
}

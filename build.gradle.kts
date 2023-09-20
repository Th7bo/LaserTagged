plugins {
    id 'java'
    id 'org.jetbrains.kotlin.jvm' version '1.9.20-Beta'
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id('com.github.johnrengelman.shadow') version '7.1.2'
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

build.dependsOn shadowJar
        shadowJar {
            dependencies {
                include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
                include(dependency("org.mariadb.jdbc:mariadb-java-client:3.1.2"))
                include(dependency("fr.mrmicky:fastboard:2.0.0"))
            }
            relocate 'fr.mrmicky.fastboard', 'com.th7bo.lasertagged.fastboard'
        }

dependencies {
    compileOnly "io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT"
//    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20-Beta"
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.2")

    compileOnly 'org.projectlombok:lombok:1.18.28'
    annotationProcessor 'org.projectlombok:lombok:1.18.28'

    testCompileOnly 'org.projectlombok:lombok:1.18.28'
    testAnnotationProcessor 'org.projectlombok:lombok:1.18.28'
    implementation 'fr.mrmicky:fastboard:2.0.0'

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

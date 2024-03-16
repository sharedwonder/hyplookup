plugins {
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

val GITHUB_REPO_URL = "https://github.com/sharedwonder/hyplookup"
val S5W5_GPR_URL = "https://maven.pkg.github.com/sharedwonder/maven-repository"

group = "net.sharedwonder.mc"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven(S5W5_GPR_URL)
}

dependencies {
    implementation(platform("io.netty:netty-bom:4.1.101.Final"))
    implementation("net.sharedwonder.mc:ptbridge:0.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.netty:netty-buffer")
    implementation("org.apache.logging.log4j:log4j-api:2.22.1")
    compileOnly("org.jetbrains:annotations:24.0.1")

    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

kotlin {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    compilerOptions.freeCompilerArgs.add("-Xjvm-default=all")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name = project.name
                description = project.description
                url = GITHUB_REPO_URL

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }

                developers {
                    developer {
                        id = "sharedwonder"
                        name = "Liu Baihao"
                        email = "liubaihaohello@outlook.com"
                    }
                }

                scm {
                    connection = "scm:git:$GITHUB_REPO_URL.git"
                    developerConnection = "scm:git:$GITHUB_REPO_URL.git"
                    url = GITHUB_REPO_URL
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven(S5W5_GPR_URL) {
            name = "GitHubPackages"
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}

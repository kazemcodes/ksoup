plugins {
    `maven-publish`
    signing
    alias(libs.plugins.nmcp)
}

val artifactId = "ksoup-network-korlibs"
val packageVersion = libs.versions.libraryVersion.get()

group = "io.github.ireaderorg"
version = packageVersion

// Create empty javadoc jar for Maven Central requirements
val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                groupId = "io.github.ireaderorg"
                version = packageVersion
                pom {
                    name.set(artifactId)
                    description.set("Ksoup is a Kotlin Multiplatform library for working with HTML and XML. (IReader fork)")
                    url.set("https://github.com/IReaderorg/IReader")
                    licenses {
                        license {
                            name.set("Mozilla Public License 2.0")
                            url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                        }
                    }
                    developers {
                        developer {
                            id.set("kazemcodes")
                            name.set("kazem.codes")
                            url.set("https://github.com/IReaderorg/IReader")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/IReaderorg/IReader.git")
                        developerConnection.set("scm:git:ssh://github.com/IReaderorg/IReader.git")
                        url.set("https://github.com/IReaderorg/IReader")
                    }
                }
            }
        }

        repositories {
            val ossrhUsername = System.getenv("MAVEN_USERNAME")
                ?: findProperty("mavenCentralUsername") as String?
            val ossrhPassword = System.getenv("MAVEN_PASSWORD")
                ?: findProperty("mavenCentralPassword") as String?
            if (ossrhUsername != null && ossrhPassword != null) {
                maven {
                    name = "OSSRH"
                    val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    setUrl(if (packageVersion.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }
        }
    }

    signing {
        val signingKey = findProperty("signing.keyId") as String?
        val signingPassword = findProperty("signing.password") as String?
        if (signingKey != null && signingPassword != null) {
            sign(publishing.publications)
        } else {
            val signingKeyEnv = System.getenv("SIGNING_KEY")
            val signingPasswordEnv = System.getenv("SIGNING_PASSWORD")
            if (signingKeyEnv != null && signingPasswordEnv != null) {
                useInMemoryPgpKeys(signingKeyEnv, signingPasswordEnv)
                sign(publishing.publications)
            }
        }
    }
}

nmcp {
    publishAllPublications {
        username = System.getenv("MAVEN_USERNAME") ?: findProperty("mavenCentralUsername") as String? ?: ""
        password = System.getenv("MAVEN_PASSWORD") ?: findProperty("mavenCentralPassword") as String? ?: ""
        publicationType = "AUTOMATIC"
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
}

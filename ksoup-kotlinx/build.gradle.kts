plugins {
    `maven-publish`
    signing
    alias(libs.plugins.nmcp)
}

val artifactId = "ksoup-kotlinx"
val packageVersion = libs.versions.libraryVersion.get()
group = "io.github.ireaderorg"
version = packageVersion

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) { archiveClassifier.set("javadoc") }

afterEvaluate {
    publishing.publications.removeIf { it.name.contains("Debug", ignoreCase = true) }
    
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
                    licenses { license { name.set("Mozilla Public License 2.0"); url.set("https://www.mozilla.org/en-US/MPL/2.0/") } }
                    developers { developer { id.set("kazemcodes"); name.set("kazem.codes"); url.set("https://github.com/IReaderorg/IReader") } }
                    scm { connection.set("scm:git:git://github.com/IReaderorg/IReader.git"); developerConnection.set("scm:git:ssh://github.com/IReaderorg/IReader.git"); url.set("https://github.com/IReaderorg/IReader") }
                }
            }
        }
        repositories {
            val ossrhUsername = System.getenv("MAVEN_USERNAME")
                ?: findProperty("ossrhUsername") as String?
                ?: findProperty("mavenUsername") as String?
                ?: findProperty("mavenCentralUsername") as String?
            val ossrhPassword = System.getenv("MAVEN_PASSWORD")
                ?: findProperty("ossrhPassword") as String?
                ?: findProperty("mavenPassword") as String?
                ?: findProperty("mavenCentralPassword") as String?
            if (ossrhUsername != null && ossrhPassword != null) {
                maven {
                    name = "OSSRH"
                    val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    setUrl(if (packageVersion.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                    credentials { username = ossrhUsername; password = ossrhPassword }
                }
            }
        }
    }
    signing { sign(publishing.publications) }
}

nmcp {
    publishAllPublications {
        username = System.getenv("MAVEN_USERNAME")
            ?: findProperty("mavenCentralUsername") as String?
            ?: findProperty("mavenUsername") as String?
            ?: ""
        password = System.getenv("MAVEN_PASSWORD")
            ?: findProperty("mavenCentralPassword") as String?
            ?: findProperty("mavenPassword") as String?
            ?: ""
        publicationType = "AUTOMATIC"
    }
}

tasks.withType<PublishToMavenRepository>().configureEach { dependsOn(tasks.withType<Sign>()) }

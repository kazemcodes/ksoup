plugins {
    `maven-publish`
    signing
    alias(libs.plugins.nmcp)
}

val artifactId = "ksoup-network-ktor2"
val packageVersion = libs.versions.libraryVersion.get()
group = "io.github.ireaderorg"
version = packageVersion

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) { archiveClassifier.set("javadoc") }

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
                    licenses { license { name.set("Mozilla Public License 2.0"); url.set("https://www.mozilla.org/en-US/MPL/2.0/") } }
                    developers { developer { id.set("kazemcodes"); name.set("kazem.codes"); url.set("https://github.com/IReaderorg/IReader") } }
                    scm { connection.set("scm:git:git://github.com/IReaderorg/IReader.git"); developerConnection.set("scm:git:ssh://github.com/IReaderorg/IReader.git"); url.set("https://github.com/IReaderorg/IReader") }
                }
            }
        }
        repositories {
            val u = System.getenv("MAVEN_USERNAME") ?: findProperty("mavenCentralUsername") as String?
            val p = System.getenv("MAVEN_PASSWORD") ?: findProperty("mavenCentralPassword") as String?
            if (u != null && p != null) {
                maven {
                    name = "OSSRH"
                    setUrl(if (packageVersion.endsWith("SNAPSHOT")) "https://s01.oss.sonatype.org/content/repositories/snapshots/" else "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials { username = u; password = p }
                }
            }
        }
    }
    
    val signingKey = findProperty("signingInMemoryKey") as String?
    val signingKeyId = findProperty("signingInMemoryKeyId") as String?
    val signingPassword = findProperty("signingInMemoryKeyPassword") as String?
    
    if (signingKey != null && signingPassword != null) {
        signing {
            useInMemoryPgpKeys(signingKeyId, signingKey.replace("\\n", "\n"), signingPassword)
            sign(publishing.publications)
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

tasks.withType<PublishToMavenRepository>().configureEach { dependsOn(tasks.withType<Sign>()) }

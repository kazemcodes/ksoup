plugins {
//    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
}

group = "io.github.ireaderorg"
version = libs.versions.libraryVersion.get()

val artifactId = "ksoup-network-ktor2"
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("io.github.ireaderorg", artifactId, libs.versions.libraryVersion.get())
    pom {
        name.set(artifactId)
        description.set("Ksoup is a Kotlin Multiplatform library for working with HTML and XML, and offers an easy-to-use API for URL fetching, data parsing, extraction, and manipulation using DOM and CSS selectors. (IReader fork with JS fixes)")
        licenses {
            license {
                name.set("Mozilla Public License 2.0")
                url.set("https://www.mozilla.org/en-US/MPL/2.0/")
            }
        }
        url.set("https://github.com/IReaderorg/IReader")
        issueManagement {
            system.set("Github")
            url.set("https://github.com/IReaderorg/IReader/issues")
        }
        scm {
            connection.set("scm:git:git://github.com/IReaderorg/IReader.git")
            developerConnection.set("scm:git:ssh://github.com/IReaderorg/IReader.git")
            url.set("https://github.com/IReaderorg/IReader")
        }
        developers {
            developer {
                id.set("kazemcodes")
                name.set("kazem.codes")
                url.set("https://github.com/IReaderorg/IReader")
            }
        }
    }
}
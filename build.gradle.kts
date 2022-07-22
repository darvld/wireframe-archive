import java.util.*

plugins {
    kotlin("jvm") apply false
    id("io.github.gradle-nexus.publish-plugin")
}

// Read credentials and signing keys from the environment (for cloud builds)
var ossrhUsername = System.getenv("OSSRH_USERNAME").orEmpty()
var ossrhPassword = System.getenv("OSSRH_PASSWORD").orEmpty()
var sonatypeStagingProfileId = System.getenv("SONATYPE_STAGING_PROFILE_ID").orEmpty()

var signingKeyId = System.getenv("SIGNING_KEY_ID").orEmpty()
var signingPassword = System.getenv("SIGNING_PASSWORD").orEmpty()
var signingKey = System.getenv("SIGNING_KEY").orEmpty()

// Use a local.properties file if available (for local builds)
project.file("local.properties").takeIf { it.exists() }?.inputStream().use { input ->
    val props = Properties().apply { load(input) }

    props.getProperty("ossrhUsername")?.let { ossrhUsername = it }
    props.getProperty("ossrhPassword")?.let { ossrhPassword = it }
    props.getProperty("sonatypeStagingProfileId")?.let { sonatypeStagingProfileId = it }
    props.getProperty("signingKeyId")?.let { signingKeyId = it }
    props.getProperty("signingPassword")?.let { signingPassword = it }
    props.getProperty("signingKey")?.let { signingKey = it }
}

// Configure Nexus publishing settings and credentials
nexusPublishing.repositories.sonatype {
    stagingProfileId.set(sonatypeStagingProfileId)

    username.set(ossrhUsername)
    password.set(ossrhPassword)

    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
    snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
}

// Configure publications in subprojects
subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    // Stub javadoc to comply with MavenCentral requirements
    val javadocJar by tasks.creating(Jar::class) { archiveClassifier.set("javadoc") }
    val publishing = extensions.getByType<PublishingExtension>()

    // Setup publication metadata
    publishing.publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)

        pom {
            name.set(project.name)
            description.set("A GraphQL server library for Kotlin.")
            url.set("https://github.com/darvld/wireframe")

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    id.set("darvld")
                    name.set("Dario Valdespino")
                    email.set("dvaldespino00@gmail.com")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/darvld/wireframe.git")
                url.set("https://github.com/darvld/wireframe/tree/main")
            }
        }
    }

    // Sign publications
    extensions.getByType<SigningExtension>().apply {
        useInMemoryPgpKeys(signingKeyId, signingKey.replace("\\n", "\n"), signingPassword)
        sign(publishing.publications)
    }
}
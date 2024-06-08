plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "monolith-domain-splitter"
include(":library")
include(":test-app")

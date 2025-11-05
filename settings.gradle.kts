import java.net.URI

pluginManagement {
    repositories {
        google() // [!code ++]
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = URI.create("https://jitpack.io")
        }
    }
}

rootProject.name = "LearningDashboard"
include(":app")
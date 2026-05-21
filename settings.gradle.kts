pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// ❌ 删掉了原本在这里的 plugins { id("org.gradle.toolchains.foojay-resolver-convention") ... }

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "A207395_LIUZHAOHE_Izwan"
include(":app")

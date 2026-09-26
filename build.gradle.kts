plugins { java }

group = "fr.noltox.hcplugins"
version = providers.gradleProperty("version").get()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

dependencies {
    compileOnly("fr.noltox.hcplugins:core-api")
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("com.github.LeonMangler:PremiumVanishAPI:2.9.18-2") {
        isTransitive = false
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
    options.encoding = "UTF-8"
}

tasks.processResources {
    val pluginVersion = project.version.toString()
    inputs.property("version", pluginVersion)
    filesMatching("paper-plugin.yml") {
        expand("version" to pluginVersion)
    }
}

tasks.jar {
    val buildDate = providers.gradleProperty("buildDate").orNull
    val buildVersion = project.version.toString()
    val fileVersion = if (buildDate == null) buildVersion else "$buildDate-b$buildVersion"
    archiveFileName.set("HCJoinMessage-$fileVersion.jar")
}

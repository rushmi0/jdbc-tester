plugins {
    kotlin("jvm") version "2.4.10"
    application
    id("com.gradleup.shadow") version "8.3.6"
}

group = "win.rushmi0"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.3.21")

    // CLI parsing — https://picocli.info
    implementation("info.picocli:picocli:4.7.7")

    // Multiplatform logging — https://klibs.io/project/GetStream/stream-log
    implementation("io.getstream:stream-log:1.3.3")

    // JDBC drivers under test
    implementation("com.oracle.database.jdbc:ojdbc11:23.8.0.25.04")
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(11)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

application {
    mainClass.set("win.rushmi0.jdbctester.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    //archiveBaseName.set("jdbc-tester")
    archiveFileName.set("${project.name}-jvm.jar")
    archiveClassifier.set("")
    mergeServiceFiles()
}

import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import java.util.Locale

plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("kapt") version "2.4.10"
    application
    id("com.gradleup.shadow") version "8.3.6"
    id("org.graalvm.buildtools.native") version "1.1.9"
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
    // Generates GraalVM reflect/resource config for @Command classes at compile time
    kapt("info.picocli:picocli-codegen:4.7.7")

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

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}


sourceSets {
    main {
        resources.srcDir(layout.buildDirectory.dir("tmp/kapt3/classes/main"))
    }
}

tasks.processResources {
    dependsOn(tasks.named("kaptKotlin"))
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

enum class OsName { WINDOWS, MAC, LINUX, UNKNOWN }
enum class OsArch { X86_64, ARM64, UNKNOWN }

val currentOs = OperatingSystem.current().let {
    when {
        it.isWindows -> OsName.WINDOWS
        it.isMacOsX -> OsName.MAC
        it.isLinux -> OsName.LINUX
        else -> OsName.UNKNOWN
    }
}

val currentArch = when (providers.systemProperty("os.arch").get().lowercase(Locale.ROOT)) {
    "aarch64", "arm64" -> OsArch.ARM64
    "x86_64", "amd64" -> OsArch.X86_64
    else -> OsArch.UNKNOWN
}

val nativeImageSuffix = run {
    val osPart = when (currentOs) {
        OsName.WINDOWS -> "windows"
        OsName.MAC -> "macos"
        OsName.LINUX -> "linux"
        OsName.UNKNOWN -> "unknown"
    }
    val archPart = when (currentArch) {
        OsArch.ARM64 -> "arm64"
        OsArch.X86_64 -> "amd64"
        OsArch.UNKNOWN -> "unknown"
    }
    "$osPart-$archPart"
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("${project.name}-${nativeImageSuffix}")
            mainClass.set("win.rushmi0.jdbctester.MainKt")

            javaLauncher.set(
                javaToolchains.launcherFor {
                    languageVersion.set(JavaLanguageVersion.of(25))
                    vendor.set(JvmVendorSpec.matching("GraalVM Community"))
                }
            )

            buildArgs.addAll(
                "--no-fallback",
                "-H:+ReportExceptionStackTraces",
                "--enable-url-protocols=https",
                "-march=compatibility",
            )

            if (currentOs == OsName.LINUX) {
                buildArgs.add("-H:+StaticExecutableWithDynamicLibC")
            }
        }
    }
}

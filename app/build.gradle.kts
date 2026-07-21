import java.io.File
import java.util.Properties
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
}

// local.properties는 git 추적에서 제외되므로, 서버 주소처럼 커밋하면 안 되는 값을 이 파일에서 읽습니다.
// iOS의 Secret.xcconfig(API_URL)와 동일한 역할입니다.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

// network_security_config.xml도 같은 이유로 커밋하지 않고 빌드 시점에 생성합니다.
// API_BASE_URL의 호스트(IP/도메인)만 뽑아서 그 호스트에만 평문(HTTP) 통신을 허용하는 XML을 만듭니다.
abstract class GenerateNetworkSecurityConfig : DefaultTask() {
    @get:Input
    abstract val backendHost: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val host = backendHost.get()
        val xmlDir = File(outputDir.get().asFile, "xml")
        xmlDir.mkdirs()

        val content = if (host.isNotBlank()) {
            """<?xml version="1.0" encoding="utf-8"?>
<!-- 개발용 백엔드가 아직 HTTPS를 제공하지 않아, 이 호스트에 한해서만 평문(HTTP) 통신을 허용합니다.
     이 파일은 local.properties의 API_BASE_URL로부터 빌드 시 생성되며, 커밋되지 않습니다. -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">$host</domain>
    </domain-config>
</network-security-config>
"""
        } else {
            """<?xml version="1.0" encoding="utf-8"?>
<!-- local.properties에 API_BASE_URL이 없어 평문(HTTP) 통신 예외를 추가하지 않았습니다. -->
<network-security-config />
"""
        }

        File(xmlDir, "network_security_config.xml").writeText(content)
    }
}

val devBackendHost = localProperties.getProperty("API_BASE_URL", "")
    .substringAfter("://", missingDelimiterValue = "")
    .substringBefore("/")
    .substringBefore(":")

android {
    namespace = "com.gms.cheerlotandroid"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.gms.cheerlotandroid"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${localProperties.getProperty("API_BASE_URL", "")}\"",
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

androidComponents {
    // 변형(variant)마다 독립된 태스크 인스턴스를 등록합니다. 태스크 하나를 여러 변형이
    // 공유하면 출력 디렉토리 배선이 변형별로 꼬일 수 있어, 변형별 이름/출력 경로를 분리합니다.
    onVariants { variant ->
        val variantTaskName = "generate${variant.name.replaceFirstChar { it.uppercase() }}NetworkSecurityConfig"
        val generateTask = tasks.register<GenerateNetworkSecurityConfig>(variantTaskName) {
            backendHost.set(devBackendHost)
            outputDir.set(layout.buildDirectory.dir("generated/networkSecurityConfig/${variant.name}/res"))
        }

        variant.sources.res?.addGeneratedSourceDirectory(
            generateTask,
            GenerateNetworkSecurityConfig::outputDir
        )
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.common)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

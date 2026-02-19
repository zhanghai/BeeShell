import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins { id("com.android.application") }

android {
    namespace = "me.zhanghai.android.beeshell"
    compileSdk = 36
    ndkVersion = "28.1.13356709"
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "me.zhanghai.android.beeshell"
        minSdk = 21
        targetSdk = 36
        versionCode = 5
        versionName = "1.0.4"
        resValue("string", "app_version", "$versionName ($versionCode)")
    }

    signingConfigs {
        create("release") {
            storeFile = System.getenv("STORE_FILE")?.let { rootProject.file(it) }
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
        resValues = true
        viewBinding = true
    }
    packaging {
        jniLibs {
            keepDebugSymbols += "**/libbsh.so"
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.fragment:fragment-ktx:1.8.8")
    val androidxLifecycleVersion = "2.9.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$androidxLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$androidxLifecycleVersion")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("me.zhanghai.android.linenoise:library:1.0.2")
    implementation("org.apache-extras.beanshell:bsh:2.0b6")
}

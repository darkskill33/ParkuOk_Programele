# ParkuOk

## Overview

ParkuOk is an Android parking system application built in Kotlin for smart devices. It demonstrates mobile app development skills with Android Jetpack libraries, material design, location-aware mapping, network integration, and a multi-screen user experience.

## Key Features

- User authentication: login, registration, and profile management
- Parking reservation history and payment history tracking
- Payment methods management and add/remove card support
- Location and map support for parking navigation
- Notifications and push-style receiver handling
- User profile editing and license plate management

## Technologies

- Kotlin
- Android SDK 24+ (compileSdk 35, targetSdk 35)
- Jetpack Compose / AndroidX UI
- Material Design 3
- Retrofit + Gson for REST API integration
- MapLibre / OSMDroid for map display
- AndroidX Lifecycle, AppCompat, ConstraintLayout
- Firebase App Distribution

## Project Structure

- `app/` – Android application module
- `app/src/main/AndroidManifest.xml` – app permissions and activity declarations
- `app/build.gradle.kts` – module configuration and dependencies
- `build.gradle.kts` – top-level Gradle configuration
- `gradle/` – dependency versions and wrapper configuration

## Build & Run

1. Open the project in Android Studio.
2. Sync Gradle.
3. Build and run on an emulator or physical device with API 24+.

## Notes

The app package is `com.ismanieji.parkingosystema` and the launcher label is `ParkuOk`.

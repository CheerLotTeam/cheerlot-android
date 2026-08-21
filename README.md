# ⚾️ 쳐랏! Android

![Kotlin](https://img.shields.io/badge/Kotlin-2.2-orange?style=flat-square&logo=kotlin)
![Platform](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square&logo=android)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/minSdk-26-blue?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-green?style=flat-square)
![Release](https://img.shields.io/github/v/release/CheerLotTeam/cheerlot-android?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

야구 팬이 선수별 응원가를 더 쉽고 빠르게 탐색하고 재생할 수 있도록 만든 Jetpack Compose 기반 Android 앱입니다.

<a href="https://play.google.com/store/apps/details?id=com.gms.cheerlotandroid">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/ko_badge_web_generic.png" alt="playstore" height="80"/>
</a>

<br>

## About

**쳐랏**은 야구 팬들이 구단과 선수 중심으로 응원가를 탐색하고 재생할 수 있도록 만든 앱입니다.
선수별 응원가 재생, 팀 테마 변경, 선수 검색, 라인업 기반 연속 재생, 백그라운드 재생(알림/잠금화면 컨트롤)까지 고려하여 팬들이 더 빠르고 직관적으로 응원가를 즐길 수 있는 경험을 제공하는 것을 목표로 합니다. [iOS 버전](https://github.com/CheerLotTeam/cheerlot-ios)과 동일한 경험을 제공합니다.

<br>

## Features

- 구단별 선수 및 경기 정보 조회
- 팀 선수 응원가 검색 및 미니 플레이어를 통한 백그라운드 재생
- 라인업 기반 응원가 연속 재생, 셔플/한 곡 반복
- 팀 선택 기반 앱 테마 및 런처 아이콘(Adaptive Icon) 적용
- 알림/잠금화면 시스템 미디어 컨트롤(다음곡/이전곡) 지원

<br>

## Tech Stack

### Frameworks
- Jetpack Compose (Material3)
- Navigation Compose
- Media3 (ExoPlayer, MediaSession)
- Room
- DataStore
- Retrofit + kotlinx.serialization
- Firebase (Remote Config, Crashlytics)
- Amplitude Analytics

### Architecture
- MVVM + Clean Architecture
- Repository
- UseCase
- 수동 DI (AppContainer 기반 ViewModelFactory)

### Tools
- Kotlin 2.2
- Android Gradle Plugin
- KSP

<br>

## Architecture

이 프로젝트는 **Jetpack Compose 기반 MVVM 구조**를 중심으로, 화면 상태 관리와 비즈니스 로직, 데이터 접근 책임을 분리하도록 구성되어 있습니다. Presentation, Domain, Data, Core, Design 계층을 기준으로 기능을 나누고, ViewModel은 UseCase를 통해 도메인 로직을 실행하며 Repository는 데이터 소스 접근을 추상화합니다.

- **Presentation**
  - Jetpack Compose 기반으로 화면을 선언적으로 구성합니다.
  - 각 화면은 `Screen`(UI) + `ViewModel`(상태/액션) + `UiState`로 구성됩니다.

- **ViewModel**
  - 화면 상태와 사용자 액션을 관리합니다.
  - View에서 필요한 데이터를 가공하고 UseCase 호출 결과를 UI 상태로 변환합니다.

- **UseCase**
  - 앱의 주요 기능 단위 로직을 담당합니다.
  - ViewModel이 직접 Repository 구현체에 의존하지 않도록 중간 계층 역할을 합니다.

- **Repository**
  - 로컬 데이터와 원격 데이터 접근을 추상화합니다.
  - Room, DataStore, 네트워크 등 실제 데이터 소스와 도메인 계층 사이의 경계를 만듭니다.

- **Core**
  - `core/di`: UseCase, Repository, ViewModel 생성 책임을 한곳(AppContainer)에서 관리합니다.
  - `core/navigation`: Navigation Compose 기반 화면 전환과 sheet/full-screen 모달 흐름을 관리합니다.
  - `core/media`: MediaSessionService 기반 백그라운드 재생과 시스템 미디어 컨트롤을 담당합니다.

- **Design**
  - 색상, 타이포그래피, 공용 컴포넌트 등 디자인 시스템을 별도 계층으로 분리해 화면 간 재사용성을 높입니다.

<br>


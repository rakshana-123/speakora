# Speakora — AI Communication Coach

Daily communication improvement platform with an AI speaking coach, habit streaks,
roleplay scenarios, vocabulary training, and analytics.

This repository contains **two deployables of the same product**:

| Artifact | Path | What it is |
|---|---|---|
| Android app | `app/` | Kotlin + Jetpack Compose app (Room, Retrofit, Gemini AI coach with on-device fallback engine) |
| Web app | `web/index.html` | Single-file, no-build web port of the coach (same exercises, same local coach engine) + APK download |

## Live deployment (Render)

The `render.yaml` blueprint at the repo root deploys `web/` as a free static site:

- Landing page + installable Android APK download
- Fully working web version: daily workout, 12 practice drills (quizzes, writing
  evaluation, roleplay chat, speaking drills with timers), Aura coach chat,
  XP/levels/streaks with localStorage persistence, heatmap and achievements

## CI/CD pipeline

```
push / PR to main
      │
      ▼
GitHub Actions (.github/workflows/ci-cd.yml)
  • CI : ./gradlew assembleDebug (compile check, APK artifact on every PR/push)
  • CD : on main → copies the fresh APK into web/speakora.apk and commits it
      │
      ▼
Render auto-deploy (autoDeploy: yes on the static site)
  • Live site + APK download updated automatically
```

- **Live site:** https://speakora-tjjz.onrender.com
- The workflow commit carries `[skip ci]` so the pipeline never loops.
- The debug keystore is committed intentionally: debug keys are public
  (`android`/`androiddebugkey`) and CI needs a stable signing identity so
  updated APKs install over previous ones without uninstalling.
- PRs get the built APK as a downloadable artifact; nothing deploys from a PR.

## Login (admin + users)

Both the Android app and the web app open with a login screen.

| Role | How to get it | Extra powers |
|---|---|---|
| **ADMIN** | Default account `admin` / `admin123` (seeded on first launch — change after first login) | Admin tab/panel: registered account list, device usage stats, version info, manual update check |
| **USER** | Self sign-up from the login screen | Standard experience |

- Authentication is local/offline-first: Room (Android) or localStorage (web).
  Passwords are stored as SHA-256(salt + password), never plain text.
- Sessions persist until logout.

## In-app updates

- Every CI build publishes `web/latest.json` (versionCode, versionName,
  apkUrl, changelog) next to the APK; Render serves it at
  https://speakora-tjjz.onrender.com/latest.json
- The Android app checks that manifest on launch (and on demand from
  Profile → *Check for updates*). A newer versionCode triggers an update
  dialog → DownloadManager download → system installer prompt
  (`REQUEST_INSTALL_PACKAGES` + FileProvider are configured).
- The web app shows a refresh banner when `latest.json` is newer than the
  deployed build.
- To ship a release: bump `versionCode`/`versionName` in `app/build.gradle.kts`
  and push to `main` — CI does the rest.

## Build the Android app

```bash
gradle assembleDebug     # APK at app/build/outputs/apk/debug/app-debug.apk
```

Requirements: JDK 17+, Android SDK (platform 36.1, build-tools 36), Gradle 9.3.1.
Set your Gemini key in `.env` (`GEMINI_API_KEY=...`) to enable live AI coaching;
without a key the app automatically uses the built-in on-device coach engine.

## Deploy to Render

Dashboard → New → Blueprint → select this repo → apply. Or with the CLI:

```bash
render blueprint launch https://github.com/<you>/speakora
```

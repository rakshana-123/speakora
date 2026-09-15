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

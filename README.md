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

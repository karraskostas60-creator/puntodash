# PuntoDash – Fiat Grande Punto OEM-style launcher

Version 2.0, based on the user's existing launcher project.

## Design
- Fiat Grande Punto title and black Grande Punto centre image
- Squarer OEM-style controls
- Dark charcoal / grey dual-tone interface with restrained blue accent
- Functional home-screen Radio preview concept
- Maps shortcut only (no maps preview)
- Phone/Bluetooth and Settings on the right
- Persistent vertical multimedia volume control
- Soft custom button-click sound with On/Off setting
- Full-screen Radio interface with presets and AUTO/SCAN control

## Radio hardware note
The UI includes the Radio controls, but the real FM tuner commands are firmware-specific on Android head units. Digital iQ manuals document FM/AM, presets, seek and Auto Save/auto station search, but they do not publish a public Android API/intent for the BLG tuner. Therefore this build does **not** fake station changes. The `radioAction()` adapter is the place to add the exact tuner intents once the unit's firmware/API is identified. The AUTO button is intentionally labeled and ready for that integration.

## Build on GitHub
A GitHub Actions workflow is included. It installs Gradle and builds `app-debug.apk` without needing a checked-in `gradlew` file.

## Important
This is an original interface concept and is not official Fiat software.

# StudyLog Developer Guide

## Product overview

StudyLog is a Java Swing desktop utility for recording completed study sessions and reviewing time totals. The product deliberately avoids creating, completing, prioritising, or scheduling tasks.

## Architecture

```text
StudyLogApplication (Swing UI)
        |
        +--> SessionRepository (in-memory operations and totals)
        |         |
        |         +--> StudySession (immutable domain record)
        |
        +--> SessionStorage (TSV persistence)
```

`StudyLogApplication` owns UI state and translates user actions into repository operations. `SessionRepository` contains the business rules for filtering and calculating totals. `SessionStorage` is isolated from UI code so it can be tested with temporary files.

## Data format

Sessions are stored in `data/sessions.tsv`. Each non-header line has five tab-separated fields: UUID, Base64URL-encoded subject, ISO-8601 date, duration in minutes, and Base64URL-encoded note. Encoding text fields keeps tabs, line breaks, and Unicode notes safe without adding an external dependency.

## Build, test, and package

Use the Gradle wrapper so contributors do not need a global Gradle installation:

```text
./gradlew test
./gradlew jar
```

On Windows, use `gradlew.bat` instead. The project requires JDK 25. Copy `build/libs/study-log-0.1.0.jar` to `release/study-log.jar` when preparing a release.

## Testing strategy

Automated JUnit tests cover:

- calculation of all-time and weekly totals;
- subject filtering;
- storage round-tripping, including Unicode text;
- malformed-storage rejection;
- edit and date-range business rules.

Before each release, follow the manual UI test plan in `test/ui-test-plan.md`, then perform a cross-platform launch where possible.

## Engineering process

Develop features in small, independently testable increments. Make the working branch pass `test` before merging it into `master`; use focused commits such as `add session storage` or `add subject filter`. Update the user guide, tests, and AI interaction summary when a user-facing feature changes.

## Acknowledgements

- The product structure and incremental quality process were informed by the [CS2103/T Project Duke guidance](https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/cs3227.html), but no Duke task-management or chatbot feature was reused.
- This initial implementation was created with AI assistance and reviewed by the author. Detailed prompt summaries and verification notes are in `logs/` and reflections are in `docs/Reflections.md`.

# Java 25, feature, and verification interaction summary

## Prompts and decisions

1. The author asked for an audit against the assignment and rough Project Duke process. The audit found Java 21 configuration, limited features/tests, incomplete UI test evidence, incomplete logs, and no GitHub repository.
2. The author then asked to fix those findings one by one. The first implementation increment changed the build to require JDK 25, added editing and inclusive date-range filters, expanded tests, added a manual UI test plan, and added GitHub Actions CI.

## Verification

- A clean `gradlew.bat clean test jar --warning-mode all` build passed under Temurin JDK 25.0.4.1.
- The JAR was regenerated at `release/study-log.jar` and has `studylog.StudyLogApplication` as its main class.
- Native UI automation was unavailable in the current environment; therefore UI test cases were documented but still require a human run on Windows, Linux, and macOS.

## Engineering judgement

Gradle 8.12.1 could not run under JDK 25. Its wrapper was upgraded to Gradle 9.1.0. Gradle 9 then reported a missing JUnit Platform launcher, so that dependency was explicitly declared. This was verified by a successful clean build instead of assuming the configuration change was sufficient.


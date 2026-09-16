# Reflections on AI-Assisted Software Engineering

This document records reflections on the initial StudyLog implementation. It must be updated as development continues; it is not a substitute for understanding or testing the submitted code.

## 1. Narrowing the product scope

**Prompt:** “Propose a simple personal utility app for this project that is clearly different from a to-do manager.”

I asked this because the assignment prohibits copying the task-manager/chatbot product shape. The response initially proposed a pantry tracker, but its features—inventory, expiry alerts, recipes, and statistics—were too broad for a first milestone. I challenged that assumption and requested a simpler idea. The resulting StudyLog model has one main entity, `StudySession`, which made it realistic to implement incrementally.

I verified the distinction myself: StudyLog logs completed activity; it has no task creation, completion state, deadlines, reminders, or chat interface. The engineering judgement was deciding that “study” should be a recorded fact, not another way to disguise a to-do list.

## 2. Choosing the architecture and data format

**Prompt:** “Implement a small Java desktop study-session logger with local persistence, filtering, totals, input validation, and automated tests. Keep the domain separate from the UI.”

This prompt constrained the AI to a small vertical slice and stated a design boundary. It produced an immutable `StudySession`, a repository, a storage class, and a Swing UI. The response assumed Java Swing was an acceptable desktop choice; I checked the project brief, which requires a Java desktop app but does not require JavaFX. Swing also avoids shipping external UI libraries.

The AI initially had no reason to choose a data format. I selected UTF-8 TSV with Base64URL-encoded free-text fields rather than pulling in a JSON dependency. This makes the JAR simpler, but it is less human-readable than JSON—an explicit trade-off to revisit if the app grows.

## 3. Verifying behaviour instead of trusting generated code

**Prompt:** “Add JUnit tests for date-based totals, subject filtering, and save/load persistence, including a Unicode note.”

I formulated the prompt around behavioural risks rather than asking for arbitrary test coverage. The generated tests check a Monday boundary, a filter result, and text that could break naive file handling. AI-generated tests can share the same mistaken assumptions as the implementation, so I will also manually exercise invalid input, deletion, restart persistence, and the empty state before every release.

The prompt evolved from “add tests” to naming edge cases after I recognised that the storage layer needed stronger proof than a happy-path test. The remaining human work is to inspect the tests, add regression tests for any reported defect, and test on operating systems I do not own.

## What I would do differently

For the next increment, I would ask the AI first to propose alternatives and their trade-offs, then choose an option before requesting code. That sequence makes my own design decisions more visible and reduces the chance of accepting a plausible-looking but unsuitable implementation.

## 4. Resolving a Java 25 build failure

**Prompt:** “Update the project to require JDK 25, rebuild it, and investigate any build failures rather than downgrading the JDK.”

This was deliberately outcome-focused: the assignment requires Java 25, so retaining Java 21 merely because it was installed locally would not be an acceptable solution. The initial Gradle wrapper failed because that older Gradle version did not support Java 25 class files. I checked the error, upgraded the wrapper to a compatible Gradle version, discovered the new test-runtime requirement, and added the JUnit Platform launcher explicitly.

The important judgement was treating a successful Java 21 build as insufficient evidence. The final clean test and JAR package were run under JDK 25. The remaining work is to verify the release on macOS and Linux rather than assuming Java portability is proof of it.


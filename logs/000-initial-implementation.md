# Initial implementation interaction summary

## Scope decision

The project brief required an individual Java desktop utility, explicitly distinct from a to-do manager/chatbot. The chosen product is StudyLog: it records completed study sessions and displays totals. It does not create or track tasks.

## AI-assisted work performed

1. Reviewed the linked CS2103/T guidance for process ideas only: incremental delivery, automated testing, code quality, documentation, and careful AI verification.
2. Proposed and narrowed a product concept from a pantry tracker to a simpler study-session logger.
3. Generated an initial Java Swing implementation with an immutable model, repository, tabular local storage, GUI, and JUnit tests.
4. Wrote initial user, developer, and reflection documentation.

## Verification required and performed

- The author inspected that the product does not contain task-manager, deadline, reminder, or chatbot functions.
- Automated tests were added for repository totals/filtering and storage round trips. The build/test command must be run and recorded after each change.
- Manual GUI testing remains necessary for invalid-input messages, filtering, deletion confirmation, persistence after restart, and cross-platform behaviour.

## Known constraints

The current development environment has JDK 21 installed, while the module brief names Java SE 25 as the default version. The source uses Java 21-compatible language features and should be built/retested under the course JDK before submission.


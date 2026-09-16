# StudyLog User Guide

StudyLog is a lightweight desktop app for recording study time. It is not a planner or task manager: each entry represents time you have already spent studying.

## Requirements and setup

- JDK 25
- Windows, macOS, or Linux

Run the packaged application from the repository root:

```text
java -jar release/study-log.jar
```

On first use, StudyLog creates `data/sessions.tsv` beside the JAR. Keep this file to retain your history. Copy it elsewhere before moving to another computer.

## Log a session

In **Log a study session**, enter:

- **Subject** — a non-empty name, such as `CS3227`.
- **Date** — an ISO date such as `2026-09-16`.
- **Minutes** — a positive whole number, such as `90`.
- **Note** — optional context, such as `revised UML diagrams`.

Select **Add session**. The new session appears in the table and is saved automatically.

## Review your history

The table lists sessions newest first. Use the **Show** drop-down to view a single subject. You can also enter inclusive **From** and **To** dates and select **Apply filters**. Leave either date blank to make that boundary open. Select **Clear filters** to return to every session.

The three summary cards show total recorded time, time from the current Monday onwards, and all-time time grouped by subject.

## Edit a session

Select a table row and choose **Edit selected**. Its details appear in the entry form. Change the required fields and choose **Save changes**. The session keeps its original identity and is saved automatically.

## Delete a session

Select one table row, choose **Delete selected**, and confirm. Deletion is permanent once confirmed.

## Troubleshooting

- If a date is rejected, use `YYYY-MM-DD`, for example `2026-09-16`.
- If saved sessions cannot be read, StudyLog starts with an empty display and preserves the original data file for inspection. Restore a backup if necessary.

## Manual test checklist

After launching the JAR, use these checks before a release:

1. Add a valid `CS3227`, `2026-09-16`, `90` session and confirm it appears in the table and totals.
2. Try `abc` for minutes and an invalid date; confirm both show helpful errors and add no session.
3. Add sessions on two dates and subjects; filter by subject and then by an inclusive date range.
4. Edit a session and confirm the changed details remain after restarting the app.
5. Select a session, delete it, confirm deletion, and restart to ensure it stays deleted.

# StudyLog manual UI test plan

Run `java -jar release/study-log.jar` from an empty folder. Record the platform, JDK version, actual result, and any defect found for every test below. Stop and fix a failed test before continuing.

| ID | Aim | Steps | Expected result |
| --- | --- | --- | --- |
| UI-01 | Empty launch | Start the JAR with no `data/` folder. | A usable empty window opens; all totals are zero and the table is empty. |
| UI-02 | Add valid session | Enter `CS3227`, `2026-09-16`, `90`, and `revision`; select **Add session**. | One row appears; all-time total is `1 h 30 min`; `data/sessions.tsv` is created. |
| UI-03 | Reject bad input | Enter `abc` minutes, then an invalid date such as `16-09-2026`. | A specific error appears each time; no row is added. |
| UI-04 | Filter sessions | Add two subjects/dates. Use subject filter, then `From`/`To`, then clear filters. | The table shows only matching rows; date bounds are inclusive; clearing restores every row. |
| UI-05 | Edit session | Select a row, choose **Edit selected**, change minutes, select **Save changes**. | The original row is updated rather than duplicated; totals update. |
| UI-06 | Delete session | Select a row, choose **Delete selected**, and confirm. Restart the JAR. | The row is removed and remains absent after restart. |

## Recorded test run

**Date:** 16 September 2026  
**Environment:** Windows; JDK 25  
**Tester:** Project author  
**Overall result:** Passed

| ID | Actual result | Status |
| --- | --- | --- |
| UI-01 | The JAR opened to a usable empty StudyLog window with zero totals and no sessions. | Pass |
| UI-02 | A valid session was added, displayed, and reflected in the total. | Pass |
| UI-03 | Invalid minutes and date input showed errors and did not add a session. | Pass |
| UI-04 | Subject and inclusive date filters showed the correct sessions; clearing restored all sessions. | Pass |
| UI-05 | Editing updated the existing session and its totals without creating a duplicate. | Pass |
| UI-06 | Deletion removed the selected session and the change persisted after restart. | Pass |

package studylog.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionRepositoryTest {
    @Test
    void totalsIncludeOnlySessionsOnOrAfterStartDate() {
        SessionRepository repository = new SessionRepository(List.of(
                new StudySession("CS3227", LocalDate.of(2026, 9, 14), 60, "Revision"),
                new StudySession("MA1521", LocalDate.of(2026, 9, 13), 45, "Tutorial")));

        assertEquals(105, repository.totalMinutes());
        assertEquals(60, repository.totalMinutesSince(LocalDate.of(2026, 9, 14)));
    }

    @Test
    void matchingSubjectReturnsOnlyTheChosenSubject() {
        SessionRepository repository = new SessionRepository(List.of(
                new StudySession("CS3227", LocalDate.of(2026, 9, 14), 60, "Revision"),
                new StudySession("MA1521", LocalDate.of(2026, 9, 13), 45, "Tutorial")));

        assertEquals(1, repository.matchingSubject("CS3227").size());
        assertEquals(2, repository.matchingSubject("All subjects").size());
    }

    @Test
    void matchingDateRangeIncludesBothBoundaries() {
        SessionRepository repository = new SessionRepository(List.of(
                new StudySession("CS3227", LocalDate.of(2026, 9, 13), 30, "A"),
                new StudySession("CS3227", LocalDate.of(2026, 9, 14), 60, "B"),
                new StudySession("CS3227", LocalDate.of(2026, 9, 15), 90, "C")));

        assertEquals(2, repository.matching("CS3227", LocalDate.of(2026, 9, 14),
                LocalDate.of(2026, 9, 15)).size());
    }

    @Test
    void replaceChangesTheSessionWithMatchingIdentifier() {
        StudySession original = new StudySession("CS3227", LocalDate.of(2026, 9, 14), 60, "A");
        SessionRepository repository = new SessionRepository(List.of(original));

        assertEquals(true, repository.replace(new StudySession(original.id(), "CS3227",
                LocalDate.of(2026, 9, 15), 90, "B")));
        assertEquals(90, repository.totalMinutes());
    }

    @Test
    void matchingRejectsAnInvertedDateRange() {
        SessionRepository repository = new SessionRepository(List.of());

        assertThrows(IllegalArgumentException.class, () -> repository.matching("All subjects",
                LocalDate.of(2026, 9, 16), LocalDate.of(2026, 9, 15)));
    }

    @Test
    void replaceReturnsFalseWhenSessionDoesNotExist() {
        SessionRepository repository = new SessionRepository(List.of());

        assertFalse(repository.replace(new StudySession("CS3227", LocalDate.of(2026, 9, 16), 60, "Test")));
    }
}

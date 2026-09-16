package studylog.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** An immutable, dated record of time spent studying one subject. */
public record StudySession(UUID id, String subject, LocalDate date, int durationMinutes, String note) {
    public StudySession {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(note, "note must not be null");
        subject = subject.trim();
        note = note.trim();
        if (subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be blank.");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number of minutes.");
        }
    }

    public StudySession(String subject, LocalDate date, int durationMinutes, String note) {
        this(UUID.randomUUID(), subject.trim(), date, durationMinutes, note.trim());
    }
}

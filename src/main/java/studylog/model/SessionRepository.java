package studylog.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** In-memory collection of study sessions. */
public final class SessionRepository {
    private final List<StudySession> sessions;

    /** Creates a repository containing a defensive copy of the supplied sessions. */
    public SessionRepository(List<StudySession> initialSessions) {
        sessions = new ArrayList<>(initialSessions);
    }

    /** Adds a session to the repository. */
    public void add(StudySession session) {
        sessions.add(session);
    }

    /** Removes the session with the given identifier, if it exists. */
    public boolean remove(UUID id) {
        return sessions.removeIf(session -> session.id().equals(id));
    }

    /** Replaces an existing session with one having the same identifier. */
    public boolean replace(StudySession updatedSession) {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).id().equals(updatedSession.id())) {
                sessions.set(i, updatedSession);
                return true;
            }
        }
        return false;
    }

    /** Returns every session, sorted newest first. */
    public List<StudySession> all() {
        return sessions.stream()
                .sorted(Comparator.comparing(StudySession::date).reversed())
                .toList();
    }

    /** Returns all sessions for a subject, or every session for the all-subjects choice. */
    public List<StudySession> matchingSubject(String subject) {
        return matching(subject, null, null);
    }

    /** Returns sessions matching an optional subject and inclusive date bounds. */
    public List<StudySession> matching(String subject, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        return all().stream()
                .filter(session -> subject == null || subject.equals("All subjects") || session.subject().equals(subject))
                .filter(session -> startDate == null || !session.date().isBefore(startDate))
                .filter(session -> endDate == null || !session.date().isAfter(endDate))
                .toList();
    }

    /** Returns each recorded subject once, in alphabetical order. */
    public List<String> subjects() {
        return sessions.stream().map(StudySession::subject).distinct().sorted().toList();
    }

    /** Returns the duration of every session in minutes. */
    public int totalMinutes() {
        return sessions.stream().mapToInt(StudySession::durationMinutes).sum();
    }

    /** Returns the duration of sessions on or after a given date, in minutes. */
    public int totalMinutesSince(LocalDate startInclusive) {
        return sessions.stream().filter(session -> !session.date().isBefore(startInclusive))
                .mapToInt(StudySession::durationMinutes).sum();
    }

    /** Returns the total recorded minutes for each subject. */
    public Map<String, Integer> totalsBySubject() {
        return sessions.stream().collect(Collectors.groupingBy(StudySession::subject,
                Collectors.summingInt(StudySession::durationMinutes)));
    }
}

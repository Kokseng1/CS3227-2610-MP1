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

    public SessionRepository(List<StudySession> initialSessions) {
        sessions = new ArrayList<>(initialSessions);
    }

    public void add(StudySession session) {
        sessions.add(session);
    }

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

    public List<StudySession> all() {
        return sessions.stream()
                .sorted(Comparator.comparing(StudySession::date).reversed())
                .toList();
    }

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

    public List<String> subjects() {
        return sessions.stream().map(StudySession::subject).distinct().sorted().toList();
    }

    public int totalMinutes() {
        return sessions.stream().mapToInt(StudySession::durationMinutes).sum();
    }

    public int totalMinutesSince(LocalDate startInclusive) {
        return sessions.stream().filter(session -> !session.date().isBefore(startInclusive))
                .mapToInt(StudySession::durationMinutes).sum();
    }

    public Map<String, Integer> totalsBySubject() {
        return sessions.stream().collect(Collectors.groupingBy(StudySession::subject,
                Collectors.summingInt(StudySession::durationMinutes)));
    }
}

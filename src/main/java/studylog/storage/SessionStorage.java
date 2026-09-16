package studylog.storage;

import studylog.model.StudySession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/** Reads and writes sessions in a small UTF-8 TSV file. */
public final class SessionStorage {
    private static final String HEADER = "id\tsubject\tdate\tdurationMinutes\tnote";
    private final Path file;

    public SessionStorage(Path file) {
        this.file = file;
    }

    public List<StudySession> load() throws IOException {
        if (Files.notExists(file)) {
            return List.of();
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<StudySession> sessions = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split("\\t", -1);
            if (fields.length != 5) {
                throw new IOException("Invalid data at line " + (i + 1) + ".");
            }
            try {
                sessions.add(new StudySession(UUID.fromString(fields[0]), decode(fields[1]),
                        LocalDate.parse(fields[2]), Integer.parseInt(fields[3]), decode(fields[4])));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid data at line " + (i + 1) + ".", exception);
            }
        }
        return sessions;
    }

    public void save(List<StudySession> sessions) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (StudySession session : sessions) {
            lines.add(session.id() + "\t" + encode(session.subject()) + "\t" + session.date() + "\t"
                    + session.durationMinutes() + "\t" + encode(session.note()));
        }
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    private String encode(String text) {
        return Base64.getUrlEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String text) {
        return new String(Base64.getUrlDecoder().decode(text), StandardCharsets.UTF_8);
    }
}

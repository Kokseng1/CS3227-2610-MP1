package studylog.storage;

import studylog.model.StudySession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveThenLoadPreservesAUnicodeNote() throws Exception {
        SessionStorage storage = new SessionStorage(temporaryDirectory.resolve("sessions.tsv"));
        List<StudySession> sessions = List.of(new StudySession("CS3227", LocalDate.of(2026, 9, 16),
                90, "Reviewed UI — needs follow-up"));

        storage.save(sessions);

        assertEquals(sessions, storage.load());
    }

    @Test
    void loadRejectsMalformedData() throws Exception {
        Path file = temporaryDirectory.resolve("sessions.tsv");
        java.nio.file.Files.writeString(file, "id\tsubject\tdate\tdurationMinutes\tnote\nnot-a-session");

        assertThrows(java.io.IOException.class, () -> new SessionStorage(file).load());
    }
}

package studylog.ui;

import studylog.model.StudySession;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Table adapter for displaying the currently selected study sessions. */
public final class StudySessionTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Date", "Subject", "Minutes", "Note"};
    private List<StudySession> sessions = new ArrayList<>();

    /** Replaces the rows displayed by this table model. */
    public void show(List<StudySession> newSessions) {
        sessions = new ArrayList<>(newSessions);
        fireTableDataChanged();
    }

    /** Returns the session at a valid displayed row index. */
    public StudySession sessionAt(int row) {
        return sessions.get(row);
    }

    /** Returns the number of displayed sessions. */
    @Override
    public int getRowCount() {
        return sessions.size();
    }

    /** Returns the number of table columns. */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /** Returns the display name for a table column. */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /** Returns a value suitable for displaying at a table cell. */
    @Override
    public Object getValueAt(int row, int column) {
        StudySession session = sessions.get(row);
        return switch (column) {
        case 0 -> session.date();
        case 1 -> session.subject();
        case 2 -> session.durationMinutes();
        case 3 -> session.note();
        default -> throw new IllegalArgumentException("Unknown column: " + column);
        };
    }
}

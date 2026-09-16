package studylog;

import studylog.model.SessionRepository;
import studylog.model.StudySession;
import studylog.storage.SessionStorage;
import studylog.ui.StudySessionTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** The StudyLog desktop application's entry point and main window. */
public final class StudyLogApplication {
    private final SessionStorage storage = new SessionStorage(Path.of("data", "sessions.tsv"));
    private final SessionRepository repository;
    private final StudySessionTableModel tableModel = new StudySessionTableModel();
    private final JComboBox<String> subjectFilter = new JComboBox<>();
    private final JLabel allTimeLabel = new JLabel();
    private final JLabel weekLabel = new JLabel();
    private final JLabel subjectTotalsLabel = new JLabel();
    private final JTextField subjectField = new JTextField(16);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 16);
    private final JTextField minutesField = new JTextField(16);
    private final JTextField noteField = new JTextField(16);
    private final JTextField startDateFilterField = new JTextField(10);
    private final JTextField endDateFilterField = new JTextField(10);
    private final JButton saveButton = new JButton("Add session");
    private JTable table;
    private UUID editingSessionId;

    private StudyLogApplication() {
        repository = new SessionRepository(loadSessions());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudyLogApplication().show());
    }

    private List<StudySession> loadSessions() {
        try {
            return storage.load();
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "Could not read saved sessions. Starting with an empty log.\n"
                    + exception.getMessage(), "StudyLog", JOptionPane.WARNING_MESSAGE);
            return List.of();
        }
    }

    private void show() {
        JFrame frame = new JFrame("StudyLog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(createContent());
        frame.setMinimumSize(new Dimension(850, 520));
        frame.pack();
        frame.setLocationByPlatform(true);
        refresh();
        frame.setVisible(true);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(createSummaryPanel(), BorderLayout.NORTH);
        content.add(createTablePanel(), BorderLayout.CENTER);
        content.add(createEntryPanel(), BorderLayout.EAST);
        return content;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.add(summaryCard("All-time total", allTimeLabel));
        panel.add(summaryCard("This week", weekLabel));
        panel.add(summaryCard("By subject", subjectTotalsLabel));
        return panel;
    }

    private JPanel summaryCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        controls.add(new JLabel("Show: "));
        subjectFilter.addActionListener(event -> refreshTable());
        controls.add(subjectFilter);
        controls.add(new JLabel("  From: "));
        controls.add(startDateFilterField);
        controls.add(new JLabel("  To: "));
        controls.add(endDateFilterField);
        JButton applyFilterButton = new JButton("Apply filters");
        applyFilterButton.addActionListener(event -> refreshTable());
        controls.add(applyFilterButton);
        JButton clearFilterButton = new JButton("Clear filters");
        clearFilterButton.addActionListener(event -> clearFilters());
        controls.add(clearFilterButton);
        JButton editButton = new JButton("Edit selected");
        editButton.addActionListener(event -> editSelected());
        controls.add(editButton);
        JButton deleteButton = new JButton("Delete selected");
        deleteButton.addActionListener(event -> deleteSelected());
        controls.add(deleteButton);
        table = new JTable(tableModel);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEntryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log a study session"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new java.awt.Insets(4, 6, 4, 6);
        constraints.anchor = GridBagConstraints.WEST;
        addField(panel, constraints, 0, "Subject", subjectField);
        addField(panel, constraints, 1, "Date (YYYY-MM-DD)", dateField);
        addField(panel, constraints, 2, "Minutes", minutesField);
        addField(panel, constraints, 3, "Note (optional)", noteField);
        saveButton.addActionListener(event -> saveSession());
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(saveButton, constraints);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints constraints, int row, String label, JTextField field) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label + ":"), constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, constraints);
    }

    private void saveSession() {
        try {
            int minutes = Integer.parseInt(minutesField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            StudySession session = editingSessionId == null
                    ? new StudySession(subjectField.getText(), date, minutes, noteField.getText())
                    : new StudySession(editingSessionId, subjectField.getText(), date, minutes, noteField.getText());
            if (editingSessionId == null) {
                repository.add(session);
            } else {
                repository.replace(session);
            }
            saveAndRefresh();
            clearEntryForm();
        } catch (NumberFormatException exception) {
            showError("Minutes must be a whole number greater than zero.");
        } catch (DateTimeParseException exception) {
            showError("Date must use the YYYY-MM-DD format, for example 2026-09-16.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void editSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select a session to edit first.");
            return;
        }
        StudySession session = tableModel.sessionAt(selectedRow);
        editingSessionId = session.id();
        subjectField.setText(session.subject());
        dateField.setText(session.date().toString());
        minutesField.setText(String.valueOf(session.durationMinutes()));
        noteField.setText(session.note());
        saveButton.setText("Save changes");
        subjectField.requestFocusInWindow();
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select a session to delete first.");
            return;
        }
        StudySession session = tableModel.sessionAt(selectedRow);
        if (JOptionPane.showConfirmDialog(null, "Delete the selected session?", "Confirm deletion",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            repository.remove(session.id());
            saveAndRefresh();
        }
    }

    private void saveAndRefresh() {
        try {
            storage.save(repository.all());
            refresh();
        } catch (IOException exception) {
            showError("The change is in memory but could not be saved: " + exception.getMessage());
        }
    }

    private void refresh() {
        String selected = (String) subjectFilter.getSelectedItem();
        subjectFilter.removeAllItems();
        subjectFilter.addItem("All subjects");
        repository.subjects().forEach(subjectFilter::addItem);
        subjectFilter.setSelectedItem(selected == null ? "All subjects" : selected);
        refreshTable();
        allTimeLabel.setText(formatMinutes(repository.totalMinutes()));
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        weekLabel.setText(formatMinutes(repository.totalMinutesSince(monday)));
        subjectTotalsLabel.setText(formatSubjectTotals(repository.totalsBySubject()));
    }

    private void refreshTable() {
        try {
            tableModel.show(repository.matching((String) subjectFilter.getSelectedItem(),
                    parseOptionalDate(startDateFilterField.getText()), parseOptionalDate(endDateFilterField.getText())));
        } catch (DateTimeParseException exception) {
            showError("Filter dates must use YYYY-MM-DD, for example 2026-09-16.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private LocalDate parseOptionalDate(String text) {
        return text.isBlank() ? null : LocalDate.parse(text.trim());
    }

    private void clearFilters() {
        subjectFilter.setSelectedItem("All subjects");
        startDateFilterField.setText("");
        endDateFilterField.setText("");
        refreshTable();
    }

    private void clearEntryForm() {
        editingSessionId = null;
        subjectField.setText("");
        dateField.setText(LocalDate.now().toString());
        minutesField.setText("");
        noteField.setText("");
        saveButton.setText("Add session");
    }

    private String formatSubjectTotals(Map<String, Integer> totals) {
        if (totals.isEmpty()) {
            return "No sessions yet";
        }
        return "<html>" + totals.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + formatMinutes(entry.getValue()))
                .reduce((left, right) -> left + "<br>" + right).orElse("") + "</html>";
    }

    private String formatMinutes(int minutes) {
        return minutes / 60 + " h " + minutes % 60 + " min";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Cannot complete action", JOptionPane.ERROR_MESSAGE);
    }
}

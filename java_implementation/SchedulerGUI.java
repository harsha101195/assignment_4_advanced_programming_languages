import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SchedulerGUI extends JFrame {
    private Scheduler scheduler = new Scheduler();
    private java.util.List<String> employees = new ArrayList<>();
    private DefaultListModel<String> prefListModel = new DefaultListModel<>();

    private JTextField empNameField = new JTextField(12);
    private JComboBox<String> empCombo = new JComboBox<>();
    private JComboBox<String> dayCombo = new JComboBox<>(Scheduler.DAYS);
    private JComboBox<String> shiftCombo = new JComboBox<>(Scheduler.SHIFTS);

    public SchedulerGUI() {
        setTitle("Employee Shift Scheduler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel empPanel = new JPanel();
        empPanel.setLayout(new BoxLayout(empPanel, BoxLayout.X_AXIS));
        empPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        empPanel.add(new JLabel("Add Employee: "));
        empPanel.add(empNameField);
        JButton addEmpBtn = new JButton("Add");
        empPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        empPanel.add(addEmpBtn);
        JLabel empCountLabel = new JLabel("Number of employees: 0");
        empPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        empPanel.add(empCountLabel);
        mainPanel.add(empPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel prefPanel = new JPanel();
        prefPanel.setLayout(new BoxLayout(prefPanel, BoxLayout.X_AXIS));
        prefPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        prefPanel.add(new JLabel("Employee: "));
        empCombo.setPreferredSize(new Dimension(100, 25));
        prefPanel.add(empCombo);
        prefPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        prefPanel.add(new JLabel("Day: "));
        dayCombo.setPreferredSize(new Dimension(100, 25));
        prefPanel.add(dayCombo);
        prefPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        prefPanel.add(new JLabel("Shift: "));
        shiftCombo.setPreferredSize(new Dimension(100, 25));
        prefPanel.add(shiftCombo);
        prefPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton addPrefBtn = new JButton("Add Preference");
        prefPanel.add(addPrefBtn);
        mainPanel.add(prefPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel prefLabel = new JLabel("Preferences:");
        listPanel.add(prefLabel);
        JList<String> prefList = new JList<>(prefListModel);
        JScrollPane prefScroll = new JScrollPane(prefList);
        prefScroll.setPreferredSize(new Dimension(400, 100));
        listPanel.add(prefScroll);
        mainPanel.add(listPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel runPanel = new JPanel();
        runPanel.setLayout(new BoxLayout(runPanel, BoxLayout.X_AXIS));
        runPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton runBtn = new JButton("Run Scheduler");
        runPanel.add(runBtn);
        mainPanel.add(runPanel);

        add(mainPanel, BorderLayout.CENTER);

        addEmpBtn.addActionListener(e -> {
            String emp = empNameField.getText().trim();
            if (emp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an employee name.");
                return;
            }
            if (employees.contains(emp)) {
                JOptionPane.showMessageDialog(this, "Employee already exists.");
                return;
            }
            employees.add(emp);
            empCombo.addItem(emp);
            empNameField.setText("");
            empCountLabel.setText("Number of employees: " + employees.size());
        });

        addPrefBtn.addActionListener(e -> {
            String emp = (String) empCombo.getSelectedItem();
            String day = (String) dayCombo.getSelectedItem();
            String shift = (String) shiftCombo.getSelectedItem();
            if (emp == null || day == null || shift == null) {
                JOptionPane.showMessageDialog(this, "Please select employee, day, and shift.");
                return;
            }
            scheduler.addEmployeePreference(emp, day, shift);
            prefListModel.addElement(emp + " prefers " + shift + " on " + day);
        });

        runBtn.addActionListener(e -> {
            for (String emp : employees) {
                scheduler.preferences.putIfAbsent(emp, new HashMap<>());
            }
            scheduler.assignShifts();
            StringBuilder sb = new StringBuilder();
            for (String day : Scheduler.DAYS) {
                sb.append("\n").append(day).append(":\n");
                for (String shift : Scheduler.SHIFTS) {
                    java.util.List<String> emps = scheduler.schedule.get(day).get(shift);
                    if (emps.size() < 2) {
                        sb.append("  ").append(capitalize(shift)).append(": Employees Unavailable\n");
                    } else {
                        sb.append("  ").append(capitalize(shift)).append(": ").append(String.join(", ", emps)).append("\n");
                    }
                }
            }
            JTextArea scheduleArea = new JTextArea(sb.toString(), 18, 40);
            scheduleArea.setEditable(false);
            JScrollPane schedScroll = new JScrollPane(scheduleArea);
            JOptionPane.showMessageDialog(this, schedScroll, "Generated Schedule", JOptionPane.PLAIN_MESSAGE);
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ScienceFairJudgeAssigner {

    static class Category {
        String name;
        List<Integer> projects;
        List<String> judges;

        public Category(String name) {
            this.name = name;
            this.projects = new ArrayList<>();
            this.judges = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Science Fair Judge Assigner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel projectsLabel = new JLabel("Projects file:");
        JTextField projectsField = new JTextField(20);
        JButton projectsButton = new JButton("Browse");
        projectsButton.addActionListener(new FileChooserActionListener(projectsField, "Select projects file"));

        JLabel judgesLabel = new JLabel("Judges file:");
        JTextField judgesField = new JTextField(20);
        JButton judgesButton = new JButton("Browse");
        judgesButton.addActionListener(new FileChooserActionListener(judgesField, "Select judges file"));

        JButton assignButton = new JButton("Assign Judges");
        assignButton.addActionListener(new AssignActionListener(projectsField, judgesField));

        panel.add(projectsLabel);
        panel.add(projectsField);
        panel.add(projectsButton);
        panel.add(judgesLabel);
        panel.add(judgesField);
        panel.add(judgesButton);
        panel.add(assignButton);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static class FileChooserActionListener implements ActionListener {
        private JTextField field;
        private String title;

        public FileChooserActionListener(JTextField field, String title) {
            this.field = field;
            this.title = title;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(title);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                field.setText(chooser.getSelectedFile().getPath());
            }
        }
    }

    private static class AssignActionListener implements ActionListener  {
        private JTextField projectsField;
        private JTextField judgesField;
        private Map<String, Category> categories;
        private Map<String, List<String>> judgesCategories;

        public AssignActionListener(JTextField projectsField, JTextField judgesField) {
            this.projectsField = projectsField;
            this.judgesField = judgesField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String projectsFile = projectsField.getText();
            String judgesFile = judgesField.getText();

            try {
                categories = readProjectsFile(projectsFile);
                judgesCategories = readJudgesFile(judgesFile);

                assignJudges(categories, judgesCategories);

                writeOutputFile("output.txt", categories);
                Window window = SwingUtilities.getWindowAncestor(projectsField);
                if (window != null) {
                    window.dispose();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error reading files: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void assignJudges(Map<String, Category> categories, Map<String, List<String>> judgesCategories) {
        Map<String, Integer> judgeProjectCount = new HashMap<>();
        Map<String, Integer> overallJudgeProjectCount = new HashMap<>();

        for (Category category : categories.values()) {
            List<List<Integer>> projectGroups = groupProjects(category.projects);

            List<String> eligibleJudges = new ArrayList<>(judgesCategories.keySet());

            if (overallJudgeProjectCount != null && !overallJudgeProjectCount.isEmpty()) {
                eligibleJudges.sort(Comparator.comparingInt(judge -> overallJudgeProjectCount.getOrDefault(judge, 0)));
            }

            Set<String> assignedJudges = new HashSet<>();

            for (List<Integer> projectGroup : projectGroups) {
                List<String> judgesForGroup = new ArrayList<>();
                int projectCount = 0;

                for (String judge : eligibleJudges) {
                    if (assignedJudges.contains(judge)) {
                        continue;
                    }

                    List<String> judgeCategories = judgesCategories.get(judge);
                    if (judgeCategories != null && judgeCategories.contains(category.name)) {
                        int projectsAssigned = judgeProjectCount.getOrDefault(judge, 0);

                        if (projectsAssigned < 6 && judgesForGroup.size() < 3) {
                            judgesForGroup.add(judge);
                            assignedJudges.add(judge);
                            projectCount++;
                            int overallProjectsAssigned = overallJudgeProjectCount.getOrDefault(judge, 0);
                            overallJudgeProjectCount.put(judge, overallProjectsAssigned + 1);
                            judgeProjectCount.put(judge, projectsAssigned + 1);
                        }
                    }

                    if (projectCount >= 6 || judgesForGroup.size() >= 3) {
                        break;
                    }
                }

                category.judges.add(String.join(", ", judgesForGroup));
            }
        }
    }



    private static Map<String, Category> readProjectsFile(String filename) throws IOException {
        Map<String, Category> categories = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+", 2);
                if (parts.length >= 2) {
                    int projectNumber = Integer.parseInt(parts[0]);
                    String categoryName = parts[1].trim();
                    Category category = categories.get(categoryName);
                    if (category == null) {
                        category = new Category(categoryName);
                        categories.put(categoryName, category);
                    }
                    category.projects.add(projectNumber);
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        }
        return categories;
    }

    private static Map<String, List<String>> readJudgesFile(String filename) throws IOException {
        Map<String, List<String>> judgesCategories = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String judgeName = parts[0].trim();
                    String[] categories = parts[1].trim().split(",");
                    List<String> judgeCategories = new ArrayList<>();
                    for (String category : categories) {
                        judgeCategories.add(category.trim());
                    }
                    judgesCategories.put(judgeName, judgeCategories);
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        }
        return judgesCategories;
    }

    private static List<List<Integer>> groupProjects(List<Integer> projects) {
        List<List<Integer>> projectGroups = new ArrayList<>();
        List<Integer> currentGroup = new ArrayList<>();
        for (Integer project : projects) {
            if (currentGroup.size() < 6) {
                currentGroup.add(project);
            } else {
                projectGroups.add(currentGroup);
                currentGroup = new ArrayList<>();
                currentGroup.add(project);
            }
        }
        if (!currentGroup.isEmpty()) {
            projectGroups.add(currentGroup);
        }
        return projectGroups;
    }

    private static void writeOutputFile(String filename, Map<String, Category> categories) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            for (Category category : categories.values()) {
                for (int i = 0; i < category.judges.size(); i++) {
                    fw.write(String.format("%s_%d %s, Projects: %s%n",
                            category.name, i + 1, category.judges.get(i),
                            getProjectNumbers(category.projects, i)));
                }
            }
        }
    }

    private static String getProjectNumbers(List<Integer> projects, int groupIndex) {
        int startIndex = groupIndex * 6;
        int endIndex = Math.min(startIndex + 6, projects.size());
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            sb.append(projects.get(i));
            if (i < endIndex - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
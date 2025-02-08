import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

class CourseRegistrationSystem {

    private JFrame frame;
    private JList<String> courseList;
    private JList<String> studentCoursesList;
    private DefaultListModel<String> availableCoursesModel;
    private DefaultListModel<String> studentCoursesModel;
    private HashMap<String, Course> courses;
    private HashMap<String, Student> students;
    private Student currentStudent;

    public CourseRegistrationSystem() {
        frame = new JFrame("Student Course Registration System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Initialize course and student data
        courses = new HashMap<>();
        students = new HashMap<>();

        availableCoursesModel = new DefaultListModel<>();
        studentCoursesModel = new DefaultListModel<>();
        loadSampleData(); // Moved to after initialization of models

        courseList = new JList<>(availableCoursesModel);
        studentCoursesList = new JList<>(studentCoursesModel);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Available Courses:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(courseList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Registered Courses:"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(studentCoursesList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());
        JButton dropButton = new JButton("Drop");
        dropButton.addActionListener(new DropButtonListener());
        buttonPanel.add(registerButton);
        buttonPanel.add(dropButton);

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        currentStudent = new Student("12345", "John Doe"); // Sample student
        students.put(currentStudent.getId(), currentStudent);
        updateCourseLists();

        frame.setVisible(true);
    }

    private void loadSampleData() {
        courses.put("CS101", new Course("CS101", "Introduction to Computer Science", "Basics of CS", 30, "Mon 10-12"));
        courses.put("MATH101", new Course("MATH101", "Calculus I", "Differential Calculus", 25, "Wed 12-2"));
        courses.put("ENG101", new Course("ENG101", "English Literature", "Introduction to Literature", 20, "Fri 2-4"));

        for (Course course : courses.values()) {
            availableCoursesModel.addElement(course.getTitle() + " (" + course.getCode() + ")");
        }
    }

    private void updateCourseLists() {
        availableCoursesModel.clear();
        for (Course course : courses.values()) {
            if (course.getCapacity() > course.getEnrolled()) {
                availableCoursesModel.addElement(course.getTitle() + " (" + course.getCode() + ")");
            }
        }

        studentCoursesModel.clear();
        for (Course course : currentStudent.getRegisteredCourses()) {
            studentCoursesModel.addElement(course.getTitle() + " (" + course.getCode() + ")");
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                String courseCode = selectedCourse.split("\\(")[1].replace(")", "");
                Course course = courses.get(courseCode);
                if (course != null && currentStudent.registerCourse(course)) {
                    updateCourseLists();
                }
            }
        }
    }

    private class DropButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedCourse = studentCoursesList.getSelectedValue();
            if (selectedCourse != null) {
                String courseCode = selectedCourse.split("\\(")[1].replace(")", "");
                Course course = courses.get(courseCode);
                if (course != null && currentStudent.dropCourse(course)) {
                    updateCourseLists();
                }
            }
        }
    }

    public static void main(String[] args) {
        new CourseRegistrationSystem();
    }
}

class Course {
    private String code, title, description, schedule;
    private int capacity, enrolled;

    public Course(String code, String title, String description, int capacity, String schedule) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.schedule = schedule;
        this.enrolled = 0;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }

    public boolean enroll() {
        if (enrolled < capacity) {
            enrolled++;
            return true;
        }
        return false;
    }

    public boolean drop() {
        if (enrolled > 0) {
            enrolled--;
            return true;
        }
        return false;
    }
}

class Student {
    private String id, name;
    private ArrayList<Course> registeredCourses;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.registeredCourses = new ArrayList<>();
    }

    public String getId() { return id; }
    public ArrayList<Course> getRegisteredCourses() { return registeredCourses; }

    public boolean registerCourse(Course course) {
        if (registeredCourses.contains(course) || !course.enroll()) {
            return false;
        }
        registeredCourses.add(course);
        return true;
    }

    public boolean dropCourse(Course course) {
        if (registeredCourses.remove(course)) {
            course.drop();
            return true;
        }
        return false;
    }
}

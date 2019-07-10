import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.*;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.*;

abstract class Empty {
    protected String name;

    public Empty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getSubInfo();

    public boolean isMyObject(String nameSubject) {
        return name.equals(nameSubject);
    }

    @Override
    public String toString() {
        return "{" + name + "}";
    }
}

class LexicComparator implements Comparator<Empty> {


    @Override
    public int compare(Empty o1, Empty o2) {
        return o1.getName().compareTo(o2.getName());
    }
}


class Subject extends Empty {

    private LocalDate d;
    private int audience;

    Subject(String name) {
        super(name);
        audience = 1;
        d = LocalDate.parse("2017-11-25");
    }
    Subject(String name, String date) {
        super(name);
        audience = 1;
        d = LocalDate.parse(date);
    }
    Subject(String name, int year, int month, int day) {
        super(name);
        audience = 1;
        d = LocalDate.of(year, month, day);
    }

    public LocalDate getDate() {
        return d;
    }
    public int getAudience() {
        return audience;
    }

    @Override
    public String getSubInfo() {
        return d.toString() + " " + audience;
    }

    public void setAudience(int audience) {
        this.audience = audience;
    }
    public void setDate(String date) {
        this.d = LocalDate.parse(date);
    }

    public StringBuilder createInfoSubject() {
        return new StringBuilder(getName() + " " + getDate() + " " + getAudience());
    }

    @Override
    public boolean equals(Object obj) {
        String name = ((Subject) obj).getName();
        return this.name.equals(name);
    }
}


class Student extends Empty {
    Faculty faculty;

    Student(String name) {
        super(name);
        faculty = null;
    }

    Student(String name, Faculty faculty) {
        super(name);
        this.faculty = faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    @Override
    public String getSubInfo() {
        return faculty != null ? faculty.getName() : "Not have subinfo";
    }

    @Override
    public boolean equals(Object obj) {
        String name = ((Student) obj).getName();
        return this.name.equals(name);
    }
}


class Faculty extends Empty {

    Set<Subject> subjects;
    Map<Student, Map<Subject, Integer>> resultExam;

    Faculty(String name) {
        super(name);
        resultExam = new TreeMap<Student, Map<Subject, Integer>>(new LexicComparator());
        subjects = new TreeSet<Subject>(new LexicComparator());
    }

    public boolean addStudent(Student s) {
        if (resultExam.get(s) == null) {
            Map<Subject, Integer> submap = new TreeMap<>(new LexicComparator());
            resultExam.put(s, submap);
            //System.out.println(subMap);
            for (Subject x : subjects) {
                submap.put(x, 0);
            }
            return true;
        }
        return false;
    }
    public boolean addSubject(Subject s) {
        return subjects.add(s);
    }

    public Subject getSubject(String nameSubject) {
        for (Subject x : subjects) {
            if (x.isMyObject(nameSubject))
                return x;
        }
        return null;
    }
    public Map<Integer, Map<Subject, Integer>> getListGrades() {


        Map<Subject, Integer> submap;
        Map<Integer, Map<Subject, Integer>> grades = new HashMap<>();
        //System.out.println(grades == null);
        for (int i = 0; i <= 5; i++) {
            submap = new TreeMap<>(new LexicComparator());
            for (Subject x : subjects) {
                submap.put(x, 0);
            }
            grades.put(i, submap);
        }

        for (Map.Entry<Student, Map<Subject, Integer>> one : resultExam.entrySet()) {
            for (Map.Entry<Subject, Integer> grade : one.getValue().entrySet()) {
                Map<Subject, Integer> res = grades.get(grade.getValue());
                res.replace(grade.getKey(), res.get(grade.getKey()) + 1);
            }
        }
        for (Map.Entry<Integer, Map<Subject, Integer>> facultyGrades : grades.entrySet()) {
            System.out.println("Grade " + facultyGrades.getKey());
            for (Map.Entry<Subject, Integer> student : facultyGrades.getValue().entrySet()) {
                System.out.println("  -" + student.getKey().getName() + " " + student.getValue());
                //System.out.println();
            }

        }
        return grades;

    }

    public void setGrade(String nameStudent, String nameSubject, int grade) {
        for (Student student : resultExam.keySet()) {
            if (student.isMyObject(nameStudent)) {
                resultExam.get(student).replace(getSubject(nameSubject), Integer.valueOf(grade));
            }
        }
    }
    public int getGrade(String nameStudent, String nameSubject) {
        for (Student student : resultExam.keySet()) {
            if (student.isMyObject(nameStudent)) {
                return resultExam.get(student).get(getSubject(nameSubject));
            }
        }
        return -1;
    }

    @Override
    public String getSubInfo() {
        return createInfoListStudents().toString() + createInfoSubjects();
    }

    public boolean hasStudent(String name){
        for(Student student : resultExam.keySet()){
            if(student.getName().equals(name))
                return true;
        }
        return false;
    }

    public void showStudents() {
        for (Student x : resultExam.keySet())
            System.out.println("  " + x.getName());
        System.out.println();
    }
    public void showSubject() {
        for (Subject x : subjects)
            System.out.println(x.getName() + x.getDate());
        System.out.println();
    }

    public StringBuilder createInfo() {
        StringBuilder info = new StringBuilder().append(getName() + ":\n")
                .append("   Number Students: " + resultExam.size());
        for (Subject subject : subjects) {
            info.append("   " + subject.createInfoSubject());
        }
        return info;
    }
    public StringBuilder createInfoListStudents() {
        StringBuilder info = new StringBuilder(100);
        for (Student student : resultExam.keySet())
            info.append("-" + student.getName() + "\n");
        return info;
    }
    public StringBuilder createInfoSubjects() {
        StringBuilder info = new StringBuilder(100);
        for (Subject subject : subjects)
            info.append("-" + subject.getName() + "\n");
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        String name = ((Faculty) obj).getName();
        return this.name.equals(name);
    }
}


class DataBase {
    Set<Faculty> faculties;
    Set<Subject> allSubjects;

    DataBase() {
        faculties = new TreeSet<>(new LexicComparator());
        allSubjects = new TreeSet<Subject>(new LexicComparator());
    }

    public boolean addFaculty(Faculty f) {
        return faculties.add(f);
    }

    public boolean addFaculty(String nameFaculty) {
        return faculties.add(new Faculty(nameFaculty));
    }

    public void setSubject(String nameFaculty, String name) {
        Subject subject = getSubject(name);
        if(subject!=null)
             getFaculty(nameFaculty).addSubject(subject);
        else{
            subject = new Subject(name);
            getFaculty(nameFaculty).addSubject(subject);
            allSubjects.add(subject);
        }
    }

    public void show() {
        for (Faculty x : faculties)
            System.out.println(x);
    }
    public void showInfo() {
        System.out.println("Start operation\n");
        for (Faculty x : faculties) {
            System.out.println(x.getName() + ":");
            x.showStudents();
            System.out.println();
        }
        System.out.println("End operation\n\n");
    }
    public void showAllSubjects(){
        for(Subject subject: allSubjects){
            System.out.println("----" + subject.getName());
        }
    }

    public Student findStudent() {
        return null;
    }

    public Faculty getFaculty(String name) {
        for (Faculty x : faculties) {
            if (x.getName().equals(name))
                return x;
        }
        return null;
    }
    public Subject getSubject(String name){
        for(Subject subject : allSubjects) {
            if(subject.getName().equals(name))
                return subject;
        }
        return null;
    }
    public void readingData() {
        try (Scanner scanner = new Scanner(new File("Fuculties"))) {
            while (scanner.hasNext()) {
                addFaculty(scanner.next());
            }
        } catch (Exception e) {
            System.out.println("No file found: Faculties\n {" + e.getMessage() + "}");
        }

        try (Scanner scanner = new Scanner(new File("Subjects"))) {
            while (scanner.hasNext()) {

                String str = scanner.next();
                Subject subject = new Subject(scanner.next(), scanner.next());
                getFaculty(str).addSubject(subject);
                allSubjects.add(subject);
            }
        } catch (Exception e) {
            System.out.println("No file found: Subjects  \n {" + e.getMessage() + "}");
        }

        try (Scanner scanner = new Scanner(new File("Students"))) {
            while (scanner.hasNext()) {
                getFaculty(scanner.next()).addStudent(new Student(scanner.next()));
            }
        } catch (Exception e) {
            System.out.println("No file found: Students \n    {" + e.getMessage() + "}");
        }
    }

    public StringBuilder createInfoAllListStudents() {
        StringBuilder info = new StringBuilder(100);
        for (Faculty x : faculties) {
            info.append(x.getName() + ":\n");
            info.append(x.createInfoListStudents() + "\n\n");
        }
        return info;
    }
    public StringBuilder createInfoOneStudent(String nameStudent) {
        StringBuilder info = new StringBuilder();
        for (Faculty faculty : faculties) {
            for (Student student : faculty.resultExam.keySet()) {
                if (student.isMyObject(nameStudent)) {
                    info.append(student.getName() + "grades:\n");
                    for (Map.Entry<Subject, Integer> en : faculty.resultExam.get(student).entrySet()) {
                        info.append("{" + en.getKey().getName() + "  " + en.getValue() + "}\n");
                    }
                    return info;
                }
            }
        }
        return info.append("Not found Student !");
    }
    public StringBuilder createInfoOneExam(String nameSubject) {
        for (Subject subject : allSubjects) {
            if (subject.isMyObject(nameSubject))
                return subject.createInfoSubject();
        }
        return new StringBuilder("Not found Subject !");
    }
    public StringBuilder createInfoGrades() {
        StringBuilder report = new StringBuilder(500);
        Map<Subject, Integer> submap;
        Map<Integer, Map<Subject, Integer>> grades = new HashMap<Integer, Map<Subject, Integer>>();
        for (int i = 0; i <= 5; i++) {
            submap = new TreeMap<Subject, Integer>(new LexicComparator());
            for (Subject x : allSubjects) {
                submap.put(x, 0);
            }
            grades.put(i, submap);
        }

        for (Faculty faculty : faculties) {
            for (Map.Entry<Integer, Map<Subject, Integer>> facultyGrades : faculty.getListGrades().entrySet()) {
                for (Map.Entry<Subject, Integer> subject : facultyGrades.getValue().entrySet()) {
                    Map<Subject, Integer> sum = grades.get(facultyGrades.getKey());
                        sum.put(subject.getKey(), sum.get(subject.getKey()) + subject.getValue());
                }
            }
        }


            for (Map.Entry<Integer, Map<Subject, Integer>> facultyGrades : grades.entrySet()) {
                report.append("Grade " + facultyGrades.getKey() + ":");
                for (Map.Entry<Subject, Integer> student : facultyGrades.getValue().entrySet()) {
                    report.append("\n    -" + student.getKey().getName() + " " + student.getValue());
                }
                report.append("\n");
            }



        return report;
    }
    public StringBuilder createReport() {
        StringBuilder report = new StringBuilder(1000);
        for (Faculty faculty : faculties) {
            report.append(faculty.createInfo() + "\n");
        }
        return report;
    }

}

class InterfaceData extends JFrame { // ---------------------MY_GUI
    DataBase db;
    JPanel left;  // Панель ввода
    JPanel right;  // Панель вывода

    JTextArea label;

    InterfaceData(DataBase db) {
        super("My Aplication");
        this.db = db;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        getContentPane().setLayout(new GridLayout());
        // ---------------------Создание меню
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu newMenu = new JMenu("New");
        JMenuItem oneItemMenu = new JMenuItem("Edit Student");
        JMenuItem twoItemMenu = new JMenuItem("Show information");

        oneItemMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                left.removeAll();
                label.setText("");
                setupDataBase();
                System.out.println("Working");
                setVisible(true);
            }
        });

        twoItemMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                left.removeAll();
                // right.removeAll();
                label.setText("");
                showMenu();
                System.out.println("Printing");

                setVisible(true);
            }
        });

        newMenu.add(oneItemMenu);
        newMenu.add(twoItemMenu);
        menuBar.add(newMenu);

        //------------------ Настройка двух панелей
        //-------------------------- Настройка левой панели


        left = new JPanel(); // Панель ввода
        left.setMinimumSize(new Dimension(200, 500));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(200, 500));

        //--------------------------- Настройка правой панели

        right = new JPanel(); // Панель вывода
        right.setMinimumSize(new Dimension(400, 500));
        right.setLayout(new BorderLayout());

        //--------------------------------------Добавление в правую панель окно вывода

        label = new JTextArea();
        label.setEditable(false);
        label.setBorder(BorderFactory.createTitledBorder("Output"));

        JScrollPane sp = new JScrollPane(label);
        right.add(sp);

        //------------------Объединение Двух панелей с помощью разделителя

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);  // we want it to split the window verticaly
        splitPane.setDividerLocation(200);
        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);
        getContentPane().add(splitPane);

        setVisible(true);
    }

    public void setupDataBase() {
        left.removeAll();
        repaint(100, 100, 0, 200, 500);
        //-------------------------Поле для названия факультета

        JTextField facultyTextField = new JTextField();
        facultyTextField.setBorder(BorderFactory.createTitledBorder("Name Faculty"));
        facultyTextField.setMinimumSize(new Dimension(100, 30));
        facultyTextField.setMaximumSize(new Dimension(170, 40));
        facultyTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(facultyTextField);
        left.add(Box.createVerticalStrut(10));

        //-------------------------Поле для имени Студента

        JTextField studentTextField = new JTextField();
        studentTextField.setBorder(BorderFactory.createTitledBorder("Name Student"));
        studentTextField.setMinimumSize(new Dimension(100, 30));
        studentTextField.setMaximumSize(new Dimension(170, 40));
        studentTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(studentTextField);
        left.add(Box.createVerticalStrut(10));

        //-------------------------Поле для названия Предмета

        JTextField subjectTextField = new JTextField();
        subjectTextField.setBorder(BorderFactory.createTitledBorder("Name Subject"));
        subjectTextField.setMinimumSize(new Dimension(100, 30));
        subjectTextField.setMaximumSize(new Dimension(170, 40));
        subjectTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(subjectTextField);
        left.add(Box.createVerticalStrut(10));

        //------------------------- Кнопки Для изменения оценки
        JButton buttonStudentInfo = new JButton("Change Grade of Student");
        buttonStudentInfo.setBounds(100, 70, 100, 30);
        buttonStudentInfo.setMinimumSize(new Dimension(100, 30));
        buttonStudentInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Faculty faculty = db.getFaculty(facultyTextField.getText());
                Subject subject = db.getSubject(subjectTextField.getText());
                if(faculty!= null && faculty.hasStudent(studentTextField.getText()) && subject!= null) {
                    showInputStudentInfo(faculty,studentTextField.getText(), subject.getName());
                }
                else
                    label.setText(label.getText() + "\nНекорректные данные!!!");
            }
        });
        buttonStudentInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(buttonStudentInfo);
        left.add(Box.createVerticalStrut(10));
        

        //----------------------------------Кнопка для изменения информации предмета
        
        JButton buttonSubjectInfo = new JButton("Change Subject Info");
        buttonSubjectInfo.setBounds(100, 70, 100, 30);
        buttonSubjectInfo.setMinimumSize(new Dimension(100, 30));
        buttonSubjectInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Subject subject = db.getSubject(subjectTextField.getText());
                if (subject == null)
                    label.setText("\nНекорректные данные!!!");
                else{
                    label.setText(subject.getName() + " " + subject.getSubInfo());
                    showInputSubjectInfo(subject);
                }
            }
        });
        buttonSubjectInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(buttonSubjectInfo);
        left.add(Box.createVerticalStrut(10));
        left.add(Box.createVerticalGlue());

        //--------------------------------------------------Кнопка выхода

        JButton backButton = new JButton("Turn Back");
        backButton.setMinimumSize(new Dimension(150, 30));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMenu();
            }
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(backButton);
        setVisible(true);

    }

    public void showInputSubjectInfo(Subject subject){        // Окно для изменения информации предмета
        left.removeAll();
        repaint(100, 100, 0, 200, 500);

        JLabel lblName = new JLabel("Input info subject" );
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        //--------------------------------------------------Поле ввода аудитории

        JTextField inputAudience = new JTextField();
        inputAudience.setMinimumSize(new Dimension(100, 30));
        inputAudience.setMaximumSize(new Dimension(170, 40));
        inputAudience.setBorder(BorderFactory.createTitledBorder("Audience xxxx"));
        inputAudience.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lblName);
        left.add(inputAudience);
        left.add(Box.createVerticalStrut(10));

        //--------------------------------------------------Кнопка для смены аудитории

        JButton audienceButton = new JButton("Change Audience");
        audienceButton.setBounds(100, 70, 100, 30);
        audienceButton.setMinimumSize(new Dimension(100, 30));
        audienceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    subject.setAudience(Integer.valueOf(inputAudience.getText()));
                    label.setText(subject.createInfoSubject().toString());
                }
                catch(Exception exc){
                    label.setText(label.getText() + "\nНекорректные данные!!!");
                }
            }
        });
        audienceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(audienceButton);
        left.add(Box.createVerticalStrut(10));
        left.add(Box.createVerticalGlue());

        //------------------------------------------------- Поле для ввода даты экзамена
        JTextField inputData = new JTextField();
        inputData.setMinimumSize(new Dimension(100, 30));
        inputData.setMaximumSize(new Dimension(170, 40));
        inputData.setBorder(BorderFactory.createTitledBorder("Data (xxxx-xx-xx)"));
        inputData.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lblName);
        left.add(inputData);
        left.add(Box.createVerticalStrut(10));
         //--------------------------------------------------- Кнопка Даты

        JButton dataButton = new JButton("Change Data");
        dataButton.setBounds(100, 70, 100, 30);
        dataButton.setMinimumSize(new Dimension(100, 30));
        dataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    subject.setDate(inputData.getText());
                    label.setText(subject.createInfoSubject().toString());
                }
                catch(Exception exc){
                    label.setText(label.getText() + "\nНекорректные данные!!!");
                }
            }
        });
        dataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(dataButton);
        left.add(Box.createVerticalStrut(10));
        left.add(Box.createVerticalGlue());
      
        
        //--------------------------------------------------Кнопка выхода

        JButton backButton = new JButton("Turn Back");
        backButton.setMinimumSize(new Dimension(150, 30));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupDataBase();
            }
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(backButton);
        setVisible(true);
    }

    public void showInputStudentInfo(Faculty faculty, String nameStudent, String nameSubject) {
        left.removeAll();
        repaint(100, 100, 0, 200, 500);

        JLabel lblName = new JLabel("Input info subject" );
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        //--------------------------------------------------Поле ввода аудитории

        JTextField inputAudience = new JTextField();
        inputAudience.setMinimumSize(new Dimension(100, 30));
        inputAudience.setMaximumSize(new Dimension(170, 40));
        inputAudience.setBorder(BorderFactory.createTitledBorder("Audience xxxx"));
        inputAudience.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lblName);
        left.add(inputAudience);
        left.add(Box.createVerticalStrut(10));

        //--------------------------------------------------Кнопка для смены аудитории

        JButton audienceButton = new JButton("Change Audience");
        audienceButton.setBounds(100, 70, 100, 30);
        audienceButton.setMinimumSize(new Dimension(100, 30));
        audienceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    faculty.setGrade(nameStudent, nameSubject, Integer.valueOf(inputAudience.getText()));
                    label.setText(db.createInfoOneStudent(nameStudent).toString());
                }
                catch(Exception exc){
                    label.setText(label.getText() + "\nНекорректные данные!!!");
                }
            }
        });

        audienceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(audienceButton);
        left.add(Box.createVerticalStrut(10));
        left.add(Box.createVerticalGlue());

        //--------------------------------------------------Кнопка выхода

        JButton backButton = new JButton("Turn Back");
        backButton.setMinimumSize(new Dimension(150, 30));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupDataBase();
            }
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(backButton);
        setVisible(true);
    }

    public void showMenu() {

        left.removeAll();
        repaint(100, 100, 0, 200, 500);

        JButton wantStudents = new JButton("getStudents");
        wantStudents.setBounds(50, 50, 100, 30);
        wantStudents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText(db.createInfoAllListStudents().toString());
                System.out.println(db.createInfoAllListStudents().toString());
            }
        });
        wantStudents.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(wantStudents);
        left.add(Box.createVerticalStrut(10));

        //----------------------------------------

        JButton wantGrades = new JButton("wantGrades");
        wantGrades.setBounds(50, 100, 100, 30);
        wantGrades.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //label.setText(db.createInfoOneStudent());
                showStud("Student");
                label.setText("It Operation isn't available yet");
            }
        });
        wantGrades.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(wantGrades);
        left.add(Box.createVerticalStrut(10));

        //------------------------------------------------

        JButton wantExam = new JButton("wantExam");
        wantExam.setBounds(100, 150, 100, 30);
        wantExam.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showStud("Subject");
                //label.setText("It Operation isn't available yet");
            }
        });
        wantExam.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(wantExam);
        left.add(Box.createVerticalStrut(10));

        //------------------------------------------------------

        JButton wantCountStudents = new JButton("wantCountStudents");
        wantCountStudents.setBounds(150, 200, 100, 30);
        wantCountStudents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText(db.createInfoGrades().toString());
                //label.setText("It Operation isn't available yet");

            }
        });
        wantCountStudents.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(wantCountStudents);
        left.add(Box.createVerticalStrut(10));

        //-----------------------------------------------------

        JButton wantReport = new JButton("wantReport");
        wantReport.setBounds(200, 250, 100, 30);
        wantReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText(db.createReport().toString());
                System.out.println(db.createReport().toString());

            }
        });
        wantReport.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(wantReport);

        setVisible(true);
    }
    public void showStud(String typeRequest) {
        left.removeAll();
        repaint(100, 100, 0, 200, 500);

        JLabel lblName = new JLabel("Input " + typeRequest);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputName = new JTextField();
        inputName.setMinimumSize(new Dimension(100, 30));
        inputName.setMaximumSize(new Dimension(170, 40));
        inputName.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lblName);
        left.add(inputName);
        left.add(Box.createVerticalStrut(10));

        //------------------------------------------------------
        JButton btnAccept = new JButton("Accept");
        btnAccept.setBounds(100, 70, 100, 30);
        btnAccept.setMinimumSize(new Dimension(100, 30));
        btnAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (typeRequest) {
                    case "Student":
                        label.setText(db.createInfoOneStudent(inputName.getText()).toString());
                        break;
                    case "Subject":
                        label.setText(db.createInfoOneExam(inputName.getText()).toString());
                        break;
                }
            }
        });
        btnAccept.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(btnAccept);
        left.add(Box.createVerticalStrut(10));
        left.add(Box.createVerticalGlue());
        //--------------------------------------------------Кнопка выхода

        JButton backButton = new JButton("Turn Back");
        backButton.setMinimumSize(new Dimension(150, 30));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMenu();
            }
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(backButton);
        setVisible(true);
    }

}

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        DataBase db = new DataBase();
        db.readingData();
        new InterfaceData(db);
        //new VisualPractice();
        db.show();
        db.showAllSubjects();
        System.out.println();
    }
}

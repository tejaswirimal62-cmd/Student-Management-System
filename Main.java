import java.io.*;
import java.util.*;

// INTERFACE 1
interface Grading {
    char calculateGrade(double marks);
}

//  INTERFACE 2
interface Storable {
    void saveData();
    void loadData();
}

//INVALID CUSTOM EXCEPTION
class InvalidMarksException extends Exception {
    public InvalidMarksException(String message) {
        super(message);
    }
}

//  ABSTRACT CLASS
abstract class Person {
    private int id;
    private String name;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public abstract void display();
}

//  STUDENT CLASS
  class Student extends Person implements Grading {
    private double marks;

    public Student(int id, String name, double marks) {
        super(id, name);
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be 0-100");
        }
        this.marks = marks;
    }

    public double getMarks() {
        return marks;
    }

    @Override
    public char calculateGrade(double marks) {
        if (marks >= 80){
            return 'A';
        }
        else if (marks >= 60){
            return 'B';
        }
        else if (marks >= 40){
            return 'C';
        }
        else{
            return 'F';
    }
    }
    @Override
    public void display() {
        System.out.println("ID: " + getId()); 
        System.out.println("Name: " + getName()); 
         System.out.println( "Marks: " + marks);
        System.out.println("Grade: " + calculateGrade(marks));
    }
}

//  GRADUATE STUDENT
class GraduateStudent extends Student {
    public GraduateStudent(int id, String name, double marks) {
        super(id, name, marks);
    }

    @Override
    public char calculateGrade(double marks) {
        if (marks >= 85){
            return 'A';
        }
        else if (marks >= 65){ 
            return 'B';
        }
        else if (marks >= 50){
            return 'C';
        }
        else{
            return 'F';
    }
    }
}

//  FILE HANDLING
 class FileUtil {
    private String fileName = "students.txt";

    public void save(List<Student> list) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for (Student s : list) {
                bw.write(s.getId() + "," + s.getName() + "," + s.getMarks());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("File saving error");
        }
    }

    public List<Student> load() {
        List<Student> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists())
            return list;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                double marks = Double.parseDouble(data[2]);
                list.add(new Student(id, name, marks));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("File loading error");
        }
        return list;
    }
}

// MANAGER CLASS
class GradeManager implements Storable {
    List<Student> students;
    FileUtil file;

    public GradeManager() {
        file = new FileUtil();
        students = file.load();
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    public void showStudents() {
        if (students.isEmpty()) {
            System.out.println("No students yet");
        } else {
            for (Student s : students) {
                s.display();
            }
        }
    }

    // Save using multithreading
    @Override
    public void saveData() {
        Thread saveThread = new Thread(() -> {
            file.save(students);
            System.out.println("Data saved in background thread!");
        });
        saveThread.start();
        try {
            saveThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }
    }

    @Override
    public void loadData() {
        students = file.load();
    }
}

//  MAIN PROGRAM
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GradeManager manager = new GradeManager();
        int choice;

        do {
            System.out.println("\n--- Student Grade System ---");
            System.out.println("1. Add Student");
            System.out.println("2. Add Graduate Student");
            System.out.println("3. Show Students");
            System.out.println("4. Save & Exit");
            System.out.print("Enter choice: ");

            while (!sc.hasNextInt()) {
                System.out.println("Invalid input! Enter number 1-4.");
                sc.next();
            }
            choice = sc.nextInt();
            sc.nextLine();

            try {
                if (choice == 1 || choice == 2) {
                    System.out.print("ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Name: ");
                    String name = sc.nextLine();

                    System.out.print("Marks: ");
                    double marks = sc.nextDouble();
                    sc.nextLine();

                    if (choice == 1)
                        manager.addStudent(new Student(id, name, marks));
                    else
                        manager.addStudent(new GraduateStudent(id, name, marks));

                    System.out.println("Student added!");
                }
                else if (choice == 3) {
                    manager.showStudents();
                }
                else if (choice == 4) {
                    manager.saveData();
                    System.out.println("Exiting program...");
                }
                else {
                    System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 4);

        sc.close();
    }
}

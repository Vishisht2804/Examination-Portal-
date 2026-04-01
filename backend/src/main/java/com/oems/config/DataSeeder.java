package com.oems.config;

import com.oems.model.*;
import com.oems.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      CourseRepository courseRepository,
                      ExamRepository examRepository,
                      QuestionRepository questionRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Clear existing data for fresh seeding
        questionRepository.deleteAll();
        examRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Admin & Teachers
        User admin = createUser("admin", "admin@oems.com", "admin123", "Admin User", Role.ADMIN);
        User teacher1 = createUser("teacher1", "smith@oems.com", "pass123", "Dr. James Smith", Role.TEACHER);
        User teacher2 = createUser("teacher2", "kumar@oems.com", "pass123", "Prof. Rajesh Kumar", Role.TEACHER);
        User teacher3 = createUser("teacher3", "johnson@oems.com", "pass123", "Ms. Emma Johnson", Role.TEACHER);
        
        // Create Students
        User student1 = createUser("student1", "alice@oems.com", "pass123", "Alice Johnson", Role.STUDENT);
        User student2 = createUser("student2", "bob@oems.com", "pass123", "Bob Wilson", Role.STUDENT);
        User student3 = createUser("student3", "carol@oems.com", "pass123", "Carol Davis", Role.STUDENT);
        User student4 = createUser("student4", "diana@oems.com", "pass123", "Diana Miller", Role.STUDENT);
        User student5 = createUser("student5", "eve@oems.com", "pass123", "Eve Brown", Role.STUDENT);
        
        userRepository.saveAll(List.of(admin, teacher1, teacher2, teacher3, student1, student2, student3, student4, student5));

        // Create Courses
        Course ds = Course.builder()
                .name("Data Structures & Algorithms")
                .description("Comprehensive course on data structures, algorithms, complexity analysis, and problem-solving techniques")
                .teacher(teacher1)
                .active(true)
                .build();
        ds.getStudents().addAll(Set.of(student1, student2, student3));
        
        Course wd = Course.builder()
                .name("Web Development Fundamentals")
                .description("Introduction to web development with HTML, CSS, JavaScript, and frameworks")
                .teacher(teacher2)
                .active(true)
                .build();
        wd.getStudents().addAll(Set.of(student2, student3, student4));
        
        Course db = Course.builder()
                .name("Database Management Systems")
                .description("SQL, NoSQL, database design, normalization, and optimization techniques")
                .teacher(teacher3)
                .active(true)
                .build();
        db.getStudents().addAll(Set.of(student1, student4, student5));
        
        courseRepository.saveAll(List.of(ds, wd, db));

        LocalDateTime now = LocalDateTime.now();
        
        // Create Exams
        Exam dsExam = Exam.builder()
                .title("Data Structures Midterm")
                .description("Comprehensive MCQ exam on arrays, linked lists, stacks, queues, trees, and graphs")
                .course(ds)
                .teacher(teacher1)
                .durationMinutes(60)
                .scheduledStart(now.minusDays(2))
                .scheduledEnd(now.plusDays(14))
                .published(true)
                .randomizeQuestions(true)
                .passPercentage(50)
                .build();
        
        Exam webExam = Exam.builder()
                .title("Web Development Quiz")
                .description("Quick assessment on HTML, CSS, and JavaScript fundamentals")
                .course(wd)
                .teacher(teacher2)
                .durationMinutes(45)
                .scheduledStart(now.minusDays(1))
                .scheduledEnd(now.plusDays(10))
                .published(true)
                .randomizeQuestions(true)
                .passPercentage(60)
                .build();
        
        Exam dbExam = Exam.builder()
                .title("Database Systems Final")
                .description("Final examination covering SQL, normalization, transactions, and optimization")
                .course(db)
                .teacher(teacher3)
                .durationMinutes(90)
                .scheduledStart(now.minusDays(5))
                .scheduledEnd(now.plusDays(20))
                .published(true)
                .randomizeQuestions(true)
                .passPercentage(45)
                .build();
        
        examRepository.saveAll(List.of(dsExam, webExam, dbExam));

        // Seed detailed questions
        seedDSQuestions(dsExam);
        seedWebQuestions(webExam);
        seedDBQuestions(dbExam);
        
        recalculateTotal(dsExam);
        recalculateTotal(webExam);
        recalculateTotal(dbExam);
    }

    private User createUser(String username, String email, String password, String fullName, Role role) {
        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .active(true)
                .build();
    }

    private void seedDSQuestions(Exam exam) {
        String[][] questions = {
                {"What is the time complexity of binary search?", "O(n)", "O(n log n)", "O(log n)", "O(1)", "C"},
                {"Which data structure uses LIFO principle?", "Queue", "Stack", "Tree", "Graph", "B"},
                {"What is the space complexity of merge sort?", "O(1)", "O(n)", "O(n log n)", "O(n²)", "B"},
                {"In a complete binary tree with n nodes, height is?", "O(n)", "O(log n)", "O(n log n)", "O(√n)", "B"},
                {"What is the average time complexity of quicksort?", "O(n)", "O(n log n)", "O(n²)", "O(log n)", "B"},
                {"Hash collision is resolved using which method?", "Linear probing", "Chaining", "Double hashing", "All above", "D"},
                {"Which sorting algorithm is most stable?", "Quicksort", "Heapsort", "Merge sort", "Bubble sort", "C"},
                {"What is the minimum number of comparisons for sorting 4 elements?", "4", "5", "6", "8", "B"},
                {"In DFS, which data structure is used?", "Queue", "Stack", "Deque", "Priority queue", "B"},
                {"What is the time complexity of inserting in AVL tree?", "O(log n)", "O(n)", "O(1)", "O(n log n)", "A"},
                {"B-tree maintains balance by?", "Right rotations", "Height restrictions", "Node value restrictions", "Parent pointers", "B"},
                {"Which algorithm uses greedy approach?", "Dijkstra", "Kruskal", "Prim", "All are greedy", "D"},
                {"Maximum edges in a simple graph with 5 vertices?", "10", "15", "20", "25", "A"},
                {"What is the recurrence for binary search?", "T(n)=T(n/2)+1", "T(n)=2T(n/2)+1", "T(n)=T(n-1)+1", "T(n)=T(n/2)+n", "A"},
                {"Floyd-Warshall finds which type of paths?", "Minimum spanning tree", "All pairs shortest path", "Single source shortest path", "Longest path", "B"},
                {"Red-Black tree ensures height?", "O(1)", "O(n)", "O(log n)", "O(n²)", "C"},
                {"Which is NOT a valid sequence for BST insertion?", "3,2,1", "2,1,3", "1,2,3", "1,3,2", "C"},
                {"Topological sort is for which graph type?", "Undirected", "Directed acyclic", "Complete graph", "Bipartite", "B"},
                {"What is the worst case for bucket sort?", "O(n)", "O(n log n)", "O(n + k)", "O(n²)", "D"},
                {"String matching using KMP has complexity?", "O(n+m)", "O(n*m)", "O(n log m)", "O(m log n)", "A"}
        };
        
        for (int i = 0; i < questions.length; i++) {
            createQuestion(exam, questions[i], i + 1);
        }
    }

    private void seedWebQuestions(Exam exam) {
        String[][] questions = {
                {"What does CSS stand for?", "Computer Style Sheets", "Cascading Style Sheets", "Creative Style Sheets", "Colorful Style Sheets", "B"},
                {"Which HTML tag is used for the largest heading?", "<h6>", "<h3>", "<h1>", "<head>", "C"},
                {"What is the correct way to comment in JavaScript?", "/* comment */", "// comment", "# comment", "<!-- comment -->", "B"},
                {"Which is NOT a valid CSS selector?", "Class selector", "ID selector", "Element selector", "Icon selector", "D"},
                {"What does HTML stand for?", "Hyper Text Markup Logic", "Hyper Tool Multi Language", "Hyper Text Markup Language", "Home Tool Markup Language", "C"},
                {"Which JavaScript method is used to write on HTML output?", "document.print()", "document.write()", "console.log()", "window.output()", "B"},
                {"What is the purpose of the <meta> tag?", "Define metadata", "Create links", "Format text", "Embed images", "A"},
                {"Which CSS property controls text color?", "font-color", "text-color", "color", "font", "C"},
                {"What is flexbox used for?", "Styling fonts", "Creating layouts", "Adding animations", "Handling events", "B"},
                {"Which is the correct JSON format?", "{'name': 'John'}", "{\"name\": \"John\"}", "{name: John}", "[name: John]", "B"},
                {"What does REST stand for?", "Representational State Transfer", "Remote Execution Service Transfer", "Resource Server Transfer", "Response Server Tech", "A"},
                {"Which HTTP method is used to delete data?", "POST", "PUT", "DELETE", "GET", "C"},
                {"What is npm?", "Node Package Manager", "New Package Manager", "Next Package Manager", "Network Protocol Manager", "A"},
                {"Which framework is a progressive JavaScript framework?", "Django", "Vue.js", "Spring", "Laravel", "B"},
                {"What does JSON stand for?", "Java Script Object Notation", "Java Standard Object Notation", "JavaScript Only Notation", "Java Server Object Network", "A"}
        };
        
        for (int i = 0; i < questions.length; i++) {
            createQuestion(exam, questions[i], i + 1);
        }
    }

    private void seedDBQuestions(Exam exam) {
        String[][] questions = {
                {"What does SQL stand for?", "Strongly Queried Language", "Structured Query Language", "Simple Query Language", "Standard Question Language", "B"},
                {"Which normal form eliminates partial dependency?", "1NF", "2NF", "3NF", "BCNF", "B"},
                {"What is a primary key?", "Any unique key", "Uniquely identifies each row", "Foreign key reference", "Can be NULL", "B"},
                {"ACID properties ensure?", "Data consistency", "Data integrity", "Data reliability", "All of above", "D"},
                {"What is NORMALIZATION?", "Organizing data", "Reducing redundancy", "Organizing data & reducing redundancy", "Sorting data", "C"},
                {"Which type of join returns unmatched rows?", "INNER JOIN", "OUTER JOIN", "CROSS JOIN", "NATURAL JOIN", "B"},
                {"What does LIMIT clause do?", "Sorts data", "Filters rows", "Restricts number of rows", "Groups data", "C"},
                {"Index improves query performance by?", "Reducing disk space", "Speeding up retrieval", "Reducing redundancy", "Normalizing data", "B"},
                {"Deadlock occurs in?", "Single transactions", "Multiple transactions", "Backup process", "Index creation", "B"},
                {"What is denormalization?", "Removing redundancy", "Adding redundancy", "Organizing data", "Sorting data", "B"},
                {"Foreign key constraint ensures?", "Referential integrity", "Domain integrity", "Entity integrity", "Data consistency", "A"},
                {"SELECT DISTINCT does what?", "Removes NULL", "Removes duplicates", "Sorts data", "Groups data", "B"},
                {"What is a view in database?", "Physical table", "Virtual table", "Index", "Trigger", "B"},
                {"UNION combines results from?", "One query", "Multiple queries", "Multiple tables", "Multiple rows", "B"},
                {"What is Transaction Isolation Level?", "ACID property", "Concurrency control", "Data backup", "Query optimization", "B"}
        };
        
        for (int i = 0; i < questions.length; i++) {
            createQuestion(exam, questions[i], i + 1);
        }
    }

    private void createQuestion(Exam exam, String[] data, int index) {
        Question question = Question.builder()
                .exam(exam)
                .questionText(data[0])
                .optionA(data[1])
                .optionB(data[2])
                .optionC(data[3])
                .optionD(data[4])
                .correctOption(OptionChoice.valueOf(data[5]))
                .marks(1)
                .orderIndex(index)
                .build();
        questionRepository.save(question);
    }

    private void recalculateTotal(Exam exam) {
        int total = questionRepository.findByExamIdOrderByOrderIndexAsc(exam.getId())
                .stream()
                .mapToInt(Question::getMarks)
                .sum();
        exam.setTotalMarks(total);
        examRepository.save(exam);
    }
}

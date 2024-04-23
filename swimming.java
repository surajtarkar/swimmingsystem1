import java.util.*;

class Coach {
    private String name;
    private List<Integer> ratings;

    public Coach(String name) {
        this.name = name;
        this.ratings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addRating(int rating) {
        ratings.add(rating);
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        return (double) sum / ratings.size();
    }
}

class Learner {
    private String name;
    private String gender;
    private int age;
    private String emergencyContact;
    private String gradeLevel;
    private String review;
    private int rating;
    private String bookingID;
    private String status;

    public Learner(String name, String gender, int age, String emergencyContact, String gradeLevel) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.emergencyContact = emergencyContact;
        this.gradeLevel = gradeLevel;
        this.review = "";
        this.rating = 0;
        this.bookingID = "";
        this.status = "booked";
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

class SwimmingLesson {
    private String grade;
    private String timeSlot;
    private int capacity;
    private List<Learner> learners;
    private Coach coach;

    public SwimmingLesson(String grade, String timeSlot, int capacity, Coach coach) {
        this.grade = grade;
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.learners = new ArrayList<>();
        this.coach = coach;
    }

    public String getGrade() {
        return grade;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Learner> getLearners() {
        return learners;
    }

    public Coach getCoach() {
        return coach;
    }

    public int getVacancies() {
        return capacity - learners.size();
    }

    public boolean addLearner(Learner learner) {
        if (!learners.contains(learner) && learners.size() < capacity) {
            learners.add(learner);
            return true;
        }
        return false;
    }

    public boolean removeLearner(Learner learner) {
        return learners.remove(learner);
    }
}

class SwimmingLessonSchedule {
    private Map<String, Map<String, List<SwimmingLesson>>> schedule; // Map<Day, Map<TimeSlot, List<SwimmingLesson>>>
    private List<Coach> coaches;
    private int bookingCounter;
    private Map<String, Map<Integer, Map<String, List<Learner>>>> monthlyReport; // Map<Month, Map<Week, Map<Day, List<Learner>>>>

    public SwimmingLessonSchedule() {
        schedule = new HashMap<>();
        coaches = new ArrayList<>();
        initializeCoaches();
        initializeSchedule();
        bookingCounter = 0;
        monthlyReport = new HashMap<>();
    }

    private void initializeCoaches() {
        coaches.add(new Coach("Coach A"));
        coaches.add(new Coach("Coach B"));
        coaches.add(new Coach("Coach C"));
        // Add more coaches if needed
    }

    private void initializeSchedule() {
        String[] days = {"Monday", "Wednesday", "Friday", "Saturday"};
        String[] times = {"4-5pm", "5-6pm", "6-7pm", "2-3pm", "3-4pm"};
        String[] grades = {"Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5"};

        for (String day : days) {
            Map<String, List<SwimmingLesson>> daySchedule = new HashMap<>();
            for (String time : times) {
                List<SwimmingLesson> lessons = new ArrayList<>();
                for (String grade : grades) {
                    Coach coach = getRandomCoach();
                    lessons.add(new SwimmingLesson(grade, time, 4, coach));
                }
                daySchedule.put(time, lessons);
            }
            schedule.put(day, daySchedule);
        }
    }

    private Coach getRandomCoach() {
        Random rand = new Random();
        return coaches.get(rand.nextInt(coaches.size()));
    }

    public void viewTimetableByDay(String day) {
        System.out.println("Timetable for " + day + ":");
        Map<String, List<SwimmingLesson>> daySchedule = schedule.get(day);
        if (daySchedule != null) {
            for (Map.Entry<String, List<SwimmingLesson>> entry : daySchedule.entrySet()) {
                String timeSlot = entry.getKey();
                List<SwimmingLesson> lessons = entry.getValue();
                System.out.println("\nTime Slot: " + timeSlot);
                for (SwimmingLesson lesson : lessons) {
                    System.out.println("Grade: " + lesson.getGrade() + ", Coach: " + lesson.getCoach().getName() +
                            ", Vacancies: " + lesson.getVacancies());
                }
            }
        } else {
            System.out.println("No lessons scheduled for " + day + ".");
        }
    }

    public boolean bookLesson(String day, String timeSlot, String grade, Learner learner) {
        Map<String, List<SwimmingLesson>> daySchedule = schedule.get(day);
        if (daySchedule != null) {
            List<SwimmingLesson> lessons = daySchedule.get(timeSlot);
            if (lessons != null) {
                for (SwimmingLesson lesson : lessons) {
                    if (lesson.getGrade().equalsIgnoreCase(grade) || isHigherGrade(lesson.getGrade(), grade)) {
                        if (lesson.addLearner(learner)) {
                            learner.setBookingID(generateBookingID());
                            addLearnerToMonthlyReport(learner);
                            System.out.println("Lesson booked successfully!");
                            return true;
                        } else {
                            System.out.println("Failed to book the lesson. The lesson might be fully booked.");
                            return false;
                        }
                    }
                }
            }
        }
        System.out.println("Failed to book the lesson. The lesson might be invalid.");
        return false;
    }

    public boolean cancelLesson(String bookingID) {
        for (Map.Entry<String, Map<String, List<SwimmingLesson>>> entry : schedule.entrySet()) {
            for (Map.Entry<String, List<SwimmingLesson>> innerEntry : entry.getValue().entrySet()) {
                for (SwimmingLesson lesson : innerEntry.getValue()) {
                    for (Learner learner : lesson.getLearners()) {
                        if (learner.getBookingID().equals(bookingID)) {
                            if (learner.getStatus().equalsIgnoreCase("attended")) {
                                System.out.println("Cannot cancel an attended lesson.");
                                return false;
                            } else {
                                if (lesson.removeLearner(learner)) {
                                    System.out.println("Lesson canceled successfully!");
                                    return true;
                                } else {
                                    System.out.println("Failed to cancel the lesson. The lesson might be invalid.");
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("No booking found with the provided ID.");
        return false;
    }


    public boolean attendLesson(String bookingID, String review, int rating) {
        for (Map.Entry<String, Map<String, List<SwimmingLesson>>> entry : schedule.entrySet()) {
            for (Map.Entry<String, List<SwimmingLesson>> innerEntry : entry.getValue().entrySet()) {
                for (SwimmingLesson lesson : innerEntry.getValue()) {
                    for (Learner learner : lesson.getLearners()) {
                        if (learner.getBookingID().equals(bookingID)) {
                            learner.setStatus("attended");
                            learner.setReview(review);
                            learner.setRating(rating);
                            System.out.println("Lesson attended successfully!");
                            return true;
                        }
                    }
                }
            }
        }
        System.out.println("No booking found with the provided ID.");
        return false;
    }

    public void viewTimetableByGrade(String grade) {
        System.out.println("Timetable for Grade " + grade + ":");
        for (Map.Entry<String, Map<String, List<SwimmingLesson>>> entry : schedule.entrySet()) {
            String day = entry.getKey();
            Map<String, List<SwimmingLesson>> daySchedule = entry.getValue();
            System.out.println("\n" + day + ":");
            for (Map.Entry<String, List<SwimmingLesson>> innerEntry : daySchedule.entrySet()) {
                String timeSlot = innerEntry.getKey();
                List<SwimmingLesson> lessons = innerEntry.getValue();
                System.out.println("\nTime Slot: " + timeSlot);
                for (SwimmingLesson lesson : lessons) {
                    if (lesson.getGrade().equals("Grade " + grade)) {
                        System.out.println("Grade: " + lesson.getGrade() + ", Coach: " + lesson.getCoach().getName() +
                                ", Vacancies: " + lesson.getVacancies());
                    }
                }
            }
        }
    }

    public void viewTimetableByCoach(String coachName) {
        System.out.println("Timetable for Coach " + coachName + ":");
        for (Map.Entry<String, Map<String, List<SwimmingLesson>>> entry : schedule.entrySet()) {
            String day = entry.getKey();
            Map<String, List<SwimmingLesson>> daySchedule = entry.getValue();
            System.out.println("\n" + day + ":");
            for (Map.Entry<String, List<SwimmingLesson>> innerEntry : daySchedule.entrySet()) {
                String timeSlot = innerEntry.getKey();
                List<SwimmingLesson> lessons = innerEntry.getValue();
                System.out.println("\nTime Slot: " + timeSlot);
                for (SwimmingLesson lesson : lessons) {
                    if (lesson.getCoach().getName().equalsIgnoreCase(coachName)) {
                        System.out.println("Grade: " + lesson.getGrade() + ", Coach: " + lesson.getCoach().getName() +
                                ", Vacancies: " + lesson.getVacancies());
                    }
                }
            }
        }
    }

    public void generateMonthlyLearnerReport(int month) {
        System.out.println("Monthly Learner Report for Month " + month + ":");
        Map<Integer, Map<String, List<Learner>>> monthReport = monthlyReport.get(String.valueOf(month));
        if (monthReport != null) {
            for (Map.Entry<Integer, Map<String, List<Learner>>> entry : monthReport.entrySet()) {
                int week = entry.getKey();
                Map<String, List<Learner>> weekReport = entry.getValue();
                System.out.println("\nWeek " + week + ":");
                for (Map.Entry<String, List<Learner>> innerEntry : weekReport.entrySet()) {
                    String day = innerEntry.getKey();
                    List<Learner> learners = innerEntry.getValue();
                    System.out.println("\n" + day + ":");
                    for (Learner learner : learners) {
                        System.out.println("Name: " + learner.getName() + ", Grade: " + learner.getGradeLevel() +
                                ", Time Slot: " + learner.getBookingID() + ", Status: " + learner.getStatus());
                    }
                }
            }
        } else {
            System.out.println("No data available for this month.");
        }
    }

    public void generateMonthlyCoachReport(int month) {
        System.out.println("Monthly Coach Report for Month " + month + ":");
        Map<String, Coach> coachMap = new HashMap<>();

        // Populate coach map with coaches
        for (Coach coach : coaches) {
            coachMap.put(coach.getName(), coach);
        }

        // Aggregate ratings for each coach
        for (Map.Entry<String, Map<String, List<SwimmingLesson>>> entry : schedule.entrySet()) {
            for (Map.Entry<String, List<SwimmingLesson>> innerEntry : entry.getValue().entrySet()) {
                for (SwimmingLesson lesson : innerEntry.getValue()) {
                    for (Learner learner : lesson.getLearners()) {
                        if (learner.getRating() > 0) {
                            String coachName = lesson.getCoach().getName();
                            Coach coach = coachMap.get(coachName);
                            if (coach != null) {
                                coach.addRating(learner.getRating());
                            }
                        }
                    }
                }
            }
        }

        // Print average ratings for each coach
        for (Map.Entry<String, Coach> entry : coachMap.entrySet()) {
            String coachName = entry.getKey();
            Coach coach = entry.getValue();
            double averageRating = coach.getAverageRating();
            System.out.println(coachName + ": Average Rating - " + averageRating);
        }
    }

    public boolean submitReviewAndRating(String bookingID, String review, int rating) {
        for (Map.Entry<String, Map<String, List<SwimmingLesson>>> entry : schedule.entrySet()) {
            for (Map.Entry<String, List<SwimmingLesson>> innerEntry : entry.getValue().entrySet()) {
                for (SwimmingLesson lesson : innerEntry.getValue()) {
                    for (Learner learner : lesson.getLearners()) {
                        if (learner.getBookingID().equals(bookingID)) {
                            learner.setReview(review);
                            learner.setRating(rating);
                            System.out.println("Review and rating submitted successfully!");
                            return true;
                        }
                    }
                }
            }
        }
        System.out.println("No booking found with the provided ID.");
        return false;
    }

    private boolean isHigherGrade(String lessonGrade, String learnerGrade) {
        int lessonGradeLevel = Integer.parseInt(lessonGrade.split(" ")[1]);
        int learnerGradeLevel = Integer.parseInt(learnerGrade.split(" ")[1]);
        return learnerGradeLevel - lessonGradeLevel == 1;
    }

    private String generateBookingID() {
        Random rand = new Random();
        int num = rand.nextInt(10000);
        return String.format("%04d", num);
    }

    private void addLearnerToMonthlyReport(Learner learner) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Months are zero-based in Calendar
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        Map<Integer, Map<String, List<Learner>>> monthReport = monthlyReport.computeIfAbsent(String.valueOf(month), k -> new HashMap<>());
        Map<String, List<Learner>> weekReport = monthReport.computeIfAbsent(week, k -> new HashMap<>());
        List<Learner> learners = weekReport.computeIfAbsent(day, k -> new ArrayList<>());
        learners.add(learner);
    }
}

public class swimming {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SwimmingLessonSchedule lessonSchedule = new SwimmingLessonSchedule();

        while (true) {
            System.out.println("\nHow would you like to proceed?");
            System.out.println("1. View Timetable by Day");
            System.out.println("2. View Timetable by Grade");
            System.out.println("3. View Timetable by Coach");
            System.out.println("4. Book a Lesson");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Attend Lesson");
            System.out.println("7. Generate Monthly Learner Report");
            System.out.println("8. Generate Monthly Coach Report");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the day: ");
                    String day = scanner.nextLine();
                    lessonSchedule.viewTimetableByDay(day);
                    break;
                case 2:
                    System.out.print("Enter the grade level: ");
                    String grade = scanner.nextLine();
                    lessonSchedule.viewTimetableByGrade(grade);
                    break;
                case 3:
                    System.out.print("Enter the coach's name: ");
                    String coachName = scanner.nextLine();
                    lessonSchedule.viewTimetableByCoach(coachName);
                    break;
                case 4:
                    System.out.print("Enter the day: ");
                    String bookingDay = scanner.nextLine();
                    System.out.print("Enter the time slot: ");
                    String bookingTime = scanner.nextLine();
                    System.out.print("Enter the grade level: ");
                    String bookingGrade = scanner.nextLine();
                    System.out.print("Enter learner's name: ");
                    String learnerName = scanner.nextLine();
                    System.out.print("Enter learner's gender: ");
                    String learnerGender = scanner.nextLine();
                    System.out.print("Enter learner's age: ");
                    int learnerAge = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter learner's emergency contact: ");
                    String emergencyContact = scanner.nextLine();
                    Learner newLearner = new Learner(learnerName, learnerGender, learnerAge, emergencyContact, bookingGrade);
                    if (lessonSchedule.bookLesson(bookingDay, bookingTime, bookingGrade, newLearner)) {
                        System.out.println("Lesson booked successfully! Booking ID: " + newLearner.getBookingID());
                    }
                    break;
                case 5:
                    System.out.print("Enter the booking ID to cancel lesson: ");
                    String cancelBookingID = scanner.nextLine();
                    lessonSchedule.cancelLesson(cancelBookingID);
                    break;
                case 6:
                    System.out.print("Enter the booking ID to attend lesson: ");
                    String attendBookingID = scanner.nextLine();
                    System.out.print("Enter your review: ");
                    String review = scanner.nextLine();
                    System.out.print("Enter your rating (1 to 5): ");
                    int rating = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    lessonSchedule.attendLesson(attendBookingID, review, rating);
                    break;
                case 7:
                    System.out.print("Enter the month number: ");
                    int month = scanner.nextInt();
                    lessonSchedule.generateMonthlyLearnerReport(month);
                    break;
                case 8:
                    System.out.print("Enter the month number: ");
                    int coachReportMonth = scanner.nextInt();
                    lessonSchedule.generateMonthlyCoachReport(coachReportMonth);
                    break;
                case 9:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
            }
        }
    }
}

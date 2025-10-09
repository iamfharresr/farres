package Main;

import config.config; // Ensure this import matches your package name
import java.util.Scanner;
import java.util.Map;

public class Main {

    static config dbConfig = new config();
    static Scanner scanner = new Scanner(System.in);

    // View all Users
    public static void viewUsers() {
        String sql = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_users";
        String[] headers = {"ID", "Name", "Email", "Type", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        // NOTE: This requires the viewRecords method in config.java
        dbConfig.viewRecords(sql, headers, columns); 
    }

    // View all Rooms
    public static void viewRooms() {
        String sql = "SELECT r_id, u_id, r_size, r_layout, r_condition, r_price FROM tbl_rooms";
        String[] headers = {"Room ID", "User ID", "Size", "Layout", "Condition", "Price"};
        String[] columns = {"r_id", "u_id", "r_size", "r_layout", "r_condition", "r_price"};
        // NOTE: This requires the viewRecords method in config.java
        dbConfig.viewRecords(sql, headers, columns);
    }

    // Checks if email exists
    public static boolean checkEmailExists(String email) {
        String sql = "SELECT u_email FROM tbl_users WHERE u_email = ?";
        // Uses the fetchRecords method from config.java
        return !dbConfig.fetchRecords(sql, email).isEmpty(); 
    }

    // Authenticate user and return user record or null
    public static Map<String, Object> authenticateUser(String email, String password) {
        String sql = "SELECT * FROM tbl_users WHERE u_email = ? AND u_password = ?";
        // 'var' is replaced with explicit type 'java.util.List<Map<String, Object>>' 
        // to ensure Java 8 compatibility if 'var' keyword isn't supported.
        java.util.List<Map<String, Object>> result = dbConfig.fetchRecords(sql, email, password);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    // Admin approval menu
    public static void adminApprovalMenu() {
        System.out.println("=== Pending Users Approval ===");
        String sqlPending = "SELECT u_id, u_name, u_email, u_type FROM tbl_users WHERE u_status = 'Pending'";
        // 'var' is replaced with explicit type
        java.util.List<Map<String, Object>> pendingUsers = dbConfig.fetchRecords(sqlPending);

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending users to approve.");
            return;
        }

        System.out.println("Pending Users:");
        int count = 1;
        for (Map<String, Object> user : pendingUsers) {
            System.out.println(count++ + ". Name: " + user.get("u_name") + ", Email: " + user.get("u_email") + ", Type: " + user.get("u_type"));
        }

        System.out.print("Enter the Email of the user to approve (or '0' to cancel): ");
        String emailToApprove = scanner.nextLine();

        if (!emailToApprove.equals("0")) {
            String sqlUpdate = "UPDATE tbl_users SET u_status = 'Approved' WHERE u_email = ? AND u_status = 'Pending'";
            // NOTE: This requires the updateRecord method in config.java
            int updated; 
            updated = dbConfig.updateRecord(sqlUpdate, emailToApprove);
            if (updated > 0) {
                System.out.println("User " + emailToApprove + " has been approved.");
            } else {
                System.out.println("Approval failed. Email not found or already approved.");
            }
        }
    }

    // User registration
    public static void registerUser() {
        System.out.println("=== User Registration ===");
        System.out.print("Enter User Name: ");
        String userName = scanner.nextLine();

        System.out.print("Enter User Contact: ");
        String userContact = scanner.nextLine();
        
        System.out.print("Enter User Email: ");
           String userEmail = scanner.nextLine();

        while (true) {
         config con = new config();
        String qry = "SELECT * FROM tbl_users WHERE u_email = ?";
        java.util.List<java.util.Map<String, Object>> result = con.fetchRecords(qry, userEmail);

        if (result.isEmpty()) {
            break;
        } else {
            System.out.print("Email already exists, Enter other Email: ");
            
            userEmail = scanner.nextLine();
        }
    }

        

        System.out.print("Enter User Password: ");
        String userPassword = scanner.nextLine();

        int userTypeChoice;
        String userType = "";
        String userStatus = "";

        while (true) {
            System.out.println("Select User Type:");
            System.out.println("1. Landlord");
            System.out.println("2. Property Manager");
            System.out.println("3. Tenant");
            System.out.print("Enter your choice (1-3): ");

            if (scanner.hasNextInt()) {
                userTypeChoice = scanner.nextInt();
                scanner.nextLine();  // consume newline
                if (userTypeChoice >= 1 && userTypeChoice <= 3) {
                    switch (userTypeChoice) {
                        case 1:
                            userType = "Landlord";
                            userStatus = "Approved";  // Landlord automatically approved
                            break;
                        case 2:
                            userType = "Property Manager";
                            userStatus = "Pending";
                            break;
                        case 3:
                            userType = "Tenant";
                            userStatus = "Pending";
                            break;
                    }
                    break;
                } else {
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }

        String sql = "INSERT INTO tbl_users(u_name, u_contact, u_email, u_password, u_type, u_status) VALUES(?, ?, ?, ?, ?, ?)";
        // NOTE: This requires the addRecord method in config.java
        dbConfig.addRecord(sql, userName, userContact, userEmail, userPassword, userType, userStatus);

        System.out.println("User registered successfully! Your account status is: " + userStatus);
    }

    // User login and dashboard
    public static void login() {
        System.out.println("=== User Login ===");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Map<String, Object> user = authenticateUser(email, password);
        if (user == null) {
            System.out.println("Login failed! Incorrect email or password.");
            return;
        }

        String status = user.get("u_status").toString();
        String type = user.get("u_type").toString();

        if ("Pending".equalsIgnoreCase(status)) {
            System.out.println("Account is pending approval. Please contact admin.");
            return;
        }

        System.out.println("Login successful! Welcome, " + user.get("u_name") + " (" + type + ").");

        if ("Landlord".equalsIgnoreCase(type)) {
            System.out.println("\n--- Landlord/Admin Dashboard ---");
            System.out.print("Do you want to review pending user approvals? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                adminApprovalMenu();
            }
        } else if ("Property Manager".equalsIgnoreCase(type)) {
            System.out.println("Welcome to Property Manager Dashboard (not implemented).");
        } else if ("Tenant".equalsIgnoreCase(type)) {
            System.out.println("Welcome to Tenant Dashboard (not implemented).");
        }
    }

    // Add Room
    public static void addRoom() {
        System.out.println("=== Add Room ===");
        System.out.print("Enter User ID (Landlord ID): ");
        // Ensure input is valid before reading int
        int userId = 0;
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number for User ID: ");
            scanner.nextLine(); // consume non-int input
        }
        userId = scanner.nextInt();
        scanner.nextLine();  // consume newline

        System.out.print("Enter Room Size: ");
        String roomSize = scanner.nextLine();

        System.out.print("Enter Room Layout: ");
        String roomLayout = scanner.nextLine();

        System.out.print("Enter Room Condition: ");
        String roomCondition = scanner.nextLine();

        System.out.print("Enter Room Price: ");
        // Ensure input is valid before reading double
        double roomPrice = 0.0;
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a number for Room Price: ");
            scanner.nextLine(); // consume non-double input
        }
        roomPrice = scanner.nextDouble();
        scanner.nextLine();  // consume newline

        String sql = "INSERT INTO tbl_rooms(u_id, r_size, r_layout, r_condition, r_price) VALUES (?, ?, ?, ?, ?)";
        // NOTE: This requires the addRecord method in config.java
        dbConfig.addRecord(sql, userId, roomSize, roomLayout, roomCondition, roomPrice);

        System.out.println("Room added successfully!");
    }

    public static void main(String[] args) {
        dbConfig.connectDB();
        int choice;
        boolean running = true;

        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Register User");
            System.out.println("2. Login");
            System.out.println("3. Add Room");
            System.out.println("4. View Users");
            System.out.println("5. View Rooms");
            System.out.println("6. Exit");
            System.out.print("Enter your choice (1-6): ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();  // consume newline

                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        addRoom();
                        break;
                    case 4:
                        viewUsers();
                        break;
                    case 5:
                        viewRooms();
                        break;
                    case 6:
                        System.out.println("Exiting... Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                        break;
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();  // consume invalid input
            }
        }

        scanner.close();
    }
}
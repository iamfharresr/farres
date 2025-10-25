package Main;

import config.config;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class Main {

    public static void viewAvailableRooms(config dbConfig) {
        System.out.println("\n--- Available Rooms ---");
        Admin.viewRooms(dbConfig); 
    }

    public static void tenantDashboard(Map<String, Object> user, config dbConfig, Scanner scanner) {
        System.out.println("\n--- Tenant Dashboard ---");
        boolean running = true;
        while (running) {
            System.out.println("\n1. View Available Rooms"); 
            System.out.println("2. View My Rental Record");
            System.out.println("3. Logout");
            System.out.print("Enter your choice (1-3): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewAvailableRooms(dbConfig);
                        break;
                    case 2:
                        System.out.println("Viewing rental record for tenant: " + user.get("u_name"));
                        break;
                    case 3:
                        System.out.println("Logging out Tenant...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 3.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    public static void registerUser(config dbConfig, Scanner scanner) {
        System.out.println("=== User Registration ===");
        
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        String userType = "";
        while (true) {
            System.out.print("Select User Type (1=Tenant, 2=Property Manager): ");
            String typeChoice = scanner.nextLine();
            if (typeChoice.equals("1")) {
                userType = "Tenant";
                break;
            } else if (typeChoice.equals("2")) {
                userType = "Property Manager";
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }

        String status = userType.equals("Tenant") ? "Approved" : "Pending"; 

        String sql = "INSERT INTO tbl_user(u_name, u_email, u_password, u_type, u_status) VALUES(?, ?, ?, ?, ?)";
        dbConfig.addRecord(sql, name, email, password, userType, status);
        
        System.out.println("Registration complete. Status: " + status);
    }
    
    public static void login(config dbConfig, Scanner scanner) {
        System.out.println("=== User Login ===");
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        String sql = "SELECT u_id, u_name, u_type, u_status FROM tbl_user WHERE u_email = ? AND u_password = ?";
        List<Map<String, Object>> results = dbConfig.fetchRecords(sql, email, password);
        
        if (results.isEmpty()) {
            System.out.println("Login failed: Invalid email or password.");
            return;
        }

        Map<String, Object> user = results.get(0);
        String status = user.get("u_status").toString();
        String userType = user.get("u_type").toString();
        
        if ("Pending".equals(status)) {
            System.out.println("Login failed: Your account is pending approval.");
            return;
        }
        
        System.out.println("Login successful! Welcome, " + user.get("u_name") + " (" + user.get("u_type") + ").");

        // CRITICAL FIX: Ensure the call matches the method signature in Admin.java
        if ("Property Manager".equals(userType)) {
            Admin.propertyManagerDashboard(user, dbConfig, scanner); 
        } else if ("SuperAdmin".equals(userType)) {
            SuperAdmin.runDashboard(user, dbConfig, scanner);
        } else if ("Tenant".equals(userType)) {
            tenantDashboard(user, dbConfig, scanner); 
        } else {
            System.out.println("Error: Unknown user type.");
        }
    }

    public static void main(String[] args) {
        config dbConfig = new config();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===== RENTAL PLATFORM MAIN MENU =====");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Register User");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        registerUser(dbConfig, scanner);
                        break;
                    case 2:
                        login(dbConfig, scanner);
                        break;
                    case 3:
                        System.out.println("Exiting application. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}
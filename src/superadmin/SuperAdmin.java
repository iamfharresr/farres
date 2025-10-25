package Main; 

import config.config;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class SuperAdmin {

    // Helper method to View all Users
    public static void viewUsers(config dbConfig) {
        String sql = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_user"; 
        String[] headers = {"ID", "Name", "Email", "Type", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        dbConfig.viewRecords(sql, headers, columns); 
    }

    // Helper method to View all Rooms
    public static void viewRooms(config dbConfig) {
        String sql = "SELECT r_id, r_size, r_layout, r_condition, r_price FROM tbl_rooms";
        String[] headers = {"Room ID", "Size", "Layout", "Condition", "Price"};
        String[] columns = {"r_id", "r_size", "r_layout", "r_condition", "r_price"};
        dbConfig.viewRecords(sql, headers, columns);
    }

    // Admin approval menu
    public static void adminApprovalMenu(config dbConfig, Scanner scanner) {
        System.out.println("=== Pending Users Approval ===");
        
        String sqlPending = "SELECT u_id, u_name, u_email, u_type FROM tbl_user WHERE u_status = 'Pending'";
        List<Map<String, Object>> pendingUsers = dbConfig.fetchRecords(sqlPending);

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending users to approve.");
            return;
        }

        System.out.println("Pending Users:");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-5s | %-15s | %-20s%n", "ID", "Name", "Type");
        System.out.println("--------------------------------------------------");
        for (Map<String, Object> user : pendingUsers) {
            System.out.printf("%-5s | %-15s | %-20s%n", 
                user.get("u_id"), user.get("u_name"), user.get("u_type"));
        }
        System.out.println("--------------------------------------------------");

        System.out.print("Enter the **ID** of the user to approve (or '0' to cancel): ");
        String idToApproveStr = scanner.nextLine();

        if (!idToApproveStr.equals("0")) {
            try {
                int idToApprove = Integer.parseInt(idToApproveStr);
                
                String sqlUpdate = "UPDATE tbl_user SET u_status = 'Approved' WHERE u_id = ? AND u_status = 'Pending'";
                
                // Using the updateRecord method with the ID as the last parameter
                dbConfig.updateRecord(sqlUpdate, "Approved", idToApprove); 
                
                System.out.println("User ID " + idToApprove + " has been processed for approval.");
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid user ID number.");
            }
        }
    }

    // SuperAdmin Dashboard - highest access
    public static void runDashboard(Map<String, Object> user, config dbConfig, Scanner scanner) {
        System.out.println("\n--- SuperAdmin Dashboard ---");
        boolean running = true;
        while (running) {
            System.out.println("\n1. View ALL Users");
            System.out.println("2. View ALL Rooms");
            System.out.println("3. Manage Pending User Approvals");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1-4): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewUsers(dbConfig);
                        break;
                    case 2:
                        viewRooms(dbConfig);
                        break;
                    case 3:
                        adminApprovalMenu(dbConfig, scanner);
                        break;
                    case 4:
                        System.out.println("Logging out SuperAdmin...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 4.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}
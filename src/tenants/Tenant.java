package Main;

import config.config;
import java.util.Scanner;
import java.util.Map;

public class Tenant {

    // Helper method to View all Rooms (accessible to tenants)
    public static void viewRooms(config dbConfig) {
        // You might modify this SQL later to only show *available* rooms
        String sql = "SELECT r_id, r_size, r_layout, r_condition, r_price FROM tbl_rooms";
        String[] headers = {"Room ID", "Size", "Layout", "Condition", "Price"};
        String[] columns = {"r_id", "r_size", "r_layout", "r_condition", "r_price"};
        dbConfig.viewRecords(sql, headers, columns);
    }

    /**
     * Menu for Tenants
     * @param user The logged-in user's record.
     * @param dbConfig The database configuration object.
     * @param scanner The shared Scanner object for input.
     */
    public static void tenantDashboard(Map<String, Object> user, config dbConfig, Scanner scanner) {
        System.out.println("\n--- Tenant Dashboard ---");
        boolean running = true;
        while (running) {
            System.out.println("\n1. View Available Rooms");
            System.out.println("2. View Rental History (Placeholder)");
            System.out.println("3. Submit Maintenance Request (Placeholder)");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1-4): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        viewRooms(dbConfig);
                        break;
                    case 2:
                        System.out.println("Rental history features would go here.");
                        break;
                    case 3:
                        System.out.println("Maintenance request features would go here.");
                        break;
                    case 4:
                        System.out.println("Logging out...");
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
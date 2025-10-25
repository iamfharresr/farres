package Main;

import config.config;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class Admin {

    // =================================================================================
    //  HELPER METHODS (UNCHANGED)
    // =================================================================================

    public static void viewAllUsers(config dbConfig) {
        System.out.println("\n--- Available Tenant Users (tbl_user) ---");
        String sql = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_user WHERE u_type = 'Tenant'"; 
        String[] headers = {"ID", "Name", "Email", "Type", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        dbConfig.viewRecords(sql, headers, columns); 
        System.out.println("-------------------------------------------------------");
    }
    
    // =================================================================================
    //  CORE CRUD METHODS (Rooms & Tenants unchanged)
    // =================================================================================

    // --- Rooms Management Methods (UNCHANGED) ---
    public static void viewRooms(config dbConfig) {
        String sql = "SELECT r_id, r_size, r_layout, r_condition, r_price FROM tbl_rooms";
        String[] headers = {"Room ID", "Size", "Layout", "Condition", "Price"};
        String[] columns = {"r_id", "r_size", "r_layout", "r_condition", "r_price"};
        dbConfig.viewRecords(sql, headers, columns);
    }
    
    public static void addRoom(Map<String, Object> user, config dbConfig, Scanner scanner) {
        System.out.println("=== Add New Room ===");
        
        System.out.print("Enter Room Size (e.g., 50 sqm): ");
        String size = scanner.nextLine();
        
        System.out.print("Enter Room Layout (e.g., 1BR, Studio): ");
        String layout = scanner.nextLine();
        
        System.out.print("Enter Room Condition (e.g., New, Good, Needs Repair): ");
        String condition = scanner.nextLine();
        
        System.out.print("Enter Monthly Rental Price: ");
        String price = scanner.nextLine(); 

        String sql = "INSERT INTO tbl_rooms(r_size, r_layout, r_condition, r_price) VALUES(?, ?, ?, ?)";
        dbConfig.addRecord(sql, size, layout, condition, price);
    }
    
    public static void deleteRoom(config dbConfig, Scanner scanner) {
        System.out.println("=== Delete Room ===");
        viewRooms(dbConfig);
        
        System.out.print("Enter the **Room ID (r_id)** to delete (or '0' to cancel): ");
        String idToDeleteStr = scanner.nextLine();

        if (idToDeleteStr.equals("0")) {
            System.out.println("Room deletion cancelled.");
            return;
        }

        try {
            int idToDelete = Integer.parseInt(idToDeleteStr);
            String sqlDelete = "DELETE FROM tbl_rooms WHERE r_id = ?";
            dbConfig.deleteRecord(sqlDelete, idToDelete);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid Room ID number.");
        }
    }

    public static void updateRoom(config dbConfig, Scanner scanner) {
        System.out.println("=== Update Room ===");
        viewRooms(dbConfig);

        System.out.print("Enter the **Room ID (r_id)** to update (or '0' to cancel): ");
        String idToUpdateStr = scanner.nextLine();

        if (idToUpdateStr.equals("0")) {
            System.out.println("Room update cancelled.");
            return;
        }

        try {
            int idToUpdate = Integer.parseInt(idToUpdateStr);
            
            System.out.println("\nSelect Field to Update:");
            System.out.println("1. Room Size");
            System.out.println("2. Room Layout");
            System.out.println("3. Room Condition");
            System.out.println("4. Monthly Rental Price");
            System.out.print("Enter choice (1-4): ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice.");
                scanner.nextLine();
                return;
            }
            int fieldChoice = scanner.nextInt();
            scanner.nextLine();
            
            String columnName;
            switch (fieldChoice) {
                case 1: columnName = "r_size"; break;
                case 2: columnName = "r_layout"; break;
                case 3: columnName = "r_condition"; break;
                case 4: columnName = "r_price"; break;
                default:
                    System.out.println("Invalid field selection.");
                    return;
            }

            System.out.print("Enter the **NEW VALUE** for " + columnName + ": ");
            String newValue = scanner.nextLine();

            String sqlUpdate = "UPDATE tbl_rooms SET " + columnName + " = ? WHERE r_id = ?";
            dbConfig.updateRecord(sqlUpdate, newValue, idToUpdate);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid Room ID number.");
        }
    }


    // --- Tenants Management Methods (UNCHANGED) ---
    public static void viewTenants(config dbConfig) {
        String sql = "SELECT t_id, u_id, t_name, t_contact FROM tbl_tenants";
        String[] headers = {"Tenant ID", "User ID", "Name", "Contact"};
        String[] columns = {"t_id", "u_id", "t_name", "t_contact"};
        dbConfig.viewRecords(sql, headers, columns);
    }

    public static void addTenant(config dbConfig, Scanner scanner) {
        System.out.println("=== Add New Tenant ===");
        
        viewAllUsers(dbConfig);
        
        System.out.print("Enter the associated **User ID (u_id)** from the list above: ");
        String u_id_str = scanner.nextLine();
        int u_id;
        
        try {
            u_id = Integer.parseInt(u_id_str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! User ID must be a number. Tenant addition cancelled.");
            return;
        }

        String sqlUserFetch = "SELECT u_name, u_email FROM tbl_user WHERE u_id = ? AND u_type = 'Tenant'";
        List<Map<String, Object>> userResults = dbConfig.fetchRecords(sqlUserFetch, u_id);

        if (userResults.isEmpty()) {
            System.out.println("Error: User ID " + u_id + " not found or is not a Tenant user. Tenant addition cancelled.");
            return;
        }

        Map<String, Object> userData = userResults.get(0);
        String tenantName = userData.get("u_name").toString();
        String tenantContact = userData.get("u_email").toString(); 
        
        System.out.println("\n--- Tenant Details Fetched ---");
        System.out.println("User ID: " + u_id);
        System.out.println("Tenant Name (u_name): " + tenantName);
        System.out.println("Tenant Contact (u_email): " + tenantContact);
        System.out.println("------------------------------");

        String sql = "INSERT INTO tbl_tenants(u_id, t_name, t_contact) VALUES(?, ?, ?)";
        dbConfig.addRecord(sql, u_id, tenantName, tenantContact);
    }
    
    public static void deleteTenant(config dbConfig, Scanner scanner) {
        System.out.println("=== Delete Tenant ===");
        viewTenants(dbConfig);
        
        System.out.print("Enter the **Tenant ID (t_id)** to delete (or '0' to cancel): ");
        String idToDeleteStr = scanner.nextLine();

        if (idToDeleteStr.equals("0")) {
            System.out.println("Tenant deletion cancelled.");
            return;
        }

        try {
            int idToDelete = Integer.parseInt(idToDeleteStr);
            String sqlDelete = "DELETE FROM tbl_tenants WHERE t_id = ?";
            dbConfig.deleteRecord(sqlDelete, idToDelete);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid Tenant ID number.");
        }
    }
    
    public static void updateTenant(config dbConfig, Scanner scanner) {
        System.out.println("=== Update Tenant Info ===");
        viewTenants(dbConfig);

        System.out.print("Enter the **Tenant ID (t_id)** to update (or '0' to cancel): ");
        String idToUpdateStr = scanner.nextLine();

        if (idToUpdateStr.equals("0")) {
            System.out.println("Tenant update cancelled.");
            return;
        }

        try {
            int idToUpdate = Integer.parseInt(idToUpdateStr);
            
            System.out.println("\nSelect Field to Update:");
            System.out.println("1. Associated User ID (u_id)");
            System.out.println("2. Tenant Name");
            System.out.println("3. Tenant Contact");
            System.out.print("Enter choice (1-3): ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice.");
                scanner.nextLine();
                return;
            }
            int fieldChoice = scanner.nextInt();
            scanner.nextLine();
            
            String columnName;
            switch (fieldChoice) {
                case 1: columnName = "u_id"; break;
                case 2: columnName = "t_name"; break;
                case 3: columnName = "t_contact"; break;
                default:
                    System.out.println("Invalid field selection.");
                    return;
            }

            System.out.print("Enter the **NEW VALUE** for " + columnName + ": ");
            String newValue = scanner.nextLine();

            String sqlUpdate = "UPDATE tbl_tenants SET " + columnName + " = ? WHERE t_id = ?";
            dbConfig.updateRecord(sqlUpdate, newValue, idToUpdate);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid Tenant ID number.");
        }
    }


    // --- Rental Records Management Methods (IMPLEMENTED STUBS) ---
    
    public static void viewRecords(config dbConfig) {
        // Using rm_id based on your database schema
        String sql = "SELECT r_id, t_id, rm_id, r_date, r_status, r_duration FROM tbl_records";
        String[] headers = {"Record ID", "Tenant ID", "Room ID", "Start Date", "Status", "Duration"};
        String[] columns = {"r_id", "t_id", "rm_id", "r_date", "r_status", "r_duration"};
        dbConfig.viewRecords(sql, headers, columns);
    }

    public static void addRecord(config dbConfig, Scanner scanner) {
        System.out.println("=== Add New Rental Record ===");
        
        System.out.print("Enter Tenant ID (t_id): ");
        String t_id = scanner.nextLine();
        
        System.out.print("Enter Room ID (rm_id): ");
        String rm_id = scanner.nextLine();

        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        System.out.print("Enter Status (e.g., Active, Expired): ");
        String status = scanner.nextLine();
        
        System.out.print("Enter Duration (e.g., 6 months): ");
        String duration = scanner.nextLine();

        String sql = "INSERT INTO tbl_records(t_id, rm_id, r_date, r_status, r_duration) VALUES(?, ?, ?, ?, ?)";
        dbConfig.addRecord(sql, t_id, rm_id, date, status, duration);
    }
    
    // NEW: Implemented Update Record
    public static void updateRentalRecord(config dbConfig, Scanner scanner) {
        System.out.println("=== Update Rental Record ===");
        viewRecords(dbConfig); // Show existing records
        
        System.out.print("Enter the **Record ID (r_id)** to update (or '0' to cancel): ");
        String idToUpdateStr = scanner.nextLine();

        if (idToUpdateStr.equals("0")) {
            System.out.println("Record update cancelled.");
            return;
        }

        try {
            int idToUpdate = Integer.parseInt(idToUpdateStr);
            
            System.out.println("\nSelect Field to Update:");
            System.out.println("1. Tenant ID (t_id)");
            System.out.println("2. Room ID (rm_id)");
            System.out.println("3. Start Date (r_date)");
            System.out.println("4. Status (r_status)");
            System.out.println("5. Duration (r_duration)");
            System.out.print("Enter choice (1-5): ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice.");
                scanner.nextLine();
                return;
            }
            int fieldChoice = scanner.nextInt();
            scanner.nextLine();
            
            String columnName;
            switch (fieldChoice) {
                case 1: columnName = "t_id"; break;
                case 2: columnName = "rm_id"; break;
                case 3: columnName = "r_date"; break;
                case 4: columnName = "r_status"; break;
                case 5: columnName = "r_duration"; break;
                default:
                    System.out.println("Invalid field selection.");
                    return;
            }

            System.out.print("Enter the **NEW VALUE** for " + columnName + ": ");
            String newValue = scanner.nextLine();

            String sqlUpdate = "UPDATE tbl_records SET " + columnName + " = ? WHERE r_id = ?";
            dbConfig.updateRecord(sqlUpdate, newValue, idToUpdate);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid Record ID number.");
        }
    }
    
    // NEW: Implemented Delete Record
    public static void deleteRentalRecord(config dbConfig, Scanner scanner) {
        System.out.println("=== Delete Rental Record ===");
        viewRecords(dbConfig);
        
        System.out.print("Enter the **Record ID (r_id)** to delete (or '0' to cancel): ");
        String idToDeleteStr = scanner.nextLine();

        if (idToDeleteStr.equals("0")) {
            System.out.println("Record deletion cancelled.");
            return;
        }

        try {
            int idToDelete = Integer.parseInt(idToDeleteStr);
            String sqlDelete = "DELETE FROM tbl_records WHERE r_id = ?";
            dbConfig.deleteRecord(sqlDelete, idToDelete);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid Record ID number.");
        }
    }


    // =================================================================================
    //  NESTED MENU HANDLERS
    // =================================================================================

    private static void handleRoomsMenu(Map<String, Object> user, config dbConfig, Scanner scanner) {
        // (UNCHANGED) ...
        boolean subRunning = true;
        while (subRunning) {
            System.out.println("\n--- Rooms Management (tbl_rooms) ---");
            System.out.println("1. Add Room");
            System.out.println("2. View Rooms");
            System.out.println("3. Update Room");
            System.out.println("4. Delete Room");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice (1-5): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1: addRoom(user, dbConfig, scanner); break;
                    case 2: viewRooms(dbConfig); break;
                    case 3: updateRoom(dbConfig, scanner); break;
                    case 4: deleteRoom(dbConfig, scanner); break;
                    case 5: subRunning = false; break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleTenantsMenu(config dbConfig, Scanner scanner) {
        // (UNCHANGED) ...
        boolean subRunning = true;
        while (subRunning) {
            System.out.println("\n--- Tenants Management (tbl_tenants) ---");
            System.out.println("1. Add Tenant");
            System.out.println("2. View ALL Tenants");
            System.out.println("3. Update Tenant Info");
            System.out.println("4. Delete Tenant");
            System.out.println("5. Back to Main Menu"); 
            System.out.print("Enter your choice (1-5): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1: addTenant(dbConfig, scanner); break;
                    case 2: viewTenants(dbConfig); break;
                    case 3: updateTenant(dbConfig, scanner); break; 
                    case 4: deleteTenant(dbConfig, scanner); break; 
                    case 5: subRunning = false; break;             
                    default: System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // UPDATED: handleRecordsMenu (Calls implemented methods)
    private static void handleRecordsMenu(config dbConfig, Scanner scanner) {
        boolean subRunning = true;
        while (subRunning) {
            System.out.println("\n--- Rental Records Management (tbl_records) ---");
            System.out.println("1. Add Record");
            System.out.println("2. View All Records");
            System.out.println("3. Update Record");
            System.out.println("4. Delete Record");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice (1-5): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1: addRecord(dbConfig, scanner); break;
                    case 2: viewRecords(dbConfig); break;
                    case 3: updateRentalRecord(dbConfig, scanner); break; // Calls the new method
                    case 4: deleteRentalRecord(dbConfig, scanner); break; // Calls the new method
                    case 5: subRunning = false; break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // =================================================================================
    //  PROPERTY MANAGER DASHBOARD (UNCHANGED)
    // =================================================================================

    public static void propertyManagerDashboard(Map<String, Object> user, config dbConfig, Scanner scanner) {
        System.out.println("\n--- Property Manager Dashboard ---");
        boolean running = true;
        while (running) {
            System.out.println("\n1. Rooms Management");
            System.out.println("2. Tenants Management");
            System.out.println("3. Rental Records Management");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1-4): ");

            if (scanner.hasNextInt()) { 
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: handleRoomsMenu(user, dbConfig, scanner); break;
                    case 2: handleTenantsMenu(dbConfig, scanner); break;
                    case 3: handleRecordsMenu(dbConfig, scanner); break;
                    case 4: System.out.println("Logging out Property Manager..."); running = false; break;
                    default: System.out.println("Invalid choice! Please enter a number between 1 and 4.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}
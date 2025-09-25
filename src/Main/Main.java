package Main;

import java.util.Scanner;
import Config.config;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("WELCOME TO MY ROOM RENTAL MANAGEMENT SERVICE SYSTEM!");
        System.out.println("1. Add User");
        System.out.println("2. Add Room");
        System.out.println("3. Add Records");

        int choice = 0;

        while (true) {
            System.out.print("Please enter your choice (1-3): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 3) {
                    break;
                } else {
                    System.out.println("Invalid choice! Please enter a number between 1 and 3.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
        }

        switch (choice) {
            case 1:
                System.out.println("You chose to Add User.");

                scanner.nextLine();
                
                System.out.print("Enter User Name: ");
                String userName = scanner.nextLine();
                
                System.out.print("Enter User Contact: ");
                String userContact = scanner.nextLine();
                
                System.out.print("Enter User Email: ");
                String userEmail = scanner.nextLine();
                
                System.out.println("\n--- User Details ---");
                System.out.println("User Name: " + userName);
                System.out.println("User Contact: " + userContact);
                System.out.println("User Email: " + userEmail);
                System.out.println("--------------------");
                
                config dbConfigUser = new config();
                String sqlUser = "INSERT INTO tbl_users(u_name, u_contact, u_email) VALUES(?, ?, ?)";
                dbConfigUser.addRecord(sqlUser, userName, userContact, userEmail);
                
                break;
            case 2:
                System.out.println("You chose to Add Room.");
                
                scanner.nextLine();

                System.out.print("Enter User ID: ");
                int userId = scanner.nextInt();
                scanner.nextLine();
                
                System.out.print("Enter Room Size: ");
                String roomSize = scanner.nextLine();
                
                System.out.print("Enter Room Layout: ");
                String roomLayout = scanner.nextLine();
                
                System.out.print("Enter Room Condition: ");
                String roomCondition = scanner.nextLine();
                
                System.out.print("Enter Room Price: ");
                double roomPrice = scanner.nextDouble();
                
                System.out.println("\n--- Room Details ---");
                System.out.println("User ID: " + userId);
                System.out.println("Room Size: " + roomSize);
                System.out.println("Room Layout: " + roomLayout);
                System.out.println("Room Condition: " + roomCondition);
                System.out.println("Room Price: " + roomPrice);
                System.out.println("--------------------");

                config dbConfigRoom = new config();
                String sqlRoom = "INSERT INTO tbl_rooms(u_id, r_size, r_layout, r_condition, r_price) VALUES(?, ?, ?, ?, ?)";
                dbConfigRoom.addRecord(sqlRoom, userId, roomSize, roomLayout, roomCondition, roomPrice);
                
                break;
            case 3:
                System.out.println("You chose to Add Records.");
                break;
            default:
                System.out.println("Something went wrong.");
        }

        scanner.close();
    }
}
package Main;

import java.util.Scanner;

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
                break;
            case 2:
                System.out.println("You chose to Add Room.");
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

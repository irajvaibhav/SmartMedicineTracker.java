import java.util.Scanner;


public class SmartMedicineTracker {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager();
        PatientTracker tracker = new PatientTracker();

        // Always generate the latest web files on startup
        userManager.generateUsersJSON();
        tracker.generateAllWebPages();
        System.out.println("✅ Web interface files generated.");
        System.out.println("➡️ Start by opening 'login.html' in your browser (using IntelliJ's server is recommended).");

        String loggedInAdmin = null;
        while (true) {
            if (loggedInAdmin == null) {
                System.out.println("\n--- Admin Console: Login ---");
                System.out.println("1. Login as Admin");
                System.out.println("2. Register New Admin");
                System.out.println("3. Exit");
                System.out.print("Select option: ");
                String choice = sc.nextLine();
                switch(choice) {
                    case "1":
                        loggedInAdmin = userManager.login(sc);
                        break;
                    case "2":
                        userManager.register(sc);
                        break;
                    case "3":
                        System.out.println("👋 Bye!");
                        sc.close();
                        return;
                    default:
                        System.out.println("❌ Invalid option.");
                }
            } else {
                System.out.println("\n--- Admin Menu (Logged in as: " + loggedInAdmin + ") ---");
                System.out.println("1. Add Medicine to Patient");
                System.out.println("2. Delete Patient");
                System.out.println("3. Logout");
                System.out.print("Select option: ");
                String choice = sc.nextLine();
                switch(choice) {
                    case "1":
                        try {
                            tracker.addMedicineToPatient(sc, loggedInAdmin);
                        } catch (Exception e) {
                            System.out.println("❌ Invalid input. Please check the data format.");
                        }
                        break;
                    case "2":
                        tracker.deletePatient(sc);
                        break;
                    case "3":
                        loggedInAdmin = null;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("❌ Invalid option.");
                }
            }
        }
    }
}
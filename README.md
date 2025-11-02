A secure, multi-user web application to help families manage medication schedules for multiple patients, built with a Java backend and a modern web front-end.

The Problem It Solves
Managing medications for family members, especially the elderly, can be confusing and stressful. It's easy to lose track of expiry dates, run low on stock, or mix up patient profiles. This project solves that by providing a private, secure, and easy-to-use dashboard for each user to track their patients' health information.

✨ Key Features
Secure Web Login: A professional login page that uses email and a securely hashed password to grant access.

Private Multi-User Accounts: Each user has their own account. When you log in, you only see the patient data you are responsible for. Raj cannot see Himanshu's data, and vice versa.

Patient-Centric Dashboard: The main screen shows a clean summary card for each of your patients, displaying their overall health risk.

Smart Alerts 💡: The system automatically provides clear, color-coded warnings for medicines that are Expired, Expiring Soon, or Low on Stock.

Interactive UI: The dashboard is easy to use with a live search bar, filter buttons, and a dark mode toggle.

Guest Mode: A "Continue as Guest" option is available to demonstrate the application with sample data.

⚙️ How It Works: The Architecture
This project uses a modern Two-Tier Architecture, which separates the "brain" from the "face" of the application.

The Java Admin Console (The "Backend" / The Brain 🧠):

This is the powerful engine of the application. As the administrator, you run this in your console (like in IntelliJ).

Its job is to register new users and to add, update, or delete all patient and medicine data.

When you make changes, it generates the necessary files (users.json, medicines.html) that the front-end will use.

The Web Interface (The "Frontend" / The Face 😊):

This is what the end-user sees and interacts with in their web browser (Chrome).

It starts with login.html, which securely checks the user's password using JavaScript.

The main medicines.html dashboard then uses JavaScript to read which user has logged in and hides all patient cards that don't belong to them, ensuring total privacy.

🛠️ Technology Stack
Backend: Core Java

Frontend: HTML5, CSS3, JavaScript

Security: SHA-256 Hashing Algorithm

Data Persistence: Java Serialization (.dat files)

IDE: IntelliJ IDEA

🚀 How to Run the Project
Run the Admin Console:

Open the project in IntelliJ IDEA.

Run the main method in the SmartMedicineTracker.java file.

Use the console menu to Register your users (e.g., raj@example.com, himanshu@example.com) and add patient data for each of them.

Launch the Web App:

In IntelliJ, find and open the login.html file in the editor.

Move your mouse to the top-right corner of the editor window and click the Chrome icon.

This will open the login page in your browser using a local server.

Log In and Explore:

Log in with one of the user accounts you created. You will be redirected to the dashboard and see only the data you added for that user.

📂 Project Structure
src folder: Contains all the Java source code, neatly organized into separate classes:

SmartMedicineTracker.java: The main entry point that runs the admin console.

UserManager.java: Handles user registration and password security.

PatientTracker.java: Manages all patient data and generates the web pages.

Patient.java / Medicine.java: Simple "blueprint" classes for our data.

login.html: The static starting page for the web interface.

Generated Files: The Java app creates these files:

medicines.html: The main dashboard page.

users.json: A secure file with hashed passwords for the login page to read.

.dat files: The "database" where your user and patient objects are saved.
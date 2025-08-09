# TourEase - Tourism Management System (Java + MySQL)

## 📌 Overview
TourEase is a Java-based Tourism Management System that allows users to book tours, accommodations, and transportation easily.  
It supports admin and travel agent roles for efficient management and uses MySQL as the database.

## ✨ Features
- 🧳 **Tour Booking** – Reserve tours and travel packages.
- 🏨 **Accommodation Management** – Manage hotels and stays.
- 🚍 **Transport Booking** – Schedule buses, flights, and cars.
- 📅 **Itinerary Planning** – Plan travel schedules with data structures.
- 👨‍💼 **Role-based Access** – Admin, Travel Agent, and Customer logins.
- 🗄 **Database Integration** – Store customer and travel data in MySQL.

## ⚙️ Technologies Used
- **Java** – Core project logic
- **MySQL** – Database
- **JDBC** – Database connectivity
- **Collections & Data Structures** – For itinerary and booking management

## 📂 Folder Structure
Tourism_Management_System/
├── src/
│   ├── Tourism_Management_System/
│   │   ├── DBMS/
│   │   │   └── DBMS.java
│   │   ├── Main/
│   │   │   └── Main.java
│   │   ├── Tasks/
│   │   │   └── BookingTask.java
│   │   ├── Tour_Info/
│   │   │   └── Tour_Info.java
│   │   ├── User_Info/
│   │   │   ├── Admin.java
│   │   │   ├── Customer.java
│   │   │   └── TravelAgent.java
│   │   └── ColorCodes.java
│   └── tourism_db.sql
└── README.md


## 🚀 How to Run
1. Compile the Java Files
Navigate to the src directory and compile all Java files.
Make sure you include the MySQL JDBC driver in your classpath.

Linux / Mac:
bash
Copy
Edit
javac -cp ".:/path/to/mysql-connector-java.jar" src/Tourism_Management_System/Main/Main.java
Windows (Command Prompt):
cmd
Copy
Edit
javac -cp ".;C:\path\to\mysql-connector-java.jar" src\Tourism_Management_System\Main\Main.java
Note: Replace /path/to/mysql-connector-java.jar or C:\path\to\mysql-connector-java.jar with the actual path to your downloaded JDBC driver file.

2. Run the Application
Linux / Mac:
bash
Copy
Edit
java -cp ".:/path/to/mysql-connector-java.jar" src.Tourism_Management_System.Main.Main
Windows (Command Prompt):
cmd
Copy
Edit
java -cp ".;C:\path\to\mysql-connector-java.jar" src.Tourism_Management_System.Main.Main
⚡ Pro Tip:
To simplify commands, store the JDBC driver inside a lib folder in your project and use:

bash
Copy
Edit
-cp ".:lib/mysql-connector-java.jar"   # Linux / Mac
-cp ".;lib\mysql-connector-java.jar"   # Windows

##📌 Future Enhancements
-Add a web interface using HTML/CSS/JavaScript
-Implement payment gateway integration
-Include real-time booking status updates

##💡 Developed by Dhruvil Thummar

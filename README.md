# TourEase - Tourism Management System (Java + MySQL)

## 📌 Overview
**TourEase** is a Java-based Tourism Management System that allows users to book tours, accommodations, and transportation easily.  
It supports admin and travel agent roles for efficient management and uses **MySQL** as the database.

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
│ ├── Tourism_Management_System/
│ │ ├── DBMS/
│ │ │ └── DBMS.java
│ │ ├── Main/
│ │ │ └── Main.java
│ │ ├── Tasks/
│ │ │ └── BookingTask.java
│ │ ├── Tour_Info/
│ │ │ └── Tour_Info.java
│ │ ├── User_Info/
│ │ │ ├── Admin.java
│ │ │ ├── Customer.java
│ │ │ └── TravelAgent.java
│ │ └── ColorCodes.java
│ └── tourism_db.sql
└── README.md

## 🚀 How to Run

### 1. Compile the Java Files
Make sure you include the MySQL JDBC driver (mysql-connector-java.jar) in your classpath.

**Linux / macOS**
```bash
cd src
javac -cp ".:/path/to/mysql-connector-java.jar" Tourism_Management_System/Main/Main.java
Windows (Command Prompt)

cd src
javac -cp ".;C:\path\to\mysql-connector-java.jar" Tourism_Management_System\Main\Main.java

⚡ Pro Tip: Put the JDBC driver into a lib/ folder inside your project and use:

Linux/macOS: -cp ".:lib/mysql-connector-java.jar"

Windows: -cp ".;lib\mysql-connector-java.jar"

2. Run the Application
Linux / macOS

java -cp ".:/path/to/mysql-connector-java.jar" src.Tourism_Management_System.Main.Main
Windows (Command Prompt)

c
Copy
Edit
java -cp ".;C:\path\to\mysql-connector-java.jar" src.Tourism_Management_System.Main.Main
📌 Future Enhancements
Add a web interface using HTML/CSS/JavaScript

Implement payment gateway integration

Include real-time booking status updates

##📝 Usage
When the app runs, you'll see a main menu for login and signup. Sample credentials used in the seed SQL:

Admin

Username: admin1

Password: adminpass

Travel Agent

Username: agentli

Password: agentpass

Customer

Username: johndoe

Password: custpass

You can also create new users via the signup option.

💡 Developed by Dhruvil Thummar

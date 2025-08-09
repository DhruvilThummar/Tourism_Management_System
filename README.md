# TourEase - Tourism Management System (Java + MySQL)

## 📌 Overview
**TourEase** is a Java-based Tourism Management System that allows users to easily book tours, accommodations, and transportation.  
It supports **Admin**, **Travel Agent**, and **Customer** roles for efficient management and uses **MySQL** for data storage.

## ✨ Features
- 🧳 **Tour Booking** – Reserve tours and travel packages  
- 🏨 **Accommodation Management** – Manage hotels and stays  
- 🚍 **Transport Booking** – Schedule buses, flights, and cars  
- 📅 **Itinerary Planning** – Plan travel schedules using data structures  
- 👨‍💼 **Role-based Access** – Admin, Travel Agent, and Customer logins  
- 🗄 **Database Integration** – Store customer and travel data in MySQL  

## ⚙️ Technologies Used
- **Java** – Core project logic  
- **MySQL** – Database  
- **JDBC** – Database connectivity  
- **Collections & Data Structures** – For itinerary and booking management  

## 📂 Folder Structure
```bash
Tourism_Management_System/
├── src/
│   ├── Tourism_Management_System/
│   │   ├── DBMS/
│   │   │   ├── DBMS.java
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
```

## 🚀 How to Run

### 1️⃣ Compile the Java Files  
Make sure you include the MySQL JDBC driver (`mysql-connector-java.jar`) in your classpath.

**Linux / macOS**
```bash
cd src
javac -cp ".:/path/to/mysql-connector-java.jar" Tourism_Management_System/Main/Main.java
```
**Windows (Command Prompt)**
```bash
cd src
javac -cp ".;C:\path\to\mysql-connector-java.jar" Tourism_Management_System\Main\Main.java
```
💡 **Tip**: Put the JDBC driver in a lib/ folder inside your project and use:
***Linux/macOS:***
```bash 
-cp ".:lib/mysql-connector-java.jar"
```
***Windows:***
```bash 
-cp ".;lib\mysql-connector-java.jar"
```

### 2️⃣ Run the Application
***Linux / macOS***
```bash
java -cp ".:/path/to/mysql-connector-java.jar" src.Tourism_Management_System.Main.Main
```
***Windows (Command Prompt)***
```bash
java -cp ".;C:\path\to\mysql-connector-java.jar" src.Tourism_Management_System.Main.Main
```

## 📌 Future Enhancements
- 🌐 Add a web interface using HTML/CSS/JavaScript
- 💳 Integrate payment gateway support
- 🔄 Real-time booking status updates

## 📝 Usage
When you run the application, you will see the main menu where you can log in or sign up.
Sample Credentials (from tourism_db.sql):

### Admin

```bash
makefile
Username: admin1
Password: adminpass
```

### Travel Agent
```bash
makefile
Username: agentli
Password: agentpass
```

### Customer
```bash
makefile
Username: johndoe
Password: custpass
```
-You can also use the signup option to create new users for any of the roles.

----
## 👨‍💻 Developed By
- -> **Dhruvil Thummar**

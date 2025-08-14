-- Database: `tourism_db`

--------------------------------------------------------

-- Table structure for table `admins`


CREATE TABLE `admins` (
  `admin_id` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `admin_name` varchar(255) DEFAULT NULL,
  `admin_phone_no` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- Dumping data for table `admins`

INSERT INTO `admins` (`admin_id`, `username`, `password`, `admin_name`, `admin_phone_no`) VALUES
('ADMIN001', 'admin1', 'adminpass', 'Alice Admin', '9876543210'),
('ADMIN002', 'superadmin', 'securepass', 'Bob Super', '9988776655');

--------------------------------------------------------


-- Table structure for table `bookings`

CREATE TABLE `bookings` (
  `booking_id` varchar(255) NOT NULL,
  `tour_id` varchar(255) NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  `agent_id` varchar(255) DEFAULT NULL,
  `booking_date` date NOT NULL,
  `status` varchar(50) DEFAULT 'Confirmed'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table `bookings`

INSERT INTO `bookings` (`booking_id`, `tour_id`, `customer_id`, `agent_id`, `booking_date`, `status`) VALUES
('BOOKING001', 'TOUR001', 'CUST001', NULL, '2025-08-01', 'Confirmed'),
('BOOKING002', 'TOUR002', 'CUST002', 'AGENT001', '2025-08-05', 'Confirmed'),
('BOOKING003', 'TOUR001', 'CUST001', NULL, '2025-08-06', 'Pending');

--------------------------------------------------------

-- Table structure for table `customers`

CREATE TABLE `customers` (
  `customer_id` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `customer_phone_no` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table `customers`

INSERT INTO `customers` (`customer_id`, `username`, `password`, `customer_name`, `customer_phone_no`, `email`) VALUES
('CUST001', 'johndoe', 'custpass', 'John Doe', '7654321098', 'john.doe@example.com'),
('CUST002', 'janedoe', 'custpass', 'Jane Doe', '7766554433', 'jane.doe@example.com');

--------------------------------------------------------

-- Table structure for table `tour`

CREATE TABLE `tour` (
  `tour_id` varchar(255) NOT NULL,
  `tour_name` varchar(255) DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `duration_in_days` int(11) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `description` text DEFAULT NULL,
  `max_group_size` int(11) DEFAULT NULL,
  `available_slots` int(11) DEFAULT NULL,
  `available_sites` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table `tour`

INSERT INTO `tour` (`tour_id`, `tour_name`, `destination`, `duration_in_days`, `price`, `description`, `max_group_size`, `available_slots`, `available_sites`) VALUES
('TOUR001', 'Himalayan Trek', 'Nepal', 7, 1200, 'An adventurous trek through the Himalayas.', 10, 8, 'Everest Base Camp,Lukla,Namche Bazaar'),
('TOUR002', 'Parisian Romance', 'France', 4, 850.5, 'Romantic getaway to the city of love.', 8, 6, 'Eiffel Tower,Louvre Museum,Notre Dame Cathedral'),
('TOUR003', 'Desert Safari', 'Dubai', 3, 450, 'Exciting desert safari experience.', 15, 15, 'Desert Dunes,Camel Ride,Belly Dance Show');

--------------------------------------------------------

-- Table structure for table `travelagents`

CREATE TABLE `travelagents` (
  `agent_id` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `agent_name` varchar(255) DEFAULT NULL,
  `agent_phone_no` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table `travelagents`

INSERT INTO `travelagents` (`agent_id`, `username`, `password`, `agent_name`, `agent_phone_no`) VALUES
('AGENT001', 'agentli', 'agentpass', 'Li Travel', '8765432109'),
('AGENT002', 'wanderlust', 'travelpass', 'Wanderlust Tours', '8877665544');

-- Indexes for dumped tables

-- Indexes for table `admins`

ALTER TABLE `admins`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `username` (`username`);

-- Indexes for table `bookings`

ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `tour_id` (`tour_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `agent_id` (`agent_id`);


ALTER TABLE Bookings MODIFY booking_id INT AUTO_INCREMENT;


-- Indexes for table `customers`

ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `username` (`username`);

-- Indexes for table `tour`

ALTER TABLE `tour`
  ADD PRIMARY KEY (`tour_id`);

-- Indexes for table `travelagents`

ALTER TABLE `travelagents`
  ADD PRIMARY KEY (`agent_id`),
  ADD UNIQUE KEY `username` (`username`);

-- Constraints for dumped tables

-- Constraints for table `bookings`

ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`tour_id`) REFERENCES `tour` (`tour_id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  ADD CONSTRAINT `bookings_ibfk_3` FOREIGN KEY (`agent_id`) REFERENCES `travelagents` (`agent_id`);
COMMIT;

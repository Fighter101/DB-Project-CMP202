DROP DATABASE IF EXISTS Restaurant;

CREATE DATABASE Restaurant;

USE Restaurant;

CREATE TABLE Assets
(
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Name VARCHAR(30) UNIQUE NOT NULL,
  Type ENUM('Branch', 'Warehouse') NOT NULL,
  Address VARCHAR(100) NOT NULL
);

CREATE TABLE AdditionalExpenses
(
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Name VARCHAR(30) NOT NULL,
  Date DATE NOT NULL,
  Value FLOAT NOT NULL,
  AssetID INT,
  FOREIGN KEY(AssetID) REFERENCES Assets(ID) ON DELETE SET NULL
);

CREATE TABLE Records
(
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Date DATE NOT NULL,
  Profits FLOAT NOT NULL DEFAULT 0,
  Expenses FLOAT NOT NULL DEFAULT 0,
  AssetID INT,
  FOREIGN KEY(AssetID) REFERENCES Assets(ID) ON DELETE SET NULL,
  CONSTRAINT RecordConstraint UNIQUE(Date, AssetID)
);

CREATE TABLE Orders
(
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Status ENUM('Ordered', 'Cooked', 'Served') NOT NULL,
  Tax FLOAT NOT NULL DEFAULT 0,
  AssetID INT,
  RecordID INT NOT NULL,
  FOREIGN KEY(AssetID) REFERENCES Assets(ID) ON DELETE SET NULL,
  FOREIGN KEY(RecordID) REFERENCES Records(ID)
);

CREATE TABLE RawMaterials
(
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Name VARCHAR(30) NOT NULL,
  Cost FLOAT NOT NULL,
  AssetID INT,
  FOREIGN KEY(AssetID) REFERENCES Assets(ID) ON DELETE SET NULL
);

CREATE TABLE Patches
(
  RawMaterialID INT,
  ExpiryDate DATE,
  PRIMARY KEY(RawMaterialID, ExpiryDate),
  FOREIGN KEY(RawMaterialID) REFERENCES RawMaterials(ID) ON DELETE CASCADE
);

CREATE TABLE Meals
(
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Description TEXT ,
  Name VARCHAR(30) UNIQUE NOT NULL,
  Price FLOAT NOT NULL
);

CREATE TABLE Recipes
(
  RawMaterialID INT,
  MealID INT,
  Amount FLOAT NOT NULL,
  PRIMARY KEY(RawMaterialID, MealID),
  FOREIGN KEY(RawMaterialID) REFERENCES RawMaterials(ID) ON DELETE CASCADE,
  FOREIGN KEY(MealID) REFERENCES Meals(ID) ON DELETE CASCADE
);

CREATE TABLE OrderComponents
(
  OrderID INT,
  MealID INT,
  Amount FLOAT NOT NULL,
  PRIMARY KEY(OrderID, MealID),
  FOREIGN KEY(OrderID) REFERENCES Orders(ID) ON DELETE CASCADE,
  FOREIGN KEY(MealID) REFERENCES Meals(ID) ON DELETE CASCADE
);

CREATE TABLE Clients
(
  PhoneNo CHAR(11) PRIMARY KEY,
  Name VARCHAR(30),
  Password VARCHAR(20),
  Address VARCHAR(100)
);

CREATE TABLE DeliveryOrders
(
  ID INT PRIMARY KEY,
  FeedBack TEXT,
  ClientPhoneNo CHAR(11),
  FOREIGN KEY(ID) REFERENCES Orders(ID) ON DELETE CASCADE,
  FOREIGN KEY(ClientPhoneNo) REFERENCES Clients(PhoneNo) ON DELETE SET NULL
);

CREATE TABLE Employees
(
  ID CHAR(14) PRIMARY KEY,
  Name VARCHAR(30),
  JobTitle ENUM('Chef', 'Cashier', 'Waiter', 'OrderDelivery', 'IT'),
  Salary FLOAT NOT NULL DEFAULT 1250,
  Supervisor CHAR(14),
  AssetID INT,
  FOREIGN KEY(Supervisor) REFERENCES Employees(ID) ON DELETE SET NULL,
  FOREIGN KEY(AssetID) REFERENCES Assets(ID) ON DELETE SET NULL
);

CREATE TABLE Vehicles
(
  MotorNo CHAR(19) PRIMARY KEY,
  Status ENUM('Available', 'Busy', 'Broken'),
  LicenceNo CHAR(7) NOT NULL UNIQUE,
  LicenceExpiryDate DATE NOT NULL
);

CREATE TABLE Delivery
(
  DeliveryOrderID INT PRIMARY KEY,
  EmployeeID CHAR(14),
  VehicleMotorNo CHAR(19),
  FOREIGN KEY(DeliveryOrderID) REFERENCES DeliveryOrders(ID) ON DELETE CASCADE,
  FOREIGN KEY(EmployeeID) REFERENCES Employees(ID) ON DELETE SET NULL,
  FOREIGN KEY(VehicleMotorNo) REFERENCES Vehicles(MotorNo) ON DELETE SET NULL
);
DROP procedure IF EXISTS `new_procedure`;

DELIMITER $$
USE `Restaurant`$$
CREATE PROCEDURE `new_procedure` (OUT order_id INT , IN asset_id INT)
  BEGIN
    DECLARE record_ID INT;
    DECLARE is_found BOOL DEFAULT FALSE;
    SELECT exists (SELECT * FROM Records where Records.Date = curdate()) INTO is_found;
    IF is_found = 0
    THEN
      INSERT INTO Records ( Date , AssetID) VALUES (CURDATE() , asset_id);
    END IF;
    SELECT ID FROM Records WHERE Date = CURDATE() INTO record_id;
    INSERT INTO Orders (Status , AssetID , RecordID) VALUES ('Ordered' , asset_id , record_id);
    SET order_id = LAST_INSERT_ID();
  END$$
DELIMITER ;
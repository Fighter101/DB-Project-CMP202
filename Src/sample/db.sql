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
  Name VARCHAR(30) PRIMARY KEY
);

CREATE TABLE Patches
(
  RawMaterialName VARCHAR(30),
  ExpiryDate DATE,
  Amount FLOAT NOT NULL,
  Cost FLOAT NOT NULL,
  AssetID INT,
  PRIMARY KEY(RawMaterialName, ExpiryDate, AssetID),
  FOREIGN KEY(RawMaterialName) REFERENCES RawMaterials(Name) ON DELETE CASCADE,
  FOREIGN KEY(AssetID) REFERENCES Assets(ID) ON DELETE CASCADE
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
  RawMaterialName VARCHAR(30),
  MealID INT,
  Amount FLOAT NOT NULL,
  PRIMARY KEY(RawMaterialName, MealID),
  FOREIGN KEY(RawMaterialName) REFERENCES RawMaterials(Name) ON DELETE CASCADE,
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
DELIMITER $$
CREATE PROCEDURE `add_order` (OUT order_id INT , IN asset_id INT)
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

DELIMITER $$
USE `Restaurant`$$
CREATE PROCEDURE `deliever_order` (IN employee_id VARCHAR(30) , IN vechile_license CHAR(7) , IN order_id INT )
  BEGIN

    INSERT INTO Delivery (DeliveryOrderID , EmployeeID , VehicleMotorNo) VALUES  (order_id ,  employee_id , (SELECT MotorNo FROM Vehicles WHERE LicenceNo = vechile_license));
    UPDATE Vehicles SET Status = 'Busy' WHERE LicenceNo = vechile_license;
  END$$
DELIMITER ;

CREATE PROCEDURE `close_order`(IN order_id INT , IN asset_ID INT)
  BEGIN
    DECLARE is_delievry BOOLEAN DEFAULT FALSE;
    DECLARE order_price INT DEFAULT 0;
    DECLARE meal_id INT DEFAULT 0;
    DECLARE meal_amount FLOAT DEFAULT 0;
    DECLARE meal_counter INT DEFAULT 0;
    DECLARE rawmaterial_name VARCHAR(30);
    DECLARE rawmaterial_amount FLOAT;
    DECLARE rawmaterial_counter INT DEFAULT 0;
    DECLARE rawmaterial_total_amount FLOAT DEFAULT 0;
    DECLARE patch_counter INT DEFAULT 0;
    DECLARE current_patch_amount FLOAT DEFAULT 0 ;
    DECLARE current_patch_expirydate DATE;
    DECLARE current_patch_cost FLOAT;
    SELECT EXISTS (SELECT * FROM DeliveryOrders WHERE DeliveryOrders.ID = order_id) INTO is_delievry;
    IF is_delievry = 1 THEN
      BEGIN
        UPDATE Vehicles,Delivery SET Vehicles.Status = 'Available' WHERE Delivery.DeliveryOrderID = order_id AND Vehicles.MotorNo = Delivery.VehicleMotorNo;
      END ;
    END IF;
    WHILE meal_counter < (SELECT COUNT(*) FROM OrderComponents WHERE order_id = OrderComponents.OrderID) DO
      BEGIN
        SELECT OrderComponents.MealID , OrderComponents.Amount INTO meal_id , meal_amount FROM OrderComponents WHERE OrderID = order_id  LIMIT meal_counter , 1;
        WHILE rawmaterial_counter < (SELECT COUNT(*) FROM Recipes WHERE Recipes.MealID = meal_id) DO
          BEGIN
            SELECT Recipes.RawMaterialName , Recipes.Amount INTO rawmaterial_name , rawmaterial_amount FROM Recipes WHERE MealID = meal_id  LIMIT rawmaterial_counter , 1;
            SET rawmaterial_total_amount = meal_amount * rawmaterial_amount;
            WHILE rawmaterial_total_amount > 0 /*AND patch_counter > (SELECT COUNT(*) FROM Patches WHERE Patches.RawMaterialName= rawmaterial_name AND AssetID = asset_ID)*/ DO
              BEGIN
                SELECT Patches.Amount , Patches.ExpiryDate , Patches.Cost INTO current_patch_amount , current_patch_expirydate , current_patch_cost FROM Patches WHERE Patches.RawMaterialName = rawmaterial_name AND Patches.AssetID = AssetID  ORDER BY ExpiryDate ASC LIMIT patch_counter , 1 ;
                IF rawmaterial_total_amount > current_patch_amount THEN
                  BEGIN
                    SET order_price = order_price + (current_patch_amount * current_patch_cost);
                    SET rawmaterial_total_amount = rawmaterial_total_amount -current_patch_amount;
                    DELETE FROM Patches WHERE Patches.RawMaterialName = rawmaterial_name AND Patches.AssetID = asset_ID AND Patches.ExpiryDate = current_patch_expirydate;
                  END;
                ELSE
                  BEGIN
                    SET order_price = order_price + (rawmaterial_total_amount  * current_patch_amount);
                    UPDATE Patches SET Amount = Amount-rawmaterial_total_amount WHERE Patches.RawMaterialName = rawmaterial_name AND Patches.ExpiryDate = current_patch_expirydate AND Patches.AssetID = asset_ID;
                    SET rawmaterial_total_amount = 0;
                  END;
                END IF;
                SET patch_counter = patch_counter+1;
              END ;
            END WHILE;
            SET rawmaterial_counter = rawmaterial_counter+1;
          END;
        END WHILE;
        SET meal_counter = meal_counter+1;
      END;
    END WHILE;
    UPDATE  Records SET Profits = (SELECT SUM(Meals.Price) FROM Meals , OrderComponents WHERE  OrderComponents.OrderID = order_id AND Meals.ID = OrderComponents.MealID) WHERE Records.Date = CURDATE() AND Records.AssetID = asset_ID;
    UPDATE Records SET Expenses = order_price WHERE Records.Date = CURDATE() AND Records.AssetID = asset_ID;
  END

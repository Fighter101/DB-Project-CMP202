package sample;

/**
 * Created by Hassan on 12/5/2016.
 * This class will generate random data and insert it into our DB for test puroses
 */

import java.lang.*;
import java.util.*;
import java.time.*;
public class Generator {
    private static final char[] symbols;
    private static final char[] numbers;
    private Connector connector;
    private Random random = new Random();
    private String assetTypes [] ={"Branch" , "Warehouse"};
    private String jobTitles[]={"Chef", "Cashier", "Waiter", "OrderDelivery", "IT"};
    private  String [] superSSN;
    private Integer numAssets , numSuper,numEmployees,numMeals,numRawMaterials,numRecipes,numPatches,numClients;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        numbers=tmp.toString().toCharArray();
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        tmp.append(" -.,");
        symbols = tmp.toString().toCharArray();

    }

    private String generateString(int length) {
        char[] buf = new char[length];
        for (int i = 0; i < length; ++i) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
    private String generateNumString(int length) {
        char[] buf = new char[length];
        for (int i = 0; i < length; ++i) {
            buf[i] = numbers[random.nextInt(numbers.length)];
        }
        return new String(buf);
    }

    public Connector getConnector() {
        return connector;
    }

    Generator() {
        connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        generateAssets(10);
        generateEmployees(2,8);
        generateMaterials(10);
        generateMeals(10);
        generateRecipes(10);
        generateClients(10);
        generatePatches(5);
    }
    private void  generateAssets(int r_numAssets)
    {
        numAssets=r_numAssets;
        for (int i=0 ;i<numAssets;++i) {
            Pair name = new Pair("Name", generateString(30));
            Pair type = new Pair("Type", assetTypes[random.nextInt(2)]);
            Pair address = new Pair("Address", generateString(100));
            connector.insert("Assets", name, type, address);
        }
    }
    private void generateEmployees (int r_numSuper , int r_numEmployees)
    {
        numSuper=r_numSuper;
        numEmployees=r_numEmployees;
        superSSN = new String[numSuper];
        //query will be like  INSERT INTO Employees (ID,Name,JOB Title , Salary ,supervisor, AssetID )
        /////////Generating Super SSN//////////////////////////
        for(int i=0 ; i<numSuper ;++i)
        {
            superSSN [i]= generateNumString(14);
            Pair ID = new Pair("ID",superSSN[i]);
            Pair name = new Pair("Name" , generateString(30));
            Pair jobTitle = new Pair("JobTitle" , jobTitles[random.nextInt(jobTitles.length)]);
            Pair salary = new Pair("Salary" , generateNumString(5));
            Pair assetID = new Pair ("AssetID" , (new Integer(random.nextInt(numAssets)+1)).toString() );
            connector.insert("Employees", ID, name, jobTitle , salary , assetID);
        }
        for(int i=0 ; i<numEmployees ;++i)
        {
            String SSN ;
            do{
                SSN = generateNumString(14);
            }
            while (Arrays.asList(superSSN).contains(SSN));
            Pair ID = new Pair("ID",generateNumString(14));
            Pair name = new Pair("Name" , generateString(30));
            Pair jobTitle = new Pair("JobTitle" , jobTitles[random.nextInt(jobTitles.length)]);
            Pair salary = new Pair("Salary" , generateNumString(5));
            Pair supervisor = new Pair ("Supervisor" , superSSN[random.nextInt(superSSN.length)]);
            Pair assetID = new Pair ("AssetID" , (new Integer(random.nextInt(numAssets)+1)).toString() );
            connector.insert("Employees", ID, name, jobTitle , salary , supervisor, assetID);
        }

    }

    private void generateMeals(int r_numMeals)
    {
        numMeals = r_numMeals;
        for (int i=0 ;i<numMeals;++i) {
            Pair Name = new Pair("Name", generateString(30));
            Pair Price = new Pair("Price", Float.toString((random.nextFloat() * 1000000) + 1));
            Pair description = new Pair("Description" , generateString(100));
             connector.insert("Meals", Name, description, Price);
        }
    }
    private void generateMaterials(int r_numMaterials)
    {
        numRawMaterials = r_numMaterials;
        for (int i = 0;i<numRawMaterials ;++i)
        {
            Pair Name = new Pair("Name",generateString(30));
            Pair Cost = new Pair( "Cost",Float.toString((random.nextFloat()*100000)+1));
            Pair AssetID = new Pair("AssetID", Integer.toString(random.nextInt(10) + 1));
            connector.insert("RawMaterials" , Name , Cost , AssetID);
        }
    }
    private void generateRecipes(int r_numRecipes)
    {
        numRecipes = r_numRecipes;
        for(int i=0; i<numRecipes; ++i) {
            Integer matID , melID;
                do {
                    matID = random.nextInt(numRawMaterials) + 1;
                    melID = random.nextInt(numMeals) + 1;
                }
                while (connector.exists("Recipes", "RawMaterialID = " + matID + " AND MealID = " + melID));
            Pair rawMaterialID = new Pair("RawMaterialID", matID.toString());
            Pair mealID = new Pair("MealID", melID.toString());
            Pair amount = new Pair("Amount", Float.toString(random.nextFloat() * 100 + 1));
            connector.insert("Recipes", rawMaterialID, mealID, amount);
        }
        }
    private void generateClients (int r_Clients)
    {
        //query will be like INSERT  INTO Clients (PhoneNo , Name , Password , Address) VALUES (' ' , ' ' , ' ',' ');
        numClients = r_Clients;
        for (int i=0; i < numClients ; ++i) {
            Pair phoneNo = new Pair("PhoneNo", generateNumString(11));
            Pair name = new Pair("Name", generateString(30));
            Pair password = new Pair("Password", generateString(20));
            Pair address = new Pair("Address", generateString(100));
            connector.insert("Clients" , phoneNo , name , password , address);
        }

    }
    private void generatePatches (int r_Patches)
    {
        //query will be like INSERT INTO Patches (RawMaterialID , ExpiryDate) VALUES ( ' ' , ' ')
        LocalDate localDate =  LocalDate.now();
        numPatches =r_Patches;
        for (int i=1 ; i<numRawMaterials ; ++i) {
            for (int j  = 0; j < numPatches; ++j) {
                Pair rawMaterialID = new Pair("RawMaterialID" , String.valueOf(i));
                LocalDate expDate ;
                do {
                     expDate = localDate.plusMonths(random.nextInt(13)).plusDays(random.nextInt(8));
                } while (connector.exists("Patches" , "RawMaterialID = '" + i + "' AND ExpiryDate = '"+ expDate.toString()+"'"));
                Pair expiryDate = new Pair("ExpiryDate", expDate.toString());
                connector.insert("Patches",rawMaterialID,expiryDate);
            }
        }
    }
}

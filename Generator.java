package sample;

/**
 * Created by Hassan on 12/5/2016.
 * This class will generate random data and insert it into our DB for test puroses
 */
import javafx.util.converter.IntegerStringConverter;

import javax.lang.model.element.Name;
import javax.sql.rowset.spi.SyncFactoryException;
import java.lang.*;
import java.util.*;
public class Generator {
    private static final char[] symbols;
    private static final char[] numbers;
    private Connector connector;
    Random random = new Random();
    String assetTypes [] ={"Branch" , "Warehouse"};
    String jobTitles[]={"Chef", "Cashier", "Waiter", "OrderDelivery", "IT"};
    private  String [] superSSN;
    private Integer numAssets = new Integer(0);
    private Integer numSuper,numEmployees,numMeals,numRawMaterials,numRecipes;

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

    Generator() {
         connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        // query will be like   INSERT INTO Assets (Name , Type , Adress )
        //generateAssets(10);
        //generateEmployees(10,1000);
        //generateMaterials(1000);
        //generateMeals(1000);
        genrateRecipes(1000);

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
            connector.insert("Meals", Name, Price);
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
    private void genrateRecipes(int r_numRecipes)
    {
        numRecipes = r_numRecipes;
        for(int i=0; i<1000; ++i) {
            Integer matID = new Integer(0), melID = new Integer(0);
            try {
                do {
                    matID = random.nextInt(numRawMaterials) + 1;
                    melID = random.nextInt(numMeals) + 1;
                }
                while (connector.viewTable("Recipes", "RawMaterialID = " + matID + " AND MealID = " + melID, "*").first());
            } catch (java.sql.SQLException ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.getSQLState());
            }
            Pair rawMaterialID = new Pair("RawMaterialID", matID.toString());
            Pair mealID = new Pair("MealID", melID.toString());
            Pair amount = new Pair("Amount", Float.toString(random.nextFloat() * 100 + 1));
            connector.insert("Recipes", rawMaterialID, mealID, amount);
        }
        }
}

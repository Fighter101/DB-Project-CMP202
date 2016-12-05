package sample;

/**
 * Created by Hassan on 12/5/2016.
 * This class will generate random data and insert it into our DB for test puroses
 */
import javafx.util.converter.IntegerStringConverter;

import java.lang.*;
import java.util.*;
public class Generator {
    private static final char[] symbols;
    private static final char[] numbers;
    Random random = new Random();
    String assetTypes [] ={"Branch" , "Warehouse"};
    String jobTitles[]={"Chef", "Cashier", "Waiter", "OrderDelivery", "IT"};
    private static final String [] superSSN = new String [10];

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
        Connector connector = new Connector("Restaurant", "127.0.0.1", "57356", "root", "AssAssin_108");
        // query will be like   INSERT INTO Assets (Name , Type , Adress )
        for (int i=0 ;i<10;++i) {
            Pair name = new Pair("Name", generateString(30));
            Pair type = new Pair("Type", assetTypes[random.nextInt(2)]);
            Pair address = new Pair("Address", generateString(100));
            connector.insert("Assets", name, type, address);
        }
        //query will be like  INSERT INTO Employees (ID,Name,JOB Title , Salary ,supervisor, AssetID )
        /////////Generating Super SSN//////////////////////////
        for(int i=0 ; i<10 ;++i)
        {
            superSSN [i]= generateNumString(14);
            Pair ID = new Pair("ID",superSSN[i]);
            Pair name = new Pair("Name" , generateString(30));
            Pair jobTitle = new Pair("JobTitle" , jobTitles[random.nextInt(jobTitles.length)]);
            Pair salary = new Pair("Salary" , generateNumString(5));
            Pair assetID = new Pair ("AssetID" , (new Integer(random.nextInt(9)+1)).toString() );
            connector.insert("Employees", ID, name, jobTitle , salary , assetID);
        }
        for(int i=0 ; i<1000 ;++i)
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
            Pair assetID = new Pair ("AssetID" , (new Integer(random.nextInt(9)+1)).toString() );
            connector.insert("Employees", ID, name, jobTitle , salary , supervisor, assetID);
        }
        //////////////////////////////////////////////////


    }
}

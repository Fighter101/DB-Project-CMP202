package sample;

/*
 * Created by Hassan on 12/5/2016
 * */

import com.sun.xml.internal.ws.api.model.MEP;
import java.io.*;
import java.lang.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Connector {
    private Connection connection;
    private FileWriter out;
    private PrintWriter writer;
    private void updateStatement (String query)
    {

        try (Statement statement =connection.createStatement()) {
            statement.executeUpdate(query);
        }
        catch (java.sql.SQLException ex){
            System.out.println("Error in Query"+query);
            System.out.println("SQL State: "+ex.getSQLState());
            ex.printStackTrace();
        }
    }
    public Connector (String r_dbName,String r_host,String r_port,String userName, String passWord)
    {
        try{
            out = new FileWriter("Inserts.sql",true);
        }
        catch (java.io.IOException ex){
            System.out.println(ex.getMessage());
        }
        writer = new PrintWriter(out);
        connection = null;
        final  String driverName = "jdbc:mysql://";
        final String connectionURL =driverName+r_host+":"+r_port+"/"+r_dbName+"?&useSSL=false";
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException ex){
            System.out.println("Error : Unable to load driver class");
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            connection = DriverManager.getConnection(connectionURL, userName, passWord);
        }
        catch (java.sql.SQLException ex) {
            System.out.println("Error : Unable to get connection");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(connection!=null)
            try{
                connection.close();
            }
            catch (java.sql.SQLException ex) {
                System.out.println("Error : can't close the connection");
                ex.printStackTrace();
                System.exit(1);
            }
        writer.close();
        out.close();
    }
    boolean exists(String table, String condition)
    {
        String query="SELECT * ";
        query+=" FROM "+ table+" WHERE "+condition+" ;";
        try (Statement statement =connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.first();
        }
        catch (java.sql.SQLException ex){
            System.out.println("Error in Query "+query);
            System.out.println("SQL State: "+ex.getSQLState());
            ex.printStackTrace();
        }
        return false;
    }
    void insert(String table , Pair ... fields)
    {
        String query = "INSERT INTO " +table + " ( ";
        for(Pair u :fields)
        {
            query+=u.getFieldName();
            if(u!=fields[fields.length-1])
                query+=" , ";
        }
        query+=" ) VALUES ( ";
        for(Pair u :fields)
        {
            query+="'"+u.getValue()+"'";
            if(u!=fields[fields.length-1])
                query+=" , ";
        }
        query+=" ) "+" ;";
        writer.println(query);
        updateStatement(query);

    }
    void update (String table ,String condition ,  Pair ... fields)
    {
        String query = "UPDATE "+table +"SET ";
        for (Pair u : fields)
        {
            query+=u.getFieldName()+" = '"+u.getValue()+"'";
            if(u!= fields[fields.length-1])
                query+=" , ";
        }
        query += "WHERE "+condition+" ;";
        updateStatement(query);

    }
    ResultSet doSomething (String query)
    {
        try (Statement statement =connection.createStatement()) {
            if( statement.execute(query)) {
                return statement.getResultSet();
            }
        }
        catch (java.sql.SQLException ex){
            System.out.println("Error in Query "+query);
            System.out.println("SQL State: "+ex.getSQLState());
            ex.printStackTrace();
        }
        return null;
    }
    List<Meal> getMeals ()
    {
        List<Meal> meals = new ArrayList<Meal>();
        String query = "Select Name , Description , Price FROM Meals";
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet=statement.executeQuery(query);
            while (resultSet.next()){
                meals.add(new Meal(resultSet.getString("Name") , resultSet.getString("Description") , resultSet.getFloat("Price")));
            }
        }
        catch (java.sql.SQLException ex){
            System.out.println("Error in Query "+query);
            System.out.println("SQL State: "+ex.getSQLState());
            ex.printStackTrace();
        }
        return meals;
    }
    void addOrder (Integer orderID , Integer assetID , Integer recordID){
        try (CallableStatement statement = connection.prepareCall("{CALL add_order(?,?,?)}")){
            statement.registerOutParameter(1,Types.INTEGER);
            statement.setInt(2 , assetID);
            statement.registerOutParameter(3 , Types.INTEGER);
            statement.execute();
            orderID = statement.getInt(1);
            recordID = statement.getInt(3);
            System.out.println(orderID);
            System.out.println(recordID);
        }
        catch (java.sql.SQLException ex){
            System.out.print("SQL State : "+ ex.getSQLState());
            System.out.print(ex.getMessage());
        }
    }

}

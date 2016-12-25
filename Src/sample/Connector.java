package sample;

/*
 * Created by Hassan on 12/5/2016
 * */

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

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
    void doSomething (String query)
    {
        try (Statement statement =connection.createStatement()) {
            statement.execute(query);
        }
        catch (java.sql.SQLException ex){
            System.out.println("Error in Query "+query);
            System.out.println("SQL State: "+ex.getSQLState());
            ex.printStackTrace();
        }
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
    void addOrder(Integer assetID, Order order){
        try (CallableStatement statement = connection.prepareCall("{CALL add_order(?,?)}")){
            statement.registerOutParameter("order_id",Types.INTEGER);
            statement.setInt("asset_id" , assetID);
            statement.execute();
            order.setID(statement.getInt("order_id"));
        }
        catch (java.sql.SQLException ex){
            System.out.print("SQL State : "+ ex.getSQLState());
            System.out.print(ex.getMessage());
        }
    }
    void addMealsToOrder (Order order){
        List<MealPair> Meals = order.getMeals();
        for (MealPair Meal :Meals) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO OrderComponents (OrderID , MealID , Amount) VALUES (? , (SELECT ID FROM Meals WHERE Name = ?) , ?)")) {
                statement.setInt(1, order.getID());
                statement.setString(2, Meal.getMealName() );
                statement.setInt(3 , Meal.getMealAmount());
                statement.execute();
            } catch (java.sql.SQLException ex) {
                System.out.print("SQL State : " + ex.getSQLState());
                System.out.print(ex.getMessage());
            }
        }
    }
    Integer getMealPrice (String mealName){
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("SELECT Price FROM Meals WHERE Name = '"+mealName + "';");
            resultSet.next();
            return resultSet.getInt(1);
        }
        catch (java.sql.SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    List<DelievryOrder> getdelievryorders(Integer assetID){
        List<DelievryOrder> delievryOrders = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
           ResultSet resultSet =  statement.executeQuery("SELECT  Orders.ID , Clients.Address , Clients.PhoneNo , Clients.Name FROM Clients , DeliveryOrders , Orders WHERE Orders.ID = DeliveryOrders.ID AND Orders.Status = 'Cooked' AND DeliveryOrders.ClientPhoneNo = Clients.PhoneNo AND Orders.AssetID = "+assetID.toString()+";");
            while(resultSet.next()){
               DelievryOrder delievryOrder = new DelievryOrder(assetID);
               delievryOrder.setID(resultSet.getInt("ID"));
               delievryOrder.setStatus(Order.Status.Cooked);
               delievryOrder.setAddress(resultSet.getString("Address"));
               delievryOrder.setClientName(resultSet.getString("Name"));
               delievryOrder.setPhoneNumber(resultSet.getInt("PhoneNo"));
               delievryOrders.add(delievryOrder);
            }
            return delievryOrders;
        }
        catch (java.sql.SQLException ex){
            System.out.println("SQL State : " + ex.getSQLState());
            System.out.println(ex.getMessage());
        }
        return null;
    }
    List<Employee> getEmployees (Integer assetID){
        List<Employee> employees = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("SELECT Name , ID FROM Employees WHERE JobTitle = 'OrderDelivery' AND Supervisor IS NOT null AND AssetID = "+assetID.toString()+";");
            while (resultSet.next()){
                Employee employee = new Employee();
                employee.setID(resultSet.getString("ID"));
                employee.setName(resultSet.getString("Name"));
                employees.add(employee);
            }
            return employees;
        }
        catch (java.sql.SQLException ex){
            System.out.println("SQL State :" + ex.getSQLState() +"\n"+ex.getMessage());
        }
        return null;
    }
    List<String> getVechiles (){
        List<String> vechiles = new ArrayList<>();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("SELECT LicenceNo FROM Restaurant.Vehicles where Status = 'Available'");
            while (resultSet.next()){
                vechiles.add(resultSet.getString("LicenceNo"));
            }
            return vechiles;
        }
        catch (java.sql.SQLException ex) {
            System.out.println("SQL State :" + ex.getSQLState() +"\n"+ex.getMessage());
        }
        return null;
    }
    void deliverOrder(DelievryOrder order , Employee employee , String vechileLicence){

        try(CallableStatement statement = connection.prepareCall("{ Call deliever_order (?,?,?)}")) {
            statement.setString("employee_id" , employee.getID());
            statement.setString("vechile_license" , vechileLicence);
            statement.setInt("order_id" , order.getID());
            statement.execute();

    }
    catch (java.sql.SQLException ex){
        System.out.println("SQL State :" + ex.getSQLState() +"\n"+ex.getMessage());
    }

    }

}

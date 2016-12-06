package sample;

/**
 * Created by Hassan on 12/5/2016
 * */
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.lang.*;
import java.sql.*;
public class Connector {
    Connection connection;
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
    }
    ResultSet viewTable (String table ,String condition ,  String ... items)
    {
        String query="SELECT ";
        for (String u : items ) {
            query += u;
            if(u!=items[items.length-1])
                query+=" ,";
        }
        query+=" FROM "+ table+" WHERE "+condition+" ;";
        try (Statement statement =connection.createStatement()) {
            return statement.executeQuery(query);
        }
        catch (java.sql.SQLException ex){
            System.out.println("Error in Query "+query);
            System.out.println("SQL State: "+ex.getSQLState());
            ex.printStackTrace();
        }
        return null;
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
        updateStatement(query);
    }
    void update (String table ,String condition ,  Pair ... fields)
    {
        String query = "UPDATE "+table +"SET ";
        for (Pair u : fields)
        {
            query+=u.getFieldName()+" = "+u.getValue();
            if(u!= fields[fields.length-1])
                query+=" , ";
        }
        query += "WHERE "+condition+" ;";
        updateStatement(query);
    }
    ResultSet dosomething (String query)
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

}
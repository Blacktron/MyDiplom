package db;

import java.sql.*;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class DBConnectionHandler {

    /**
     * Opens a connection to the database and returns it as instance.
     */
    public static Connection openDatabaseConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "mydb";
        String driver = "com.mysql.jdbc.Driver";
        String dbUsername = "root";
        String dbPassword = "1111";

        try {
            Class.forName(driver).newInstance();
            System.out.println("Opening connection");
            return DriverManager.getConnection(url + dbName, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
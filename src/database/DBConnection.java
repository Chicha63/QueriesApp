package database;

import java.sql.*;

public class DBConnection {
    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    public DBConnection() {
        try{
            String connectionUrl = "jdbc:sqlserver://"
                    + System.getenv("DBNAME") + ";encrypt=false;databaseName=Querries;user="
                    + System.getenv("USER") + ";password="
                    + System.getenv("PASSWORD");
            connection = DriverManager.getConnection(connectionUrl);
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public ResultSet executeQuerry(String querry) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(querry);
        } catch (SQLException exception){
            exception.printStackTrace();
            return null;
        }
    }
}
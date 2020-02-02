package edu.itstep.courseproject;

import static edu.itstep.courseproject.Start.log;
import java.sql.*;

public class Database {

    public static void openDatabase(String DbName, Connection conn) throws SQLException {

        DatabaseMetaData meta = conn.getMetaData();
        log.info("The driver name is " + meta.getDriverName());
        log.info("A new database has been created.");

    }

    public static void createNewDbStatement(String DbName, String statement, Connection conn) throws SQLException {

        Statement stmt;
        stmt = conn.createStatement();
        stmt.execute(statement);
        log.info("Statement: " + statement + " executed");

    }

    public static ResultSet makeDbQuery(String DbName, String query, Connection conn) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        return rs;
    }

    public static String getUserSaltedHashFromDb(String DbName, String username, Connection conn) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        String query = "SELECT PasswordSaltedHash FROM Users WHERE Username='" + username + "'";
        ResultSet rs = stmt.executeQuery(query);
        String saltedHash = null;

        while (rs.next()) {
            saltedHash = rs.getString("PasswordSaltedHash");
        }

        return saltedHash;
    }

    public static int getUserIdFromDb(String DbName, String username, Connection conn) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        String query = "SELECT UserId FROM Users WHERE Username='" + username + "'";
        ResultSet rs = stmt.executeQuery(query);
        int Id = 0;

        while (rs.next()) {
            Id = rs.getInt("UserId");
        }

        return Id;
    }

    public static void addUser(String DbName, String username, String pass, Connection conn) throws SQLException {

        String User = "INSERT INTO Users (Username, PasswordSaltedHash) VALUES ('" + username + "','" + pass + "');";
        createNewDbStatement(DbName, User, conn);
        log.info("User " + username + " added");
    }

    public static void addUserInfo(String DbName, int id, String fname, String lname, String egn, String city, Connection conn) throws SQLException {

        String User = "INSERT INTO UserInfo (UserId, FirstName, LastName, EGN, City) VALUES (" + id + ",'" + fname + "','" + lname + "','" + egn + "','" + city + "');";
        createNewDbStatement(DbName, User, conn);
    }

    public static void getUserInfo(String DbName, String username, Connection conn) throws SQLException {
        
        int id = getUserIdFromDb(DbName, username, conn);

        Statement stmt;
        stmt = conn.createStatement();
        String query = "SELECT * FROM UserInfo WHERE UserId=" + id;
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("UserId: " + rs.getInt("UserId"));
            System.out.println("FirstName: " + rs.getString("FirstName"));
            System.out.println("LastName: " + rs.getString("LastName"));
            System.out.println("EGN: " + rs.getString("EGN"));
            System.out.println("City: " + rs.getString("City"));
        }
    }

    public static void addUserActivityInfo(String DbName, int id, String logged, Connection conn) throws SQLException {

        String Activity = "INSERT INTO UserActivity (UserId, Logged) VALUES (" + id + ",'" + logged + "');";
        createNewDbStatement(DbName, Activity, conn);
    }

}

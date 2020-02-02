package edu.itstep.courseproject;

import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Start {

    static Logger log = LogManager.getLogger(Start.class.getName());

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        Scanner sc = new Scanner(System.in);

        //Database info
        String DbName = "UsersDB.db";
        String url = "jdbc:sqlite:" + Paths.get(".").normalize().toAbsolutePath().toString() + "\\data\\" + DbName;

        //Creating table "Users"
        String CreateTableUsers = "CREATE TABLE IF NOT EXISTS Users "
                + "(UserId INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT,"
                + " Username VARCHAR(30) UNIQUE NOT NULL, "
                + " PasswordSaltedHash binary(128) NOT NULL)";

        //Creating table "UserInfo"
        String CreateTableUserInfo = "CREATE TABLE IF NOT EXISTS UserInfo "
                + "(UserId INTEGER NOT NULL,"
                + " FirstName VARCHAR(30) NOT NULL, "
                + " LastName VARCHAR(30) NOT NULL, "
                + " EGN VARCHAR(26) NOT NULL, "
                + " City NVARCHAR(26) NOT NULL, "
                + " FOREIGN KEY(UserId) REFERENCES User(UserId))";

        //Creating table "UserInfo"
        String CreateTableUserActivity = "CREATE TABLE IF NOT EXISTS UserActivity "
                + "(UserId INTEGER NOT NULL,"
                + " Logged VARCHAR(30) NOT NULL, "
                + " FOREIGN KEY(UserId) REFERENCES User(UserId))";

        //Oppening connection to database
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            log.info("Connection oppened");
            //Open database
            Database.openDatabase(DbName, conn);

            //Creating tables
            Database.createNewDbStatement(DbName, CreateTableUsers, conn);
            Database.createNewDbStatement(DbName, CreateTableUserInfo, conn);
            Database.createNewDbStatement(DbName, CreateTableUserActivity, conn);

            //Inserting users manually
//            Database.addUser(DbName, "Ivan123", PasswordSecurity.getSaltedHash("ivanpass"), conn);
//            Database.addUserInfo(DbName, 1, "Ivan", "Ivanov", "143245", "Sofia", conn);
//            Database.addUser(DbName, "Pesho345", PasswordSecurity.getSaltedHash("peshopass"), conn);
//            Database.addUserInfo(DbName, 2, "Petar", "Petrov", "933442", "Pernik", conn);
//            Database.addUser(DbName, "Kiro567", PasswordSecurity.getSaltedHash("kiropass"), conn);
//            Database.addUserInfo(DbName, 3, "Kiril", "Kirilov", "92945", "Sofia", conn);
//            Database.addUser(DbName, "Stoil532", PasswordSecurity.getSaltedHash("stoilpass"), conn);
//            Database.addUserInfo(DbName, 4, "Stoil", "Stoilov", "534784", "Varna", conn);
//            Database.addUser(DbName, "Gosho756", PasswordSecurity.getSaltedHash("goshopass"), conn);
//            Database.addUserInfo(DbName, 5, "Georgi", "Georgiev", "437879", "Burgas", conn);
            String exit;
            do {
                System.out.println("Choose option from menu:\n1. Register\n2. Login\n3. Exit\n");
                String choice = sc.nextLine();

                switch (choice) {
                    case "1":
                        //Register user 
                        System.out.println("Register new username: ");
                        String user = sc.nextLine();

                        if (user.contains(" ")) {
                            user = user.replace(" ", "");
                            System.out.println("Whitespaces are not allowed, new username is:");
                            System.out.println(user);
                        }

                        System.out.println("Register new password: ");
                        String pass = sc.nextLine();

                        if (pass.contains(" ")) {
                            System.out.println("Whitespaces in password are not allowed!");
                            break;
                        }
                        if (pass.length() < 8) {
                            System.out.println("Password should be at least 8 characters!");
                            break;
                        }

                        Database.addUser(DbName, user, PasswordSecurity.getSaltedHash(pass), conn);
                        int userId = Database.getUserIdFromDb(DbName, user, conn);

                        //Add user info
                        System.out.println("Enter first name: ");
                        String fname = sc.nextLine();

                        System.out.println("Enter last name: ");
                        String lname = sc.nextLine();

                        System.out.println("Enter egn: ");
                        String egn = sc.nextLine();

                        System.out.println("Enter city: ");
                        String city = sc.nextLine();

                        Database.addUserInfo(DbName, userId, fname, lname, egn, city, conn);
                        userId = 0;
                        break;
                    case "2":
                        boolean IsPasswordMatch = false;
                        int attempts = 5;
                        //Password validation
                        do {
                            System.out.println("Enter username: ");
                            String enteredUsername = sc.nextLine();

                            //Check entered password to match from a salted hash in the database
                            System.out.println("Enter password: ");
                            String enteredPassword = sc.nextLine();

                            //Get password salted hash of a user from the database
                            String realPasswordSaltedHash = Database.getUserSaltedHashFromDb(DbName, enteredUsername, conn);

                            if (realPasswordSaltedHash != null) {
                                IsPasswordMatch = PasswordSecurity.check(enteredPassword, realPasswordSaltedHash);
                                if (IsPasswordMatch) {
                                    System.out.println("Access granted");
                                    userId = Database.getUserIdFromDb(DbName, enteredUsername, conn);

                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                    LocalDateTime now = LocalDateTime.now();

                                    Database.addUserActivityInfo(DbName, userId, dtf.format(now), conn);
                                    log.info("Logged: " + dtf.format(now));

                                    Database.getUserInfo(DbName, enteredUsername, conn);
                                } else {
                                    attempts--;
                                    System.out.println("Wrong password! (Remaining attempts: " + attempts + ")");
                                    if (attempts < 1) {
                                        System.out.println("You entered wrong password too many times!");
                                        System.out.println("Blocked for 30 seconds!");
                                        TimeUnit.SECONDS.sleep(30);
                                        break;
                                    }
                                }
                            } else {
                                System.err.println("Wrong user name!");
                            }
                        } while (!IsPasswordMatch);
                        break;
                    default:
                        System.out.println("Exit");
                        break;
                }
                System.out.println("Do you want to exit? (y = yes / n = back to menu)");
                exit = sc.nextLine();
            } while (exit.equals("n"));
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

    }

}

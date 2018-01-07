package TASK_4_1;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static private final String DB_CONNECTION = "jdbc:mysql://192.168.0.101:3306/test_db123";
    static private final String DB_USER = "root";
    static private final String DB_PASSWORD = "root";
    static private final String TableName = "TASK_4_1";

    static private Connection connection;

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                // create connection
                connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while (true)
                {
                    System.out.println("1: Add Flat");
                    System.out.println("2: Select flats");
                    System.out.println("3: Delete Flat");
                    System.out.println("5: View list");
                    System.out.println("0: Exit");
                    System.out.print("Input command: ");

                    String s = sc.nextLine();
                    switch (s)
                    {
                        case "1":
                            addFlat(sc);
                            break;
                        case "2":
                            selectFlat(sc);
                            break;
                        case "3":
                            deleteFlat(sc);
                            break;
                        case "5":
                            viewFlats();
                            break;
                        case "0":
                            return;
                        default:
                            System.out.println("Command not found!");
                            break;
                    }
                }
            }
            finally {
                sc.close();
                if (connection != null)
                    connection.close();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException
    {

        try (PreparedStatement preparedStatement = connection.prepareStatement("SHOW TABLES;"))
        {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                if (resultSet.getString(1).equals(TableName))
                    return;
            }
        }

        try (Statement statement = connection.createStatement())
        {
//            statement.execute("DROP TABLE IF EXISTS " + TableName);
            statement.execute("CREATE TABLE " + TableName + "" +
                    " (" +
                    "id_flat INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "district VARCHAR(20) NOT NULL," +
                    "address VARCHAR(20) NOT NULL," +
                    "square INT NOT NULL," +
                    "rooms INT NOT NULL," +
                    "price INT NOT NULL);"
            );
        }
    }

    private static void addFlat(Scanner sc) throws SQLException
    {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Address: ");
        String address = sc.nextLine();
        System.out.print("Square: ");
        int square = Integer.parseInt(sc.nextLine());
        System.out.print("Amount of rooms: ");
        int rooms = Integer.parseInt(sc.nextLine());
        System.out.print("Price: ");
        int price = Integer.parseInt(sc.nextLine());


        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TASK_4_1 (district, address, square, rooms, price) VALUES(?, ?, ?, ?, ?)"))
        {
            preparedStatement.setString(1, district);
            preparedStatement.setString(2, address);
            preparedStatement.setInt(3, square);
            preparedStatement.setInt(4, rooms);
            preparedStatement.setInt(5, price);
            preparedStatement.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
    }

    private static void deleteFlat(Scanner sc)
    {
        System.out.print("Enter flat ID: ");
        int iD = Integer.parseInt(sc.nextLine());

        try (PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM " + TableName + " WHERE id_flat = ?"))
        {
            prepareStatement.setInt(1, iD);
            prepareStatement.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
        catch (SQLException e) { System.out.print("ERROR!"); }
    }

    private static void viewFlats() throws SQLException
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TableName))
        {
            // table of data representing a database result set,
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                // can be used to get information about the types and properties of the columns in a ResultSet object
                ResultSetMetaData metaData = resultSet.getMetaData();

                System.out.println();
                for (int i = 1; i <= metaData.getColumnCount(); i++)
                    System.out.print(metaData.getColumnName(i) + "\t\t\t");
                System.out.println();

                while (resultSet.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        System.out.print(resultSet.getString(i) + "\t\t\t\t");
                    }
                    System.out.println();
                }
                System.out.println();
            }
        }
    }

    private  static void selectFlat(Scanner scanner)
    {
        String sQLQuery = null;
        System.out.println("Select parameter to sort by: 1 = district, 2 = address, 3 = square, 4 = numer of rooms, 5 = price.");
        System.out.println("0 = Exit.");
        System.out.print("Parameter: ");
        int parameter = Integer.parseInt(scanner.nextLine());
        String value = null;
        if (parameter == 0)
            return;
        else if (parameter >= 1 && parameter <= 5) { }
        else {
            System.out.println("Error! Incorrect value!");
            return;
        }

        System.out.print("Sort value: ");
        value = scanner.nextLine();
        if (parameter == 1) {
            sQLQuery = "SELECT * FROM " + TableName + " WHERE district='" + value + "'";
        }
        else if (parameter == 2) {
            sQLQuery = "SELECT * FROM " + TableName + " WHERE address='" + value + "'";
        }
        else if (parameter == 3) {
            sQLQuery = "SELECT * FROM " + TableName + " WHERE square=" + Integer.parseInt(value);
        }
        else if (parameter == 4) {
            sQLQuery = "SELECT * FROM " + TableName + " WHERE rooms=" + Integer.parseInt(value);
        }
        else if (parameter == 5) {
            sQLQuery = "SELECT * FROM " + TableName + " WHERE price=" + Integer.parseInt(value);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sQLQuery); ResultSet resultSet = preparedStatement.executeQuery();)
        {
//            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();

            System.out.println();
            for (int i = 1; i <= metaData.getColumnCount(); i++)
                System.out.print(metaData.getColumnName(i) + "\t\t\t");
            System.out.println();

            while (resultSet.next())
            {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.print(resultSet.getString(i) + "\t\t\t\t");
                }
                System.out.println();
            }
            System.out.println();
        }
        catch (SQLException e) { e.printStackTrace(); }

    }

}

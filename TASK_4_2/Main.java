package TASK_4_2;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String DB_CONNECTION = "jdbc:mysql://192.168.0.101:3306/TASK_4_3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static Connection connection;

    private static String CLIENT_TABLE = "CLIENTS";
    private static String ORDER_TABLE = "ORDERS";
    private static String ITEM_TABLE = "ITEMS";

    public static void main (String[] args0) throws SQLException
    {
        connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        createTables();

        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            System.out.println("1: Add client");
            System.out.println("2: View clients");
            System.out.println("3: Delete clients");
            System.out.println("4: Add order");
            System.out.println("5: Delete order");
            System.out.println("6: View orders");
            System.out.println("0: Exit");
            System.out.print("Input command: ");

            String s = scanner.nextLine();
            switch (s)
            {
                case "1":
                    addClient(scanner);
                    break;
                case "2":
                    viewClients();
                    break;
                case "3":
                    deleteClient(scanner);
                    break;
                case "4":
                    addItem(scanner);
                    break;
                case "5":
                    deleteOrder(scanner);
                    break;
                case "6":
                    viewOrders();
                    break;
                case "0":
                    scanner.close();
                    connection.close();
                    return;
                default:
                    System.out.println("Command not found!");
                    break;
            }
        }
    }

    private static void createTables() throws SQLException
    {
        try (Statement statement = connection.createStatement())
        {
            statement.execute("DROP TABLE IF EXISTS " + ITEM_TABLE);
            statement.execute("DROP TABLE IF EXISTS " + ORDER_TABLE);
            statement.execute("DROP TABLE IF EXISTS " + CLIENT_TABLE);

            statement.execute("CREATE TABLE " + CLIENT_TABLE + "(" +
                    "client_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(20) NOT NULL)"
            );

            statement.execute("CREATE TABLE " + ORDER_TABLE + "(" +
                    "order_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "client_id INT NOT NULL," +
                    "FOREIGN KEY (client_id) REFERENCES " + CLIENT_TABLE + "(client_id))"
            );

            statement.execute("CREATE TABLE " + ITEM_TABLE + "(" +
                    "item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(20) NOT NULL," +
                    "order_id INT NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES " + ORDER_TABLE + "(order_id))"
            );
        }
    }

    private static void addClient(Scanner scanner)
    {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + CLIENT_TABLE + "(name) VALUES (?)"))
        {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    private static void deleteClient(Scanner scanner)
    {
        System.out.print("Enter Client ID: ");
        int iD = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM " + CLIENT_TABLE + " WHERE client_id = ?"))
        {
            prepareStatement.setInt(1, iD);
            prepareStatement.executeUpdate();
        }
        catch (SQLException e) { System.out.println("\nERROR!\n"); }
    }

    private static void deleteOrder(Scanner scanner) throws SQLException {
        System.out.print("Enter Order ID: ");
        int iD = Integer.parseInt(scanner.nextLine());

        connection.setAutoCommit(false);
        try (PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM " + ITEM_TABLE + " WHERE order_id = ?"))
        {
            prepareStatement.setInt(1, iD);
            prepareStatement.executeUpdate();
        }
        catch (SQLException e) { System.out.println("\nERROR!\n"); }

        try (PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM " + ORDER_TABLE + " WHERE order_id = ?"))
        {
            prepareStatement.setInt(1, iD);
            prepareStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("\nERROR!\n");
            return;
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    private static void viewClients()
    {
        resultPrint(CLIENT_TABLE);
    }

    private static void viewOrders()
    {
        resultPrint(ORDER_TABLE);
    }

    private static void resultPrint(String orderTable)
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + orderTable))
        {
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("\nEMPTY LIST\n");
                    return;
                }
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
            } catch (SQLException e) { e.printStackTrace(); }
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    private static void addItem(Scanner scanner) throws SQLException {
        System.out.print("Select Client by ID: ");
        int iD = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter item: ");
        String name = scanner.nextLine();

        connection.setAutoCommit(false);
        int order_id;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + ORDER_TABLE + "(client_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS ))
        {
            preparedStatement.setInt(1, iD);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            order_id = resultSet.getInt(1);

        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + ITEM_TABLE + "(order_id, name) VALUES (?, ?)"))
        {
            preparedStatement.setInt(1, order_id);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) { e.printStackTrace(); }
        connection.commit();
        connection.setAutoCommit(true);
    }

}

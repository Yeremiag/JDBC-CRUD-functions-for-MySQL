/*
If you see this, I assume you are either maintaining this code or some smart-ass kid trying to look cool
in front of his friend by breaking into the app. If you are maintaining this code, please fix the visibility
of the IP and password. Try not to show the IP and password publicly. If you are that smart-ass kid,
please stop with whatever you do. Thanks.
 */

import java.util.Random;
import java.sql.*;

public class MyJDBC {
    //Create data from table
    static void create(String tableName, Statement statement, int idCreate, String name, int price, int quantity) throws SQLException {
        int updatedResult = statement.executeUpdate("INSERT INTO " + tableName + " (Id, Name, Price, Quantity) VALUES (" + idCreate + ", " + name + ", " + price + ", " + quantity + ")");
        System.out.println(updatedResult);
    }

    //Read data from table
    static void read(String tableName, Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        while (resultSet.next()) {
            System.out.println("Id: " + resultSet.getString("Id"));
            System.out.println("Name: " + resultSet.getString("Name"));
            System.out.println("Price: " + resultSet.getString("Price"));
            System.out.println("Qty: " + resultSet.getString("Quantity"));
            System.out.println("\n");
        }
    }

    //Update data from table
    static void update(String tableName, Statement statement, String nameUpdated, String nameUpdate, int price, int quantity) throws SQLException {
        int updatedResult = statement.executeUpdate("UPDATE " + tableName + " SET Name = " + nameUpdated + ", Price = " + price + ", Quantity = " + quantity + " WHERE Name = " + nameUpdate);
        System.out.println(updatedResult);
    }

    //Delete data from table
    static void delete(String tableName, Statement statement, String nameDelete) throws SQLException {
        int updatedResult = statement.executeUpdate("DELETE FROM " + tableName + " WHERE Name = " + nameDelete);
        System.out.println(updatedResult);
    }

    //Create Cart
    static void createCart(String tableName, Statement statement) throws SQLException {
        int updatedResult = statement.executeUpdate("CREATE TABLE " + tableName + " (Id int AUTO_INCREMENT, Name varchar(45), Price int, Quantity int, PRIMARY KEY (Id));");
        System.out.println(updatedResult);
    }

    //Add data to cart
    static void addCart(String tableName, Statement statement, int idCreate, String name, int price, int quantity) throws SQLException {
        int updatedResult = statement.executeUpdate("INSERT INTO " + tableName + " (Id, Name, Price, Quantity) VALUES (" + idCreate + ", " + name + ", " + price + ", " + quantity + ")");
        System.out.println(updatedResult);
    }

    public static void main(String[] args) {

        /*
        Operation Types:
        Create           = 1
        Read             = 2
        Update           = 3
        Delete           = 4
        Create cart      = 5
        Add data to cart = 6

        Table Names:
        Drinks      = actdr
        Main Course = actmc
        Snacks      = actsn
        Queue       = actqueue
         */

        //Create
        String tableNameCreate = "actcart2043012437";
        int idCreate = 0; // DO NOT change, let it be 0!
        String nameCreate = "\"jus\"";
        int priceCreate = 1000;
        int quantityCreate = 2;

        //Read
        String tableNameRead = "actdr";

        //Update
        String tableNameUpdate = "actdr";
        String nameUpdate = "\"cocacola\""; //Food name that want to be updated
        String nameUpdated = "\"cocacola\""; //New food name
        int priceUpdate = 20000;
        int quantityUpdate = 50;

        //Delete
        String tableNameDelete = "actdr";
        String nameDelete = "\"cocacola\"";

        //Create cart (Make sure to run this only once)
        Random rand = new Random();
        int cartNumber = rand.nextInt(2147483647);
        String newTable = "actCart" + String.valueOf(cartNumber);

        //Add data to cart
        String cartName = newTable;
        int idCartCreate = 0; // DO NOT change, let it be 0!
        String nameCartCreate = "\"cocaine\"";
        int priceCartCreate = 1000;
        int quantityCartCreate = 10;

        try{
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.81:3306/acitya_canteen", // FIX THIS
                    "root",                                               // FIX THIS
                    "*dbTesting#"                                         // ESPECIALLY THIS
            );
            System.out.println("conntec");

            //Type of operation
            int operation = 1;

            switch(operation){
                //Create
                case 1:
                    create(tableNameCreate, connection.createStatement(), idCreate, nameCreate, priceCreate, quantityCreate);
                    System.out.println("Data created successfully!");
                    break;

                //Read
                case 2:
                    read(tableNameRead,connection.createStatement());
                    break;

                //Update
                case 3:
                    update(tableNameUpdate, connection.createStatement(),nameUpdated, nameUpdate, priceUpdate, quantityUpdate);
                    System.out.println("Data updated successfully!");
                    break;

                //Delete
                case 4:
                    delete(tableNameDelete, connection.createStatement(), nameDelete);
                    System.out.println("Data deleted successfully hoorayyy!");
                    break;

                //Add Cart
                case 5:
                    createCart(newTable, connection.createStatement());
                    System.out.println("Table created successfully!");
                    break;

                case 6:
                    addCart(cartName, connection.createStatement(), idCartCreate, nameCartCreate, priceCartCreate, quantityCartCreate);
                    System.out.println("Data added to cart successfully!");
                    break;
            }
            //asdkfhladskfhjalkdsfjasdfjasdjlfjhkladsfjhkfdashjkladkjhakjldfkj
            //sdafasdfkhjfgakjdsgfkjadhsg
            //adsfhagsdkfhjgasjdhf
            /*
            operation = 6;
            switch(operation){
                //Create
                case 1:
                    create(tableNameCreate, connection.createStatement(), idCreate, nameCreate, priceCreate, quantityCreate);
                    System.out.println("Data created successfully!");
                    break;

                //Read
                case 2:
                    read(tableNameRead,connection.createStatement());
                    break;

                //Update
                case 3:
                    update(tableNameUpdate, connection.createStatement(),nameUpdated, nameUpdate, priceUpdate, quantityUpdate);
                    System.out.println("Data updated successfully!");
                    break;

                //Delete
                case 4:
                    delete(tableNameDelete, connection.createStatement(), nameDelete);
                    System.out.println("Data deleted successfully!");
                    break;

                //Add Cart
                case 5:
                    createCart(newTable, connection.createStatement());
                    System.out.println("Table created successfully!");
                    break;

                case 6:
                    addCart(cartName, connection.createStatement(), idCartCreate, nameCartCreate, priceCartCreate, quantityCartCreate);
                    System.out.println("Data added to cart successfully!");
                    break;
            }
        */
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}

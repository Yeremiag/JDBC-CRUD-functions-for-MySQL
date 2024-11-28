/*
If you see this, I assume you are either maintaining this code or some smart-ass kid trying to look cool
in front of his friend by breaking into the app. If you are maintaining this code, please fix the visibility
of the IP and password. Try not to show the IP and password publicly. If you are that smart-ass kid,
please stop with whatever you do. Thanks.
 */

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.sql.*;

public class MyJDBC {
    //Create data from table
    static void create(String tableName, Connection connection, int idCreate, InputStream image, String name, int price, int quantity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tableName + " VALUES (?,?,?,?,?)");
        ps.setInt(1, idCreate);
        ps.setString(2,name);
        ps.setBinaryStream(3, image);
        ps.setInt(4, price);
        ps.setInt(5, quantity);
        ps.execute();

        //int updatedResult = statement.executeUpdate("INSERT INTO " + tableName + " (Id, Name, Image, Price, Quantity) VALUES (" + idCreate + ", " + name + ", " + image + ", " + price + ", " + quantity + ")");
        //System.out.println(updatedResult);
    }

    //Read data from table
    static void read(String tableName, Statement statement) throws SQLException, IOException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        while (resultSet.next()) {
            System.out.println("Id: " + resultSet.getString("Id"));
            System.out.println("Name: " + resultSet.getString("Name"));
            System.out.println("Price: " + resultSet.getString("Price"));
            System.out.println("Qty: " + resultSet.getString("Quantity"));
            System.out.println("\n");
        }

        //Read image and download
        ResultSet rs = statement.executeQuery(
                "SELECT Image FROM actdr");
        byte buff[] = new byte[1024];

        for(int i = 1;rs.next();i++){
            Blob ablob = rs.getBlob(1);
            File newfile = new File("newimage" + String.valueOf(i) + ".jpg");

            InputStream is = ablob.getBinaryStream();

            FileOutputStream fos =
                    new FileOutputStream(newfile);

            for (int b = is.read(buff); b != -1; b = is.read(buff)) {
                fos.write(buff, 0, b);
            }

            is.close();
            fos.close();
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

    //Add cart data to Queue table
    static void addQueue(String tableName, String tableQueue, Statement statement, int idQueue) throws SQLException {
        String tableSize = "0";
        ResultSet countTable = statement.executeQuery("SELECT COUNT(Id) FROM " + tableName);
        if(countTable.next()){
            tableSize = countTable.getString("COUNT(Id)");
        }
        int tableSize_ = Integer.parseInt(tableSize);
        String[] itemName = new String[tableSize_];
        String[] itemPrice = new String[tableSize_];
        String[] itemQuantity = new String[tableSize_];
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        for(int i = 0;resultSet.next();i++){
            itemName[i] = resultSet.getString("Name");
            itemPrice[i] = resultSet.getString("Price");
            itemQuantity[i] = resultSet.getString("Quantity");
        }
        String itemName_ = Arrays.toString(itemName);
        String itemName2_ = "\"" + itemName_.substring(1,itemName_.length()-1) + "\"";
        String itemPrice_ = Arrays.toString(itemPrice);
        String itemPrice2_ = "\"" + itemPrice_.substring(1,itemPrice_.length()-1) + "\"";
        String itemQuantity_ = Arrays.toString(itemQuantity);
        String itemQuantity2_ = "\"" + itemQuantity_.substring(1,itemQuantity_.length()-1) + "\"";
        int updatedResult = statement.executeUpdate("INSERT INTO " + tableQueue + " (Id, Items, Price, Quantity) VALUES (" + idQueue + ", " + itemName2_ + ", " + itemPrice2_ + ", " + itemQuantity2_ + ")");
        System.out.println(updatedResult);
    }

    public static void main(String[] args) throws FileNotFoundException {

        /*
        Operation Types:
        Create             = 1
        Read               = 2
        Update             = 3
        Delete             = 4
        Create cart        = 5
        Add data to cart   = 6
        Copy cart to queue = 7

        Table Names:
        Drinks      = actdr
        Main Course = actmc
        Snacks      = actsn
        Queue       = actqueue
        Payment     = actpay
         */

        //Create
        String tableNameCreate = "actdr";
        int idCreate = 0; // DO NOT change, let it be 0!
        String nameCreate = "vodka";
        InputStream imageCreate = new FileInputStream("C:\\Users\\yereg\\Documents\\Coding\\Projects\\2024\\5\\JDBC CRUD functions for MySQL\\image\\backgroundsnack.png");
        int priceCreate = 1000;
        int quantityCreate = 100;

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

        //Move cart data to queue table
        String tableCartQueueName = "actcart2043012437";
        String tableQueue = "actQueue";
        int idQueue = 0;

        try{
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.81:3306/acitya_canteen", // FIX THIS
                    "root",                                               // FIX THIS
                    "*dbTesting#"                                         // ESPECIALLY THIS
            );
            System.out.println("conntec");

            //Type of operation
            int operation = 2;

            switch(operation){
                //Create
                case 1:
                    create(tableNameCreate, connection, idCreate, imageCreate, nameCreate, priceCreate, quantityCreate);
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

                //Add data to cart
                case 6:
                    addCart(cartName, connection.createStatement(), idCartCreate, nameCartCreate, priceCartCreate, quantityCartCreate);
                    System.out.println("Data added to cart successfully!");
                    break;

                //Add cart data to queue table
                case 7:
                    addQueue(tableCartQueueName,tableQueue,connection.createStatement(), idQueue);
                    System.out.println("Cart data moved to Queue table successfully");
                    break;
            }
        }catch(SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

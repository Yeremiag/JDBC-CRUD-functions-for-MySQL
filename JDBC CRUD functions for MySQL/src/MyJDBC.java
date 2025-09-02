/*
If you see this, I assume you are either maintaining this code or some smart-ass kid trying to look cool
in front of his friend by breaking into the app. If you are maintaining this code, please fix the visibility
of the IP and password. Try not to show the IP and password publicly. If you are that smart-ass kid,
please stop with whatever you do. Thanks.
 */

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.Date;
import java.sql.Timestamp;



import java.sql.*;



import static java.sql.JDBCType.NULL;
import static java.sql.Timestamp.valueOf;

public class MyJDBC {
    //Create data from table
    static void create(String tableName, Connection connection, int idCreate, InputStream image, String name, int price, int quantity, String addOns) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?)");
        ps.setInt(1, idCreate);
        ps.setString(2,name);
        ps.setBinaryStream(3, image);
        ps.setInt(4, price);
        ps.setInt(5, quantity);
        ps.setString(6,addOns);
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
    static void update(String tableName, Statement statement, Connection connection, InputStream image, String nameUpdated, String nameUpdate, int price, int quantity) throws SQLException {
        int updatedResult = statement.executeUpdate("UPDATE " + tableName + " SET Name = " + nameUpdated + ", Price = " + price + ", Quantity = " + quantity + " WHERE Name = " + nameUpdate);
        System.out.println(updatedResult);

        PreparedStatement ps = connection.prepareStatement("UPDATE " + tableName + " SET Image = ? where Name = " + nameUpdate);
        ps.setBlob(1, image);
        ps.execute();
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

    //Add cart data to temp Queue table
    static void addTempQueue(String tableName, String tableQueue, Statement statement, Connection connection, int idQueue, String userID, InputStream paymentImage, String notes) throws SQLException {
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
        //String itemName2_ = "\"" + itemName_.substring(1,itemName_.length()-1) + "\"";
        String itemName2_ = itemName_.substring(1,itemName_.length()-1);
        String itemPrice_ = Arrays.toString(itemPrice);
        //String itemPrice2_ = "\"" + itemPrice_.substring(1,itemPrice_.length()-1) + "\"";
        String itemPrice2_ = itemPrice_.substring(1,itemPrice_.length()-1);
        String itemQuantity_ = Arrays.toString(itemQuantity);
        //String itemQuantity2_ = "\"" + itemQuantity_.substring(1,itemQuantity_.length()-1) + "\"";
        String itemQuantity2_ = itemQuantity_.substring(1,itemQuantity_.length()-1);

        PreparedStatement ps = connection.prepareStatement("INSERT INTO " + tableQueue + " VALUES (?,?,?,?,?,?,?,?)");
        ps.setInt(1,idQueue);
        ps.setBinaryStream(2, new ByteArrayInputStream(new byte[0],0,0));
        ps.setTimestamp(3, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        ps.setString(4,itemName2_);
        ps.setString(5,itemPrice2_);
        ps.setString(6,itemQuantity2_);
        ps.setString(7,userID);
        ps.setString(8,notes);
        ps.execute();
    }

    //Copy from temp Queue to final Queue
    static void addQueue(String tempQueue, String finalQueue, Statement statement, String userID, InputStream paymentImage) throws SQLException {
        int updatedResult = statement.executeUpdate("INSERT INTO " + finalQueue + " SELECT * FROM " + tempQueue + " WHERE User = '" + userID + "'");
        System.out.println(updatedResult);
    }

    //Delete cart
    static void deleteCart(String tableName, Statement statement) throws SQLException {
        int updatedResult = statement.executeUpdate("DROP TABLE " + tableName);
        System.out.println(updatedResult);
    }

    //Read data from Queue table then convert it to an array of string
    static void readQueue(String tableName, Statement statement) throws SQLException, IOException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        while (resultSet.next()) {
            String id = "Id: " + resultSet.getString("Id");

            String items = resultSet.getString("Items"); // ---> Get data from Items column in string.
            String[] itemsArray = items.split(",");           // ---> Make an array by dividing it when there is a comma.
            for (int i = 0; i < itemsArray.length; i++) {           //  ---> Loop through the array to display the data
                System.out.println("Items " + String.valueOf(i) + ": " + itemsArray[i] + ", ");
            }

            String price = resultSet.getString("Price"); // ---> Get data from Price column in string.
            String[] priceArray = price.split(",");           // ---> Make an array by dividing it when there is a comma.
            for (int i = 0; i < priceArray.length; i++) {           //  ---> Loop through the array to display the data
                System.out.println("Price " + String.valueOf(i) + ": " + priceArray[i] + ", ");
            }

            String quantity = resultSet.getString("Quantity"); // ---> Get data from Quantity column in string.
            String[] quantityArray = quantity.split(",");           // ---> Make an array by dividing it when there is a comma.
            for (int i = 0; i < quantityArray.length; i++) {              //  ---> Loop through the array to display the data
                System.out.println("Quantity " + String.valueOf(i) + ": " + quantityArray[i] + ", ");
            }
            System.out.println("\n");
        }
    }

    //Add user data to data table
    static void createUser(String tableName, String userMail, Statement statement, Connection connection, int idCreate, String name, String classNumber, String email, int password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tableName + " VALUES (?,?,?,?,?)");
        ps.setInt(1,idCreate);
        ps.setString(2,name);
        ps.setString(3,classNumber);
        ps.setString(4,email);
        ps.setInt(5,password);
        ps.execute();

        //Create history table for each user
        int updatedResult = statement.executeUpdate("CREATE TABLE " + userMail + "  LIKE actqueue;");
        System.out.println(updatedResult);
    }

    //Add purchase to user history table
    static void addHistory(String finalQueue, String historyTable, Statement statement, Connection connection, String userID, InputStream paymentImage) throws SQLException {
        int updatedResult = statement.executeUpdate("INSERT INTO " + historyTable + " SELECT * FROM " + finalQueue + " WHERE User = '" + userID + "'");
        System.out.println(updatedResult);
    }

    //Checking if a user is registered or not
    static void userCheck(Statement statement, String email) throws SQLException {
        int updatedResult = statement.executeUpdate("UPDATE actuser SET Email = '" + email + "' WHERE Email = '" + email + "'");
        System.out.println(updatedResult);
    }

    //Read data from user table
    static void readUser(Statement statement) throws SQLException, IOException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM actuser");
        while (resultSet.next()) {
            System.out.println("Id: " + resultSet.getString("Id"));
            System.out.println("Name: " + resultSet.getString("Name"));
            System.out.println("Class: " + resultSet.getString("Class"));
            System.out.println("Email: " + resultSet.getString("Email"));
            System.out.println("Pasword: " + resultSet.getString("Pasword"));
            System.out.println("\n");
        }
    }

    //Read spesific data from user data table
    static void readSpecificUser(Statement statement, String email) throws SQLException, IOException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM actuser WHERE Email = '" + email + "'");
        while (resultSet.next()) {
            String id = "Id: " + resultSet.getString("Id");
            String Name = resultSet.getString("Name");
            String Class = resultSet.getString("Class");
            String Email = resultSet.getString("Email");
            String Password = resultSet.getString("Password");
            System.out.println(id + Name + Class + Email + Password);
        }
    }

    //Read spesific data from user data table
    static void readSpecificActTempQueue(Statement statement, String email) throws SQLException, IOException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM actuser WHERE Email = '" + email + "'");
        while (resultSet.next()) {
            String id = "Id: " + resultSet.getString("Id");
            String Name = resultSet.getString("Name");
            String Class = resultSet.getString("Class");
            String Email = resultSet.getString("Email");
            String Password = resultSet.getString("Password");
            System.out.println(id + Name + Class + Email + Password);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        /*
        Operation Types:
        Create                   = 1
        Read                     = 2
        Update                   = 3
        Delete                   = 4
        Create cart              = 5
        Add data to cart         = 6
        Copy cart to temp queue  = 7
        Copy temp queue to queue = 8
        Read Queue               = 9
        Delete cart              = 10
        Create user data         = 11
        Add purchase to History  = 12
        Check user               = 13
        Read data user           = 14
        Read specific user       = 15

        Table Names:
        Drinks      = actdr
        Main Course = actmc
        Snacks      = actsn
        Queue       = actqueue
        Temp Queue  = acttempqueue
        User Data   = actuserdata
         */

        //Create user
        int idUser = 0;
        Random rand = new Random();
        String tableNameUser = "actuser";
        String userName = "Mittems";
        String classNumber = "XI-2";
        String email = "hallo@gmail.com";
        String historyTable = userName + "_" + email.substring(0,10);
        int password = rand.nextInt(2147483647);

        //Create
        String tableNameCreate = "actdr";
        int idCreate = 0; // DO NOT change, let it be 0!
        String nameCreate = "vodka";
        InputStream imageCreate = new FileInputStream("C:\\Users\\yereg\\Documents\\Coding\\Projects\\2024\\5\\JDBC CRUD functions for MySQL\\image\\backgroundsnack.png");
        int priceCreate = 1000;
        int quantityCreate = 100;
        String addOnsCreate = "Tambah Bakso";

        //Read
        String tableNameRead = "actdr";

        //Update
        String tableNameUpdate = "actdr";
        String nameUpdate = "\"cocacola2\""; //Food name that want to be updated
        String nameUpdated = "\"cocacola2\""; //New food name
        InputStream imageUpdate = new FileInputStream("C:\\Users\\yereg\\Documents\\Coding\\Projects\\2024\\5\\JDBC CRUD functions for MySQL\\image\\backgroundsnack.png");
        int priceUpdate = 20000;
        int quantityUpdate = 50;

        //Delete
        String tableNameDelete = "actdr";
        String nameDelete = "\"cocacola\"";

        //Create cart (Make sure to run this only once)
        String newTable = "cart_" + historyTable;

        //Add data to cart
        String cartName = newTable;
        int idCartCreate = 0; // DO NOT change, let it be 0!
        String nameCartCreate = "\"cocaine\"";
        int priceCartCreate = 1000;
        int quantityCartCreate = 10;

        //Move cart data to temporary queue table
        String tableCartQueueName = newTable;
        String tableTempQueue = "acttempqueue";
        int idQueue = 0;
        String userID = historyTable;
        String notes = "tesssssssssssssss";

        //Move data from temporary queue table to final queue table and also delete the data in teporary queue table
        String tableQueue = "actqueue";
        InputStream paymentImage = new FileInputStream("C:\\Users\\yereg\\Documents\\Coding\\Projects\\2024\\5\\JDBC CRUD functions for MySQL\\image\\backgroundsnack.png");

        //Read Queue
        String tableQueueNameRead = "actqueue";

        //Delete cart
        String deleteCart = newTable;

        //Create History
        int date = 0;


        try{
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.81:3306/acitya_canteen",
                    "root",
                    "*dbTesting#"
            );
            System.out.println("conntec");

            //Type of operation
            int operation = 15;

            switch(operation){
                //Create
                case 1:
                    create(tableNameCreate, connection, idCreate, imageCreate, nameCreate, priceCreate, quantityCreate, addOnsCreate);
                    System.out.println("Data created successfully!");
                    break;

                //Read
                case 2:
                    read(tableNameRead,connection.createStatement());
                    break;

                //Update
                case 3:
                    update(tableNameUpdate, connection.createStatement(),connection, imageUpdate, nameUpdated, nameUpdate, priceUpdate, quantityUpdate);
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

                //Add cart data to temp queue table
                case 7:
                    addTempQueue(tableCartQueueName,tableTempQueue,connection.createStatement(), connection, idQueue, userID, paymentImage, notes);
                    System.out.println("Cart data moved to Queue table successfully");
                    break;

                //Copy temp queue data to final queue table
                case 8:
                    addQueue(tableTempQueue,tableQueue,connection.createStatement(), historyTable, paymentImage);
                    System.out.println("Temp Queue data moved to Queue table successfully");
                    break;

                    //Read Queue
                case 9:
                    readQueue(tableQueueNameRead,connection.createStatement());
                    break;

                //Delete cart
                case 10:
                    deleteCart(deleteCart,connection.createStatement());
                    System.out.println("Cart deleted successfully!");
                    break;

                //Create user data
                case 11:
                    createUser(tableNameUser, historyTable, connection.createStatement(), connection, idUser, userName, classNumber, email, password);
                    System.out.println("User data created successfully");
                    break;

                //Add purchase to history
                case 12:
                    addHistory(tableQueue, historyTable, connection.createStatement(), connection, historyTable,paymentImage);
                    System.out.println("History added successfully");
                    break;

                //Check user registered or not
                case 13:
                    userCheck(connection.createStatement(), email);
                    System.out.println("Checked successfully");
                    break;

                //Read user data
                case 14:
                    readUser(connection.createStatement());
                    break;

                case 15:
                    readSpecificUser(connection.createStatement(),email);
                    break;

            }
        }catch(SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

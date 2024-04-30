package singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StartMySQL {
   private static Connection instance;

   public static Connection getInstance() {
      if (instance == null) {
         try {
            String url = "jdbc:mysql://localhost:3307/astaOnLine";
            String username = "root";
            String password = "";
            instance = DriverManager.getConnection(url, username, password);
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }

      return instance;
   }
   
	  
}
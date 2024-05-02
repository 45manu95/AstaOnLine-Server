package singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DESIGN PATTERN SINGLETON
 * Utilizzato per avere una sola istanza globale di strutture dati e metodi per accedervi
 */
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
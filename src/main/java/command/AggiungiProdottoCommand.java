package command;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import singleton.StartMySQL;

public class AggiungiProdottoCommand implements ActionListener {
   private Connection connection = StartMySQL.getInstance();
   private JFrame frameMessage;
   private String query = "INSERT INTO prodotti (titolo, immagine, prezzo, descrizione, data_fine) VALUES (?, ?, ?, ?, ?)";
   private String title;
   private String description;
   private float firstPrice;
   private int day;
   private int month;
   private int year;
   private int hour;
   private int minute;
   private ImageIcon icon;
   private String message;

   public AggiungiProdottoCommand(String title, String description, float firstPrice, int day, int month, int year, int hour, int minute, ImageIcon icon) {
      this.title = title;
      this.description = description;
      this.firstPrice = firstPrice;
      this.icon = icon;
      this.day = day;
      this.month = month;
      this.year = year;
      this.hour = hour;
      this.minute = minute;
   }

   public void actionPerformed(ActionEvent e) {
	  LocalDateTime dataFineAsta = LocalDateTime.of(this.year, this.month, this.day,this.hour, this.minute);

      try {
         FileInputStream in = new FileInputStream(this.icon.getDescription());

         try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(this.query);

            try {
               preparedStatement.setString(1, this.title);
               preparedStatement.setBlob(2, in);
               preparedStatement.setFloat(3, this.firstPrice);
               preparedStatement.setString(4, this.description);
               preparedStatement.setObject(5, dataFineAsta);
               int rowsAffected = preparedStatement.executeUpdate();
               if (rowsAffected > 0) {
                  this.message = "SUCCESSO - Articolo aggiunto e asta avviata";
               } else {
                  this.message = "ERRORE - Errore durante l'aggiunta del prodotto al database.";
               }
            } catch (Throwable e1) {
               if (preparedStatement != null) {
                  try {
                     preparedStatement.close();
                  } catch (Throwable e11) {
                     e11.addSuppressed(e11);
                  }
               }

               throw e1;
            }

            if (preparedStatement != null) {
               preparedStatement.close();
            }
         } catch (SQLException var10) {
            var10.printStackTrace();
            message = "ERRORE - Errore durante l'aggiunta del prodotto al database.";
         }
      } catch (FileNotFoundException var11) {
         var11.printStackTrace();
      }

      this.displayMessage(message);
   }

   private void displayMessage(String message) {
      this.frameMessage = new JFrame("Messaggio");
      this.frameMessage.setVisible(true);
      this.frameMessage.setLocationRelativeTo((Component)null);
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      JLabel messageLabel = new JLabel(message);
      JButton button = new JButton("OK");
      button.addActionListener(new ActionListener() { 
    	  public void actionPerformed(ActionEvent e) { 
    		  frameMessage.dispose();
    	} 
      });
      JPanel textPanel = new JPanel(new FlowLayout(1));
      JPanel buttonPanel = new JPanel(new FlowLayout(1));
      textPanel.add(messageLabel);
      buttonPanel.add(button);
      panel.add(textPanel);
      panel.add(buttonPanel);
      this.frameMessage.add(panel);
      this.frameMessage.pack();
   }
}
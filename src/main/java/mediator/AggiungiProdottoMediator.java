package mediator;

import command.AggiungiProdottoCommand;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * DESIGN PATTERN MEDIATOR
 * Per incapsulare il modo in si interagisce per accedere al sistema. 
 * Viene disaccoppiata l'azione dalla sua rappresentazione grafica e per dare
 * anche qualche controllata sulle informazioni passate al server
 * In questo caso come struttura generale questa classe corrisponde a Mediator1,
 * il quale implementa la sua interfaccia Mediator
 */
public class AggiungiProdottoMediator implements Mediator {
   private JFrame frameError = new JFrame();
   private JButton invioProdotto;
   private JButton selectImage;
   private JTextField titleText = new JTextField();
   private JTextField description = new JTextField();
   private JTextField firstPrice = new JTextField();
   private ImageIcon image;
   private int day;
   private int month;
   private int year;
   private int hour;
   private int minute;
   private final Integer[] years = new Integer[]{Calendar.getInstance().get(1), Calendar.getInstance().get(1) + 1};
   private String[] errors = new String[]{"Campo titolo prodotto vuoto", "Campo descrizione prodotto vuoto", "Campo prezzo base prodotto vuoto", "Campo immagine vuoto", "Campo prezzo base prodotto non valido", "Devi aggiungere una data successiva al giorno odierno"};

   public void setButton(JButton invioProdotto, JButton selectImage) {
      this.invioProdotto = invioProdotto;
      this.selectImage = selectImage;
   }

   public void setDati(JTextField titleText, JTextField description, JTextField firstPrice, ImageIcon image, int day, int month, int year, int hour, int minute) {
      this.titleText = titleText;
      this.description = description;
      this.firstPrice = firstPrice;
      this.image = image;
      this.day = day + 1;
      this.month = month + 1;
      this.year = this.years[year];
      this.hour = hour;
      this.minute = minute;
   }

   public void notify(JComponent component) {
      if (component == this.invioProdotto) {
         int error = this.controllaCorrettezza();
         if (error == 0) {
            (new AggiungiProdottoCommand(this.titleText.getText(), this.description.getText(), Float.parseFloat(this.firstPrice.getText()), this.day, this.month, this.year, this.hour, this.minute, this.image)).actionPerformed((ActionEvent)null);
            this.titleText.setText("");
            this.description.setText("");
            this.firstPrice.setText("");
            this.image = null;
            this.selectImage.setEnabled(true);
         } else {
            this.displayErrors(error);
         }
      }

   }

   private int controllaCorrettezza() {
      int error = 0;
      if (this.titleText.getText().length() == 0) {
         error = 1;
      } else if (this.description.getText().length() == 0) {
         error = 2;
      } else if (this.firstPrice.getText().length() == 0) {
         error = 3;
      } else if (this.image == null) {
         error = 4;
      } else if (!this.isPrezzo(this.firstPrice.getText())) {
         error = 5;
      } else if (!isDataValida()) {
          error = 6;
      }

      return error;
   }

   private boolean isPrezzo(String input) {
      String currencyPattern = "[0-9]+([.][0-9]{1,2})?";
      return input.matches(currencyPattern);
   }

   private void displayErrors(int error) {
      this.frameError = new JFrame("Errore");
      this.frameError.setVisible(true);
      this.frameError.setLocationRelativeTo((Component)null);
      this.frameError.setSize(new Dimension(400, 50));
      this.frameError.setLayout(new FlowLayout(1));
      JLabel message = new JLabel(this.errors[error - 1]);
      this.frameError.add(message);
   }
   
   private boolean isDataValida() {
	   Calendar calendar = Calendar.getInstance();
	    int currentYear = calendar.get(Calendar.YEAR);
	    int currentMonth = calendar.get(Calendar.MONTH) + 1; // Mese è 0-based in Calendar
	    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
	    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
	    int currentMinute = calendar.get(Calendar.MINUTE);
	    
	    // Controlla se l'anno selezionato è successivo all'anno corrente
	    if (this.year > currentYear) {
	        return true;
	    }
	    // Controlla se l'anno selezionato è lo stesso dell'anno corrente e se il mese selezionato è successivo al mese corrente
	    else if (this.year == currentYear && this.month > currentMonth) {
	        return true;
	    }
	    // Controlla se l'anno e il mese selezionati sono gli stessi dell'anno e del mese corrente e se il giorno selezionato è successivo al giorno corrente
	    else if (this.year == currentYear && this.month == currentMonth && this.day > currentDay) {
	        return true;
	    }
	    // Controlla se la data selezionata è la stessa data corrente e se l'ora selezionata è successiva all'ora corrente
	    else if (this.year == currentYear && this.month == currentMonth && this.day == currentDay &&
	             this.hour > currentHour) {
	        return true;
	    }
	    // Controlla se la data e l'ora selezionate sono la stessa data e ora correnti e se il minuto selezionato è successivo al minuto corrente
	    else if (this.year == currentYear && this.month == currentMonth && this.day == currentDay &&
	             this.hour == currentHour && this.minute > currentMinute) {
	        return true;
	    }
	    
	    // Se nessuna delle condizioni sopra è soddisfatta, la data non è valida
	    return false;
	}
}

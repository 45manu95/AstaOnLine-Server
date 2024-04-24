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
   private String[] errors = new String[]{"Campo titolo prodotto vuoto", "Campo descrizione prodotto vuoto", "Campo prezzo base prodotto vuoto", "Campo immagine vuoto", "Campo prezzo base prodotto non valido"};

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
      this.hour = hour + 1;
      this.minute = minute + 1;
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
}

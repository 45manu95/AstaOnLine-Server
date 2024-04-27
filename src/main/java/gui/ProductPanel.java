package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.google.protobuf.ByteString;

import astaOnlineProto.AstaOnLine.Articolo;

public class ProductPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	//JPanel
	private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	
	//JLabel
	private JLabel title;
	private JLabel imageProduct;
	private JLabel description;
	private JLabel price;
	
	
	public ProductPanel(Articolo articolo) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.WHITE);
		
		//otteniamo l'immagine
		ByteString byteString = articolo.getImmagine();
		byte[] imageData = byteString.toByteArray();
        ImageIcon imageIcon = creaImageIcon(imageData);
        
        title = new JLabel(articolo.getNome().toUpperCase());
        imageProduct = new JLabel(imageIcon);
        description = new JLabel(articolo.getDescrizione());
        price = new JLabel(String.valueOf(articolo.getValorePartenza())+ " €");
        
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16));
		
		titlePanel.add(title);
		imagePanel.add(imageProduct);
		descriptionPanel.add(description);
		pricePanel.add(price);
		
		add(titlePanel);
		add(imagePanel);
		add(descriptionPanel);
		add(pricePanel);
		
		addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ShowProduct productView = new ShowProduct(articolo);
                productView.setVisible(true);
            }
        });
	}
	
	private ImageIcon creaImageIcon(byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bis);
            
            Image resizedImage = image.getScaledInstance(200, 200, Image.SCALE_REPLICATE);

            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	/**
	 * Alcune JLabel e JPanel sono state ricreate anche in questa inner class (senza chiamarle per riferimento dalla classe superiore)
	 * per il motivo secondo il quale nel momento in cui si chiudeva la schermata di
	 * descrizione del prodotto veniva invocato il dispose anche su quegli oggetti di riferimento
	 * e quindi scomparivano anche dalla schermata di visualizzazione dei prodotti in vendita (usciva un riquadro bianco)
	 * @see title
	 */
	
	private class ShowProduct extends JFrame {
		private static final long serialVersionUID = 1L;
		
	    private Timer timer;
		
		private JPanel disposition = new JPanel();
		private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel dateStartDisposition = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel dateEndDisposition = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel timerDisposition = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private JPanel buttonDisposition = new JPanel(new FlowLayout(FlowLayout.CENTER));

		private JLabel title;
		private JLabel imageProduct;
		private JLabel description;
		private JLabel price;
		private JLabel start;
		private JLabel end;
		private JLabel countdownLabel = new JLabel();
						
		public ShowProduct(Articolo articolo) {
			setTitle("Dettagli articolo");
			setSize(800,600);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLocationRelativeTo(null);
			setBackground(Color.WHITE);
			setResizable(false);
			
			disposition.setLayout(new BoxLayout(disposition, BoxLayout.Y_AXIS));
			
			//viene introdotto il replace perchè la stringa time data contiene una T
			start = new JLabel("Data inserimento articolo = "+articolo.getDataInizio().replace("T", " "));
			end = new JLabel("Data fine asta = "+articolo.getDataFine().replace("T", " "));
				
			timer = new Timer(1000, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                updateCountdown(articolo.getDataFine().replace("T", " "));
	            }
	        });
	        timer.start();
	        
			//otteniamo l'immagine
			ByteString byteString = articolo.getImmagine();
			byte[] imageData = byteString.toByteArray();
	        ImageIcon imageIcon = creaImageIcon(imageData);
	        
	        title = new JLabel(articolo.getNome().toUpperCase());
	        imageProduct = new JLabel(imageIcon);
	        description = new JLabel(articolo.getDescrizione());
	        price = new JLabel(String.valueOf(articolo.getValorePartenza())+ " €");
	        
	        title.setFont(title.getFont().deriveFont(Font.BOLD, 16));
			
			titlePanel.add(title);
			imagePanel.add(imageProduct);
			descriptionPanel.add(description);
			pricePanel.add(price);
	
			dateStartDisposition.add(start);
			dateEndDisposition.add(end);
			timerDisposition.add(countdownLabel);
			
			disposition.add(titlePanel);
			disposition.add(imagePanel);
			disposition.add(descriptionPanel);
			disposition.add(pricePanel);
			disposition.add(dateStartDisposition);
			disposition.add(dateEndDisposition);
			disposition.add(timerDisposition);
			disposition.add(buttonDisposition);
			
			add(disposition, BorderLayout.CENTER);
		}
		
		private void updateCountdown(String end) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date endDate;
            try {
                endDate = sdf.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

			Date now = new Date();
		    long millisecondsLeft = endDate.getTime() - now.getTime();

		    if (millisecondsLeft <= 0) {
		        countdownLabel.setText("Asta scaduta");
		        timer.stop();
		    } else {
		        long secondsLeft = millisecondsLeft / 1000;
		        long days = secondsLeft / 86400; // 86400 secondi in un giorno
		        long hours = (secondsLeft % 86400) / 3600;
		        long minutes = (secondsLeft % 3600) / 60;
		        long seconds = secondsLeft % 60;
		        String countdownText = String.format("%02d giorni %02d:%02d:%02d", days, hours, minutes, seconds);
		        countdownLabel.setText("Tempo mancante: " + countdownText);
		    }
	    }
	}
}

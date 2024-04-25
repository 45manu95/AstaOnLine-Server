package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.Year;
import java.time.Month;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import mediator.*;


public class FinestraHome extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private PannelloOvest p1=null;
	private PannelloCentrale p2=null;

	//TextFields
    private JTextField email = new JTextField();
    private JPasswordField password = new JPasswordField();

    //Labels
    private JLabel titleMenu = new JLabel("MENU");
    
    //JButtons
    private JButton insertArticles = new JButton("INSERISCI ARTICOLI");
    private JButton purchaseArticles = new JButton("ARTICOLI IN VENDITA");
    private JButton purchasedArticles = new JButton("ARTICOLI VENDUTI");
    private JButton interestedArticles = new JButton("ARTICOLI INTERESSATI");
    private JButton messagesArticles = new JButton("MESSAGGI");
    
    //gestione server
    private ManageServerMediator manageServer;
    
    //JTextField fittizie per la gestione del server, servono solo per darle in pasto al mediator
    private JTextField start = new JTextField();
    private JTextField close = new JTextField();

    //ritocchi grafici	
	public FinestraHome(int port) {
		setTitle("Asta Online");
		setSize(1100,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	    setBackground(Color.WHITE);
		setResizable(false);
	    setLayout(new GridBagLayout());
	    
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.fill = GridBagConstraints.BOTH;

	        p1 = new PannelloOvest();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.weightx = 0.2; // Imposta il peso della colonna per il PannelloOvest
	        gbc.weighty = 1.0;
	        add(p1, gbc);

	        p2 = new PannelloCentrale();
	        gbc.gridx = 1;
	        gbc.gridy = 0;
	        gbc.weightx = 0.8; // Imposta il peso della colonna per il PannelloCentrale
	        gbc.weighty = 1.0;
	        add(p2, gbc);		
	        
	        //gestione server
	        manageServer = new ManageServerMediator(start, close, port);
	        manageServer.notify(start);
	        this.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	            	manageServer.notify(close);
	            }
	        });
	}
	
	private class PannelloOvest extends JPanel{

		private static final long serialVersionUID = 1L;		
		
		public PannelloOvest() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			setBackground(Color.ORANGE);
			titleMenu.setFont(new Font(titleMenu.getFont().getName(), titleMenu.getFont().getStyle(), 25));
			
			titleMenu.setAlignmentX(CENTER_ALIGNMENT);
			insertArticles.setAlignmentX(CENTER_ALIGNMENT);
			purchasedArticles.setAlignmentX(CENTER_ALIGNMENT);
	        purchaseArticles.setAlignmentX(CENTER_ALIGNMENT);
			interestedArticles.setAlignmentX(CENTER_ALIGNMENT);
			messagesArticles.setAlignmentX(CENTER_ALIGNMENT);

		    add(Box.createVerticalGlue()); // Spazio vuoto sopra i bottoni
		    add(titleMenu);
		    add(Box.createVerticalGlue()); // Spazio vuoto tra il titolo e i bottoni
		    add(insertArticles);
		    add(purchaseArticles);
		    add(purchasedArticles);
		    add(interestedArticles);
		    add(messagesArticles);
		    add(Box.createVerticalGlue());
			
		}
		
	}
	
	private class PannelloCentrale extends JPanel{

		private static final long serialVersionUID = 1L;
		
		//JPanels
		private JPanel centralInfo = new JPanel();
		private JPanel titleDisposition = new JPanel();
		private JPanel descriptionDisposition = new JPanel();
		private JPanel firstPriceDisposition = new JPanel();
		private JPanel endDateDisposition = new JPanel();
		private JPanel imageDisposition = new JPanel();
		
		//JLabels
		private JLabel titleLabel = new JLabel("VENDI ARTICOLO");
		private JLabel titleProduct = new JLabel("Nome prodotto:");
		private JLabel descriptionLabel = new JLabel("Descrizione:");
		private JLabel firstPriceLabel = new JLabel("Prezzo base:");
		private JLabel endDateLabel = new JLabel("data fine asta (inserisci prima il mese):");
		private JLabel hourLabel = new JLabel("ora:");
		private JLabel minuteLabel = new JLabel("minuti:");
		private JLabel imageLabel = new JLabel("immagine:");

		//JTextFields
		private JTextField titleText = new JTextField();
		private JTextField description = new JTextField();
		private JTextField firstPrice = new JTextField();

		//JButtons
		private JButton image = new JButton("Carica");
		private JButton loadProduct = new JButton("Vendi articolo");
		
		//ImageIcons
		private ImageIcon imageProduct;
		
		//giorni in un mese
		private Integer[] day = {1};
		//mesi anno
		private final String[] months = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre" };
		//scelta anno (l'asta ha come tempo limite l'anno prossimo
		private final Integer[] years = {Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.YEAR)+1};
		//ora e minuti
		private Integer[] hour = {0};
		private Integer[] minute = {0};
		
		//JComboBoxes
		private final JComboBox<String> monthsComboBox = new JComboBox<String>(months);
		private JComboBox<Integer> yearComboBox = new JComboBox<Integer>(years);
		private JComboBox<Integer> dayComboBox = new JComboBox<Integer>(day);
		private JComboBox<Integer> hourComboBox = new JComboBox<Integer>(hour);
		private JComboBox<Integer> minuteComboBox = new JComboBox<Integer>(minute);
		
		//design pattern Mediator
    	private AggiungiProdottoMediator aggiungi;

		
		public PannelloCentrale() {
			aggiungi = new AggiungiProdottoMediator();
			aggiungi.setButton(loadProduct,image);
			
	        final JFrame frame = new JFrame("Image Loading Example");

			 setLayout(new GridBagLayout()); 
		        GridBagConstraints gbc = new GridBagConstraints();
		        gbc.insets = new Insets(10, 10, 10, 10); // Aggiunge spaziatura
		        gbc.gridx = 0;
		        gbc.gridy = 0;
		        gbc.anchor = GridBagConstraints.CENTER; // Allinea al centro
		        
		        centralInfo.setLayout(new BoxLayout(centralInfo, BoxLayout.Y_AXIS));
		        
		        titleLabel.setFont(new Font(titleLabel.getFont().getName(), titleLabel.getFont().getStyle(), 20));
		        
		        image.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					    // Crea un selettore di file
					    JFileChooser fileChooser = new JFileChooser();
					    fileChooser.setDialogTitle("Seleziona un'immagine");
					    
					    // Aggiungi un filtro per limitare la selezione ai file di immagine
					    FileNameExtensionFilter filter = new FileNameExtensionFilter("Immagini", "jpg", "jpeg", "png");
					    fileChooser.setFileFilter(filter);

					    // Mostra il selettore di file e gestisci la risposta
					    int result = fileChooser.showOpenDialog(frame);
					    if (result == JFileChooser.APPROVE_OPTION) {
					        // Ottieni il file selezionato
					        File selectedFile = fileChooser.getSelectedFile();
					        
					        long fileSizeInBytes = selectedFile.length();
					        long fileSizeInKB = fileSizeInBytes / 1024L;
					        long fileSizeInMB = fileSizeInKB / 1024L;
					        
					        if (fileSizeInMB > 1L) {
						        imageLabel.setText("Il file supera 1MB");
						        loadProduct.setEnabled(false);
					        }else {
					        	// Carica l'immagine dal file selezionato
						        imageProduct = new ImageIcon(selectedFile.getAbsolutePath());
						        if(isImageFile(selectedFile)) {
							        imageLabel.setText("Immagine selezionata");
							        image.setEnabled(false);
							        loadProduct.setEnabled(true);
						        }
						        else {
							        imageLabel.setText("Non risulta un immagine");
							        loadProduct.setEnabled(false);
						        }
					        }   
	
					    }
					}
				});
		        
				monthsComboBox.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                updateDayComboBox(); // Aggiorna i giorni disponibili in base al mese selezionato
		            }
		        });
				
				loadProduct.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                aggiungi.setDati(titleText, description, firstPrice, imageProduct, dayComboBox.getSelectedIndex(), monthsComboBox.getSelectedIndex(), yearComboBox.getSelectedIndex(), hourComboBox.getSelectedIndex(), minuteComboBox.getSelectedIndex());
		                aggiungi.notify(loadProduct);
		            }
		        });
				
		        // Popola i ComboBox per le ore (da 0 a 23)
		        populateHourComboBox();

		        // Popola i ComboBox per i minuti (da 0 a 59)
		        populateMinuteComboBox();
		        
		        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		        titleProduct.setAlignmentX(Component.CENTER_ALIGNMENT);
		        description.setAlignmentX(Component.CENTER_ALIGNMENT);
		        firstPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
		        image.setAlignmentX(Component.CENTER_ALIGNMENT);
		        loadProduct.setAlignmentX(CENTER_ALIGNMENT);
		        
		        titleDisposition.setLayout(new FlowLayout(FlowLayout.CENTER));
		        descriptionDisposition.setLayout(new FlowLayout(FlowLayout.CENTER));
		        firstPriceDisposition.setLayout(new FlowLayout(FlowLayout.CENTER));
		        endDateDisposition.setLayout(new FlowLayout(FlowLayout.CENTER));
		        imageDisposition.setLayout(new FlowLayout(FlowLayout.CENTER));
		        
		        titleText.setPreferredSize(new Dimension(150,25));
		        description.setPreferredSize(new Dimension(150,25));
		        firstPrice.setPreferredSize(new Dimension(150,25));
		        image.setPreferredSize(new Dimension(150,25));

		        titleDisposition.add(titleProduct);
		        titleDisposition.add(titleText);
		        descriptionDisposition.add(descriptionLabel);
		        descriptionDisposition.add(description);
		        firstPriceDisposition.add(firstPriceLabel);
		        firstPriceDisposition.add(firstPrice);
		        endDateDisposition.add(endDateLabel);
		        endDateDisposition.add(dayComboBox);
		        endDateDisposition.add(monthsComboBox);
		        endDateDisposition.add(yearComboBox);
		        endDateDisposition.add(hourLabel);
		        endDateDisposition.add(hourComboBox);
		        endDateDisposition.add(minuteLabel);
		        endDateDisposition.add(minuteComboBox);
		        imageDisposition.add(imageLabel);
		        imageDisposition.add(image);
		        
		        centralInfo.add(titleLabel);
		        centralInfo.add(Box.createVerticalStrut(10));
		        centralInfo.add(titleDisposition);
		        centralInfo.add(descriptionDisposition);
		        centralInfo.add(firstPriceDisposition);
		        centralInfo.add(endDateDisposition);
		        centralInfo.add(imageDisposition);
		        centralInfo.add(Box.createVerticalStrut(10));
		        centralInfo.add(loadProduct);
		        
		        add(centralInfo, gbc);
		}
		
		private boolean isImageFile(File file) {
		    String name = file.getName().toLowerCase();
		    return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
		}
		
		// Popola il ComboBox per le ore (da 0 a 23)
	    private void populateHourComboBox() {
	        for (int hour = 1; hour < 24; hour++) {
	            hourComboBox.addItem(hour);
	        }
	    }

	    // Popola il ComboBox per i minuti (da 0 a 59)
	    private void populateMinuteComboBox() {
	        for (int minute = 1; minute < 60; minute++) {
	            minuteComboBox.addItem(minute);
	        }
	    }
		
		private void updateDayComboBox() {
	        int selectedMonthIndex = monthsComboBox.getSelectedIndex();
	        int selectedYear = (int) yearComboBox.getSelectedItem();

	        // Determina il numero massimo di giorni per il mese selezionato
	        int maxDays = Month.of(selectedMonthIndex + 1).length(Year.isLeap(selectedYear));

	        // Ottieni il modello del ComboBox per i giorni
	        DefaultComboBoxModel<Integer> dayComboBoxModel = new DefaultComboBoxModel<>();
	        for (int i = 1; i <= maxDays; i++) {
	            dayComboBoxModel.addElement(i);
	        }

	        // Imposta il nuovo modello per il ComboBox dei giorni
	        dayComboBox.setModel(dayComboBoxModel);
	    }
		
	}

}
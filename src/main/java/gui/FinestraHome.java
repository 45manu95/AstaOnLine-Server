package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import mediator.*;

/**
 * Interfaccia amministrazione server
 */
public class FinestraHome extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private PannelloOvest p1=null;
	private PannelloAggiuntaProdotti p2 = null;
	private PannelloProdottiVendita p3 = null;
	private PannelloProdottiVenduti p4 = null;
	private PannelloVisualizzaMessaggi p5 = null;
	private PannelloProdottiNonVenduti p6 = null;
    
	private String ultimoPannello="PannelloAggiuntaProdotti";

    //Labels
    private JLabel titleMenu = new JLabel("MENU");
    
    //JButtons
    private JButton insertArticles = new JButton("INSERISCI ARTICOLI");
    private JButton purchaseArticles = new JButton("ARTICOLI IN VENDITA");
    private JButton purchasedArticles = new JButton("ARTICOLI VENDUTI");
    private JButton notPurchasedArticles = new JButton("ARTICOLI NON VENDUTI");
    private JButton messagesArticles = new JButton("MESSAGGI");
    
    //gestione server
    private ManageServerMediator manageServer;
    
    //JTextField fittizie per la gestione del server, servono solo per darle in pasto al mediator
    private JTextField start = new JTextField();
    private JTextField close = new JTextField();
    
	private GridBagConstraints gbc = new GridBagConstraints();

    //ritocchi grafici	
	public FinestraHome(int port) {
		setTitle("Asta Online SERVER");
		setSize(1100,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	    setBackground(Color.WHITE);
		setResizable(false);
	    setLayout(new GridBagLayout());
	    
	        gbc.fill = GridBagConstraints.BOTH;

	        p1 = new PannelloOvest();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.weightx = 0.2; // Imposta il peso della colonna per il PannelloOvest
	        gbc.weighty = 1.0;
	        add(p1, gbc);

	        p2 = new PannelloAggiuntaProdotti();
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
	
	private class PannelloOvest extends JPanel implements ActionListener {

		private static final long serialVersionUID = 1L;		
		
		public PannelloOvest() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			setBackground(Color.ORANGE);
			titleMenu.setFont(new Font(titleMenu.getFont().getName(), titleMenu.getFont().getStyle(), 25));
			
			titleMenu.setAlignmentX(CENTER_ALIGNMENT);
			insertArticles.setAlignmentX(CENTER_ALIGNMENT);
			purchasedArticles.setAlignmentX(CENTER_ALIGNMENT);
	        purchaseArticles.setAlignmentX(CENTER_ALIGNMENT);
	        notPurchasedArticles.setAlignmentX(CENTER_ALIGNMENT);
			messagesArticles.setAlignmentX(CENTER_ALIGNMENT);
			

		    add(Box.createVerticalGlue()); // Spazio vuoto sopra i bottoni
		    add(titleMenu);
		    add(Box.createVerticalGlue()); // Spazio vuoto tra il titolo e i bottoni
		    add(insertArticles);
		    add(purchaseArticles);
		    add(purchasedArticles);
		    add(notPurchasedArticles);
		    add(messagesArticles);
		    add(Box.createVerticalGlue());
		    
		    insertArticles.addActionListener(this);
		    purchaseArticles.addActionListener(this);
		    purchasedArticles.addActionListener(this);
		    notPurchasedArticles.addActionListener(this);
		    messagesArticles.addActionListener(this);
		}

		@Override
	    public void actionPerformed(ActionEvent evt) {		
	        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
	    	rimuovi(frame);
	    	if(evt.getSource() == insertArticles) {
				p2 = new PannelloAggiuntaProdotti();
				ultimoPannello = "PannelloAggiuntaProdotti";
				frame.add(p2,gbc);
			}
			else if(evt.getSource() == purchaseArticles) {
				p3 = new PannelloProdottiVendita();
				ultimoPannello = "PannelloProdottiVendita";
				frame.add(p3,gbc);
			}
			else if(evt.getSource() == purchasedArticles) {
				p4 = new PannelloProdottiVenduti();
				ultimoPannello = "PannelloProdottiVenduti";
				frame.add(p4,gbc);
			}
			else if(evt.getSource() == messagesArticles) {
				p5 = new PannelloVisualizzaMessaggi();
				ultimoPannello = "PannelloVisualizzaMessaggi";
				frame.add(p5,gbc);
			}
			else if(evt.getSource() == notPurchasedArticles) {
				p6 = new PannelloProdottiNonVenduti();
				ultimoPannello = "PannelloProdottiNonVenduti";
				frame.add(p6,gbc);
			}
	    	frame.revalidate();
	    	frame.repaint();
		}
	    
	    private void rimuovi(JFrame frame) {
	    	//recupero il frame genitore ed elimino il panel corrente
	        if(ultimoPannello.equals("PannelloAggiuntaProdotti")) {
		        frame.remove(p2);
	        }
	        else if(ultimoPannello.equals("PannelloProdottiVendita")) {
		        frame.remove(p3);
	        }
	        else if(ultimoPannello.equals("PannelloProdottiVenduti")) {
		        frame.remove(p4);
	        }
	        else if(ultimoPannello.equals("PannelloVisualizzaMessaggi")) {
		        frame.remove(p5);
	        }
	        else if(ultimoPannello.equals("PannelloProdottiNonVenduti")) {
		        frame.remove(p6);
	        }
	    }
		
	}	
}
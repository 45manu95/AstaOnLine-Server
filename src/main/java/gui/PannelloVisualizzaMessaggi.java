package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PannelloVisualizzaMessaggi extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel title = new JLabel("MESSAGGI");
	
	private JTextArea messageArea;
	private JScrollPane scrollPane;

	public PannelloVisualizzaMessaggi() {
		setLayout(new GridBagLayout()); 
	    GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Aggiunge spaziatura
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Allinea al centro
		add(title);
		
		// Creazione dell'area di testo per i messaggi
        messageArea = new JTextArea(10, 20); // 10 righe, 20 colonne iniziali
        messageArea.setEditable(false); // Non modificabile dall'utente

        // Creazione del pannello di scorrimento per l'area di testo
        scrollPane = new JScrollPane(messageArea);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH; // Espandi il componente in entrambe le direzioni
        add(scrollPane, gbc);
        
        //NewsMessage.aff
    }
	
    private void addMessage(String message) {
        messageArea.append(message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }
}

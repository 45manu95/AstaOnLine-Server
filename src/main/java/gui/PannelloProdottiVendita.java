package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import astaOnlineProto.AstaOnLine.Articolo;
import utils.SqlQuery;

public class PannelloProdottiVendita extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JPanel showProducts = new JPanel();
	private JPanel centralInfo = new JPanel();
	
	private JLabel imageProduct = new JLabel(new ImageIcon("src/main/resources/images/image.png"));
	private JLabel info = new JLabel("Caricamento in corso");
	
    private GridBagConstraints gbc = new GridBagConstraints();

	
	public PannelloProdottiVendita() {
		 setLayout(new GridBagLayout()); 
	        gbc.insets = new Insets(10, 10, 10, 10); // Aggiunge spaziatura
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.anchor = GridBagConstraints.CENTER; // Allinea al centro
	     
	        centralInfo.setLayout(new BoxLayout(centralInfo, BoxLayout.Y_AXIS));
	        
	        imageProduct.setAlignmentX(Component.CENTER_ALIGNMENT);
	        info.setAlignmentX(Component.CENTER_ALIGNMENT);
	        
	        centralInfo.add(imageProduct);
	        centralInfo.add(info);
	        
	        add(centralInfo, gbc);
	        
            new Thread(() -> {
            	
            	
                List<Articolo> products = SqlQuery.visualizzaArticoli();
                SwingUtilities.invokeLater(() -> {     
	                showProducts.setLayout(new GridLayout(0, 3));
	                if(products.size() != 0) {
	                	for (Articolo product : products) {
	                    	ProductPanel productPanel = new ProductPanel(product);
	                        showProducts.add(productPanel);
	                    }
	                    remove(centralInfo);
	                    setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	                    JScrollPane scrollPane = new JScrollPane(showProducts);
	                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		                add(scrollPane, gbc);
	                    revalidate(); 
	                }
	                else {
	                    info.setText("Nessun elemento da visualizzare");
	                }
                });
            }).start();
        }
}

package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.google.protobuf.ByteString;

import astaOnlineProto.AstaOnLine.Articoli;
import astaOnlineProto.AstaOnLine.Articolo;
import singleton.StartMySQL;

public class SqlQuery {
	private static Connection connection = StartMySQL.getInstance();
	
	public static List<Articolo> visualizzaArticoli() {
		LinkedList<Articolo> listaArticoli = new LinkedList<Articolo>();
		   String query = "SELECT p.* FROM prodotti p LEFT JOIN prodotto_venduto pv ON p.id = pv.ID WHERE pv.ID IS NULL AND p.data_fine < CURRENT_DATE()";
	       try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	           ResultSet resultSet = preparedStatement.executeQuery();

	           while (resultSet.next()) {
		           int id = resultSet.getInt("id");
		           String titolo = resultSet.getString("titolo");
	               String descrizione = resultSet.getString("descrizione");
	               byte[] immagine = resultSet.getBytes("immagine"); 
	               float prezzo = resultSet.getFloat("prezzo"); 
	               LocalDateTime dataInizio = resultSet.getTimestamp("data_inserimento").toLocalDateTime();
	               LocalDateTime dataFine = resultSet.getTimestamp("data_fine").toLocalDateTime();

	               Articolo articolo = Articolo.newBuilder()
	            		   						.setId(id)
	                                            .setNome(titolo)
	                                            .setDescrizione(descrizione)
	                                            .setImmagine(ByteString.copyFrom(immagine)).setValorePartenza(prezzo)
	                                            .setDataInizio(dataInizio.toString()).setDataFine(dataFine.toString())
	                                            .build();
	               
	               listaArticoli.add(articolo);
	           }
	       }
	    catch (SQLException e) {
	       e.printStackTrace();
	   }
           return listaArticoli;
	}
	
	public static List<Articolo> visualizzaArticoliVenduti() {
		LinkedList<Articolo> listaArticoli = new LinkedList<Articolo>();

		   String query = "SELECT p.* FROM prodotti p JOIN prodotto_venduto pv ON p.id = pv.ID";   
		   
		   try {
		         PreparedStatement showStatement = connection.prepareStatement(query);

		            ResultSet resultSet = showStatement.executeQuery();

		            while (resultSet.next()) {
		            	int id = resultSet.getInt("id");
		                String titolo = resultSet.getString("titolo");
		                String descrizione = resultSet.getString("descrizione");
		                byte[] immagine = resultSet.getBytes("immagine"); 
		                float prezzo = resultSet.getFloat("prezzo"); 
		                LocalDateTime dataInizio = resultSet.getTimestamp("data_inserimento").toLocalDateTime();
		                LocalDateTime dataFine = resultSet.getTimestamp("data_fine").toLocalDateTime();

		                Articolo articolo = Articolo.newBuilder()
		                							 .setId(id)
		                                             .setNome(titolo)
		                                             .setDescrizione(descrizione)
		                                             .setImmagine(ByteString.copyFrom(immagine)).setValorePartenza(prezzo)
		                                             .setDataInizio(dataInizio.toString()).setDataFine(dataFine.toString())
		                                             .build();
			           listaArticoli.add(articolo);
		            }
		        }
		     catch (SQLException e) {
		        e.printStackTrace();
		    }
		   return listaArticoli;
	}


	public static List<Articolo> visualizzaArticoliNonVenduti() {
		LinkedList<Articolo> listaArticoli = new LinkedList<Articolo>();

		   String query = "SELECT p.* FROM prodotti p LEFT JOIN prodotto_venduto pv ON p.id = pv.ID WHERE p.data_fine < CURRENT_DATE() AND pv.ID IS NULL";   
		   
		   try {
		         PreparedStatement showStatement = connection.prepareStatement(query);

		            ResultSet resultSet = showStatement.executeQuery();

		            while (resultSet.next()) {
		            	int id = resultSet.getInt("id");
		                String titolo = resultSet.getString("titolo");
		                String descrizione = resultSet.getString("descrizione");
		                byte[] immagine = resultSet.getBytes("immagine"); 
		                float prezzo = resultSet.getFloat("prezzo"); 
		                LocalDateTime dataInizio = resultSet.getTimestamp("data_inserimento").toLocalDateTime();
		                LocalDateTime dataFine = resultSet.getTimestamp("data_fine").toLocalDateTime();

		                Articolo articolo = Articolo.newBuilder()
		                							 .setId(id)
		                                             .setNome(titolo)
		                                             .setDescrizione(descrizione)
		                                             .setImmagine(ByteString.copyFrom(immagine)).setValorePartenza(prezzo)
		                                             .setDataInizio(dataInizio.toString()).setDataFine(dataFine.toString())
		                                             .build();
			           listaArticoli.add(articolo);
		            }
		        }
		     catch (SQLException e) {
		        e.printStackTrace();
		    }
		   return listaArticoli;
	}
	
	public static int trovaMaggioreOfferente(int articolo_id) {
		/*
		 * clientID = valore di default nel caso in cui non sia trovata nessuna offerta
		 * Significa che il prodotto non risulta venduto
		 */
	    int clientId = -1; 
		String query = "SELECT COUNT(*) AS num_rows FROM offerta WHERE ID_prodotto = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, articolo_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int numRows = resultSet.getInt("num_rows");
                    if(numRows > 0) { 
                    	String sql = "SELECT o.id_cliente FROM offerta o INNER JOIN prodotti p ON o.ID_prodotto = p.id WHERE p.id = ? AND o.prezzo = (SELECT MAX(prezzo) FROM offerta WHERE ID_prodotto = p.id)";
                    	try (PreparedStatement preparedStatement1 = connection.prepareStatement(sql)) {
                    		preparedStatement1.setInt(1, articolo_id);
                    		try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
                    			if (resultSet1.next()) {
                    				clientId = resultSet1.getInt("ID_cliente");
                    			}
                    		}
                    	}
                    }
                }
            }
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }
		return clientId;
	}

}

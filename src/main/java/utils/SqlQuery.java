package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.google.protobuf.ByteString;

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

}

package service;

import astaOnlineProto.AstaOnLine.Articoli;
import astaOnlineProto.AstaOnLine.Articolo;
import astaOnlineProto.AstaOnLine.ArticoloNotifica;
import astaOnlineProto.AstaOnLine.Empty;
import astaOnlineProto.AstaOnLine.MessaggioGenerico;
import astaOnlineProto.AstaOnLine.Offerta;
import astaOnlineProto.AstaOnLine.Utente;
import astaOnlineProto.AstaServiceGrpc.AstaServiceImplBase;
import io.grpc.stub.StreamObserver;
import observerPubSub.PublisherImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import com.google.protobuf.ByteString;

import singleton.StartMySQL;

/**
 * Serie di richieste gRPC
 */
public class GestioneAstaOnLineImpl extends AstaServiceImplBase {
   private Connection connection = StartMySQL.getInstance();
   private PublisherImpl publisher = new PublisherImpl();

   public void registraUtente(Utente request, StreamObserver<MessaggioGenerico> responseObserver) {
      String messaggio = "";
      String checkEmailQuery = "SELECT COUNT(*) FROM clienti WHERE email = ?";

      try {
         PreparedStatement checkEmailStatement = this.connection.prepareStatement(checkEmailQuery);

            checkEmailStatement.setString(1, request.getEmail());
            ResultSet resultSet = checkEmailStatement.executeQuery();
            resultSet.next();
            int emailCount = resultSet.getInt(1);
            if (emailCount > 0) {
               messaggio = "ERRORE - L'email del cliente è già presente nel database.";
            } else {
               String insertClientQuery = "INSERT INTO clienti (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
               PreparedStatement insertClientStatement = this.connection.prepareStatement(insertClientQuery);

              
                  insertClientStatement.setString(1, request.getNome());
                  insertClientStatement.setString(2, request.getCognome());
                  insertClientStatement.setString(3, request.getEmail());
                  insertClientStatement.setString(4, request.getPassword());
                  int rowsAffected = insertClientStatement.executeUpdate();
                  if (rowsAffected > 0) {
                     messaggio = "SUCCESSO - Cliente inserito con successo.";
                  } else {
                     messaggio = "ERRORE - Errore durante l'inserimento del cliente.";
                  }
               

               if (insertClientStatement != null) {
                  insertClientStatement.close();
               }
            }

            MessaggioGenerico response = MessaggioGenerico.newBuilder().setMessaggio(messaggio).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
       

         if (checkEmailStatement != null) {
            checkEmailStatement.close();
         }
      } catch (SQLException var16) {
         var16.printStackTrace();
      }

   }

   public void accediUtente(Utente request, StreamObserver<MessaggioGenerico> responseObserver) {
      String messaggio = "";
      String checkEmailQuery = "SELECT password FROM clienti WHERE email = ?";

      try {
         PreparedStatement checkEmailStatement = this.connection.prepareStatement(checkEmailQuery);

            checkEmailStatement.setString(1, request.getEmail());
            ResultSet resultSet = checkEmailStatement.executeQuery();
            if (resultSet.next()) {
               String storedPassword = resultSet.getString("password");
               if (storedPassword.equals(request.getPassword())) {
                  messaggio = "SUCCESSO - Accesso effettuato con successo.";
               } else {
                  messaggio = "ERRORE - Password errata.";
               }
            } else {
               messaggio = "ERRORE - Email non trovata nel database.";
            }

            MessaggioGenerico response = MessaggioGenerico.newBuilder().setMessaggio(messaggio).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

         if (checkEmailStatement != null) {
            checkEmailStatement.close();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void visualizzaArticoliAcquistati(Utente request, StreamObserver<Articoli> responseObserver) {
	   String query = "SELECT p.* FROM clienti c JOIN prodotto_venduto pv ON c.id_cliente = pv.acquirente JOIN prodotti p ON pv.ID = p.id WHERE c.email = ? AND c.password = ?";   
	   
	   try {
	         PreparedStatement showStatement = this.connection.prepareStatement(query);

	         	showStatement.setString(1, request.getEmail());
	         	showStatement.setString(2, request.getPassword());
	            ResultSet resultSet = showStatement.executeQuery();

	            Articoli.Builder articoliBuilder = Articoli.newBuilder();
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
	                articoliBuilder.addArticoli(articolo);
	            }

	            Articoli articoli = articoliBuilder.build();
	            responseObserver.onNext(articoli);
	            responseObserver.onCompleted();
	        }
	     catch (SQLException e) {
	        e.printStackTrace();
	    }
   }

   public void visualizzaArticoliRegistrati(Utente request, StreamObserver<Articoli> responseObserver) {
	   String query = "SELECT id_cliente FROM clienti WHERE email = ?";
       try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
    	   selectStatement.setString(1, request.getEmail());
           ResultSet resultSelectSet = selectStatement.executeQuery();

           if (resultSelectSet.next()) {
               int idCliente = resultSelectSet.getInt("id_cliente");
               
               // Inserimento dell'offerta nella tabella Offerta
        	   query = "SELECT DISTINCT p.* FROM offerta o JOIN prodotti p ON o.ID_prodotto = p.ID LEFT JOIN prodotto_venduto pv ON p.ID = pv.ID WHERE o.id_cliente = ? AND pv.ID IS NULL";   
               try (PreparedStatement selStatement = connection.prepareStatement(query)) {
            	   selStatement.setInt(1, idCliente);
            	   ResultSet resultSet = selStatement.executeQuery();

            	   Articoli.Builder articoliBuilder = Articoli.newBuilder();
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
   	            		articoliBuilder.addArticoli(articolo);
   	               }
   	               Articoli articoli = articoliBuilder.build();
   	               responseObserver.onNext(articoli);
   	               responseObserver.onCompleted();
               }
           }
       }
	   catch (SQLException e) {
		        e.printStackTrace();
	   }
   }

   public void inviaOfferta(Offerta request, StreamObserver<MessaggioGenerico> responseObserver) {
	   String query = "SELECT prezzo FROM prodotti WHERE id = ?";
	   try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
           preparedStatement.setInt(1, request.getArticoloId());
           ResultSet resultSet = preparedStatement.executeQuery();

           if (resultSet.next()) {
               float prezzoAttuale = resultSet.getFloat("prezzo");
               
               // Confronto il prezzo attuale con il nuovo prezzo proposto
               if (request.getValoreOfferta() <= prezzoAttuale) {
                   // Se il nuovo prezzo è minore o uguale al prezzo attuale, restituisci un messaggio di errore
                   MessaggioGenerico errore = MessaggioGenerico.newBuilder()
                       .setMessaggio("Il nuovo prezzo deve essere maggiore del prezzo attuale.")
                       .build();
                   responseObserver.onNext(errore);
                   responseObserver.onCompleted();
               } else {
            		   String checkProductQuery = "SELECT * FROM prodotto_venduto WHERE ID = ?";
                       try (PreparedStatement checkStatement = connection.prepareStatement(checkProductQuery)) {
                           checkStatement.setInt(1, request.getArticoloId());
                           ResultSet checkResultSet = checkStatement.executeQuery();

                           if (checkResultSet.next()) {
                               // Se l'ID del prodotto è già venduto, restituisci un messaggio di errore
                               MessaggioGenerico errore = MessaggioGenerico.newBuilder()
                                       .setMessaggio("ERRORE - L'asta di questo prodotto risulta conclusa")
                                       .build();
                               responseObserver.onNext(errore);
                               responseObserver.onCompleted();
                           } else {
                        	   String updateQuery = "UPDATE prodotti SET prezzo = ? WHERE id = ?";
                               PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                               updateStatement.setFloat(1, request.getValoreOfferta());
                               updateStatement.setInt(2, request.getArticoloId());
                               updateStatement.executeUpdate();
                               query = "SELECT id_cliente FROM clienti WHERE email = ?";
                               try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
                            	   selectStatement.setString(1, request.getEmailUser());
                                   ResultSet resultSelectSet = selectStatement.executeQuery();

                                   if (resultSelectSet.next()) {
                                       int idCliente = resultSelectSet.getInt("id_cliente");
                                       
                                       // Inserimento dell'offerta nella tabella Offerta
                                       String insertQuery = "INSERT INTO Offerta (ID_cliente, ID_prodotto, prezzo) VALUES (?, ?, ?)";
                                       try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                           insertStatement.setInt(1, idCliente);
                                           insertStatement.setInt(2, request.getArticoloId());
                                           insertStatement.setFloat(3, request.getValoreOfferta());
                                           insertStatement.executeUpdate();
                                           
                                           publisher.subscribe(request.getArticoloId(), idCliente);
                                           publisher.inserisciMessaggioOfferta(idCliente, request.getArticoloId(), request.getValoreOfferta());

                                           // Invia un messaggio di conferma al client
                                           MessaggioGenerico conferma = MessaggioGenerico.newBuilder()
                                               .setMessaggio("SUCCESSO - Offerta inserita con successo.")
                                               .build();
                                           responseObserver.onNext(conferma);
                                           responseObserver.onCompleted();
                                       }
                                   }                       
                               }
                           }
                       }
            	   
               }
           } 
       }
    catch (SQLException e) {
       e.printStackTrace();
       // Invia un messaggio di errore al client in caso di eccezione SQL
       MessaggioGenerico errore = MessaggioGenerico.newBuilder()
           .setMessaggio("ERRORE - Si è verificato un errore durante l'aggiornamento del prezzo.")
           .build();
       responseObserver.onNext(errore);
       responseObserver.onCompleted();
   }
}

   public void getArticoliInVendita(Empty request, StreamObserver<Articoli> responseObserver) {
	   String query = "SELECT p.* FROM prodotti p LEFT JOIN prodotto_venduto pv ON p.id = pv.ID WHERE pv.ID IS NULL AND p.data_fine > CURRENT_DATE()";
       try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
           ResultSet resultSet = preparedStatement.executeQuery();

           Articoli.Builder articoliBuilder = Articoli.newBuilder();
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
               articoliBuilder.addArticoli(articolo);
           }

           Articoli articoli = articoliBuilder.build();
           responseObserver.onNext(articoli);
           responseObserver.onCompleted();
       }
    catch (SQLException e) {
       e.printStackTrace();
   }
}

   public void riceviNotifiche(ArticoloNotifica articoloNotifica, StreamObserver<MessaggioGenerico> responseObserver) {
	   String testoMessaggio;
	   try {
		   testoMessaggio = publisher.notifySubscribers(articoloNotifica.getIndexNotifica(),articoloNotifica.getArticolo().getId());
		   MessaggioGenerico message = MessaggioGenerico.newBuilder().setMessaggio(testoMessaggio).build();
		   responseObserver.onNext(message);
		   responseObserver.onCompleted();
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   }
	}
}

package service;

import astaOnlineProto.AstaOnLine.Articoli;
import astaOnlineProto.AstaOnLine.Articolo;
import astaOnlineProto.AstaOnLine.Empty;
import astaOnlineProto.AstaOnLine.MessaggioGenerico;
import astaOnlineProto.AstaOnLine.Offerta;
import astaOnlineProto.AstaOnLine.Utente;
import astaOnlineProto.AstaServiceGrpc.AstaServiceImplBase;
import io.grpc.stub.StreamObserver;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.google.protobuf.ByteString;

import singleton.StartMySQL;

public class GestioneAstaOnLineImpl extends AstaServiceImplBase {
   private Connection connection = StartMySQL.getInstance();

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

   public void notificaSuccesso(Empty request, StreamObserver<MessaggioGenerico> responseObserver) {
      super.notificaSuccesso(request, responseObserver);
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
	                String titolo = resultSet.getString("titolo");
	                String descrizione = resultSet.getString("descrizione");
	                byte[] immagine = resultSet.getBytes("immagine"); 
	                float prezzo = resultSet.getFloat("prezzo"); 
	                LocalDateTime dataInizio = resultSet.getTimestamp("data_inserimento").toLocalDateTime();
	                LocalDateTime dataFine = resultSet.getTimestamp("data_fine").toLocalDateTime();

	                Articolo articolo = Articolo.newBuilder()
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
      super.visualizzaArticoliRegistrati(request, responseObserver);
   }

   public void inviaOfferta(Offerta request, StreamObserver<MessaggioGenerico> responseObserver) {
      super.inviaOfferta(request, responseObserver);
   }

   public void getArticoliInVendita(Empty request, StreamObserver<Articoli> responseObserver) {
	   String query = "SELECT p.* FROM prodotti p LEFT JOIN prodotto_venduto pv ON p.id = pv.ID WHERE pv.ID IS NULL";
       try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
           ResultSet resultSet = preparedStatement.executeQuery();

           Articoli.Builder articoliBuilder = Articoli.newBuilder();
           while (resultSet.next()) {
               String titolo = resultSet.getString("titolo");
               String descrizione = resultSet.getString("descrizione");
               byte[] immagine = resultSet.getBytes("immagine"); 
               float prezzo = resultSet.getFloat("prezzo"); 
               LocalDateTime dataInizio = resultSet.getTimestamp("data_inserimento").toLocalDateTime();
               LocalDateTime dataFine = resultSet.getTimestamp("data_fine").toLocalDateTime();

               Articolo articolo = Articolo.newBuilder()
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
 

   public void riceviNotifiche(Empty request, StreamObserver<MessaggioGenerico> responseObserver) {
      super.riceviNotifiche(request, responseObserver);
   }
}

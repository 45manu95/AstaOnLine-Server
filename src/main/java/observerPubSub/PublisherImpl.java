package observerPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import utils.SqlQuery;

public class PublisherImpl implements Publisher {
	//per tenere traccia degli utenti iscritti
	private static Map<Integer, Set<Integer>> subscribersArticleMap = new HashMap<Integer, Set<Integer>>();
	//per bloccarli in attesa di notifica
	private static Map<Integer, LinkedList<Semaphore>> attendiNotifica = new HashMap<Integer,LinkedList<Semaphore>>();
	//lista messaggi in arrivo
	private static Map<Integer, LinkedList<String>> messaggiArticoli = new HashMap<Integer, LinkedList<String>>();

	@Override
	public void subscribe(int articolo_id,int subscriber) {
		if(subscribersArticleMap.containsKey(articolo_id)){
    		Set<Integer> subscribers = subscribersArticleMap.get(articolo_id);
    		if(!subscribers.contains(subscriber)) {
    			subscribers.add(subscriber);
        		subscribersArticleMap.put(articolo_id, subscribers);
    		}   			
        }
    	else {
    		Set<Integer> subscribers = new HashSet<Integer>();
    		LinkedList<String> messages = new LinkedList<String>();
    		subscribers.add(subscriber);
    		subscribersArticleMap.put(articolo_id, subscribers);
    		LinkedList<Semaphore> sem = new LinkedList<Semaphore>();
    		sem.add(new Semaphore(0));
    		attendiNotifica.put(articolo_id, sem);
    		messaggiArticoli.put(articolo_id, messages);
        }   		
	}

	@Override
	public void unsubscribe(int subscriber, int articolo_id) {
	       if(subscribersArticleMap.containsKey(articolo_id)){
	        	Set<Integer> subscribers = subscribersArticleMap.get(articolo_id);
	        	subscribers.remove(subscriber);
	        	subscribersArticleMap.put(articolo_id, subscribers);
	        }			
	}

	@Override
	public String notifySubscribers(int indexNotifica,int articolo_id) throws InterruptedException {
		String message = "";
		LinkedList<Semaphore> sem = attendiNotifica.get(articolo_id);
		sem.get(indexNotifica).acquire();
		//problema il nuovo client parte da 0 come index
		if(messaggiArticoli.get(articolo_id).get(indexNotifica) != null) {
			message = messaggiArticoli.get(articolo_id).get(indexNotifica);
			attendiNotifica.get(articolo_id).add(new Semaphore(0));
		}
		return message;
	}
		

	public void inserisciMessaggioOfferta(int cliente_id, int articolo_id, float prezzo) {
		String builder = "Il cliente "+cliente_id+" ha offerto "+prezzo+" euro per articolo "+articolo_id;
		LinkedList<String> messaggi = messaggiArticoli.get(articolo_id);
		messaggi.add(builder);
		messaggiArticoli.put(articolo_id, messaggi);
		int numUtenti = subscribersArticleMap.get(articolo_id).size();
		LinkedList<Semaphore> sem = attendiNotifica.get(articolo_id);
		for(Semaphore single : sem) {
			single.release(numUtenti);
		}
		//metodo sql che mette il tutto nel database
	}
	
	public void pubblicaVincitore(int articolo_id) {
		String builder;
		int cliente_id = SqlQuery.trovaMaggioreOfferente(articolo_id);
		if(cliente_id != -1) {
			builder = "Asta CONCLUSA per il prodotto "+articolo_id+" - Ha vinto il cliente "+cliente_id;
			LinkedList<String> messaggi = messaggiArticoli.get(articolo_id);
			messaggi.add(builder);
			messaggiArticoli.put(articolo_id, messaggi);
			int numUtenti = subscribersArticleMap.get(articolo_id).size();
			LinkedList<Semaphore> sem = attendiNotifica.get(articolo_id);
			for(Semaphore single : sem) {
				single.release(numUtenti);
			}
		}
		else {
			builder = "Asta CONCLUSA per il prodotto "+articolo_id+" - nessun vincitore";
		}
		
		//metodo sql che mette il tutto nel database
		//metodo sql che mette il prodotto in prodotto finito
		//metodo che non fa inviare offerte dopo che l'asta risulta conclusa
		//ogni volta che si invoca il dispose su finestraHome si chiude tutto (errore thread) e magari i thread si resettano
		//i messaggi al invio della offerta vengono aggiunti ai vecchi replicandoli, magari resettare prima
	}
}

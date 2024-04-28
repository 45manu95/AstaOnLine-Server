package observerPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;



public class PublisherImpl implements Publisher {
	//per tenere traccia degli utenti iscritti
	private Map<Integer, Set<Integer>> subscribersArticleMap = new HashMap<Integer, Set<Integer>>();
	//per bloccarli in attesa di notifica
	private Map<Integer, Semaphore> attendiNotifica = new HashMap<Integer, Semaphore>();
	//lista messaggi in arrivo
	private Map<Integer, LinkedList<String>> messaggiArticoli = new HashMap<Integer, LinkedList<String>>();

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
    		attendiNotifica.put(articolo_id, new Semaphore(0));
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
	public String notifySubscribers(int articolo_id) throws InterruptedException {
		String message = "";
		if(messaggiArticoli.get(articolo_id).size() > 0) {
			int numUtenti = subscribersArticleMap.get(articolo_id).size();
			attendiNotifica.get(articolo_id).release(numUtenti);
			message = messaggiArticoli.get(articolo_id).getFirst();
			messaggiArticoli.get(articolo_id).removeFirst();
		}
		else {
			attendiNotifica.get(articolo_id).acquire();
		}

		return message;
	}
		

	public void inserisciMessaggioOfferta(int cliente_id, int articolo_id, float prezzo) {
		String builder = "Il cliente "+cliente_id+" ha offerto "+prezzo+" euro per articolo "+articolo_id;
		LinkedList<String> messaggi = messaggiArticoli.get(articolo_id);
		messaggi.add(builder);
		messaggiArticoli.put(articolo_id, messaggi);
		//metodo sql che mette il tutto nel database
	}
}

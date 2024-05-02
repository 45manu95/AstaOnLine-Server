package observerPubSub;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import utils.SqlQuery;

/**
 * DESIGN PATTERN OBSERVER
 * Qui viene implementata la classe concreta del Publisher. Il Subscriber non viene
 * rappresentato da una classe in se, ma viene inteso il client come intera entità
 * e i rapporti con questa classe vengono facilitati dal fatto che vengono richiamati
 * i metodi gRPC.
 * La logica di funzionamento divide l'OBSERVER in due lati. Dalla parte del server
 * abbiamo il Publisher, dalla parte del client il Subscriber, rappresentato dal client stesso
 */
public class PublisherImpl implements Publisher {
	//per tenere traccia degli utenti iscritti
	private static Map<Integer, Set<Integer>> subscribersArticleMap = new HashMap<Integer, Set<Integer>>();
	//per bloccarli in attesa di notifica
	private static Map<Integer, LinkedList<Semaphore>> attendiNotifica = new HashMap<Integer,LinkedList<Semaphore>>();
	//lista messaggi in arrivo
	private static Map<Integer, LinkedList<String>> messaggiArticoli = new HashMap<Integer, LinkedList<String>>();
	
	//oggetto utilizzato per garantire concorrenza nell'accesso alle chiamate
    private static final Object lock = new Object();


	@Override
	public void subscribe(int articolo_id,int subscriber) {
        synchronized (lock) {
        	if(subscribersArticleMap.containsKey(articolo_id)){
        		Set<Integer> subscribers = subscribersArticleMap.get(articolo_id);
        		if(!subscribers.contains(subscriber)) {
        			subscribers.add(subscriber);
            		subscribersArticleMap.put(articolo_id, subscribers);
        		}   			
            }
        	else {
        		//viene creato il tutto
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
	}
	
	/**
	 * Il seguente metodo viene utilizzato per eliminare dalla struttura dati
	 * tutti i riferimenti all'articolo e a chi stava seguendo le sue notifiche
	 * una volta conclusa l'asta. Viene comunque commentato il codice per eliminare
	 * un singolo utente da un articolo nel caso in cui in futuro viene resa 
	 * disponibile una funzione secondo cui l'acquirente si disiscrive dall'asta di
	 * uno specifico oggetto.
	 */
	@Override
	public void unsubscribe(int subscriber, int articolo_id) {
        synchronized (lock) {
 	       if(subscribersArticleMap.containsKey(articolo_id)){
	    	   	subscribersArticleMap.remove(articolo_id);
	        	//Set<Integer> subscribers = subscribersArticleMap.get(articolo_id);
	        	//subscribers.remove(subscriber);
	        	//subscribersArticleMap.put(articolo_id, subscribers);
	        }	
        }		
	}

	/**
	 * Nelle strutture dati una volta identificato l'id articolo si ottiene una
	 * LinkedList di semafori o di stringhe (messaggi). Ogni posizione della linkedList
	 * va a riferirsi ad uno specifico messaggio di quel particolare articolo.
	 * Esempio alla posizione 0 troviamo il primo messaggio generato per quell'articolo ecc.
	 * I semafori servono proprio a bloccare i client che vogliono una notifica che ancora 
	 * non risulta creata
	 * @param articolo_id
	 * @param indexNotifica
	 */
	@Override
	public String notifySubscribers(int indexNotifica,int articolo_id) throws InterruptedException {
        synchronized (lock) {
    		String message = "";
    		LinkedList<Semaphore> sem = attendiNotifica.get(articolo_id);
    		sem.get(indexNotifica).acquire();
    		//problema il nuovo client parte da 0 come index
    		if(messaggiArticoli.get(articolo_id).get(indexNotifica) != null) {
    			message = messaggiArticoli.get(articolo_id).get(indexNotifica);
    			//Aggiungiamo il prossimo semaforo in modo che il client possa ricercare la prossima notifica
    			attendiNotifica.get(articolo_id).add(new Semaphore(0));
    		}
    		return message;
        }
	}

	/**
	 * Si genera il messaggio di un'offerta appena creata liberando un certo numero di posti 
	 * equivalenti al numero di utenti iscritti per quel determinato prodotto. Serviranno per 
	 * liberare i semafori.
	 * @param cliente_id
	 * @param articolo_id
	 * @param prezzo
	 */
	public void inserisciMessaggioOfferta(int cliente_id, int articolo_id, float prezzo) {
        synchronized (lock) {
    		String builder = ottieniDataCorrente()+" - Il cliente "+cliente_id+" ha offerto "+prezzo+" euro per articolo "+articolo_id;
    		LinkedList<String> messaggi = messaggiArticoli.get(articolo_id);
    		messaggi.add(builder);
    		messaggiArticoli.put(articolo_id, messaggi);
    		int numUtenti = subscribersArticleMap.get(articolo_id).size();
    		LinkedList<Semaphore> sem = attendiNotifica.get(articolo_id);
    		for(Semaphore single : sem) {
    			single.release(numUtenti);
    		}
    		int notifica_id = SqlQuery.inserisciNotifica(articolo_id, builder);
    		SqlQuery.inserisciRicezione(notifica_id, cliente_id, articolo_id);	
        }
	}
	
	/**
	 * Si va a trovare il vincitore per poi notificarlo a tutti i subscriber interessati
	 * @param articolo_id
	 */
	public void pubblicaVincitore(int articolo_id) {
        synchronized (lock) {
    		String builder;
    		int cliente_id = SqlQuery.trovaMaggioreOfferente(articolo_id);
    		if(cliente_id != -1) {
    			builder = ottieniDataCorrente()+" - Asta CONCLUSA per il prodotto "+articolo_id+" - Ha vinto il cliente "+cliente_id;
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
    			builder = ottieniDataCorrente()+" - Asta CONCLUSA per il prodotto "+articolo_id+" - nessun vincitore";
    		}
    		int notifica_id = SqlQuery.inserisciNotifica(articolo_id, builder);
    		SqlQuery.inserisciRicezione(notifica_id, cliente_id, articolo_id);
    		this.unsubscribe(-1, articolo_id);
        }
	}
		
	private String ottieniDataCorrente() {
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(calendar.getTime());
	}
	
	
}

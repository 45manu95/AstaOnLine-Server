package observerPubSub;

/**
 * DESIGN PATTERN OBSERVER
 * Per iscrivere i subscriber (i vari client) ai relativi prodotti interessati, per poi
 * ricevere notifiche.
 */
public interface Publisher {	
    public void subscribe(int id_cliente, int articolo_id);
    public void unsubscribe(int id_cliente, int articolo_id);
    public String notifySubscribers(int articolo_id, int notifica) throws InterruptedException;
}

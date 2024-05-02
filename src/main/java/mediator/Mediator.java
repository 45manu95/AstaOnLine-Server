package mediator;

import javax.swing.JComponent;

/**
 * DESIGN PATTERN MEDIATOR
 * Utilizzato per disaccoppiare l'azione di un certo evento scatenata da un component
 * con la parte che si occuperà di gestire l'azione
 * Questa interfaccia funge da Mediator
 */
public interface Mediator {
	/**
	 * gestisce una determinata azione in base al component passato per parametro
	 * @param component
	 */
   void notify(JComponent var1);
}
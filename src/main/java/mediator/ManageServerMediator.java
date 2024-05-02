package mediator;

import command.CloseServerCommand;
import command.StartServerCommand;
import io.grpc.Server;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * DESIGN PATTERN MEDIATOR
 * Per incapsulare il modo in si interagisce per accedere al sistema. 
 * In questo caso come struttura generale questa classe corrisponde a Mediator1,
 * il quale implementa la sua interfaccia Mediator
 */
public class ManageServerMediator implements Mediator {
   private JTextField start;
   private JTextField close;
   private Server server;
   private int port;

   public ManageServerMediator(JTextField start, JTextField close, int port) {
      this.start = start;
      this.close = close;
      this.port = port;
   }

   public void notify(JComponent component) {
      if (component == this.start) {
         StartServerCommand start = new StartServerCommand(this.port);
         start.execute();
         this.server = start.getServer();
      }

      if (component == this.close) {
         (new CloseServerCommand(this.server)).execute();
      }

   }
}
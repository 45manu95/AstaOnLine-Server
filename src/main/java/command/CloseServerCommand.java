package command;

import io.grpc.Server;

/**
 * DESIGN PATTERN COMMAND
 * Questa classe rappresenta nella struttura generale il ConcreteCommand, la quale
 * va ad implementare l'interfaccia che nella struttura generale si riferisce al
 * Command (Command). L'invoker in questo caso risulta rappresentato 
 * dal Mediator corrispondente.
 */
public class CloseServerCommand implements Command{
	Server server;
	
	public CloseServerCommand(Server server) {
		this.server = server;
	}

	@Override
	public void execute() {
		server.shutdown();
		try {
            server.awaitTermination();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
	}
}

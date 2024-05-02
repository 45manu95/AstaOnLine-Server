package command;

import java.io.IOException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.GestioneAstaOnLineImpl;

/**
 * DESIGN PATTERN COMMAND
 * Questa classe rappresenta nella struttura generale il ConcreteCommand, la quale
 * va ad implementare l'interfaccia che nella struttura generale si riferisce al
 * Command (Command). L'invoker in questo caso risulta rappresentato 
 * dal Mediator corrispondente.
 */
public class StartServerCommand implements Command {
	Server server;
	int port;
	
	public StartServerCommand(int port) {
		this.port = port;
	}
	
	public Server getServer() {
		return server;
	}
	
	@Override
	public void execute() {
		server = ServerBuilder.forPort(port).addService(new GestioneAstaOnLineImpl()).build();
        try {
            server.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
}
package command;

import java.io.IOException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.GestioneAstaOnLineImpl;

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
package command;

import io.grpc.Server;

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

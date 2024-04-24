package main;

import gui.FinestraHome;

public class Main {
	public static void main(String[] args) {
        final int port=8989;
		
		FinestraHome finestra = new FinestraHome(port);
		finestra.setVisible(true);
		
		
	}
}

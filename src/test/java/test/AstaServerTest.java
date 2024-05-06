package test;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.ImageIcon;
import org.junit.jupiter.api.Test;
import command.AggiungiProdottoCommand;

class AstaServerTest {

	private AggiungiProdottoCommand aggiungiProdotto;
	private String title;
	private String description;
	private float firstPrice;
	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	private ImageIcon icon;

	@Test
	void aggiungiProdotto() throws Exception {
		title = "Test prodotto 1";
		description = "Descrizione prodotto 1";
		firstPrice = 18;
		day = 8;
		month = 9;
		year = 2024;
		hour = 16;
		minute = 57;
		icon = new ImageIcon("src/main/resources/images/image.png");
		aggiungiProdotto = new AggiungiProdottoCommand(title, description, firstPrice, day, month, year, hour, minute, icon);
		assertNotNull(aggiungiProdotto);
		aggiungiProdotto.actionPerformed(null);
		aggiungiProdotto = null;
		assertNull(aggiungiProdotto);
	}

}

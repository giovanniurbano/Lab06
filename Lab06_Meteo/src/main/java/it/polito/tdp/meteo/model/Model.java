package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO mDao;
	private List<Rilevamento> migliore;
	private List<Citta> citta;
	private List<Rilevamento> partenza;
	private int costoMigliore;
	
	public Model() {
		mDao = new MeteoDAO();
		citta = mDao.getCitta();
	}

	// of course you can change the String output with what you think works best
	public Map<String, Double> getUmiditaMedia(int mese) {
		return mDao.getUmiditaMedia(mese);
	}
	
	// of course you can change the String output with what you think works best
	public List<Rilevamento> trovaSequenza(int mese) {
		List<Rilevamento> parziale = new ArrayList<Rilevamento>();
		migliore = new ArrayList<Rilevamento>();
		costoMigliore = COST*100;
		
		this.cerca(parziale, 0, mese);
		
		return migliore;
	}
	
	private void cerca(List<Rilevamento> parziale, int livello, int mese) {
		//caso terminale
		if(livello == NUMERO_GIORNI_TOTALI) {
			return;
		}
		
		for(Citta c : citta) {
			partenza = new ArrayList<Rilevamento>(mDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
			
			if(c.getCounter() == NUMERO_GIORNI_CITTA_MAX)
				return;
				
			if(c.getCounter() < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
				parziale.add(partenza.get(livello));
				c.increaseCounter();
				
				cerca(parziale, livello+1, mese);
				parziale.remove(partenza.get(livello));
				c.setCounter(c.getCounter()-1);
			}
		}
		
		int costo = this.calcolaCosto(parziale);
		if(costo < costoMigliore) {
			migliore = new ArrayList<Rilevamento>(parziale);
			costoMigliore = costo;
			return;
		}
		
	}

	private int calcolaCosto(List<Rilevamento> parziale) {
		int costo = 0;
		for(Rilevamento r : parziale) {
			costo += COST + r.getUmidita();
		}
		return costo;
	}
	

}

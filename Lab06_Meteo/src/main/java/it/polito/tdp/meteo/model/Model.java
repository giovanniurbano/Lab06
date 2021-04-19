package it.polito.tdp.meteo.model;

import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO mDao;
	
	public Model() {
		mDao = new MeteoDAO();
	}

	// of course you can change the String output with what you think works best
	public Map<String, Double> getUmiditaMedia(int mese) {
		return mDao.getUmiditaMedia(mese);
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		return "TODO!";
	}
	

}

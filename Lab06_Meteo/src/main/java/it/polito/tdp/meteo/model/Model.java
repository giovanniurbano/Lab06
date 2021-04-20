package it.polito.tdp.meteo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO mDao;
	private List<Rilevamento> migliore;
	private List<Citta> citta;
	private Map<LocalDate, List<Rilevamento>> partenza;
	private Map<String, Integer> consecutivi;
	private Map<String, Integer> assoluti;
	private int costoMigliore;
	
	public Model() {
		mDao = new MeteoDAO();
		consecutivi = new TreeMap<String, Integer>();
		consecutivi.put("Genova", 0);
		consecutivi.put("Torino", 0);
		consecutivi.put("Milano", 0);
		assoluti = new TreeMap<String, Integer>();
		assoluti.put("Genova", 0);
		assoluti.put("Torino", 0);
		assoluti.put("Milano", 0);
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
		partenza = new TreeMap<LocalDate, List<Rilevamento>>(mDao.getAllRilevamentiMese(mese));
		
		this.cerca(parziale, 0, mese);
		
		return migliore;
	}
	
	private void cerca(List<Rilevamento> parziale, int livello, int mese) {
		//caso terminale
		if(livello == NUMERO_GIORNI_TOTALI) {
			return;
		}
		
		int costo = this.calcolaCosto(parziale);
		if(costo < costoMigliore && costo > 0) {
			migliore = new ArrayList<Rilevamento>(parziale);
			costoMigliore = costo;
			return;
		}
		
		LocalDate d = LocalDate.of(2013, mese, livello+1);
		Rilevamento min = this.minUmidita(partenza.get(d)); 
		if(assoluti.get(min.getLocalita()) == NUMERO_GIORNI_CITTA_MAX ) {
			return;
		}
		
		if(consecutivi.get(min.getLocalita()) < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
			parziale.add(min);
			consecutivi.replace(min.getLocalita(), consecutivi.get(min.getLocalita())+1);
			assoluti.replace(min.getLocalita(), assoluti.get(min.getLocalita())+1);
			cerca(parziale, livello+1, mese);
			parziale.remove(min);
			consecutivi.replace(min.getLocalita(), consecutivi.get(min.getLocalita())-1);
			assoluti.replace(min.getLocalita(), assoluti.get(min.getLocalita())-1);
		}
		/*partenza.get(d).remove(min);
		min = this.minUmidita(partenza.get(d));
		cerca(parziale, livello, mese);*/
	}

	private int calcolaCosto(List<Rilevamento> parziale) {
		int costo = 0;
		for(Rilevamento r : parziale) {
			costo += COST + r.getUmidita();
		}
		return costo;
	}
	
	private Rilevamento minUmidita(List<Rilevamento> rr) {
		int min = 100;
		Rilevamento rMin = null;
		for(Rilevamento r : rr) {
			if(r.getUmidita() < min) {
				min = r.getUmidita();
				rMin = r;
			}
		}
		return rMin;
	}
	

}

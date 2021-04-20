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
	//private Map<String, Integer> assoluti;
	private int costoMigliore;
	
	public Model() {
		mDao = new MeteoDAO();
		consecutivi = new TreeMap<String, Integer>();
		consecutivi.put("Genova", 0);
		consecutivi.put("Torino", 0);
		consecutivi.put("Milano", 0);
		/*assoluti = new TreeMap<String, Integer>();
		assoluti.put("Genova", 0);
		assoluti.put("Torino", 0);
		assoluti.put("Milano", 0);*/
		citta = mDao.getCitta();
	}

	
	public Map<String, Double> getUmiditaMedia(int mese) {
		return mDao.getUmiditaMedia(mese);
	}
	
	//LIVELLO = giorno 
	//PARZIALE = lista di rilevamenti
	
	public List<Rilevamento> trovaSequenza(int mese) {
		List<Rilevamento> parziale = new ArrayList<Rilevamento>();
		migliore = new ArrayList<Rilevamento>();
		costoMigliore = COST*100;
		partenza = new TreeMap<LocalDate, List<Rilevamento>>(mDao.getAllRilevamentiMese(mese));
		for(Citta c : citta)
			c.setRilevamenti(mDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		
		this.cerca(parziale, 0, mese);
		
		return migliore;
	}
	
	private void cerca(List<Rilevamento> parziale, int livello, int mese) {
		//caso terminale
		if(livello == NUMERO_GIORNI_TOTALI) { //controllo giorni totali
			String c = "";
			int cons = 1;
			int costo = 0; // o 100?
			for(Rilevamento r : parziale) {
				if(c.compareTo(r.getLocalita()) == 0) { //controllo giorni consecutivi
					cons++;	
					costo += r.getUmidita();
				}
				else {
					if(!parziale.get(0).equals(r) && cons < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)
						return;
					cons = 0;
					costo += COST + r.getUmidita();
				}
				c = r.getLocalita();
			}
			if(costo < costoMigliore) {
				migliore = new ArrayList<Rilevamento>(parziale);
				costoMigliore = costo;
				System.out.println(costoMigliore);
				return;
			}
		}
		
		for(Citta c : citta) {
			if(c.getCounter() == NUMERO_GIORNI_CITTA_MAX) //controllo giorni assoluti 
				return;
			
			/*if(livello > 1)
				if(!parziale.get(livello).getLocalita().equals(parziale.get(livello-1).getLocalita()))
					consecutivi.replace(c.getNome(), 0);*/
			
			
			parziale.add(c.getRilevamenti().get(livello));	//generazione sottoproblemi
			//consecutivi.replace(c.getNome(), consecutivi.get(c.getNome())+1);
			c.increaseCounter();
			cerca(parziale, livello+1, mese);
			
			parziale.remove(c.getRilevamenti().get(livello));	//BACKTRACKING
			//consecutivi.replace(c.getNome(), consecutivi.get(c.getNome())-1);
			c.setCounter(c.getCounter()-1);
			//cerca(parziale, livello+1, mese);
		}
		
		/*if(costo < costoMigliore && costo > 0) {
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
		else {
			partenza.get(d).remove(min);
			min = this.minUmidita(partenza.get(d));
			cerca(parziale, livello, mese);
		}*/
	}
	
	/*private int totUmidita(List<Rilevamento> parziale) {
		int tot = 0;
		for(Rilevamento r : parziale) {
			tot += r.getUmidita();
		}
		return tot;
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
	}*/
	

}

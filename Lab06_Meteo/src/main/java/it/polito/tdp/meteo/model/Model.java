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
	private int costoMigliore;
	
	public Model() {
		mDao = new MeteoDAO();
		citta = mDao.getCitta();
	}

	
	public Map<String, Double> getUmiditaMedia(int mese) {
		return mDao.getUmiditaMedia(mese);
	}
	
	public int getCostoMigliore() {
		return costoMigliore;
	}
	
	//LIVELLO = giorno 
	//PARZIALE = lista di rilevamenti
	
	public List<Rilevamento> trovaSequenza(int mese) {
		List<Rilevamento> parziale = new ArrayList<Rilevamento>();
		migliore = new ArrayList<Rilevamento>();
		costoMigliore = COST*100;
		
		for(Citta c : citta)
			c.setRilevamenti(mDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		
		this.cerca(parziale, 0);
		
		return migliore;
	}
	
	private void cerca(List<Rilevamento> parziale, int livello) {
		//caso terminale
		if(livello == NUMERO_GIORNI_TOTALI) { //controllo giorni totali
			String c = parziale.get(0).getLocalita();
			int costo = 0;
			for(Rilevamento r : parziale) {
				costo += r.getUmidita();
				
				if(c.compareTo(r.getLocalita()) != 0)
					costo += COST ;
				
				c = r.getLocalita();
			}
			if(costo < costoMigliore) {
				migliore = new ArrayList<Rilevamento>(parziale);
				costoMigliore = costo;
				return;
			}
		}
		
		for(Citta c : citta) {
			if(c.getCounter() < NUMERO_GIORNI_CITTA_MAX //controllo giorni assoluti 
			 && this.isValid(parziale, c)) { //controllo consecutivi
				
				parziale.add(c.getRilevamenti().get(livello));	//generazione sottoproblemi
				c.increaseCounter();
				cerca(parziale, livello+1);
				
				parziale.remove(c.getRilevamenti().get(livello));	//BACKTRACKING
				c.setCounter(c.getCounter()-1);
			}
		}
	}
	
	private boolean isValid(List<Rilevamento> parziale, Citta t) {
		if(parziale.size() == 0) 
			return true; //qualsiasi scelta è valida
		
		if(parziale.size() < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
			if(parziale.get(parziale.size()-1).getLocalita().compareTo(t.getNome()) == 0)
				return true;
			else
				return false;
		}
		
		//se hai già superato i casi precedenti si può sempre procedere aggiungendo una città uguale alla precedente
		//tanto il controllo sul max di 6 se siamo entrati qui è anch'esso già stato superato
		if(parziale.get(parziale.size()-1).getLocalita().compareTo(t.getNome()) == 0)
			return true;
		
		//infine se gli ultimi tre sono uguali fra di loro posso fare qualsiasi scelta, sarà sempre buona
		if(parziale.get(parziale.size()-1).getLocalita().compareTo(parziale.get(parziale.size()-2).getLocalita()) == 0
			&& parziale.get(parziale.size()-2).getLocalita().compareTo(parziale.get(parziale.size()-3).getLocalita()) == 0)
			return true;
				
		//solo se nessuno dei precedenti va bene, allora è false
		return false;

	}
}

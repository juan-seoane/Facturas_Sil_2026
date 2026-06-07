package com.sil.facturas.domain.records;

import java.util.ArrayList;

public class ConfigData{

//#region CAMPOS
	public String user;
	public Anho anho;
	public RutasTrabajo rutas;
	public ArrayList<Integer> tiposIVA;
	public ArrayList<String> origenesCaja;
//#endregion
public ConfigData() {
}

//#region Constructor
	public ConfigData(String user, Anho anho, RutasTrabajo rutas, ArrayList<Integer> tiposIVA, ArrayList<String> origenesCaja ){
			this.user = user;
			this.anho = anho;
			this.rutas = rutas;
			this.tiposIVA = tiposIVA;
			this.origenesCaja = origenesCaja;
	}
//#endregion

//#region Getters
	public String getUser(){
		return user;
	}

	public Anho getAnho(){
	return anho;
	}

	public RutasTrabajo getRutas(){
	return rutas;
	}

	public ArrayList<Integer> getTiposIVA(){
	return tiposIVA;
	}

	public ArrayList<String> getOrigenesCaja(){
	return origenesCaja;
	}
//#endregion

//#region Setters
	 public void setUser(String user) {
        this.user = user;
    }

    public void setAnho(Anho anho) {
        this.anho = anho;
    }

    public void setRutas(RutasTrabajo rutas) {
        this.rutas = rutas;
    }

    public void setTiposIVA(ArrayList<Integer> tiposIVA) {
        this.tiposIVA = tiposIVA;
    }

    public void setOrigenesCaja(ArrayList<String> origenesCaja) {
        this.origenesCaja = origenesCaja;
    }
//#endregion

//#region toJSON()
	public String toJSON() {
		// REVIEW - 2024-04-28 : Falta el objeto 'TiposGasto' (clase 'Concepto') antes de 'tiposIVA'
		// REVIEW - 2024-05-03 : El campo 'origenesCaja' falta por serializar (no salen con comillas)
		// REVIEW - 2024-05-03 : El archivo RS.rs debería copiarse y renovarse cada anho...
		// STUB   - 2026-03-31 : Falta completar las rutas desde el objeto 'rutas' (clase 'RutasTrabajo') y origenesCaja
		String cadenaResp = "{\n\t\"user\": \"" + this.user +  "\",\n\t\"anho\": { \"anho\": " + this.anho.getAnho() + ", \"trimestre\": " + this.anho.getTrimestre() + " },\n\t\"rutas\": {\n\t\t\"FCT\": \"" + this.rutas.getFCT() + "\",\n\t\t\"RS\": \"" + this.rutas.getRS() + "\",\n\t\t\"CJA\": \"" + this.rutas.getCJA() + "\"\n\t},\n\t\"origenesCaja\":\t" + this.origenesCaja.toString() + "\n}";
		//System.out.println("[ConfigData.java]->\n"+cadenaResp);
		return cadenaResp;
	}
//#endregion
}

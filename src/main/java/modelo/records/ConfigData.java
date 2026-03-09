package modelo.records;

import java.util.ArrayList;

public class ConfigData{

//#region CAMPOS
	public String user;
	public Anho año;
	public RutasTrabajo rutas; 
	public ArrayList<Integer> tiposIVA; 
	public ArrayList<String> origenesCaja;
//#endregion

//#region Constructor
	public ConfigData(String user, Anho año, RutasTrabajo rutas, ArrayList<Integer> tiposIVA, ArrayList<String> origenesCaja ){
			this.user = user;
			this.año = año;
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
	return año;
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

    public void setAnho(Anho año) {
        this.año = año;
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
		// REVIEW - 2024-05-03 : El archivo RS.rs debería copiarse y renovarse cada año... 
		String cadenaResp = "{\n\t\"user\": \"" + this.user +  "\",\n\t\"año\": { \"año\": 2024, \"trimestre\": 2 },\n\t\"rutas\": {\n\t\t\"FCT\": \"./datos/" + this.user.toUpperCase() + "/FCT" + this.año.getAnho() + this.año.getTrimestre() + ".fct\",\n\t\t\"RS\":  \"./datos/" + this.user.toUpperCase() + "/RS.rs\",\n\t\t\"CJA\": \"./datos/" + this.user.toUpperCase() + "/CJA" + this.año.getAnho() + this.año.getTrimestre() + ".cja\"\n\t\t},\n\t\"tiposIVA\": " + this.tiposIVA + ",\n\t\"origenesCaja\": [ \"caja\", \"otros\" ]\n}";
		System.out.println("[ConfigData.java]->\n"+cadenaResp);
		return cadenaResp;
	}
//#endregion
}

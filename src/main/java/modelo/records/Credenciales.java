package modelo.records;

import java.util.ArrayList;

import modelo.base.Config;

public class Credenciales{

	public ArrayList<Contrasena> creds;

	public Credenciales(){
		this.creds= new ArrayList<Contrasena>();
	}

	public Credenciales(ArrayList<Contrasena> listacreds){
			this.creds = listacreds;
	}

	public ArrayList<Contrasena> getlistacreds(){
		return this.creds;
	}

	public static boolean setUsuarioActual(String user){
		if(!((Config.configActual.usuario = user).equals("")))
			return true;
		else
			return false;
	}
	// REVIEW - 2024-04-19 : Comprobar que pase a String en el mismo formato que el JSON de credenciales
	@Override
	public String toString(){
		String listaContr ="";
		int i=0;
		for (Contrasena c : this.creds){
			i++;
			if (i<this.creds.size())
				listaContr += "	" + c.toString()+",\n";
			else
				listaContr += "	" + c.toString()+"\n";
				
		}
		
		return ("{\n \"creds\": [\n" + listaContr+"	]\n}");
	}

	public void setListaCredenciales(ArrayList<Contrasena> listaCreds) {
		this.creds = listaCreds;
	}
}
package modelo.records;
// REVIEW - 24-05-07 : Hacer un método estático para chequear si los archivos de Configuracion de un Usuario determinado existen... public static archivosConfigExisten(String user)()
public class RutasConfig {
    private String user;
    private String rutaconfigdata;
    private String rutamisdatos;
    private String rutauidata;

    public RutasConfig(String user, String rutaconfigdata, String rutamisdatos, String rutauidata) {
        this.user = user;
        this.rutaconfigdata = rutaconfigdata;
        this.rutamisdatos = rutamisdatos;
        this.rutauidata = rutauidata;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRutaConfigData() {
        return rutaconfigdata;
    }

    public void setRutaConfigData(String rutaconfigdata) {
        this.rutaconfigdata = rutaconfigdata;
    }

    public String getRutaMisDatos() {
        return rutamisdatos;
    }

    public void setRutaMisDatos(String rutamisdatos) {
        this.rutamisdatos = rutamisdatos;
    }

    public String getRutaUIData() {
        return rutauidata;
    }

    public void setRutaUIData(String rutauidata) {
        this.rutauidata = rutauidata;
    }

	public String toJSON(){
		String respuesta;
		respuesta = "{\t\"user\": \""+this.user+"\",\t\"rutaconfigdata\": \""+this.rutaconfigdata+"\",\t\"rutamisdatos\": \""+this.rutamisdatos+"\",\t\"rutauidata\": \""+this.rutauidata+"\"\t}";
		return respuesta;
	}
}

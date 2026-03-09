package modelo.records;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

// REVIEW : 09/04/24 - Si las Contrasenas se guardan en formato json, no es necesaria la encriptación
// REVIEW : 12/04/24 - Contrasena sin encriptar (por ahora) y sin Ñ (daba error en el JSON)

public class Contrasena{

    public String usuario;
    public String contra;

    public Contrasena(){
/*
	usuario = "admin";
        try {
            this.contrasena = encriptar("admin");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Contrasena.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            Acceso.imprimir(Acceso.getCanvas(), "La nueva contrasena es  : "+ this.contrasena);
        }catch(Exception ex){
            ex.printStackTrace();
        }
*/
        this.usuario = "admin";
        this.contra = "admin";
  }
  
  public Contrasena(String usuario,String contr){
	
	this.usuario = usuario;
    this.contra = contr;
    /*
        try {	
            this.contrasena = encriptar(contrasena);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Contrasena.class.getName()).log(Level.SEVERE, null, ex);
        }
    */
  }

  public void setUsuario(String user){
    this.usuario = user;
  }

  public String getUsuario(){
    return this.usuario;
  }

  public void setContrasena(String contr){
    /*
    try {
        this.contrasena = encriptar(contrasena);
    } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error al encriptar la contrasena!");
    }
    */
    this.contra = contr;
  }

  public String getContrasena(){
    return this.contra;
  }
/*
  public String encriptar(String contras) throws NoSuchAlgorithmException{
        this.key = KeyGenerator.getInstance("AES").generateKey();
        
        try {
            Cryptographical crypto;
            crypto = AESCryptoImpl.initialize(new AESCryptoKey(this.key));
            String enc = crypto.encrypt(contras);
            System.out.println("La contrasena encriptada es: "+enc);
            return enc;
        } catch (Exception ex) {
            Logger.getLogger(Contrasena.class.getName()).log(Level.SEVERE, null, ex);
        }
       return null;
    }
    public String desencriptar(String contras){
        Cryptographical crypto;
        try {
            crypto = AESCryptoImpl.initialize(new AESCryptoKey(this.key));
            String dec = crypto.decrypt(contras);
            System.out.println("La contrasena desencriptada es: "+dec);
            return dec;
        } catch (Exception ex) {
            Logger.getLogger(Contrasena.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

private static class AESCryptoKey implements CryptoKeyable {
        
        SecretKey key;
        
        public AESCryptoKey(SecretKey key) {
            this.key = key;
        }

        @Override
        public Key getKey() {
            return this.key;
        }
    }
*/
    @Override
    public String toString(){
        return ("{\"usuario\":\""+this.usuario+"\",\"contra\":\""+this.contra+"\"}");
    }
}  

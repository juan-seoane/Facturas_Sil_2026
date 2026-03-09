    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.helpers;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author juanseoane
 */
public class AESCryptoImpl implements Cryptographical {

 private Key key;
 private Cipher ecipher;
 private Cipher dcipher;

 private AESCryptoImpl(Key key) throws NoSuchAlgorithmException,
   NoSuchPaddingException, InvalidKeyException {
  this.key = key;
  this.ecipher = Cipher.getInstance("AES");
  this.dcipher = Cipher.getInstance("AES");
  this.ecipher.init(Cipher.ENCRYPT_MODE, key);
  this.dcipher.init(Cipher.DECRYPT_MODE, key);
 }

 public static Cryptographical initialize(CryptoKeyable key) throws Exception {
  try {
   return new AESCryptoImpl(key.getKey());
  } catch (NoSuchAlgorithmException e) {
   throw new Exception(e);
  } catch (NoSuchPaddingException e) {
   throw new Exception(e);
  } catch (InvalidKeyException e) {
   throw new Exception(e);
  }
 }

 public String encrypt(String plaintext) {
  try {
   String enctext= Base64.getEncoder().encodeToString(plaintext.getBytes());
   return enctext;
  } catch (Exception e) {
   throw new RuntimeException(e);
  }
 }

 public String decrypt(String ciphertext) {
  try { 
    byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
    String decodedString = new String(decodedBytes);
    return decodedString;
  } catch (Exception e) {
   throw new RuntimeException(e);
  }
 }
}
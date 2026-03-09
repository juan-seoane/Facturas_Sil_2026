/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.helpers;

/**
 *
 * @author juanseoane
 */
public interface Cryptographical {
 String encrypt(String plaintext);
 String decrypt(String ciphertext);
}

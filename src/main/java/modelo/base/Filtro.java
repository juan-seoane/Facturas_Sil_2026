/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.base;

import java.util.List;

/**
 *
 * @author Juan Seoane
 */
public interface Filtro{
    List filtrar(List lista);
    boolean filtrar(int index, List lista);
    Object filtrar (Object elemento);
}

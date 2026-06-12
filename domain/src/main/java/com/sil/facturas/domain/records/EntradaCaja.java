package com.sil.facturas.domain.records;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;

/**
 * @author Juan Seoane
 */
public record EntradaCaja(
    int ID, Fecha fecha, Date date, String origen, double caja, boolean haber, Nota nota)
    implements Comparable<EntradaCaja> {

  @Override
  public int compareTo(EntradaCaja b) {
    return this.fecha().compareTo(b.fecha());
  }
}

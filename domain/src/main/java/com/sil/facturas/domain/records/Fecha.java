package com.sil.facturas.domain.records;

import java.time.LocalDate;

public record Fecha(int dia, int mes, int anho) implements Comparable<Fecha> {

  public Fecha {
    // Validación opcional
    if (dia < 1 || dia > 31) throw new IllegalArgumentException("Día inválido");
    if (mes < 1 || mes > 12) throw new IllegalArgumentException("Mes inválido");
    if (anho < 1) throw new IllegalArgumentException("Año inválido");
  }

  public static Fecha fromString(String s) {
    String[] p = s.split("/");
    int dia = Integer.parseInt(p[0]);
    int mes = Integer.parseInt(p[1]);
    int anho = Integer.parseInt(p[2]);
    return new Fecha(dia, mes, anho);
  }

  public static Fecha hoy() {
    LocalDate hoy = LocalDate.now();
    return new Fecha(hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
  }

  @Override
  public String toString() {
    return "%02d/%02d/%04d".formatted(dia, mes, anho);
  }

  @Override
  public int compareTo(Fecha b) {
    LocalDate f1 = LocalDate.of(anho, mes, dia);
    LocalDate f2 = LocalDate.of(b.anho(), b.mes(), b.dia());
    return f1.compareTo(f2);
  }
}

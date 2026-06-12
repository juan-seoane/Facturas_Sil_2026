package com.sil.facturas.domain.interfaces;

public interface IAuthService {
  boolean autenticar(String user, String pass);

  int autenticar(String user, String pass, int intentos);
}

package com.sil.facturas.domain.pojos;

import java.util.List;

import com.sil.facturas.domain.records.Creds;

public class Credenciales {

    public List<Creds> creds;

    public Credenciales() {
    }

    public List<Creds> getCreds() {
        return creds;
    }

    public void setCreds(List<Creds> creds) {
        this.creds = creds;
    }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Credenciales ===\n");

    if (creds == null || creds.isEmpty()) {
      sb.append("(sin credenciales)\n");
    } else {
      for (int i = 0; i < creds.size(); i++) {
        Creds c = creds.get(i);
        sb.append("[").append(i).append("] ");
        sb.append(c != null ? c.toString() : "(null)");
        sb.append("\n");
      }
    }

    sb.append("====================");
    return sb.toString();
  }
}


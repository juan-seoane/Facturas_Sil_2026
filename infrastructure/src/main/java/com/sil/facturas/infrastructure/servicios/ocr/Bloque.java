package com.sil.facturas.infrastructure.servicios.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sil.facturas.infrastructure.records.Rect;

import java.util.ArrayList;
import java.util.List;

public class Bloque {
  public String nombre;
  public Rect zona;

  public String extensibleHacia;
  public String relativoA;
  public double offsetY;

  public Rect cabecera;
  public Rect lineaBase;
  public Rect finalExtractos;

  public double alturaLineaAprox;

  public List<Campo> campos = new ArrayList<>();

public static JsonObject toJSON(Bloque bloque){
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonObject json = gson.toJsonTree(bloque).getAsJsonObject();

    return json;

  }
}

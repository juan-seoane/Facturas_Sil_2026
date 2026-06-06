package com.sil.facturas.infrastructure.records;

public record LineaCsvDTO(String[] columnas) {
    public String get(int index) {
        if (index >= columnas.length) {
        return ""; // o null
        }

        return columnas[index];
    }

    public void set(int index, String value) {
        columnas[index] = value;
    }

    public int length() {
        return columnas.length;
    }
}

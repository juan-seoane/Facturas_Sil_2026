package infraestructure.csv;

public record LineaCsvDTO(String[] columnas) {
    public String get(int index) {
        return columnas[index];
    }

    public void set(int index, String value) {
        columnas[index] = value;
    }
}

package infrastructure.servicios.ocr.aux;

public record Rect(double x1, double y1, double x2, double y2) {
  public double width() {
    return x2 - x1;
  }

  public double height() {
    return y2 - y1;
  }

  public Rect desplazado(double dx, double dy) {
    return new Rect(x1 + dx, y1 + dy, x2 + dx, y2 + dy);
  }
}

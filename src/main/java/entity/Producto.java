package entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "precio_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnit;

    @Column(name = "peso_unit", precision = 10, scale = 2)
    private BigDecimal pesoUnit;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int stock;

    public Producto() {

    }

    public Producto(String nombre, String descripcion, BigDecimal precioUnit, BigDecimal pesoUnit, int stock) {
        this.nombre = nombre.substring(0, Math.min(nombre.length(), 44));
        this.descripcion = descripcion.substring(0, Math.min(descripcion.length(), 199));
        this.precioUnit = precioUnit;
        this.pesoUnit = pesoUnit;
        this.stock = stock;
    }

    public Producto(int id, String nombre, String descripcion, BigDecimal precioUnit, BigDecimal pesoUnit, int stock) {
        this.id = id;
        this.nombre = nombre.substring(0, Math.min(nombre.length(), 44));
        this.descripcion = descripcion.substring(0, Math.min(descripcion.length(), 199));
        this.precioUnit = precioUnit;
        this.pesoUnit = pesoUnit;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "\n\tid = " + id +
                ",\n\tnombre = " + nombre +
                ",\n\tdesc. = " + descripcion +
                ",\n\tprecio_unit = " + precioUnit +
                ",\n\tpeso_unit = " + pesoUnit +
                ",\n\tstock = " + stock;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioUnit() {
        return precioUnit;
    }

    public BigDecimal getPesoUnit() {
        return pesoUnit;
    }

    public int getStock() {
        return stock;
    }
}
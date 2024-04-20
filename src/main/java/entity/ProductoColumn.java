package entity;

import gui.ProductoTableModel;
import jakarta.persistence.*;

@Entity
public abstract class ProductoColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String name;

    public ProductoColumn() {

    }

    public ProductoColumn(int id, String name) {
        this.id = id;
        this.name = name.substring(0, Math.min(name.length(), 44));
    }

    public ProductoColumn(String name) {
        this.name = name.substring(0, Math.min(name.length(), 44));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract Object getValue(Producto p, ProductoTableModel tableModel);
}

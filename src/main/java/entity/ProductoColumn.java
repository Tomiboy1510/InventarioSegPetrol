package entity;

import gui.ProductoTableModel;

public abstract class ProductoColumn {


    private int id;
    private final String name;

    public ProductoColumn(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductoColumn(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract Object getValue(Producto p, ProductoTableModel tableModel);
}

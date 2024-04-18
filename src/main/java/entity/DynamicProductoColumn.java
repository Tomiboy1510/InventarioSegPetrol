package entity;

import gui.ProductoTableModel;
import jakarta.persistence.*;

@Entity
public class DynamicProductoColumn extends ProductoColumn {

    private final String expression;

    public DynamicProductoColumn(int id, String name, String expression) {
        super(id, name);
        this.expression = expression;
    }

    public DynamicProductoColumn(String name, String expression) {
        super(name);
        this.expression = expression;
    }

    @Override
    public Object getValue(Producto p, ProductoTableModel tableModel) {
        return "Val";
    }
}

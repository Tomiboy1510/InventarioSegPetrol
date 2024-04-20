package entity;

import gui.ProductoTableModel;

import java.util.function.Function;

public class BaseProductoColumn extends ProductoColumn {

    private final Function<Producto, Object> extractor;

    public BaseProductoColumn(int id, String name, Function<Producto, Object> extractor) {
        super(id, name);
        this.extractor = extractor;
    }

    @Override
    public Object getValue(Producto p, ProductoTableModel tableModel) {
        return extractor.apply(p);
    }
}

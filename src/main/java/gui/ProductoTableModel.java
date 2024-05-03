package gui;

import entity.*;
import entity.BaseProductoColumn;
import entity.ProductoColumn;
import utils.ExchangeRates;

import javax.swing.table.AbstractTableModel;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ProductoTableModel extends AbstractTableModel {

    private boolean descendingOrder = true;
    private int sortingColumnId;

    public static final String COL_ID = "ID";
    public static final String COL_NAME = "Nombre";
    public static final String COL_DESC = "Descripción";
    public static final String COL_PRICE = "Precio unitario (USD)";
    public static final String COL_WEIGHT = "Peso unitario (KG)";
    public static final String COL_STOCK = "Stock";

    private List<Producto> productos;
    private final List<ProductoColumn> baseColumns;
    private List<ProductoColumn> columns;

    public ProductoTableModel(List<Producto> productos, List<DynamicProductoColumn> dynamicColumns) {
        this.productos = productos;
        this.baseColumns = new ArrayList<>();

        // I'm using negative IDs to ensure that they are distinct from dynamic columns' IDs which start from 1
        // Atomic columns
        baseColumns.add(new BaseProductoColumn(-1, COL_ID, Producto::getId));
        baseColumns.add(new BaseProductoColumn(-2, COL_NAME, (Producto::getNombre)));
        baseColumns.add(new BaseProductoColumn(-3, COL_DESC, (Producto::getDescripcion)));
        baseColumns.add(new BaseProductoColumn(-4, COL_PRICE, (Producto::getPrecioUnit)));
        baseColumns.add(new BaseProductoColumn(-5, COL_WEIGHT, (Producto::getPesoUnit)));
        baseColumns.add(new BaseProductoColumn(-6, COL_STOCK, Producto::getStock));

        // Generated baseColums
        baseColumns.add(new BaseProductoColumn(-7, "Precio unitario (ARS)",
                p -> p.getPrecioUnit()
                        .multiply(ExchangeRates.getDolar())
                        .setScale(2, RoundingMode.HALF_EVEN)
        ));
        baseColumns.add(new BaseProductoColumn(-8, "Precio por kilo (USD)",
                p -> p.getPrecioUnit() == null ? null :
                        p.getPrecioUnit()
                                .divide(p.getPesoUnit(), 2, RoundingMode.HALF_EVEN)
        ));
        baseColumns.add(new BaseProductoColumn(-9, "Precio por kilo (ARS)",
                p -> p.getPrecioUnit() == null ? null :
                        p.getPrecioUnit()
                                .divide(p.getPesoUnit(), 2, RoundingMode.HALF_EVEN)
                                .multiply(ExchangeRates.getDolar())
        ));

        columns = new ArrayList<>();
        columns.addAll(baseColumns);

        // Dynamic columns
        columns.addAll(dynamicColumns);
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getValue(productos.get(rowIndex), this);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getName();
    }

    public List<ProductoColumn> getColumns() {
        return columns;
    }

    public void sortByColumn(int columnIndex) {
        //noinspection unchecked
        Comparator<Producto> comparator = Comparator.comparing(
                (Producto p) -> (Comparable<Object>) columns.get(columnIndex).getValue(p, this),
                Comparator.nullsFirst(Comparator.naturalOrder())
        );

        if (comparator == null)
            return;

        if (descendingOrder)
            comparator = comparator.reversed();

        sortingColumnId = columns.get(columnIndex).getId();
        productos.sort(comparator);
        descendingOrder = !descendingOrder;
    }

    public boolean isBaseColumn(int columnIndex) {
        return (columns.get(columnIndex) instanceof BaseProductoColumn);
    }

    public OptionalInt indexOf(String name) {
        return IntStream.range(0, columns.size())
                .filter(i -> columns.get(i).getName().equals(name))
                .findFirst();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        fireTableDataChanged();
    }

    public void setDynamicColumns(List<DynamicProductoColumn> dynamicColumns) {
        columns = new ArrayList<>();
        columns.addAll(baseColumns);
        columns.addAll(dynamicColumns);

        // If previous sorting column is not present anymore, set sorting by ID
        dynamicColumns.stream()
                .filter(col -> col.getId() == sortingColumnId)
                .findFirst()
                .ifPresentOrElse(
                        (col -> { /* Do nothing */ }),
                        () -> sortingColumnId = -1
                );

        // Sort
        IntStream.range(0, columns.size())
                .filter(i -> columns.get(i).getId() == sortingColumnId)
                .findFirst()
                .ifPresent(i -> {
                    descendingOrder = !descendingOrder;
                    sortByColumn(i);
                });

        fireTableStructureChanged();
    }
}
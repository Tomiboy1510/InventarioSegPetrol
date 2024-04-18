package entity;

import gui.ProductoTableModel;
import jakarta.persistence.*;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;

import java.math.BigDecimal;

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
        // Replace column references by values
        String exp = expression;
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            String colRef = "COL" + tableModel.getColumns().get(i).getId();
            if (exp.contains(colRef)) {
                exp = exp.replace(
                        colRef,
                        tableModel
                                .getColumns()
                                .get(i)
                                .getValue(p, tableModel)
                                .toString()
                );
            }
        }

        // Parse constant expression
        try {
            return BigDecimal.valueOf(evaluate(exp));
        } catch (ParseException | NumberFormatException e) {
            return "Expresión inválida";
        }
    }

    private static double evaluate(String expression) throws ParseException {
        JEP parser = new JEP();
        parser.parseExpression(expression);

        if (parser.hasError())
            throw new ParseException();

        return parser.getValue();
    }
}

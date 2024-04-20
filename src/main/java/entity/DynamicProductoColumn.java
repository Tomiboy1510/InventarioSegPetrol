package entity;

import gui.ProductoTableModel;
import jakarta.persistence.*;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import utils.ExchangeRates;

import java.math.BigDecimal;

@Entity
public class DynamicProductoColumn extends ProductoColumn {

    @Column(name = "expresion", length = 200)
    private String expression;

    public DynamicProductoColumn() {

    }

    public DynamicProductoColumn(String name, String expression) {
        super(name);
        if (expression.isEmpty())
            this.expression = expression;
        else
            this.expression = expression.substring(0, Math.min(expression.length(), 199));
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public Object getValue(Producto p, ProductoTableModel tableModel) {
        // Replace column references by values
        String exp = expression;
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            int colId = tableModel.getColumns().get(i).getId();

            if (exp.contains("COL" + colId)) {
                if (colId >= this.getId())
                    return "Expresión inválida";

                Object value = tableModel.getColumns().get(i).getValue(p, tableModel);
                if (value == null)
                    return null;
                exp = exp.replace("COL" + colId, value.toString());
            }
        }

        if (exp.contains("DOLAR"))
            exp = exp.replace("DOLAR", ExchangeRates.getDolar().toString());

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

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


        // Parse constant expression
        try {
            return BigDecimal.valueOf(evaluate(expression));
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

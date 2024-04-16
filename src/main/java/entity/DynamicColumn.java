package entity;

import jakarta.persistence.*;

@Entity
public class DynamicColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String name;

    @Column(name = "expresion", nullable = false)
    private String expression;

    public DynamicColumn(String name, String expression) {
        this.name = name;
        this.expression = expression;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }
}

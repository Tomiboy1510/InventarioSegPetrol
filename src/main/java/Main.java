import com.formdev.flatlaf.FlatDarkLaf;
import persistence.DynamicColumnDAO;
import persistence.ProductoDAO;
import gui.MainWindow;
import utils.ExchangeRates;
import persistence.SessionFactoryBuilder;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

public class Main {

    /*
        TODO:
            - Terminar GUI para añadir nuevas columnas
            - Persistir columnas
            - Quitar las columnas base generadas que dependen del tipo de cambio (pueden ser columnas dinámicas)
            - Añadir tooltip de expresión para los headers de columnas dinámicas
     */

    public static void main(String[] args) {

        // Set look and feel
        FlatDarkLaf.setup();

        // Get exchange rate from API
        ExchangeRates.requestDolarOficial();

        // Allow user to input exchange rate manually
        while (ExchangeRates.getDolar() == null) {
            String input = JOptionPane.showInputDialog("No se pudo obtener el tipo de cambio, ingréselo manualmente");
            try {
                if (input == null)
                    System.exit(0);
                ExchangeRates.setDolar(new BigDecimal(input));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        SessionFactory sessionFactory = SessionFactoryBuilder.getSessionFactory();
        ProductoDAO productoDAO = new ProductoDAO(sessionFactory);
        DynamicColumnDAO dynamicColumnDAO = new DynamicColumnDAO(sessionFactory);

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(productoDAO, dynamicColumnDAO);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (sessionFactory != null && !sessionFactory.isClosed()) {
                        sessionFactory.close();
                    }
                }
            });
            window.setVisible(true);
        });
    }
}

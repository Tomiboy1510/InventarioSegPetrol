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

        if (sessionFactory == null) {
            JOptionPane.showMessageDialog(null, "No se pudo acceder a la instancia de base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        ProductoDAO productoDAO = new ProductoDAO(sessionFactory);
        DynamicColumnDAO dynamicColumnDAO = new DynamicColumnDAO(sessionFactory);

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(productoDAO, dynamicColumnDAO);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (!sessionFactory.isClosed()) {
                        sessionFactory.close();
                    }
                }
            });
            window.setVisible(true);
        });
    }
}

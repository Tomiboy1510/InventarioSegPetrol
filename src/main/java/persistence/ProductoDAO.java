package persistence;

import entity.Producto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ProductoDAO {

    private final SessionFactory sessionFactory;

    public ProductoDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Producto> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Producto ORDER BY id", Producto.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(int id) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.remove(s.byId(Producto.class).load(id));
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Producto producto) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.persist(producto);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Producto producto) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.merge(producto);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

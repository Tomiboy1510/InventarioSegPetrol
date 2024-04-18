package persistence;

import entity.DynamicProductoColumn;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DynamicColumnDAO {

    private final SessionFactory sessionFactory;

    public DynamicColumnDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<DynamicProductoColumn> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM DynamicProductoColumn", DynamicProductoColumn.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(int id) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.remove(s.byId(DynamicProductoColumn.class).load(id));
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(DynamicProductoColumn column) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.persist(column);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

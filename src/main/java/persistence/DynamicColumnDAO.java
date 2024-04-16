package persistence;

import entity.DynamicColumn;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DynamicColumnDAO {

    private final SessionFactory sessionFactory;

    public DynamicColumnDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<DynamicColumn> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM DynamicColumn", DynamicColumn.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(int id) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.remove(s.byId(DynamicColumn.class).load(id));
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(DynamicColumn column) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.persist(column);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

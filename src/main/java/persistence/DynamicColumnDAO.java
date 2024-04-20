package persistence;

import entity.DynamicProductoColumn;
import entity.ProductoColumn;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
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

    private void delete(int id) {
        try (Session s = sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.remove(s.byId(DynamicProductoColumn.class).load(id));
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteWithDependencies(int id, List<DynamicProductoColumn> columns) {
        ProductoColumn column =  columns.stream()
                .filter(col -> col.getId() == id)
                .findFirst()
                .orElse(null);

        if (column == null)
            return;

        // Use max depth to avoid infinite recursion (to handle circular dependencies)
        deleteWithDependenciesRecursive(id, columns, columns.size());
    }

    private void deleteWithDependenciesRecursive(int id, List<DynamicProductoColumn> columns, int maxDepth) {
        if (maxDepth == 0)
            return;

        // Get dependencies
        List<Integer> idsForRemoval = new ArrayList<>();
        columns.stream()
                .filter(col -> (col.getExpression().contains("COL" + id)))
                .forEach(col -> idsForRemoval.add(col.getId()));

        // Remove dependencies recursively
        idsForRemoval.forEach(r -> columns.removeIf(c -> c.getId() == r));
        idsForRemoval.forEach(r -> deleteWithDependenciesRecursive(r, columns, maxDepth - 1));

        // Remove column
        delete(id);
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

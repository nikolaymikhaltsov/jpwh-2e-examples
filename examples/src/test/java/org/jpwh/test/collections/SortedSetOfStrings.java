package org.jpwh.test.collections;

import org.jpwh.env.JPATest;
import org.jpwh.model.collections.sortedsetofstrings.Item;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

public class SortedSetOfStrings extends JPATest {

    @Override
    public void configurePersistenceUnit() throws Exception {
        configurePersistenceUnit("SortedSetOfStringsPU");
    }

    @Test
    public void storeLoadCollection() throws Exception {
        UserTransaction tx = TM.getUserTransaction();
        try {
            tx.begin();
            EntityManager em = JPA.createEntityManager();
            Item someItem = new Item();

            someItem.getImages().add("foo.jpg");
            someItem.getImages().add("bar.jpg");
            someItem.getImages().add("baz.jpg");
            someItem.getImages().add("baz.jpg"); // Duplicate, filtered at Java level by HashSet!

            em.persist(someItem);
            tx.commit();
            em.close();
            Long ITEM_ID = someItem.getId();

            tx.begin();
            em = JPA.createEntityManager();
            Item item = em.find(Item.class, ITEM_ID);
            assertEquals(item.getImages().size(), 3);

            // Sorted in-memory with TreeSet
            Iterator<String> it = item.getImages().iterator();
            String image;
            image = it.next();
            assertEquals(image, "bar.jpg");
            image = it.next();
            assertEquals(image, "baz.jpg");
            image = it.next();
            assertEquals(image, "foo.jpg");

            tx.commit();
            em.close();
        } finally {
            TM.rollback();
        }
    }

}

package net.nordist.lloydproof;

import android.test.AndroidTestCase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;

/*
 * NB: setUp() deletes all stored corrections; each test is expected
 * to clean up after itself and leave the storage empty again when done
 * (to avoid failures from indeterminate test order).
 */
public class CorrectionStorageTest extends AndroidTestCase
{
    private CorrectionStorage store;
 
    @Override
    protected void setUp() {
        store = new CorrectionStorage(getContext());
        store.deleteAll();
    }

    public void testSave() {
        // test save():
        int id = store.save("XYZZY");
        Assert.assertTrue(id > 0);
        Assert.assertEquals(1, store.count());
        // clean up:
        store.deleteAll();  // FIXME delete by id & assert empty
        Assert.assertEquals(0, store.count());
    }

    public void testDeleteByIdArray() {
        List<Integer> idsToDelete = new ArrayList<Integer>();
        // save some corrections:
        final int nDelete = 3;
        idsToDelete.add(store.save("PLUGH"));
        idsToDelete.add(store.save("Y2"));
        idsToDelete.add(store.save("Frobozz"));
        // FIXME save one not on the delete list too
        Iterator<Integer> i = idsToDelete.iterator();
        while (i.hasNext()) {
            Assert.assertTrue(i.next().intValue() > 0);
        }
        Assert.assertEquals(nDelete, store.count());
        // delete the just-added corrections:
        Assert.assertEquals(nDelete, store.deleteByIdList(idsToDelete));
        Assert.assertEquals(0, store.count());
    }
}

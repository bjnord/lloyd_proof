package net.nordist.lloydproof;

import android.test.AndroidTestCase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;

public class CorrectionStorageTest extends AndroidTestCase
{
    private CorrectionStorage store;
 
    @Override
    protected void setUp() {
        store = new CorrectionStorage(getContext());
    }

    public void testSave() {
        int startCount = store.count();
        int id = store.save("XYZZY");
        Assert.assertTrue(id > 0);
        Assert.assertEquals(startCount + 1, store.count());
    }

    public void testDeleteByIdArray() {
        int startCount = store.count();
        List<Integer> idsToDelete = new ArrayList<Integer>();
        // save some corrections:
        final int nDelete = 3;
        idsToDelete.add(store.save("PLUGH"));
        idsToDelete.add(store.save("Y2"));
        idsToDelete.add(store.save("Frobozz"));
        Iterator<Integer> i = idsToDelete.iterator();
        while (i.hasNext()) {
            Assert.assertTrue(i.next().intValue() > 0);
        }
        Assert.assertEquals(startCount + nDelete, store.count());
        // delete the just-added corrections:
        Assert.assertEquals(nDelete, store.deleteByIdList(idsToDelete));
        Assert.assertEquals(startCount, store.count());
    }
}

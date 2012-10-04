package net.nordist.lloydproof;

import android.test.AndroidTestCase;
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
        int id = store.save("Xyzzy");
        Assert.assertTrue(id > 0);
        Assert.assertEquals(startCount + 1, store.count());
    }
}

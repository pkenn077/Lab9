package mgarzon.createbest.productcatalog;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ProductTest {
    @Test
    public void checkProductName() {
        Product aProduct = new Product("1", "DELL MONITOR", 180);
        assertEquals("Check name of the product", "DELL MONITOR", aProduct.getProductName());
    }

    @Test
    public void checkProductId() {
        Product aProduct = new Product("1", "DELL MONITOR", 180);
        assertEquals("Check id of the product", "1", aProduct.getId());
    }

    @Test
    public void checkProductPrice() {
        Product aProduct = new Product("1", "DELL MONITOR", 180);
        assertTrue("Check price of the product", 180 == aProduct.getPrice());
    }
}
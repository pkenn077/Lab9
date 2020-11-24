package mgarzon.createbest.productcatalog;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextPrice;
    Button buttonAddProduct;
    ListView listViewProducts;

    FirebaseDatabase firebaseDB;
    DatabaseReference databaseProducts;

    List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        listViewProducts = (ListView) findViewById(R.id.listViewProducts);
        buttonAddProduct = (Button) findViewById(R.id.addButton);

        products = new ArrayList<>();

        FirebaseApp.initializeApp(this);
        firebaseDB = FirebaseDatabase.getInstance();
        databaseProducts = firebaseDB.getReference("products");

        //adding an onclicklistener to button
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        listViewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = products.get(i);
                showUpdateDeleteDialog(product.getId(), product.getProductName());
                return true;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Product product = postSnapshot.getValue(Product.class);
                    products.add(product);
                }

                //Create the adapter to display the products
                ProductList productsAdapter = new ProductList(MainActivity.this, products);
                listViewProducts.setAdapter(productsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void showUpdateDeleteDialog(final String productId, String productName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText editTextPrice  = (EditText) dialogView.findViewById(R.id.editTextPrice);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        dialogBuilder.setTitle(productName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = editTextName.getText().toString().trim();
                if( TextUtils.isEmpty(productName)){
                    editTextName.setError("Product name required");
                    return;
                }
                double productPrice = 0;
                try{
                    productPrice = Double.parseDouble(String.valueOf(editTextPrice.getText().toString()));
                }catch(NumberFormatException e){
                    editTextPrice.setError("Price must be a number");
                    return;
                }
                updateProduct(productId, productName, productPrice);
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(productId);
                b.dismiss();
            }
        });
    }

    private void updateProduct(String id, String name, double price) {

        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("products").child(id);
        Product updatedProduct = new Product(id, name, price);
        productReference.setValue(updatedProduct);

        Toast.makeText(getApplicationContext(), "Product updated", Toast.LENGTH_LONG).show();
    }

    private boolean deleteProduct(String id) {
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("products").child(id);
        productReference.removeValue();

        Toast.makeText(getApplicationContext(), "Product deleted", Toast.LENGTH_LONG).show();
        return true;
    }

    private void addProduct() {
        //Get the product information with form validation
        String productName = editTextName.getText().toString().trim();
        if( TextUtils.isEmpty(productName)){
            editTextName.setError("Product name required");
            return;
        }
        double productPrice = 0;
        try{
            productPrice = Double.parseDouble(String.valueOf(editTextPrice.getText().toString()));
        }catch(NumberFormatException e){
            editTextPrice.setError("Price must be a number");
            return;
        }

        String productId = databaseProducts.push().getKey();
        Product newProduct = new Product(productId, productName, productPrice);
        databaseProducts.child(productId).setValue(newProduct);

        editTextName.setText("");
        editTextPrice.setText("");

        Toast.makeText(this, "Product added", Toast.LENGTH_LONG).show();
    }
}
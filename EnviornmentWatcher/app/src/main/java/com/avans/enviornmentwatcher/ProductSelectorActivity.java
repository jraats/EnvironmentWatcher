package com.avans.enviornmentwatcher;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Class that shows all products
 */
public class ProductSelectorActivity extends AppCompatActivity {

    private EditText editText_Selected_Product;                             //
    private Button button_Select_Product, button_Unregister_Product;
    private Spinner spinner_Room, spinner_Location;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_selector);

        //set the textbox that looks if the user has already reserved a product
        //When the user clicks on it, it wil go to the product overview
        editText_Selected_Product = (EditText) findViewById(R.id.editText_Selected_Product);
        editText_Selected_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataCommunicator.getInstance().getUser().getProductID() != -1) {
                    Intent i = new Intent(getApplicationContext(), ProductOverviewActivity.class);
                    i.putExtra("product", DataCommunicator.getInstance().getUser().getProductID());
                    startActivity(i);
                }
            }
        });

        //selecting product
        if(DataCommunicator.getInstance().getUser().getProductID() != -1)
            editText_Selected_Product.setText(String.valueOf(DataCommunicator.getInstance().getUser().getProductID()));

        //Creating button with logic
        button_Unregister_Product=(Button) findViewById(R.id.button_Unregister_Product);
        button_Unregister_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("productId", null);
                JSONCommunicator.getInstance().changeData("user", map, new CommunicationInterface<Integer>() {
                    @Override
                    public void getResponse(Integer object) {
                        if (object == 0) {
                            DataCommunicator.getInstance().getUser().setProductID(-1);
                            editText_Selected_Product.setText(getResources().getString(R.string.editText_Selected_Product));
                            Toast.makeText(ProductSelectorActivity.this, getResources().getString(R.string.toast_UnsubscribeProductSucces),
                                    Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(ProductSelectorActivity.this, getResources().getString(R.string.toast_UnsubscribeProductFailed),
                                    Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        button_Select_Product=(Button) findViewById(R.id.button_Select_Product);
        button_Select_Product.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int productID = getProductID(spinner_Room.getSelectedItem().toString(), spinner_Location.getSelectedItem().toString());
                if(productID != -1){
                    Intent i = new Intent(getApplicationContext(), ProductOverviewActivity.class);
                    i.putExtra("product", productID);
                    startActivity(i);
                }

            }
        });

        //Creating first spinner and filling them
        spinner_Room = (Spinner) findViewById(R.id.spinner_Room);
        spinner_Room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillSecondSpinner(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty method, must override
            }
        });

        //Creates second spinner, but not filling it yet
        spinner_Location = (Spinner) findViewById(R.id.spinner_Location);



        //Get all products
        try {
            JSONCommunicator.getInstance().getAllData("product", new CommunicationInterface<ArrayList<HashMap<String, String>>>() {

                        @Override
                        public void getResponse(ArrayList<HashMap<String, String>> object) {
                            products = new ArrayList<>();
                            if (!object.equals(null)) {
                                for(int i = 0; i < object.size(); i++)
                                {
                                    Product p = new Product();
                                    p.setId(Integer.parseInt(object.get(i).get("id")));
                                    p.setRoom(object.get(i).get("roomName"));
                                    p.setLocation(object.get(i).get("location"));
                                    products.add(p);
                                }
                            }
                            onResume();
                            fillFirstSpinner();
                        }
                    });
        }catch (Exception e)
        {
            System.out.println("Error" + e.toString());
        }
    }


    /*! \creates settings button
    *  @param menu the menu that is used in the app
    *  creates settingsbutton on the topRight
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_product_overview, menu);
        return true;
    }

    /*! \acts on pressing settings button
     *  @param item (In this case not needed, since we dont use items)
     *  Change screen when settings button is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        return true;
    }

    /*! \fill spinner
     *  Filling the first spinner with rooms
     */
    private void fillFirstSpinner(){
        List<String> spinnerArray =  new ArrayList<>();
        for(int i = 0; i < products.size(); i++)
        {
            if(!spinnerArray.contains(products.get(i).getRoom()))
                spinnerArray.add(products.get(i).getRoom());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_Room.setAdapter(adapter);
    }

    /*! \fill spinner
    *   Filling the second spinner with locations from the room selected in first spinner
    */
    private void fillSecondSpinner(String room){
        List<String> spinnerArray =  new ArrayList<>();
        for(int i = 0; i < products.size(); i++)
        {

            if(products.get(i).getRoom().equals(room))
                spinnerArray.add(products.get(i).getLocation());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_Location.setAdapter(adapter);
    }

    /*! \when screen resumes
    *   When the screen is reselected it will check if the product reservation has changed
    */
    public void onResume() {
        super.onResume();

        if(DataCommunicator.getInstance().getUser().getProductID() != -1) {
            Product p = getProductRoomLocation(DataCommunicator.getInstance().getUser().getProductID());
            if(p != null)
                editText_Selected_Product.setText(p.getRoom() + ": " + p.getLocation());
        }
        else
            editText_Selected_Product.setText(getResources().getString(R.string.editText_Selected_Product));

    }

    /*! \Get product id
     *  @param room the room in wich the sensor lies
     *  @param location the location within the room the sensor lies
     *  Sets the roomname and location to an ID, needed to get data from server
     */
    private int  getProductID(String room, String location){
        for (int i = 0; i < products.size(); i++)
        {
            Product p = products.get(i);
            if(p.getRoom().equals(room) && p.getLocation().equals(location))
                return p.getId();
        }
        return -1;
    }

    /*! \Get product based on id
     *  @param id the id of the sensor needed
     *  gets the location of the location and room from the list with products
     */
    private Product getProductRoomLocation(int id){
        if(products != null) {
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                if (p.getId() == id)
                    return p;
            }
        }
        return null;
    }
}

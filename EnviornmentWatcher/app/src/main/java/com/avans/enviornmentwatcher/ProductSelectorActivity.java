package com.avans.enviornmentwatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductSelectorActivity extends AppCompatActivity {

    private EditText editText_Selected_Product;
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
                    i.putExtra("product", editText_Selected_Product.getText());
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
                        if (object == 0)
                            editText_Selected_Product.setText(null);
                        //TODO: else error
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

                            fillFirstSpinner();
                        }
                    });
        }catch (Exception e)
        {
            System.out.println("Error" + e.toString());
        }


    }

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

    private int  getProductID(String room, String location){
        for (int i = 0; i < products.size(); i++)
        {
            Product p = products.get(i);
            if(p.getRoom().equals(room) && p.getLocation().equals(location))
                return p.getId();
        }
        return -1;
    }
}

package com.avans.enviornmentwatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
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
        editText_Selected_Product.setText(DataCommunicator.getInstance().getUser().getProductID());
        editText_Selected_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Creating button with logic
        button_Unregister_Product=(Button) findViewById(R.id.button_Unregister_Product);
        button_Unregister_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        button_Select_Product=(Button) findViewById(R.id.button_Select_Product);
        button_Select_Product.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        spinner_Room = (Spinner) findViewById(R.id.spinner_Room);
        spinner_Room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("test");
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
            JSONCommunicator.getInstance().getAllProducts(DataCommunicator.getInstance().getUser(),
                    new CommunicationInterface<ArrayList<Product>>() {

                        @Override
                        public void getResponse(ArrayList<Product> object) {
                            if (!object.equals(null)) {
                                products = object;
                            }
                            else
                                products = new ArrayList<Product>();

                            fillFirstSpinner();
                        }
                    });
        }catch (Exception e)
        {
            System.out.println("Error" + e.toString());
        }


    }

    private void fillFirstSpinner(){
        List<String> spinnerArray =  new ArrayList<String>();
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
        List<String> spinnerArray =  new ArrayList<String>();
        for(int i = 0; i < products.size(); i++)
        {

            if(products.get(i).getRoom().equals(room))
                spinnerArray.add(products.get(i).getLocation());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_Location.setAdapter(adapter);
    }
}

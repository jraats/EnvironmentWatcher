package com.avans.enviornmentwatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProductSelectorActivity extends AppCompatActivity {

    private Button button_Select_Product, button_Unregister_Product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_selector);

        /*
        //Get all products
        try {
            JSONCommunicator.getInstance().getAllProducts(DataCommunicator.getInstance().getUser(),
                    new CommunicationInterface<ArrayList<Product>>() {

                        @Override
                        public void getResponse(ArrayList object) {
                            if (!object.equals(null))
                            {

                            }
                        }
                    });
        }catch (Exception e)
        {

        }
*/

        //Creating button with logic
        button_Select_Product=(Button)

        findViewById(R.id.button_Select_Product);

        button_Select_Product.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        }

        );

        button_Unregister_Product=(Button)

        findViewById(R.id.button_Unregister_Product);

        button_Unregister_Product.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {

             }
        });


    }
}

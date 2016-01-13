package com.avans.enviornmentwatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ProductOverviewActivity extends AppCompatActivity {

    private EditText editText_Current_Temperature, editText_Current_Light;
    private TextView text_Current_Light, text_Current_Temperature;
    private ImageButton imageButton_Settings;
    private Button button_Register_Product;
    private Product product = new Product();
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);

        //Get items sent from previous screen
        Bundle extra = getIntent().getExtras();
        //checks if there is an productID
        if (extra.get("product") != null)
            product.setId((int) extra.get("product"));
        else
            product.setId(-1);

        //Set textboxes
        text_Current_Light = (TextView) findViewById(R.id.text_Current_Light);
        text_Current_Light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GraphActivity.class);
                i.putExtra("item", "light");
                startActivity(i);
            }
        });
        text_Current_Temperature = (TextView) findViewById(R.id.text_Current_Temperature);
        text_Current_Temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GraphActivity.class);
                i.putExtra("item", "temperature");
                startActivity(i);
            }
        });

        editText_Current_Light = (EditText) findViewById(R.id.editText_Current_Light);
        editText_Current_Light.setEnabled(false);
        editText_Current_Temperature = (EditText) findViewById(R.id.editText_Current_Temperature);
        editText_Current_Temperature.setEnabled(false);

        //Retrieves data from server
        updateData();

        //Creating a button
        button_Register_Product = (Button) findViewById(R.id.button_Register_Product);
        button_Register_Product.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(product.getId() != -1) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", String.valueOf(product.getId()));
                    JSONCommunicator.getInstance().changeData("user", map, new CommunicationInterface<Integer>() {
                        @Override
                        public void getResponse(Integer object) {
                            if (0 == object) {
                                Toast.makeText(ProductOverviewActivity.this, getResources().getString(R.string.toast_SubscribeProductSucces),
                                        Toast.LENGTH_SHORT).show();
                                DataCommunicator.getInstance().getUser().setProductID(product.getId());
                            } else
                                Toast.makeText(ProductOverviewActivity.this, getResources().getString(R.string.toast_SubscribeProductFailed),
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_product_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        return true;
    }

    //Pauses the timer
    public void onPause(){
        super.onPause();
        timer.cancel();
    }

    //Timer sending a update request every 30 seconds
    public void onResume(){
        super.onResume();
        try {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    updateData();
                }
            };
            timer.schedule(timerTask, 30000, 30000);
        } catch (IllegalStateException e) {
            System.out.println("Timer Error");
        }
    }

    //retrieves latest data from the server
    private void updateData(){
        if(product.getId() != -1) {
            JSONCommunicator.getInstance().getObject("sensorData", String.valueOf(product.getId()), "/getLatest",
                    new CommunicationInterface<HashMap<String, String>>() {

                        @Override
                        public void getResponse(HashMap<String, String> object) {
                            if (object.size() != 0) {
                                System.out.println(object.toString());
                                editText_Current_Light.setText(object.get("light"));
                                editText_Current_Temperature.setText(object.get("temperature"));
                            }
                        }
                    }
            );
        }
        else
            System.out.println("No valid productID");
    }

}

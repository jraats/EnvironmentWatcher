package com.avans.enviornmentwatcher;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Console;

public class LoginActivity extends AppCompatActivity {

    private EditText editText_Username, editText_Password;
    private Button button_Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Create communication singleton
        JSONCommunicator.getInstance(getApplicationContext());

        //Creating text fields
        editText_Username = (EditText) findViewById(R.id.editText_Username);
        editText_Password = (EditText) findViewById(R.id.editText_Password);

        //Creating button with logic
        button_Login = (Button) findViewById(R.id.button_Login);
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONCommunicator.getInstance().login(editText_Username.getText().toString(), editText_Password.getText().toString(),
                    new CommunicationInterface<String>() {
                        @Override
                        public void getResponse(String result) {
                            if (!result.isEmpty()) {
                                try {
                                    DataCommunicator.getInstance().createUser(editText_Username.getText().toString(), result);
                                    JSONCommunicator.getInstance().getProductIDUser(DataCommunicator.getInstance().getUser(),
                                        new CommunicationInterface<String>() {

                                            @Override
                                            public void getResponse(String object) {
                                                if (object.equals("0"))
                                                {
                                                    System.out.println("Wheeee!");
                                                    startActivity(new Intent(getApplicationContext(), ProductSelectorActivity.class));
                                                }
                                                else
                                                    getAlert("Something went wrong, dunno what");
                                            }
                                        });
                                }catch (Exception e){

                                }

                                    //Example of using data
                                //Intent i = new Intent(getApplicationContext(), ProductSelectorActivity.class);
                                //i.putExtra("key", value);

                            } else {
                                //Send an alert
                                getAlert("Wrong Username or Password");
                            }
                        }
                    });
                }
        });
    }

    //Creating a alert when the user cannot login (assuming this is actually the problem
    //TODO: create different errors
    private void getAlert(String argument)
    {

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage(argument);
        dlgAlert.setTitle("Error Message...");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}

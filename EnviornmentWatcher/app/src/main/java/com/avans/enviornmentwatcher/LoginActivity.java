package com.avans.enviornmentwatcher;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                JSONCommunicator.getInstance().Login(editText_Username.getText().toString(), editText_Password.getText().toString(), new CommunicationInterface<String>() {
                    @Override
                    public void Login(String result) {
                        if (!result.isEmpty()) {

                            DataCommunicator.getInstance().createUser(editText_Username.getText().toString(), result);

                            //Example of using data
                            //Intent i = new Intent(getApplicationContext(), ProductSelectorActivity.class);
                            //i.putExtra("key", value);

                            startActivity(new Intent(getApplicationContext(), ProductSelectorActivity.class));
                        } else {
                            //Send an alert
                            getAlert();
                        }
                    }
                });
            }
        });

    }

    //Creating a alert when the user cannot login (assuming this is actually the problem
    //TODO: create different errors
    private void getAlert()
    {

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage("wrong password or username");
        dlgAlert.setTitle("Error Message...");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}

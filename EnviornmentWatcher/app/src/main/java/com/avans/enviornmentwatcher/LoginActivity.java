package com.avans.enviornmentwatcher;

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
        JsonCommunication.getInstance(getApplicationContext());

        editText_Username = (EditText) findViewById(R.id.editText_Username);
        editText_Password = (EditText) findViewById(R.id.editText_Password);

        button_Login = (Button) findViewById(R.id.button_Login);
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonCommunication.getInstance().Login(editText_Username.getText().toString(), editText_Password.getText().toString(), new JsonInterface<String>() {
                    @Override
                    public void Login(String result) {
                        if (!result.isEmpty()) {
                            //TODO: Go to next page
                             /* Navigatie
                            Intent i = new Intent(getApplicationContext(), NextClass.class);

                            int value = 1;
                            i.putExtra("key", value);

                            startActivity(i);*/
                        }
                        else
                        {
                            //TODO Display Error
                        }
                    }
                });
            }
        });

    }
}

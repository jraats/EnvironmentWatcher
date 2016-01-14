package com.avans.enviornmentwatcher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private EditText editText_Settings_Temperature, editText_Settings_Light;
    private Button button_Settings_ChangePreference, button_Settings_ChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        editText_Settings_Light = (EditText) findViewById(R.id.EditText_Settings_Light);
        editText_Settings_Temperature = (EditText) findViewById(R.id.EditText_Settings_Temperature);
        button_Settings_ChangePreference = (Button) findViewById(R.id.button_Settings_ChangePreference);
        button_Settings_ChangePassword = (Button) findViewById(R.id.button_Settings_ChangePassword);


        JSONCommunicator.getInstance().getObject("preferences", DataCommunicator.getInstance().getUser().getUsername(), "", new CommunicationInterface<HashMap<String, String>>() {
            @Override
            public void getResponse(HashMap<String, String> object) {
                if(object.get("temperatureTreshold").equals("null"))
                    editText_Settings_Temperature.setHint(object.get("temperatureTreshold"));
                else
                    editText_Settings_Temperature.setText(object.get("temperatureTreshold"));

                if(object.get("temperatureTreshold").equals("null"))
                    editText_Settings_Light.setHint(object.get("lightTreshold"));
                else
                    editText_Settings_Light.setText(object.get("lightTreshold"));
            }
        });

        button_Settings_ChangePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText_Settings_Light.getText().toString().equals("") && !editText_Settings_Temperature.getText().toString().equals("")) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if(Integer.parseInt(editText_Settings_Light.getText().toString()) < 99 && Integer.parseInt(editText_Settings_Light.getText().toString()) > 0){
                        map.put("lightTreshold", editText_Settings_Light.getText().toString());
                        map.put("temperatureTreshold", editText_Settings_Temperature.getText().toString());

                        JSONCommunicator.getInstance().changeData("preferences", map, new CommunicationInterface<Integer>() {
                            @Override
                            public void getResponse(Integer object) {
                                if(object == 0)
                                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.toast_ChangePreferenceSucces),
                                            Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.toast_ChangePreferenceFailed),
                                            Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
                //TODO: else error
            }
        });

        button_Settings_ChangePassword.setOnClickListener(new View.OnClickListener(){

            String m_Text = "";
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Verander Wachtwoord");
                LinearLayout layout = new LinearLayout(SettingsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText passwordOld = new EditText(SettingsActivity.this);
                passwordOld.setHint("old password");
                passwordOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(passwordOld);

                final EditText passwordNew = new EditText(SettingsActivity.this);
                passwordNew.setHint("new password");
                passwordNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(passwordNew);

                final EditText passwordNew2 = new EditText(SettingsActivity.this);
                passwordNew2.setHint("repeat password");
                passwordNew2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(passwordNew2);

                builder.setView(layout);


                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (passwordNew.getText().toString().equals(passwordNew2.getText().toString()) && !passwordNew.getText().toString().equals("")) {
                            JSONCommunicator.getInstance().getObject("user", DataCommunicator.getInstance().getUser().getUsername(),
                                    "", new CommunicationInterface<HashMap<String, String>>() {
                                        @Override
                                        public void getResponse(HashMap<String, String> object) {
                                            //TODO: i can request passwords?????????!!!!!!!
                                            if (object.get("password").equals(passwordOld.getText().toString())) {
                                                HashMap<String, String> map = new HashMap<String, String>();
                                                map.put("password", passwordNew.getText().toString());

                                                JSONCommunicator.getInstance().changeData("user", map, new CommunicationInterface<Integer>() {
                                                    @Override
                                                    public void getResponse(Integer object) {
                                                        if(object == 0)
                                                            Toast.makeText(SettingsActivity.this, getResources().getString(
                                                                    R.string.toast_OperationSucces), Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(SettingsActivity.this, getResources().getString(
                                                                    R.string.toast_OperationFailed), Toast.LENGTH_SHORT).show();


                                                    }
                                                });
                                            }
                                            else
                                                getAlert(getResources().getString(R.string.alert_NoMatchingWithOldPassword));
                                        }
                                    });
                        } else
                            getAlert(getResources().getString(R.string.alert_NoMatchingPasswords));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
    }

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

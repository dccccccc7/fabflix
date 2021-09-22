package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private TextView message;
    private Button loginButton;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    //private final String host = "10.0.2.2"; //local
    //private final String port = "8080";  //local

    private final String host = "54.177.64.82"; //aws
    private final String port = "8443"; //aws
    //private final String domain = "cs122b-spring21-project2-login-cart-example-war";
    private final String domain = "cs122b-spring21-team-122";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        message = findViewById(R.id.message);
        loginButton = findViewById(R.id.login);

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    public void login() {

        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    // parse the json response to redirect to appropriate functions
                    // upon different response value.

                    try {
                        JSONObject loginResponse = new JSONObject(response);

                        if (loginResponse.getString("status").equals("success")) {
                            Log.d("login.success", response);
                            // initialize the activity(page)/destination
                            Intent listPage = new Intent(Login.this, ListViewActivity.class);
                            // activate the list page.
                            startActivity(listPage);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), loginResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            username.setText("");
                            password.setText("");
                            message.setText("Invalid Login Credentials. Please try again");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }
}
package com.example.autclub.LoginSignupController;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.autclub.AppModel.Club;
import com.example.autclub.AppModel.User;
import com.example.autclub.InitialController.InstructionPage;
import com.example.autclub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Login activity control the login activity page.
 */
public class LoginActivity extends AppCompatActivity {
    private final String databaseURL = "https://softwareteamproject.000webhostapp.com/"; //datebase link
    private final String loginPHP = "Login.php"; //php file

    protected RequestQueue requestQueue;// request info form database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set view variable
        final EditText usernameEditText = findViewById(R.id.login_etUserName);
        final EditText passwordEditText = findViewById(R.id.login_etPassword);
        TextView signupTextView = findViewById(R.id.login_signup);
        Button loginButton = findViewById(R.id.login_btnLogin);
        Button resetPasswordButton = findViewById(R.id.login_resetButton);
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        Button guestuserButton = findViewById(R.id.login_btnGuest);

        //Guest Button on click
        guestuserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHandleGuestButton();
            }
        });

        //SignUp Button on click
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHandleSignUpTextView();
            }
        });

        //Login Button on click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHandleLoginButton(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        //Reset Button on click
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHandleSresetPasswordButton();
            }
        });
    }

    private void eventHandleGuestButton() {
        newActivityPage(InstructionPage.class);
    }

    private void eventHandleSresetPasswordButton() {
        newActivityPage(ResetPasswordActivity.class);
    }

    private void eventHandleSignUpTextView() {
        newActivityPage(SignUpActivity.class);
    }

    private void eventHandleLoginButton(String username, String password) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                eventHandleLoginButtonResponse(response);
            }
        };
        loginRequest(username, password, responseListener);
    }

    private void eventHandleLoginButtonResponse(String response) {
        try {
            Log.d("user", "onResponse: " + response + "\n------------------------------------------------------------");

            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                responseHandleSuccess(jsonObject);
            } else {
                message("Login Failed", "Retry");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loginRequest(final String username, final String password, Response.Listener<String> responseListener) {
        String loginRequestURL = databaseURL + loginPHP;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginRequestURL, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("DB_HOST", "localhost");
                params.put("DB_USER", "id9336220_autclubdb");
                params.put("DB_PASSWORD", "software");
                params.put("DB_NAME", "id9336220_autclubdb");
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void responseHandleSuccess(JSONObject jsonObject) throws JSONException {
        String userName = jsonObject.getString("userName");
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        String email = jsonObject.getString("email");
        String time = jsonObject.getString("time");
        ArrayList<Club> clubList = new ArrayList<>();
        JSONArray arrayClub = jsonObject.getJSONArray("club");
        for (int i = 0; i < arrayClub.length(); i++) {
            JSONArray jsonArray = arrayClub.getJSONArray(i);
            String clubID = jsonArray.getString(0);
            String clubName = jsonArray.getString(1);
            String tokens = jsonArray.getString(2);

            Log.d("user", "responseHandleSuccess: " + clubID + "|" + clubName + "|" + tokens);
            Club club = new Club(Integer.parseInt(clubID), clubName, tokens);
            clubList.add(club);
        }
        User user = new User(userName, firstName, lastName, email, Double.parseDouble(time));
        user.setFollowClub(clubList);
        Log.d("user", "responseHandleSuccess: " + user.toString());
        newActivityPage(InstructionPage.class);
    }

    private void message(String message, String buttonTxt) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(LoginActivity.this);
        alertdialog.setMessage(message);
        alertdialog.setPositiveButton(buttonTxt, null);//when ok button is pressed then the error message will go away
        alertdialog.show();
    }

    private void newActivityPage(Class nextClass) {
        Intent intent = new Intent(LoginActivity.this, nextClass);
        LoginActivity.this.startActivity(intent);
    }

}

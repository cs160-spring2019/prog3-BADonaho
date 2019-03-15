package com.example.cs160_sp18.prog3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Button loginButton = findViewById(R.id.login_button);
        final EditText createUsername = findViewById(R.id.create_username);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = createUsername.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    createUsername.requestFocus();
                } else {
                    openMainMenu();
                }
            }
        });
    }

    private void openMainMenu() {
        Intent startupIntent = new Intent(this, MainActivity.class);
        startupIntent.putExtra("username", username);
        startActivity(startupIntent);
    }

}

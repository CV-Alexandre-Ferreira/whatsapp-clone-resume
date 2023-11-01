package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private TextView signUpButton;
    private FirebaseAuth auth;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpButton = findViewById(R.id.buttonParaCadastro);

        auth = FirebaseConfig.getFirebaseAuth();

        emailField = findViewById(R.id.editEmailLogin);
        passwordField = findViewById(R.id.editSenhaLogin);
        signInButton = findViewById(R.id.buttonLogar);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) openMainScreen();
    }

    public void openMainScreen(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void logUser(User user){

        auth.signInWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                openMainScreen();

                }else{

                    Toast.makeText(LoginActivity.this, "Erro ao autenticar", Toast.LENGTH_SHORT).show();

                }

            }
        });



    }
    public void validateUserAuth(View view){

        String emailText = emailField.getText().toString();
        String passwordText = passwordField.getText().toString();

        if( !emailText.isEmpty() && !passwordText.isEmpty()){

            User user = new User();
            user.setEmail(emailText);
            user.setPassword(passwordText);

            logUser(user);

        }
        else{
            Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }

    }
}
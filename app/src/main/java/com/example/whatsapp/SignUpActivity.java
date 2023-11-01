package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameField, emailField, passwordField;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = findViewById(R.id.editNome);
        emailField = findViewById(R.id.editEmail);
        passwordField = findViewById(R.id.editSenha);
    }

    public void signUpUser(User user){

        auth = FirebaseConfig.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
            user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(SignUpActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                    UserFirebaseHelper.updateUserName(user.getName());
                    finish();

                    try{

                        String userId = Base64Custom.encodeBase64(user.getEmail());
                        user.setId(userId);
                        user.save();

                    }catch (Exception e){e.printStackTrace();}
                }else{
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        exception = "Digite senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Digite um email valido";
                    }catch (FirebaseAuthUserCollisionException e){
                        exception = "Já existe um cadastro nessa conta";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(SignUpActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void validateUserSignUp(View view){

        String nameText = nameField.getText().toString();
        String emailText = emailField.getText().toString();
        String passwordText = passwordField.getText().toString();

        if(!nameText.isEmpty() && !emailText.isEmpty() && !passwordText.isEmpty()){
            User user = new User();
            user.setName(nameText);
            user.setEmail(emailText);
            user.setPassword(passwordText);

            signUpUser(user);
        }
        else{
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }

    }

}
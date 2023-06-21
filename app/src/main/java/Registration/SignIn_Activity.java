package Registration;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.twog.MainActivity;
import com.twog.R;

import Models.Auth;
import Models.OnSingleUserRetrievedListener;
import Models.User;
import Models.UserDataProvider;

import static com.twog.MainActivity.REQUEST_CODE_SHOW_LOGIN_SNACKBAR;

public class SignIn_Activity extends AppCompatActivity {
    private ActionBar bar;
    private EditText loginET;
    private EditText paswdET;
    private Button btnLogIn;
    private TextView clickHere;
    private String loginStr;
    private String paswdStr;
    private String emailStr;
    private Auth auth;
    private SwitchCompat switchCompat;
    private Context context;
    private FirebaseAuth mAuth;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    private boolean userIsVerificating;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Intent intent = new Intent(this, SignUp_Activity.class);
        if (loginStr != null && paswdStr != null) {
            intent.putExtra("login", loginStr);
            intent.putExtra("password", paswdStr);
        }
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        bar = getSupportActionBar();
        context = this;
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Log In");
        auth = new Auth(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        switchCompat = findViewById(R.id.switch1);
        loginET = findViewById(R.id.editTextLogin);
        paswdET = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginStr = loginET.getText().toString().trim();
                paswdStr = paswdET.getText().toString().trim();
                boolean noErrors = check();
                if (noErrors) {
                    OnSingleUserRetrievedListener listener = new OnSingleUserRetrievedListener() {
                        @Override
                        public void OnUserRetrieved(User user) {
                            if (user.getLogin() == null) {
                                //Toast.makeText(getApplicationCoarnurdzhumabekov2007hysp3ntext(), user.getLogin(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                loginET.setError("Wrong login info!");
                                paswdET.setError("Wrong login info!");
                            } else {
                                userIsVerificating=true;
                                FirebaseUser userrrrr =  FirebaseAuth.getInstance().getCurrentUser();
                                emailStr = user.getEmail();
                                mAuth.signInWithEmailAndPassword(emailStr, paswdStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            if (firebaseUser.isEmailVerified()) {
                                                user.setHasEmailVerified(true);
                                                if (switchCompat.isChecked()) {
                                                    Toast.makeText(getApplicationContext(), "Singning you in...", Toast.LENGTH_SHORT).show();
                                                    auth.signInWithoutRemembering(user);
                                                } else {
                                                    auth.signIn(user);
                                                }
                                                Intent intent = new Intent(context, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                setResult(RESULT_OK, intent);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                user.setHasEmailVerified(false);
                                                firebaseUser.sendEmailVerification();
                                                Snackbar snackbar = Snackbar.make(v, "Check your email to verify your account", BaseTransientBottomBar.LENGTH_SHORT);
                                                snackbar.setAction("Help", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        builder.setTitle("Why do I need to verify my account?");
                                                        builder.setMessage("We need our users to verify their accounts to prevent abuse\n\nIt also helps us to maintain our users' data safe and secure\n\n" +
                                                                "If verification page isn't opening, use VPN service. (This problem appears if your government blocks Google auth site)");
                                                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                                snackbar.show();
                                            }
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            loginET.setError("Wrong login info!");
                                            paswdET.setError("Wrong login info!");
                                            Toast.makeText(context, "Failed to login! Check your credentials", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    };
                    UserDataProvider.getInstance().getUser(loginStr, listener);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(userIsVerificating){
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("Exit?");
            builder.setMessage("If you haven't verified your users' email, your account will be deleted");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                User u = SignUp_Activity.SIGNING_UP_USER;
                                UserDataProvider.getInstance().deleteUser(SignUp_Activity.SIGNING_UP_USER);
                                finish();
                            }else{
                                Toast.makeText(context, "Something wrong happened. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }else{
            super.onBackPressed();
        }
    }

    private boolean check() {
        boolean isOk = true;
        if (loginStr.isEmpty() || loginStr == null) {
            isOk = false;
            loginET.setError("Empty input");
        } else if (paswdStr.isEmpty() || paswdStr == null) {
            isOk = false;
            paswdET.setError("Empty input");
        }
        return isOk;
    }
}
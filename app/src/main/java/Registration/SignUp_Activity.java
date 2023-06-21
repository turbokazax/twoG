package Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.twog.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Models.OnSingleUserRetrievedListener;
import Models.User;
import Models.UserDataProvider;

import static com.twog.MainActivity.REQUEST_CODE_SHOW_LOGIN_SNACKBAR;

public class SignUp_Activity extends AppCompatActivity {
    private EditText loginET;
    private EditText paswdET;
    private EditText emailET;
    private Button btnSignUp;
    private TextView clickHere;
    private String loginStr;
    private String paswdStr;
    private String emailStr;
    private FirebaseAuth mAuth;
    private Context context;
    private Pattern pattern;
    private Matcher matcher;
    private ProgressBar progressBar;
    private static final String PASSWORD_REGEX_STRONG = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{12,}$";
    private static final String PASSWORD_REGEX_MEDIUM = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{12,}$";
    private static final Pattern PASSWORD_PATTERN_STRONG = Pattern.compile(PASSWORD_REGEX_STRONG);
    private static final Pattern PASSWORD_PATTERN_MEDIUM = Pattern.compile(PASSWORD_REGEX_MEDIUM);
    public static final User SIGNING_UP_USER = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        View view = this.findViewById(android.R.id.content);
        loginET = findViewById(R.id.editTextLogin);
        context = this;
        progressBar=findViewById(R.id.progressBar);
        getSupportActionBar().setTitle("Sign Up");
        paswdET = findViewById(R.id.editTextPassword);
        emailET = findViewById(R.id.editTextEmail);
        btnSignUp = findViewById(R.id.btnSignUp);
        clickHere = findViewById(R.id.clickHere);
        mAuth = FirebaseAuth.getInstance();
        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn_Activity.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginStr = loginET.getText().toString().trim();
                paswdStr = paswdET.getText().toString().trim();
                emailStr = emailET.getText().toString().trim();
                boolean noErrors = check();
                if (noErrors) {
                    OnSingleUserRetrievedListener listener = new OnSingleUserRetrievedListener() {
                        @Override
                        public void OnUserRetrieved(User user) {
                            if (user.getLogin() != null) {
                                loginET.setError("User exists");
                            } else {
                                String abc = passwordIsStrong();
                                if (passwordIsStrong().equals("Strong") || passwordIsStrong().equals("Medium")) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    mAuth.createUserWithEmailAndPassword(emailStr, paswdStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                User u = SIGNING_UP_USER;
                                                u.setLogin(loginStr);
                                                u.setEmail(emailStr);
                                                u.setName("");
                                                u.setAge(-1);
                                                u.setAvatarBase64("");
                                                u.setUserInfo("");
                                                u.setInterests(null);
                                                u.setKey(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                User u2 = SIGNING_UP_USER;
                                                UserDataProvider.getInstance().addUser(u, context);
                                                Snackbar.make(view, "Account created!", BaseTransientBottomBar.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), SignIn_Activity.class);
                                                intent.putExtra("email", emailStr);
                                                startActivityForResult(intent, REQUEST_CODE_SHOW_LOGIN_SNACKBAR);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to register! Try to user another email", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Snackbar snackbar = Snackbar.make(v, "Password is too weak!", BaseTransientBottomBar.LENGTH_SHORT);
                                    snackbar.setAction("How to fix it?", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle("How to create a strong password?");
                                            builder.setMessage("Password length MUST be at least 10.\n\n" +
                                                    "Use at least 1 capital letter and if you want to, 1 number.\n\nUsing special symbols (e.g _ * e.t.c) makes your password way more secure.");
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
                            }
                        }
                    };
                    UserDataProvider.getInstance().getUser(loginStr, listener);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
                ;
            }
        });
    }

    ;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("You sure you want to exit?");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SignUp_Activity.super.onBackPressed();
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private boolean check() {
        boolean isOk = true;
        if (loginStr.isEmpty() || loginStr == null) {
            isOk = false;
            loginET.setError("Empty input");
        }  if (paswdStr.isEmpty() || paswdStr == null) {
            isOk = false;
            paswdET.setError("Empty input");
        }
            boolean flag = isValidEmail(emailStr);
            if (!flag) {
                emailET.setError("Invalid email");
                isOk = false;
            }
        return isOk;
    }

    private String passwordIsStrong() {
        String passwordStrength = "";
        if (paswdStr.length() >= 10) {
            String password = paswdStr;
            if (!(password.contains("@") || password.contains("#")
                    || password.contains("!") || password.contains("~")
                    || password.contains("$") || password.contains("%")
                    || password.contains("^") || password.contains("&")
                    || password.contains("*") || password.contains("(")
                    || password.contains(")") || password.contains("-")
                    || password.contains("+") || password.contains("/")
                    || password.contains(":") || password.contains(".")
                    || password.contains(", ") || password.contains("<")
                    || password.contains(">") || password.contains("?")
                    || password.contains("|"))) {
                int count = 0;
                //letters
                for (int i = 97; i <= 122; i++) {
                    char c = (char) i;
                    String str1 = Character.toString(c);
                    if (password.contains(str1)) {
                        count = 1;
                    }
                }
                //capital letters
                for (int i = 65; i <= 90; i++) {
                    char c = (char) i;
                    String str1 = Character.toString(c);
                    if (password.contains(str1)) {
                        count = 2;
                    }
                }
                if (count == 2) {
                    passwordStrength = "Medium";
                }
            } else {
                int count = 0;
                for (int i = 97; i <= 122; i++) {
                    char c = (char) i;
                    String str1 = Character.toString(c);
                    if (password.contains(str1)) {
                        count = 1;
                    }
                }

                for (int i = 65; i <= 90; i++) {
                    char c = (char) i;
                    String str1 = Character.toString(c);
                    if (password.contains(str1)) {
                        count = 2;
                    }
                }
                //numbers
                for (int i = 0; i <= 9; i++) {
                    String str1 = Integer.toString(i);
                    if (password.contains(str1)) {
                        count = 3;
                    }
                }
                if (count == 3) {
                    passwordStrength = "Strong";
                }
            }
        } else {
            passwordStrength = "Weak";
        }
        return passwordStrength;
    }

    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
package Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.twog.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailET;
    private Button btnReset;
    private ProgressBar progressBar;
    private String emailStr;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailET = findViewById(R.id.editTextEmail);
        btnReset = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        context = this;
        getSupportActionBar().setTitle("Password recovering");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(v);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void resetPassword(View v) {
        emailStr = emailET.getText().toString().trim();
        if (isValidEmail(emailStr)) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(emailStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Snackbar snackbar = Snackbar.make(v, "Check your email to restore your password", BaseTransientBottomBar.LENGTH_LONG);
                        snackbar.setAction("Help", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Check your email to restore your password");
                                builder.setMessage("If verification page isn't opening, use VPN service. (This problem appears if your government blocks Google auth site)\n\n" +
                                        "Right after that, you can proceed back to log in page and log in with your new password");
                                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                        });
                        snackbar.show();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Something went wrong, maybe this email isn't linked to any account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            emailET.setError("Invalid email!");
        }
    }
}
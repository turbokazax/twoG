package UserEdit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.twog.MainActivity;
import com.twog.R;

import java.util.ArrayList;

import Models.Auth;
import Models.Interest;
import Models.User;
import Models.UserDataProvider;

public class InterestsActivity extends AppCompatActivity {
    private User user;
    private Auth auth;
    private Button btnSave;
    private Button btnDone;
    private ArrayList<String> arrayList;
    private Context context;
    //Interests Activity is made for user to set his interests
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        getSupportActionBar().setTitle("Interests");
        auth = new Auth(this);
        context = this;
        arrayList = new ArrayList<>();
        user = auth.getSignedInUser();
        btnSave = findViewById(R.id.btnSave);
        btnDone = findViewById(R.id.btnDone);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getInterests() != null) {
                    user.getInterests().clear();
                    int counter = 0;
                    LinearLayout ll = findViewById(R.id.linear);
                    for (int i = 0; i < ll.getChildCount(); i++) {
                        View element = ll.getChildAt(i);
                        if (element instanceof SwitchCompat) {
                            SwitchCompat sw = (SwitchCompat) element;
                            if (sw.isChecked()) {
                                user.getInterests().add(sw.getText().toString());
                                counter++;
                            }
                        }
                    }
                    if (counter > 0) {
                        Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
                        UserDataProvider.getInstance().updateUser(user);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Please choose at least one interest!");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                } else {
                    int counter = 0;
                    LinearLayout ll = findViewById(R.id.linear);
                    for (int i = 0; i < ll.getChildCount(); i++) {
                        View element = ll.getChildAt(i);
                        if (element instanceof SwitchCompat) {
                            SwitchCompat sw = (SwitchCompat) element;
                            if (sw.isChecked()) {
                                //user.getInterests().add(sw.getText().toString());
                                arrayList.add(sw.getText().toString());
                                counter++;
                            }
                        }
                    }
                    if(counter>0){
                        Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
                        UserDataProvider.getInstance().updateUser(user);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Please choose at least one interest!");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                    user.setInterests(arrayList);
                    Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
                    UserDataProvider.getInstance().updateUser(user);
                }
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Exit. Are you sure?");
                    builder.setMessage("Clicking on this button DOES NOT saves your interests\n\nYou could change interests anytime though");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            dialog.dismiss();
                            dialog.cancel();
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } catch (Exception ex) {

                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = findViewById(R.id.linear);
        for (Interest in : Interest.LIST) {
            SwitchCompat sw = new SwitchCompat(this);
            sw.setText(in.getTitle());
            sw.setTag(in.getId());
            if (user.getInterests() != null) {
                sw.setChecked(user.getInterests().contains(in.getTitle()));
            }
            ll.addView(sw, params);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, EditUserActivity.class);
        startActivity(intent);
    }
}
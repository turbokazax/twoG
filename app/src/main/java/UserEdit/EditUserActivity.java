package UserEdit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.twog.MainActivity;
import com.twog.R;

import Models.Auth;
import Models.OnSingleUserRetrievedListener;
import Models.User;
import Models.UserDataProvider;

public class EditUserActivity extends AppCompatActivity {
    private EditText nameET;
    private EditText ageET;
    private EditText descET;
    private ImageView imageView;
    private Button btnSave;
    private String nameStr;
    private String ageStr;
    private int age;
    private String descStr;
    private Auth auth;
    private User user;
    private Context context;
    private User currentUser;
    private boolean isOpenedFromFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edituser);
        nameET = findViewById(R.id.editUser_nameET);
        ageET = findViewById(R.id.editUser_ageET);
        descET = findViewById(R.id.editUser_descET);
        imageView = findViewById(R.id.editUser_imageview);
        btnSave = findViewById(R.id.editUser_btnSave);
        context=this;
        getSupportActionBar().setTitle("Edit");
        auth=new Auth(context);
        currentUser=auth.getSignedInUser();
        nameET.setText(auth.getSignedInUser().getName());
        int age1 = auth.getSignedInUser().getAge();
        String age1str = Integer.toString(age1);
        if(age1>0){
            ageET.setText(age1str);
        }
        descET.setText(auth.getSignedInUser().getUserInfo());
        TextInputLayout box = findViewById(R.id.editUser_descBox);
        int counter= 100-descET.getText().length();
        box.setHelperText("Symbols left: "+counter +"/100");
        descET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextInputLayout box = findViewById(R.id.editUser_descBox);
                int counter= 100-descET.getText().length();
                box.setHelperText("Symbols left: "+counter +"/100");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getSize();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getAvatarBase64() != null && !currentUser.getAvatarBase64().isEmpty()) {
                    byte[] decodedString = Base64.decode(currentUser.getAvatarBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }
                        nameStr = nameET.getText().toString().trim();
                        ageStr=ageET.getText().toString().trim();
                        descStr=descET.getText().toString().trim();
                        boolean noErrors = check();
                        if(noErrors){
                            user=auth.getSignedInUser();
                            age= Integer.parseInt(ageStr);
                            user.setName(nameStr);
                            user.setAge(age);
                            user.setUserInfo(descStr);
                            UserDataProvider.getInstance().updateUser(user);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Your data has been succesfully changed!");
                            builder.setMessage("Would you like to proceed to interests choose page now?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, InterestsActivity.class);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    dialog.dismiss();
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Would you like to set profile picture now?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, SetAvatarActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        if (currentUser.getAvatarBase64() != null && !currentUser.getAvatarBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(currentUser.getAvatarBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }
    }

    private void getSize() {
        if (descET.getLineCount()+1 != descET.getMaxLines()) {
            descET.setMaxLines((descET.getLineCount() + 1));
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wait!");
        builder.setMessage("You may have several changes, if you exit they will be lost!");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    private boolean check(){
            boolean isOk = true;
            if (nameStr.isEmpty() || nameStr == null) {
                isOk = false;
                nameET.setError("Empty input");
            } else if (ageStr.isEmpty() || ageStr == null) {
                isOk = false;
               ageET.setError("Empty input");
            } else if (descStr.isEmpty() || descStr == null) {
                isOk = false;
               descET.setError("Empty input");
            }
            return isOk;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
            if (currentUser.getAvatarBase64() != null && !currentUser.getAvatarBase64().isEmpty()) {
                byte[] decodedString = Base64.decode(currentUser.getAvatarBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

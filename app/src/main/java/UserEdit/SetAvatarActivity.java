package UserEdit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.twog.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import Models.Auth;
import Models.User;
import Models.UserDataProvider;

public class SetAvatarActivity extends AppCompatActivity {
    private ImageView imageView;
    private Auth auth;
    private User currentUser;
    private UserDataProvider provider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);
        provider= UserDataProvider.getInstance();
        imageView = findViewById(R.id.imageView);
        auth = new Auth(this);
        currentUser = auth.getSignedInUser();
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (currentUser.getAvatarBase64() != null && !currentUser.getAvatarBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(currentUser.getAvatarBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveAvatarToFB(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        currentUser.setAvatarBase64(encoded);
        provider.updateUser(currentUser);
    }

    public void onAvatarClicked(View v) {
        String[] options = new String[]{"Take photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your avatar");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), options[which], Toast.LENGTH_LONG).show();
                if (which == 0) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 0);
                } else if (which == 1) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 0) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                saveAvatarToFB(bitmap);
                imageView.setImageBitmap(bitmap);
            } else if (requestCode == 1) {
                try {
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    saveAvatarToFB(bitmap);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception ex) {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.twog.MainActivity;
import com.twog.R;

import java.util.ArrayList;

import Models.Auth;
import Models.User;
import UserEdit.EditUserActivity;

public class ProfileFragment extends Fragment {
    private ImageView imageView;
    private TextView lblName;
    private TextView lblAge;
    private TextView lblEmail;
    private TextView lblDesc;
    private TextView lblInterests;
    private Button btnEdit;
    private User user;
    private Auth auth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try{
            imageView=view.findViewById(R.id.imageView);
            user = MainActivity.DISPLAY_USER;
            if (user.getAvatarBase64() != null && !user.getAvatarBase64().isEmpty()) {
                byte[] decodedString = Base64.decode(user.getAvatarBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
            auth = new Auth(view.getContext());
            lblName = view.findViewById(R.id.lblName);
            lblAge = view.findViewById(R.id.lblAge);
            lblEmail = view.findViewById(R.id.lblEmail);
            lblDesc = view.findViewById(R.id.lblDesc);
            lblInterests = view.findViewById(R.id.lblInterests);
            btnEdit = view.findViewById(R.id.btnEdit);
            User currentUser = auth.getSignedInUser();
            if (user.getKey().equals(currentUser.getKey())) {
                btnEdit.setVisibility(View.VISIBLE);
            } else {
                btnEdit.setVisibility(View.INVISIBLE);
            }
            lblName.setText(user.getName());
            lblAge.setText(Integer.toString(user.getAge()));
            lblEmail.setText(user.getEmail());
            lblDesc.setText(user.getUserInfo());
            if (user.getName().length() > 10) {
                lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            } else if (user.getName().length() < 10 && user.getName().length() > 0) {
                lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                ;
            }
            ArrayList<String> interests = user.getInterests();
            String lblIntsStr = "Interested in: ";
            for (int i = 0; i < interests.size(); i++) {
                if (i != interests.size() - 1) {
                    lblIntsStr += interests.get(i) + ", ";
                } else {
                    lblIntsStr += interests.get(i) + ".";
                }
            }
            lblInterests.setText(lblIntsStr);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), EditUserActivity.class);
                    //intent.putExtra("isOpenedFromFragment", true);
                    startActivity(intent);
                }
            });
        }catch (Exception ex){

        }
    }
}

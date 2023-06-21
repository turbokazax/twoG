package Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twog.R;

import java.util.ArrayList;




public class UserAdapter extends ArrayAdapter<User> {
    private Context context;
    private int resource;
    private ArrayList<User> users;
    private Auth auth;
    public UserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource= resource;
        this.users = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(this.context).inflate(resource, null);
        auth = new Auth(v.getContext());
        TextView lblName = v.findViewById(R.id.lblName);
        TextView lblAge = v.findViewById(R.id.lblAge);
        TextView lblInterests = v.findViewById(R.id.lblInterests);
        TextView lblDesc = v.findViewById(R.id.lblDesc);
        TextView lblEmail = v.findViewById(R.id.lblEmail);
        ImageView imageView = v.findViewById(R.id.imageView);
        User user = this.getItem(position);
        if (user.getAvatarBase64() != null && !user.getAvatarBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(user.getAvatarBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }
        if(user.getName().length()>10){
            lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }else if(user.getName().length()<10 && user.getName().length()>0){
            lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);;
        }
        if(user.getLogin().equals(auth.getSignedInUser().getLogin())){
            lblName.setText(user.getName()+"(You)");
        }else{
            lblName.setText(user.getName());
        }
        lblEmail.setText(user.getEmail());
        lblAge.setText(Integer.toString(user.getAge())+" y.o");
        lblDesc.setText(user.getUserInfo());
        ArrayList<String> interests = user.getInterests();
        String lblIntsStr = "Interested in: ";
        for(int i=0; i<interests.size();i++){
            if(i!=interests.size()-1){
                lblIntsStr+=interests.get(i)+", ";
            }else{
                lblIntsStr+=interests.get(i)+".";
            }
        }
        lblInterests.setText(lblIntsStr);
        return v;
    }
}

package Conversations;

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
import androidx.cardview.widget.CardView;

import com.twog.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Models.Auth;
import Models.Conversation;
import Models.OnSingleUserRetrievedListener;
import Models.User;
import Models.UserDataProvider;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
    private Context context;
    private int resource;
    private ArrayList<Conversation> conversations;
    private User receiverUser;
    private Auth auth;
    public ConversationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Conversation> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.conversations = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(this.context).inflate(resource, null);
        ImageView imageView = v.findViewById(R.id.imageView);
        TextView lblName = v.findViewById(R.id.lblName);
        TextView lblMessage = v.findViewById(R.id.lblMsg);
        Conversation conversation = this.getItem(position);
        auth = new Auth(v.getContext());
        String receiverUserID = "";
        if(!conversation.getUser1ID().equals(auth.getSignedInUserKey())){
            receiverUserID = conversation.getUser1ID();
        }else{
            receiverUserID = conversation.getUser2ID();
        }
        OnSingleUserRetrievedListener listener = new OnSingleUserRetrievedListener() {
            @Override
            public void OnUserRetrieved(User user) {
                receiverUser=user;
                String receiverName = receiverUser.getName();
                lblName.setText(receiverUser.getName());
                if (receiverName.length() > 10) {
                    lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else if (receiverName.length() < 10 && receiverName.length() > 0) {
                    lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    ;
                }
                if (receiverUser.getAvatarBase64() != null && !receiverUser.getAvatarBase64().isEmpty()) {
                    byte[] decodedString = Base64.decode(receiverUser.getAvatarBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }
            }
        };
        UserDataProvider.getInstance().getUserByKey(receiverUserID,listener );
        //lblMessage.setText(); //SET LAST MESSAGE TEXT HERE
        return v;
    }
}

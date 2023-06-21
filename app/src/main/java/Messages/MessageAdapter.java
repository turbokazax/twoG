package Messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.twog.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.Auth;
import Models.Message;
import Models.OnSingleUserRetrievedListener;
import Models.User;
import Models.UserDataProvider;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private int resource;
    private ArrayList<Message> messages;
    private Auth auth;
    private  User senderUser;
    public MessageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Message> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(this.context).inflate(resource, null);
        Message message = this.getItem(position);
        auth = new Auth(v.getContext());
        String userSenderID = message.getSenderID();
        senderUser= new User();
        Drawable myMessage = v.getResources().getDrawable(R.drawable.custom_mymessage_background);
        Drawable otherMessage = v.getResources().getDrawable(R.drawable.custom_message_background);
        TextView lblMessage = v.findViewById(R.id.lblMessage);
        TextView lblTimeSent = v.findViewById(R.id.lblTimeSent);
        ImageView imageView = v.findViewById(R.id.imageView);
        CardView imageViewBox = v.findViewById(R.id.imageViewBox);
        RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams) lblMessage.getLayoutParams();
        RelativeLayout.LayoutParams timeSentParams = (RelativeLayout.LayoutParams) lblTimeSent.getLayoutParams();
        RelativeLayout.LayoutParams imageViewBoxParams = (RelativeLayout.LayoutParams) imageViewBox.getLayoutParams();
        OnSingleUserRetrievedListener listener = new OnSingleUserRetrievedListener() {
            @Override
            public void OnUserRetrieved(User user) {
                senderUser=user;
                if(!senderUser.getKey().equals(auth.getSignedInUserKey())){
                    if (senderUser.getAvatarBase64() != null && !senderUser.getAvatarBase64().isEmpty()) {
                        byte[] decodedString = Base64.decode(senderUser.getAvatarBase64(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageView.setImageBitmap(decodedByte);
                    }
                }
            }
        };
        UserDataProvider.getInstance().getUserByKey(userSenderID, listener);
        //if message wasn't sent by current user we change it's appearance
        if (!userSenderID.equals(auth.getSignedInUserKey())) {
            //editing params
            imageViewBoxParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            imageViewBoxParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            msgParams.addRule(RelativeLayout.LEFT_OF, 0);
            msgParams.addRule(RelativeLayout.RIGHT_OF, R.id.imageViewBox);
            timeSentParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.lblMessage);
            timeSentParams.addRule(RelativeLayout.ALIGN_RIGHT, 0);
            //setting params
            lblMessage.setGravity(Gravity.LEFT);
            lblMessage.setBackground(otherMessage);
            lblMessage.setLayoutParams(msgParams);
            imageViewBox.setLayoutParams(imageViewBoxParams);
            lblTimeSent.setLayoutParams(timeSentParams);
        }else{
            if (auth.getSignedInUser().getAvatarBase64() != null && !auth.getSignedInUser().getAvatarBase64().isEmpty()) {
                byte[] decodedString = Base64.decode(auth.getSignedInUser().getAvatarBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
        }
        lblMessage.setText(message.getMessageText());
        lblTimeSent.setText(getHHMMTime(message.getTimeSent()));

        return v;
    }

    private String getHHMMTime(long epochTIme) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epochTIme * 1000);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        String hoursStr = Integer.toString(hours);
        String minStr = "";
        String dayStr, monthStr;
        if (minutes < 10) {
            minStr = "0" + Integer.toString(minutes);
        } else {
            minStr = Integer.toString(minutes);
        }
        if(month<10){
            monthStr="0"+month;
        }else{
            monthStr=month+"";
        }
        if(day<10){
            dayStr="0"+day;
        }else{
            dayStr=day+"";
        }
        String timeHHMM =dayStr + "."+ monthStr +" "+ hoursStr + ":" + minStr;
        return timeHHMM;
    }
}

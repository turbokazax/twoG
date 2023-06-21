package Messages;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Models.Conversation;
import Models.Message;
import Models.OnMessagesRetrievedListener;

public class MessagesDataProvider {
    private FirebaseDatabase db;
    private DatabaseReference messages;
    private Conversation currentConversation;

    public MessagesDataProvider(Conversation conversation) {
        db = FirebaseDatabase.getInstance();
        currentConversation = conversation;
        messages = db.getReference().child("Conversations").child(currentConversation.getId()).child("Messages");
    }
    public void addMessage(Message message, View view){
        if(!currentConversation.isBlocked()){
            DatabaseReference push = messages.push();
            message.setId(push.getKey());
            message.setTimeSent(System.currentTimeMillis()/1000);
            push.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                    }else{
                        Toast.makeText(view.getContext(), "Error sending a message, please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(view.getContext(), "Conversation is blocked", Toast.LENGTH_SHORT).show();
        }
    }
    public void getMessages(OnMessagesRetrievedListener listener){
        ArrayList<Message> messageArrayList = new ArrayList<>();
        Query query= messages.orderByChild("timeSent");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Message message = new Message();
                messageArrayList.clear();
                for(DataSnapshot msgData : snapshot.getChildren()){
                    message=msgData.getValue(Message.class);
                    if(message!=null){
                        messageArrayList.add(message);
                    }
                }
                listener.OnMessagesRetrieved(messageArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void deleteMessage(Message message){
        messages.child(message.getId()).removeValue();
    }
}

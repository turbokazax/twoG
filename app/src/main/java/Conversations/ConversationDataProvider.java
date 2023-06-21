package Conversations;

import android.content.Context;
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
import Models.OnConversationsRetrievedListener;
import Models.OnSingleConversationRetrievedListener;
import Models.User;

public class ConversationDataProvider {
    private FirebaseDatabase db;
    private DatabaseReference conversations;
    private DatabaseReference deletedConversations;
    public ConversationDataProvider() {
        db = FirebaseDatabase.getInstance();
        conversations = db.getReference().child("Conversations");
        deletedConversations=db.getReference().child("DeletedConversations");
    }

    private static ConversationDataProvider provider = new ConversationDataProvider();

    public static ConversationDataProvider getInstance() {
        return provider;
    }

    public void addConversation(Conversation conversation, Context context) {
        /*conversations.child(conversation.getId()).setValue(conversation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Conversation created!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Something went wrong when adding a conversation. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        DatabaseReference push = conversations.push();
        conversation.setId(push.getKey());
        push.setValue(conversation);
    }
    /**
     * @param user1    user1 is the sender user
     * @param user2    user2 is the accepter
     * @param listener listener...
     */
    public void getConversation(User user1, User user2, OnSingleConversationRetrievedListener listener) {
        Query query = conversations.orderByChild("user1");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conversation conversation = new Conversation();
                for (DataSnapshot convData : snapshot.getChildren()) {
                    conversation = convData.getValue(Conversation.class);
                }
                listener.OnSingleConversationRetrived(conversation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //comment just 4 test
    //gets all conversations with User1
    public void getConversations(User user1, OnConversationsRetrievedListener listener) {
        ArrayList<Conversation> conversationArrayList = new ArrayList<>();
        Query query = conversations.orderByChild("id");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationArrayList.clear();
                for (DataSnapshot convData : snapshot.getChildren()) {
                    Conversation conversation = convData.getValue(Conversation.class);
                    if (conversation != null) {
                        if (conversation.getUser1ID().equals(user1.getKey()) || conversation.getUser2ID().equals(user1.getKey())) {
                            conversationArrayList.add(conversation);
                        }
                    }
                }
                listener.OnConversationsRetrieved(conversationArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getConversation(Conversation conversation, OnSingleConversationRetrievedListener listener){
        Query query= conversations.orderByChild("id").equalTo(conversation.getId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conversation conversation = new Conversation();
                for(DataSnapshot convData : snapshot.getChildren()){
                    Conversation conversation1 = convData.getValue(Conversation.class);
                    if(conversation1!=null){
                        conversation = conversation1;
                    }
                }
                listener.OnSingleConversationRetrived(conversation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getConversations(User user1, User user2, OnConversationsRetrievedListener listener) {
        ArrayList<Conversation> conversationArrayList = new ArrayList<>();
        Query query = conversations.orderByChild("id");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationArrayList.clear();
                for (DataSnapshot convData : snapshot.getChildren()) {
                    Conversation conversation = convData.getValue(Conversation.class);
                    if (conversation != null) {
                        if ((conversation.getUser1ID().equals(user1.getKey()) && conversation.getUser2ID().equals(user2.getKey())) || (
                                conversation.getUser2ID().equals(user1.getKey()) && conversation.getUser1ID().equals(user2.getKey()))
                        ) {
                           conversationArrayList.add(conversation);
                        }
                    }
                }
                listener.OnConversationsRetrieved(conversationArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteConversation(Conversation conversation){
        conversations.child(conversation.getId()).child("Messages").removeValue();
    }
    public void deleteConversation1(Conversation conversation){
        conversations.child(conversation.getId()).removeValue();
    }
    public void updateConversation(Conversation conversation){
        conversations.child(conversation.getId()).setValue(conversation);
    }
    public void updateConversation(Conversation conversation, String userWhoBlockedID, boolean isBlocked){
        conversations.child(conversation.getId()).child("blocked").setValue(isBlocked);
        conversations.child(conversation.getId()).child("userWhoBlockedID").setValue(userWhoBlockedID);
    }
}

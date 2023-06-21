package Models;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDataProvider {
    private FirebaseDatabase db;
    private DatabaseReference users;

    public UserDataProvider() {
        db = FirebaseDatabase.getInstance();
        users = db.getReference().child("Users");
    }

    private static UserDataProvider provider = new UserDataProvider();

    public static UserDataProvider getInstance() {
        return provider;
    }

    /*public void addUser(User u){
        DatabaseReference push = users.push();
        String key = push.getKey();
        u.setKey(key);
        push.setValue(u);
    }*/
    public void addUser(User user, Context context) {
        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
                    Toast.makeText(context, "Something wrong happened. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getUser(String login, OnSingleUserRetrievedListener listener) {
        Query query = users.orderByChild("login").equalTo(login);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                for (DataSnapshot userData : snapshot.getChildren()) {
                    user = userData.getValue(User.class);
                }
                listener.OnUserRetrieved(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getUserByKey(String key, OnSingleUserRetrievedListener listener) {
        Query query = users.orderByChild("key").equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                    for (DataSnapshot userData : snapshot.getChildren()) {
                    user = userData.getValue(User.class);
                }
                listener.OnUserRetrieved(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    /** *
     * Значения филтрра соотвествуют item-ам в меню в MainActivity
     * No, One, Half
     *
     **/
    public void getUsers(OnUsersRetrievedListener listener, String filter, ArrayList<String> Interests) {
        ArrayList<User> userArrayList = new ArrayList<>();
        Query query = users.orderByChild("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot userData : snapshot.getChildren()) {
                    User user = userData.getValue(User.class);
                    if(user.getName()!=null && !user.getName().isEmpty() && user.getInterests()!=null ){
                        if (filter.equals("No")) {
                            userArrayList.add(user);
                        } else if (filter.equals("One")) {
                            int counter=0;
                            for (int i = 0; i < Interests.size(); i++) {
                                ArrayList<String> checkInts = user.getInterests();
                                if(checkInts.contains(Interests.get(i))){
                                    counter++;
                                }
                            }
                            if(counter>=1){
                                userArrayList.add(user);
                            }
                        }else if(filter.equals("Half")){
                            int counter=0;
                            for (int i = 0; i < Interests.size(); i++) {
                                ArrayList<String>   checkInts = user.getInterests();
                                if(checkInts.contains(Interests.get(i))){
                                    counter++;
                                }
                            }
                            if(counter>=Interests.size()/2){
                                userArrayList.add(user);
                            }
                        }
                    }
                }
                listener.OnUsersRetrieved(userArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateUser(User u) {
        users.child(u.getKey()).setValue(u);
    }

    public void deleteUser(User u) {
        users.child(u.getKey()).removeValue();
    }
}

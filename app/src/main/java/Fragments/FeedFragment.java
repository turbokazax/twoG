package Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.twog.MainActivity;
import com.twog.R;

import java.util.ArrayList;

import Conversations.ConversationDataProvider;
import Models.Auth;
import Models.Conversation;
import Models.OnConversationsRetrievedListener;
import Models.OnUsersRetrievedListener;
import Models.User;
import Models.UserAdapter;
import Models.UserDataProvider;

public class FeedFragment extends Fragment {
    private Auth auth;
    private ArrayList<User> userArrayList;
    private ListView feedListView;
    private UserAdapter userAdapter;
    private User user;
    private OnUsersRetrievedListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        auth = new Auth(view.getContext());
        user = auth.getSignedInUser();
        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(view.getContext(), R.layout.user_tile, userArrayList);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });
        feedListView = view.findViewById(R.id.feedListView);
        feedListView.setAdapter(userAdapter);
        listener = new OnUsersRetrievedListener() {
            @Override
            public void OnUsersRetrieved(ArrayList<User> users) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                userArrayList.clear();
                userArrayList.addAll(users);
                userAdapter.notifyDataSetChanged();
            }
        };
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.DISPLAY_USER = userArrayList.get(position);
                //Toast.makeText(view.getContext(), MainActivity.DISPLAY_USER.getName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder= new AlertDialog.Builder(view.getContext());
                String[] items = new String[]{"Open profile", "Write a message", "Cancel"};
                builder.setTitle("Choose an action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            ((MainActivity) getActivity()).setProfileFragment(userArrayList.get(position));
                        }else if(which==1){
                            if(MainActivity.DISPLAY_USER.getKey().equals(auth.getSignedInUserKey())){
                                Toast.makeText(view.getContext(), "You can't message yourself!", Toast.LENGTH_SHORT).show();
                            }else{
                                Conversation conversation = new Conversation();
                                conversation.setUser1ID(auth.getSignedInUser().getKey());
                                conversation.setUser2ID(userArrayList.get(position).getKey());
                                OnConversationsRetrievedListener listener = new OnConversationsRetrievedListener() {
                                    @Override
                                    public void OnConversationsRetrieved(ArrayList<Conversation> conversations) {
                                        if(conversations.isEmpty()){
                                            ConversationDataProvider.getInstance().addConversation(conversation, getActivity().getApplicationContext());
                                            Toast.makeText(view.getContext(), "Conversation created!", Toast.LENGTH_SHORT).show();
                                        }
                                        ((MainActivity) getActivity()).setMessagesFragment();
                                    }
                                };
                                ConversationDataProvider.getInstance().getConversations(auth.getSignedInUser(), userArrayList.get(position), listener);
                            }
                        }
                    }
                });
                builder.show();
            }
        });
        UserDataProvider.getInstance().getUsers(listener, MainActivity.FILTER, user.getInterests());
    }

    public void refreshFeed() {
        swipeRefreshLayout.setRefreshing(true);
        UserDataProvider.getInstance().getUsers(listener, MainActivity.FILTER, user.getInterests());
    }

}

package Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.twog.MainActivity;
import com.twog.R;

import java.util.ArrayList;

import Conversations.ConversationAdapter;
import Conversations.ConversationDataProvider;
import Messages.MessagesDataProvider;
import Models.Appeal;
import Models.Auth;
import Models.Conversation;
import Models.Message;
import Models.OnConversationsRetrievedListener;
import Models.OnMessagesRetrievedListener;
import Models.User;
import Support.SupportDataProvider;

public class MessagesFragment extends Fragment {
    private ListView conversationsListView;
    private ConversationAdapter conversationAdapter;
    private ArrayList<Conversation> conversationArrayList;
    private ProgressBar progressBar;
    private Auth auth;
    private LinearLayout linearLayout;
    private User currentUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnConversationsRetrievedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshConvs();
            }
        });
        linearLayout = view.findViewById(R.id.linear);
        auth = new Auth(view.getContext());
        currentUser = auth.getSignedInUser();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        conversationArrayList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(view.getContext(), R.layout.message_tile, conversationArrayList);
        conversationsListView = view.findViewById(R.id.conversationsListView);
        conversationsListView.setAdapter(conversationAdapter);
        listener = new OnConversationsRetrievedListener() {
            @Override
            public void OnConversationsRetrieved(ArrayList<Conversation> conversations) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                conversationArrayList.clear();
                conversationArrayList.addAll(conversations);
                conversationAdapter.notifyDataSetChanged();
                if (conversationArrayList.isEmpty()) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        };
        conversationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).setconversationFragment(conversationArrayList.get(position));
            }
        });
        conversationsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                String[] itemsNonBlocked = new String[]{"Clear message history", "Block this conversation", "Cancel"};
                String[] itemsBlocked = new String[]{"Clear message history", "Unblock this conversation", "Cancel"};
                String[] items = new String[0];
                if(conversationArrayList.get(position).isBlocked()){
                    items=itemsBlocked;
                }else{
                    items=itemsNonBlocked;
                }
                builder.setTitle("Choose an action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            dialog.dismiss();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                            builder1.setTitle("Clear message history");
                            builder1.setMessage("This action CAN NOT be undone\n\nMessages will be deleted for both users!");
                            builder1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder1.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Conversation conversation = conversationArrayList.get(position);
                                    ConversationDataProvider.getInstance().deleteConversation(conversation);
                                    refreshConvs();
                                }
                            });
                            builder1.show();
                        }else if(which==1){
                            dialog.dismiss();
                            if(conversationArrayList.get(position).isBlocked()){
                                if(conversationArrayList.get(position).getUserWhoBlockedID().equals(auth.getSignedInUserKey())){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                    builder1.setTitle("Unblock this conversation?");
                                    builder1.setMessage("You and other user could again write in this conversation");
                                    builder1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder1.setNeutralButton("Unblock", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Conversation conversation = conversationArrayList.get(position);
                                            conversation.setUserWhoBlockedID("");
                                            conversation.setBlocked(false);
                                            ConversationDataProvider.getInstance().updateConversation(conversation, conversation.getUserWhoBlockedID(), conversation.isBlocked());
                                            refreshConvs();
                                        }
                                    });
                                    builder1.show();
                                }else{
                                    Toast.makeText(view.getContext(), "You can't unblock this conversation. Only user who blocked you can unblock this conversation", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                builder1.setTitle("Block this conversation?");
                                builder1.setMessage("You and other user could not write in this conversation\n\nYou can also report this user " +
                                        "if he's violating our rules");
                                builder1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder1.setNegativeButton("Report and block", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Conversation conversation = conversationArrayList.get(position);
                                        String userReportedID;
                                        if(conversation.getUser1ID().equals(auth.getSignedInUserKey())){
                                            userReportedID=conversation.getUser2ID();
                                        }else{
                                            userReportedID=conversation.getUser1ID();
                                        }
                                        Appeal appeal = new Appeal();
                                        appeal.setMessage("Violating rules");
                                        appeal.setTitle("Report on "+userReportedID);
                                        appeal.setSenderId(auth.getSignedInUserKey());
                                        SupportDataProvider.getInstance().addReport(appeal, view.getContext());
                                        conversation.setUserWhoBlockedID(auth.getSignedInUserKey());
                                        conversation.setBlocked(true);
                                        ConversationDataProvider.getInstance().updateConversation(conversation, conversation.getUserWhoBlockedID(), conversation.isBlocked());
                                        refreshConvs();
                                    }
                                });
                                builder1.setNeutralButton("Block", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Conversation conversation = conversationArrayList.get(position);
                                        ArrayList<Message> messages = conversation.getMessageArrayList();
                                        conversation.setUserWhoBlockedID(auth.getSignedInUserKey());
                                        conversation.setBlocked(true);
                                        ConversationDataProvider.getInstance().updateConversation(conversation, conversation.getUserWhoBlockedID(), conversation.isBlocked());
                                        refreshConvs();
                                    }
                                });
                                builder1.show();
                            }
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        ConversationDataProvider.getInstance().getConversations(currentUser, listener);
    }

    public void refreshConvs() {
        swipeRefreshLayout.setRefreshing(true);
        ConversationDataProvider.getInstance().getConversations(currentUser, listener);
    }
}

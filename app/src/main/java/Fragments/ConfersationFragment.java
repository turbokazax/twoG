package Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.twog.R;

import java.io.InputStream;
import java.util.ArrayList;

import Conversations.ConversationDataProvider;
import Messages.MessageAdapter;
import Messages.MessagesDataProvider;
import Models.Auth;
import Models.Conversation;
import Models.Message;
import Models.OnMessagesRetrievedListener;
import Models.OnSingleConversationRetrievedListener;

import static android.app.Activity.RESULT_OK;

public class ConfersationFragment extends Fragment {
    private MessageAdapter adapter;
    private Conversation currentConversation;
    private ImageView btnAttach;
    private ImageView btnSend;
    private EditText messageET;
    private String messageStr;
    private ListView messagesListView;
    private MessagesDataProvider messagesDataProvider;
    private Auth auth;
    private ArrayList<Message> messageArrayList;

    public ConfersationFragment(Conversation conversation) {
        this.currentConversation = conversation;
    }

    public ConfersationFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        auth = new Auth(view.getContext());
        messageET = view.findViewById(R.id.messageET);
        btnSend = view.findViewById(R.id.btnSend);
        //btnAttach = view.findViewById(R.id.btnAttach);
        messagesDataProvider = new MessagesDataProvider(currentConversation);
        OnSingleConversationRetrievedListener conversationRetrievedListener = new OnSingleConversationRetrievedListener() {
            @Override
            public void OnSingleConversationRetrived(Conversation conversation) {
                currentConversation = conversation;
                if (currentConversation != null) {
                    if (currentConversation.isBlocked()) {
                        messageET.setFocusable(false);
                        messageET.setText("Conversation is blocked");
                    } else {
                        messageET.setText("");
                        messageET.setFocusableInTouchMode(true);
                        ;
                    }
                }
            }
        };
        ConversationDataProvider.getInstance().getConversation(currentConversation, conversationRetrievedListener);
        messageArrayList = new ArrayList<>();
        messagesListView = view.findViewById(R.id.messagesListView);
        adapter = new MessageAdapter(view.getContext(), R.layout.conversation_message_tile, messageArrayList);
        messagesListView.setAdapter(adapter);
        /*btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.custom_onclick_anim));
                String[] options = new String[]{"Take photo", "Choose from gallery", "Cancel"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
                builder.setTitle("Attach a file");
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
        });*/
        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSize();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.custom_onclick_anim));
                if (!currentConversation.isBlocked()) {
                    messageStr = messageET.getText().toString().trim();
                    if (!messageStr.isEmpty()) {
                        Message message = new Message();
                        message.setMessageText(messageStr);
                        message.setSenderID(auth.getSignedInUserKey());
                        messagesDataProvider.addMessage(message, view);
                        messageET.setText("");
                    }
                    //Toast.makeText(view.getContext(), currentConversation.getId(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        messagesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Message clickedMessage = messageArrayList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                String[] itemsForSender = {"Delete message", "Copy to clipboard", "Cancel"};
                String[] justItems = {"Copy to clipboard", "Cancel"};
                String[] items;
                if(messageArrayList.get(position).getSenderID().equals(auth.getSignedInUserKey())){
                    items=itemsForSender;
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                dialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                builder1.setTitle("Delete message?");
                                builder1.setMessage("This action CAN NOT be undone\n\nIt will delete this message for both users");
                                builder1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder1.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        messagesDataProvider.deleteMessage(clickedMessage);
                                    }
                                });
                                builder1.show();
                            } else if (which == 1) {
                                setClipboard(view.getContext(), clickedMessage.getMessageText());
                                Toast.makeText(view.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }else{
                    items=justItems;
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                setClipboard(view.getContext(), clickedMessage.getMessageText());
                                Toast.makeText(view.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
        OnMessagesRetrievedListener listener = new OnMessagesRetrievedListener() {
            @Override
            public void OnMessagesRetrieved(ArrayList<Message> messages) {
                messageArrayList.clear();
                messageArrayList.addAll(messages);
                adapter.notifyDataSetChanged();
            }
        };
        messagesDataProvider.getMessages(listener);

    }

    private void getSize() {
        if (messageET.getLineCount() + 1 != messageET.getMaxLines()) {
            messageET.setMaxLines((messageET.getLineCount() + 1));
        }
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

}

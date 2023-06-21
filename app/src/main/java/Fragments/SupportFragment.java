package Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.twog.R;

import Models.Appeal;
import Models.Auth;
import Support.SupportDataProvider;

public class SupportFragment extends Fragment {
    private EditText supportET;
    private String supportStr;
    private Button btnSend;
    private EditText titleET;
    private String titleStr;
    private Context context;
    private Auth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context=view.getContext();
        supportET = view.findViewById(R.id.supmessageET);
        titleET=view.findViewById(R.id.suptitleET);
        btnSend=view.findViewById(R.id.btnSend);
        auth=new Auth(context);
        supportET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextInputLayout box = view.findViewById(R.id.supmessageBox);
                int counter = 200 - supportET.getText().length();
                box.setHelperText("Symbols left: "+counter +"/200");
            }

            @Override
            public void afterTextChanged(Editable s) {
                getSize();
            }
        });
        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextInputLayout box = view.findViewById(R.id.suptitleBox);
                int counter = 30 - titleET.getText().length();
                box.setHelperText("Symbols left: "+counter +"/30");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportStr=supportET.getText().toString().trim();
                titleStr=titleET.getText().toString().trim();
                if(!supportStr.isEmpty()){
                    if(!titleStr.isEmpty()){
                        Appeal appeal = new Appeal();
                        appeal.setMessage(supportStr);
                        appeal.setTitle(titleStr);
                        appeal.setSenderId(auth.getSignedInUserKey());
                        SupportDataProvider.getInstance().addAppeal(appeal, context);
                        supportET.setText("");
                        titleET.setText("");
                    }else{
                        titleET.setError("Empty title");
                    }
                }else{
                    supportET.setError("Empty message");
                }
            }
        });
    }
    private void getSize() {
        if (supportET.getLineCount()+1 != supportET.getMaxLines()) {
            supportET.setMaxLines((supportET.getLineCount() + 1));
        }
    }
}

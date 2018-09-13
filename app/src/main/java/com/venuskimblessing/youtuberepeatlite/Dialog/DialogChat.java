package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.venuskimblessing.youtuberepeatlite.Chat.ChatDTO;
import com.venuskimblessing.youtuberepeatlite.R;

public class DialogChat extends Dialog implements View.OnClickListener, ValueEventListener {
    private final String TAG = "DialogChat";
    private Context mContext;
    private ListView mListView;
    private EditText mEditText;
    private Button mSendButton;

    private ArrayAdapter<String> mArrayAdapter;

    //firebase
    private FirebaseDatabase mDatabase = null;
    private DatabaseReference mChatRef = null;

    public DialogChat(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogChat(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogChat(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_chat);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mListView = (ListView)findViewById(R.id.chat_listview);
        mEditText = (EditText) findViewById(R.id.chat_bottom_editText);
        mSendButton = (Button)findViewById(R.id.chat_bottom_send_button);
        mSendButton.setOnClickListener(this);

        mArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1);
        mListView.setAdapter(mArrayAdapter);

        mDatabase = FirebaseDatabase.getInstance();
        mChatRef = mDatabase.getReference("chat");
        mChatRef.addValueEventListener(this);
    }

    @Override
    public void onClick(View v) {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNick("test");
        chatDTO.setMessage(mEditText.getText().toString().trim());
        mChatRef.setValue(chatDTO);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange...");
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        if(chatDTO != null){
            String chatMessage = chatDTO.getNick() + " : " + chatDTO.getMessage();
            mArrayAdapter.add(chatMessage);
            mEditText.setText("");
        }else{
            Log.d(TAG, "onDataChange... chatDTO is Null");
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(TAG, "onCancelled...");

    }
}

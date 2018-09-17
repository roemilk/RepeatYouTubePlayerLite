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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.venuskimblessing.youtuberepeatlite.Chat.ChatDTO;
import com.venuskimblessing.youtuberepeatlite.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogChat extends Dialog implements View.OnClickListener, ValueEventListener {
    private final String TAG = "DialogChat";
    private Context mContext;
    private RelativeLayout mChatBodyLay;
    private LinearLayout mChatIntroLay;
    private ListView mListView;
    private EditText mNickEditText, mMessageEditText;
    private Button mJoinButton, mMessageSendButton, mCloseButton;

    private ArrayAdapter<String> mArrayAdapter;

    //firebase
    private FirebaseDatabase mDatabase = null;
    private DatabaseReference mChatRef = null;

    //UserInfo
    private String mNickName = "";

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

        mChatIntroLay = (LinearLayout)findViewById(R.id.chat_intro_lay);
        mChatBodyLay = (RelativeLayout)findViewById(R.id.chat_body_lay);
        mListView = (ListView)findViewById(R.id.chat_listview);
        mNickEditText = (EditText)findViewById(R.id.chat_intro_nick_editText);
        mMessageEditText = (EditText) findViewById(R.id.chat_bottom_editText);

        mJoinButton = (Button) findViewById(R.id.chat_intro_join_button);
        mJoinButton.setOnClickListener(this);

        mMessageSendButton = (Button)findViewById(R.id.chat_bottom_send_button);
        mMessageSendButton.setOnClickListener(this);

        mCloseButton = (Button)findViewById(R.id.chat_top_lay_close_button);
        mCloseButton.setOnClickListener(this);

        mArrayAdapter = new ArrayAdapter<>(mContext, R.layout.view_chat_textview, R.id.view_chat_textView1);
        mListView.setAdapter(mArrayAdapter);

        mDatabase = FirebaseDatabase.getInstance();
        mChatRef = mDatabase.getReference("chat");
        mChatRef.addValueEventListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_bottom_send_button:
                String message = mMessageEditText.getText().toString().trim();
                sendMessage(mNickName, message);
                break;
            case R.id.chat_intro_join_button:
                mNickName = mNickEditText.getText().toString().trim();
                sendJoinMessage();
                mChatIntroLay.setVisibility(View.GONE);
                mChatBodyLay.setVisibility(View.VISIBLE);
                mMessageEditText.requestFocus();
                break;
            case R.id.chat_top_lay_close_button:
                dismiss();
                break;
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange...");
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        if(chatDTO != null){
            String nick = chatDTO.getNick();
            String chatMessage = "";
            if(nick.equals("")){
                chatMessage = chatDTO.getMessage();
            }else{
                chatMessage = chatDTO.getNick() + " : " + chatDTO.getMessage();
            }
            mArrayAdapter.add(chatMessage);
            mListView.smoothScrollToPosition(mArrayAdapter.getCount());
            mMessageEditText.setText("");
        }else{
            Log.d(TAG, "onDataChange... chatDTO is Null");
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(TAG, "onCancelled...");

    }

    private void sendJoinMessage(){
        String joinMesage = mNickName + "님이 입장하셨습니다.";
        sendMessage("", joinMesage);
    }

    /**
     * 메시지 전송
     * @param nick
     * @param message
     */
    private void sendMessage(String nick, String message){
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setNick(nick);
        chatDTO.setMessage(message);
        chatDTO.setTime(getTime());
        mChatRef.setValue(chatDTO);
    }

    private String getTime(){
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);
        return formatDate;
    }
}

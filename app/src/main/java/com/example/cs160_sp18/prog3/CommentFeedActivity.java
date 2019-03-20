package com.example.cs160_sp18.prog3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

// Displays a list of comments for a particular landmark.
public class CommentFeedActivity extends AppCompatActivity {

    private static final String TAG = CommentFeedActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Comment> mComments;
    private String username;
    private FirebaseDatabase database;
    private DatabaseReference statueRef;

    // UI elements
    EditText commentInputBox;
    RelativeLayout layout;
    Button sendButton;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String landmarkName = getIntent().getStringExtra("statue name");
        username = getIntent().getStringExtra("username");

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        statueRef = database.getReference(landmarkName);
        ValueEventListener statueRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mComments = new ArrayList<Comment>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    String timestamp = commentSnapshot.getKey();
                    String username = (String) commentSnapshot.child("username").getValue();
                    String commentText = (String) commentSnapshot.child("comment").getValue();
                    mComments.add(new Comment(commentText, username, timestamp));
                    setAdapterAndUpdateData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("0", "cancelled");
            }
        };
        statueRef.addValueEventListener(statueRefListener);

        setContentView(R.layout.activity_comment_feed);

        // sets the app bar's title
        setTitle(landmarkName + ": Posts");

        // hook up UI elements
        layout = (RelativeLayout) findViewById(R.id.comment_layout);
        commentInputBox = (EditText) layout.findViewById(R.id.comment_input_edit_text);
        sendButton = (Button) layout.findViewById(R.id.send_button);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(landmarkName);

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create an onclick for the send button
        setOnClickForSendButton();
    }

    private void setOnClickForSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInputBox.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    // don't do anything if nothing was added
                    commentInputBox.requestFocus();
                } else {
                    // clear edit text, post comment
                    commentInputBox.setText("");
                    postNewComment(comment);
                }
            }
        });
    }

    private void setAdapterAndUpdateData() {
        // create a new adapter with the updated mComments array
        // this will "refresh" our recycler view
        mAdapter = new CommentAdapter(this, mComments);
        mRecyclerView.setAdapter(mAdapter);

        // scroll to the last comment
        if (mComments.size() > 0) {
            mRecyclerView.smoothScrollToPosition(mComments.size() - 1);
        }
    }

    private void postNewComment(String commentText) {
        Comment newComment = new Comment(commentText, username, new Date().toString());
        mComments.add(newComment);
        DatabaseReference commentRef = statueRef.child(newComment.timestamp);
        commentRef.child("username").setValue(username);
        commentRef.child("comment").setValue(commentText);
        setAdapterAndUpdateData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

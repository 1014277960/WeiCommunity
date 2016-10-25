package com.wulinpeng.weicommunity.post.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/25 下午4:15
 * @description:
 */
public class CommentActivity extends AppCompatActivity {

    @BindView(R.id.top_cancel)
    public TextView mCancel;
    @BindView(R.id.post_type)
    public TextView mTypeText;
    @BindView(R.id.user_name)
    public TextView mUserName;
    @BindView(R.id.send)
    public TextView mSend;

    @BindView(R.id.edit_text)
    public EditText mEditText;

    @BindView(R.id.bottom_picture)
    public ImageView mBottomPic;
    @BindView(R.id.bottom_at)
    public ImageView mBottomAt;
    @BindView(R.id.bottom_topic)
    public ImageView mBottomTopic;
    @BindView(R.id.bottom_emoji)
    public ImageView mBottomEmoji;
    @BindView(R.id.bottom_more)
    public ImageView mBottomMore;

    private Status mStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        mStatus = (Status) getIntent().getSerializableExtra("status");

        mTypeText.setText("发评论");
        refreshUserName();
        setupListener();
    }

    private void refreshUserName() {
        UsersAPI api = new UsersAPI(this, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(this));
        api.show(Long.parseLong(AccessTokenKeeper.getAccessToken(this).getUid()), new RequestListener() {
            @Override
            public void onComplete(String s) {
                User user = User.parse(s);
                mUserName.setText(user.name);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mSend.setClickable(true);
                    mSend.setBackgroundResource(R.drawable.post_send_clickable_bg);
                } else {
                    mSend.setClickable(false);
                    mSend.setBackgroundResource(R.drawable.post_send_normal_bg);
                }
            }
        });
        mBottomPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CommentActivity.this, "不能发送图片评论", Toast.LENGTH_SHORT).show();
            }
        });
        mBottomAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.getText().insert(mEditText.getSelectionStart(), "@");
            }
        });
        mBottomTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.getText().insert(mEditText.getSelectionStart(), "##");
                mEditText.setSelection(mEditText.getSelectionStart() - 1);
            }
        });
        mBottomEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBottomMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mEditText.getText().toString();
                if (comment.length() > 140) {
                    Toast.makeText(CommentActivity.this, "字数超过140", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendComment(comment);
            }
        });
    }

    private void sendComment(String comment) {
        CommentsAPI api = new CommentsAPI(this, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(this));
        api.create(comment, Long.parseLong(mStatus.id), false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(CommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}


















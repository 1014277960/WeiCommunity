package com.wulinpeng.weicommunity.repost.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.repost.adapter.UpLoadImgAdapter;
import com.wulinpeng.weicommunity.repost.entity.ImageInfo;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;
import com.wulinpeng.weicommunity.util.StatusFillUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/20 下午7:47
 * @description:
 */
public class PostActivity extends AppCompatActivity {

    public static final String TYPE_POST = "发布微博";
    public static final String TYPE_REPOST = "转发微博";

    @BindView(R.id.top_cancel)
    public TextView mCancel;
    @BindView(R.id.post_type)
    public TextView mTypeText;
    @BindView(R.id.user_name)
    public TextView mUserName;
    @BindView(R.id.send)
    public TextView mSend;
    @BindView(R.id.content)
    public EditText mEditText;
    @BindView(R.id.repost_layout)
    public LinearLayout mRepostLayout;
    @BindView(R.id.repost_img)
    public ImageView mRepostImage;
    @BindView(R.id.repost_name)
    public TextView mRepostName;
    @BindView(R.id.repost_content)
    public TextView mRepostContent;
    @BindView(R.id.imgs)
    public RecyclerView mRecyclerView;
    @BindView(R.id.with_comment)
    public CheckBox mWithComment;
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

    private String mType;

    private Status mStatus;

    private List<ImageInfo> mImageList = new ArrayList<>();

    private UpLoadImgAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        mType = getIntent().getStringExtra("type");
        mTypeText.setText(mType);
        if (mType.equals(TYPE_REPOST)) {
            mStatus = (Status) getIntent().getSerializableExtra("status");
        }

        refreshUserName();
        initContent();
        initRecyclerView();
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
                Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initContent() {
        if (mType.equals(TYPE_REPOST)) {
            initRepostContent();
        }

        changSendState();
    }

    private void changSendState() {
        if (mEditText.getText() != null || mStatus != null || mImageList.size() > 0) {
            // 可发送
            mSend.setBackgroundResource(R.drawable.post_send_clickable_bg);
            mSend.setClickable(true);
            return;
        }
        mSend.setBackgroundResource(R.drawable.post_send_normal_bg);
        mSend.setClickable(false);
    }

    private void initRepostContent() {
        if (mStatus == null) {
            return;
        }

        Status status;
        if (mStatus.retweeted_status != null) {
            status = mStatus.retweeted_status;
        } else {
            status = mStatus;
        }

        if (mStatus.retweeted_status != null) {
            // 转发的微博也是转发的, 填充editText
            StatusFillUtil.fillStatusContent(this, mEditText, "//@" + mStatus.user.name + ":" + mStatus.text);
            mEditText.setSelection(0);
        }

        // origin
        mRepostLayout.setVisibility(View.VISIBLE);
        mRepostName.setText("@" + status.user.name);
        StatusFillUtil.fillStatusContent(PostActivity.this, mRepostContent, status.text);
        String url = status.bmiddle_pic.isEmpty() ? status.user.avatar_large : status.bmiddle_pic;
        Glide.with(this).load(url).into(mRepostImage);
    }

    private void initRecyclerView() {
        mAdapter = new UpLoadImgAdapter(this, mImageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void setupListener() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changSendState();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        mBottomPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.equals(TYPE_REPOST)) {
                    Toast.makeText(PostActivity.this, "转发微博不能附带图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(PostActivity.this, ImgSelectActivity.class);
                intent.putParcelableArrayListExtra("selectedImages", (ArrayList<? extends Parcelable>) mImageList);
                startActivityForResult(intent, 0);
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
    }

    private void post() {
        String content = mEditText.getText().toString();
        if (content.length() > 140) {
            Toast.makeText(PostActivity.this, "字数过长", Toast.LENGTH_SHORT).show();
            return;
        }
        StatusesAPI api = new StatusesAPI(this, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(this));
        if (mType == TYPE_POST) {

        } else if (mType.equals(TYPE_REPOST)) {
            api.repost(Long.parseLong(mStatus.retweeted_status == null ? mStatus.id : mStatus.retweeted_status.id),
                    mEditText.getText().toString(), mWithComment.isChecked() ? 1 : 0, new RequestListener() {
                @Override
                public void onComplete(String s) {
                    Toast.makeText(PostActivity.this, "转发成功", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    ArrayList<ImageInfo> imageInfos = data.getParcelableArrayListExtra("selected_images");
                    mImageList.clear();
                    mImageList.addAll(imageInfos);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}






















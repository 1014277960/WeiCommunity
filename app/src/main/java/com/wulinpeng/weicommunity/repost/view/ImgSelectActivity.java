package com.wulinpeng.weicommunity.repost.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.repost.adapter.ImgSelectAdapter;
import com.wulinpeng.weicommunity.repost.entity.ImageFolder;
import com.wulinpeng.weicommunity.repost.entity.ImageInfo;
import com.wulinpeng.weicommunity.repost.event.FolderChooseEvent;
import com.wulinpeng.weicommunity.repost.event.ImageSelectChangeEvent;
import com.wulinpeng.weicommunity.repost.view.custom.FolderWindow;
import com.wulinpeng.weicommunity.util.ImageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午3:10
 * @description:
 */
public class ImgSelectActivity extends AppCompatActivity {

    @BindView(R.id.top_bar)
    public RelativeLayout mTopbarLayout;

    @BindView(R.id.cancel)
    public TextView mCancel;

    @BindView(R.id.folder)
    public LinearLayout mFolderLayout;

    @BindView(R.id.folder_name)
    public TextView mFolderName;

    @BindView(R.id.next)
    public TextView mNext;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    /**
     * 本机所有含有图片的folder
     */
    private List<ImageFolder> mFolders = new ArrayList<>();

    /**
     * 当前选择的folder下的所有图片
     */
    private List<ImageInfo> mCurrentImgs = new ArrayList<>();

    private ImgSelectAdapter mSelectAdapter;

    private FolderWindow mFolderWindow;

    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initRecyclerView();
        setUpListener();

        // 开始扫描本机图片,使用loader加载contentProvider数据,异步且自动跟新数据
        ImageUtil.scanImages(this, ImgSelectActivity.this.getLoaderManager(), new ImageUtil.OnScanListener() {
            @Override
            public void onFinishOrUpdate(List<ImageFolder> folders) {
                updateImages(folders);
                onSelectChange(new ImageSelectChangeEvent());
            }
        });

    }

    private void initRecyclerView() {
        mSelectAdapter = new ImgSelectAdapter(this, mCurrentImgs);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mSelectAdapter);
    }

    private void setUpListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFolderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderWindow == null) {
                    mFolderWindow = new FolderWindow(ImgSelectActivity.this);
                    mFolderWindow.setFolders(mFolders);
                }
                mFolderWindow.showAsDropDown(mTopbarLayout);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ImageInfo> selectedImages = getSelectedImages();
                if (selectedImages.size() > 9) {
                    Toast.makeText(ImgSelectActivity.this, "最多9张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("selected_images", (ArrayList<ImageInfo>)selectedImages);
                setResult(0, intent);
                finish();
            }
        });
    }

    /**
     * 返回所有选择图片的集合
     * @return
     */
    private List<ImageInfo> getSelectedImages() {
        List<ImageInfo> selectedImages = new ArrayList<>();
        if (mFolders.size() > 0 && mFolders.get(0).getImages() != null) {
            for (ImageInfo imageInfo : mFolders.get(0).getImages()) {
                if (imageInfo.isSelected()) {
                    selectedImages.add(imageInfo);
                }
            }
        }
        return selectedImages;
    }

    /**
     * 选择某一文件夹后,更新具体文件夹数据
     * @param event
     */
    @Subscribe
    public void onFolderChoose(FolderChooseEvent event) {
        mFolderName.setText(event.getFolder().getName());
        mCurrentImgs.clear();
        mCurrentImgs.addAll(event.getFolder().getImages());
        mSelectAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 更新下一步按钮的状态和文本
     * 因为有一个allFolder是其他folder总和,里面的info和其他的是一个引用,所以得到的计数肯定是*2的,所以除以2
     * @param event
     */
    @Subscribe
    public void onSelectChange(ImageSelectChangeEvent event) {
        int num = 0;
        for (ImageFolder folder : mFolders) {
            for (ImageInfo imageInfo : folder.getImages()) {
                num += imageInfo.isSelected() ? 1 : 0;
            }
        }
        if (num == 0) {
            mNext.setText("下一步");
            mNext.setBackgroundResource(R.drawable.post_send_normal_bg);
            mNext.setClickable(false);
        } else {
            mNext.setText("下一步(" + (num / 2) + ")");
            mNext.setBackgroundResource(R.drawable.post_send_clickable_bg);
            mNext.setClickable(true);
        }
    }

    /**
     * 更新选择的数据
     * @param folders
     */
    private void updateImages(List<ImageFolder> folders) {
        if (folders == null || folders.size() == 0) {
            return;
        }
        List<ImageInfo> selectedImages = new ArrayList<>();
        if (isFirstLoad) {
            // 第一次加载,把上一个activity传过来的选中数据设置上去
            selectedImages = getIntent().getParcelableArrayListExtra("selectedImages");
            isFirstLoad = false;
        } else if (mFolders.size() != 0) {
            selectedImages = mFolders.get(0).getImages();
        }

        for (ImageInfo info : selectedImages) {
            if (info.isSelected()) {
                String path = info.getPath();
                for (ImageInfo i : folders.get(0).getImages()) {
                    if (path.equals(i.getPath())) {
                        i.setSelected(true);
                    }
                }
            }
        }

        mFolders.clear();
        mFolders.addAll(folders);

        if (mFolders.size() != 0) {
            mCurrentImgs.clear();
            mCurrentImgs.addAll(mFolders.get(0).getImages());
            mSelectAdapter.notifyDataSetChanged();
        }
    }
}

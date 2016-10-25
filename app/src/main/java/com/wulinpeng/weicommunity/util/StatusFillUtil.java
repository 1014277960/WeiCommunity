package com.wulinpeng.weicommunity.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.adapter.ImageListAdapter;
import com.wulinpeng.weicommunity.login.home.view.custom.CircleTransformation;
import com.wulinpeng.weicommunity.post.view.PostActivity;
import com.wulinpeng.weicommunity.weibodetail.view.WeiboDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wulinpeng
 * @datetime: 16/10/15 下午2:55
 * @description:
 */
public class StatusFillUtil {

    public static void fillTopBar(Context context, Status status, ImageView profileImg, ImageView profileVefity, TextView profileName, TextView statusTime, TextView statusFrom) {
        User user = status.user;

        Glide.with(context).load(user.profile_image_url).transform(new CircleTransformation(context)).diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImg);
        if (user.verified == true && user.verified_type == 0) {
            profileVefity.setImageResource(R.drawable.avatar_vip);
        } else if (user.verified == true && (user.verified_type == 1 || user.verified_type == 2 || user.verified_type == 3)) {
            profileVefity.setImageResource(R.drawable.avatar_enterprise_vip);
        } else if (user.verified == false && user.verified_type == 220) {
            profileVefity.setImageResource(R.drawable.avatar_grassroot);
        } else {
            profileVefity.setVisibility(View.INVISIBLE);
        }

        profileName.setText(user.name);
        statusTime.setText(buildTime(status.created_at));
        statusFrom.setText(getFromText(status.source));


    }

    /**
     * 获得真实的来源
     * @param source
     * @return
     */
    private static String getFromText(String source) {
        Pattern pattern = Pattern.compile("<a.+>(.+)</a>");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            return "来自 " + matcher.group(1);
        }
        return "";
    }

    /**
     * 通过时间戳的差值获得时间差
     * @param time
     * @return
     */
    public static String buildTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            // 格式化返回的String数据为date
            long createTime = format.parse(time).getTime();
            long currentTime = new Date().getTime();
            long dif = (currentTime - createTime) / 1000;
            if (dif < 60) {
                return "刚刚";
            }
            dif /= 60;
            if (dif < 60) {
                return dif + "分钟前";
            }
            dif /= 60;
            if (dif < 24) {
                return dif + "小时前";
            }
            // 一天前的返回具体时间
            Date date = format.parse(time);
            int year = (date.getYear() + 1900) % 100;
            int month = date.getMonth() + 1;
            int day = date.getDate();
            int hour = date.getHours();
            int minute = date.getMinutes();
            return year + "-" + month + "-" + day + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void fillRetweetContent(Context context, Status status, TextView textView) {
        StringBuffer sb = new StringBuffer();
        sb.append("@" + status.retweeted_status.user.name + ": ");
        sb.append(status.retweeted_status.text);
        fillStatusContent(context, textView, sb.toString());
    }

    public static void fillStatusContent(final Context context, TextView textView, String content) {
        // 名称是中文 英文 数字 下划线 减号
        String AT = "@[\\u4e00-\\u9fa5a-zA-Z0-9_-]{2,30}";
        // 匹配话题,里面不能有#
        String TOPIC = "#[^#]+#";
        // url
        String URL = "http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]";
        String EMOJI = "\\[.+?\\]";

        String ALL = "(" + AT + ")" + "|" + "(" + TOPIC + ")" + "|" + "(" + URL + ")" + "|" + "(" + EMOJI + ")";

        SpannableString spannableString = new SpannableString(content);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

        Pattern pattern = Pattern.compile(ALL);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            final String at = matcher.group(1);
            final String topic = matcher.group(2);
            final String url = matcher.group(3);
            String emoji = matcher.group(4);
            if (at != null) {
                int start = matcher.start(1);
                int end = start + at.length();
                StatusClickableSpan statusClickableSpan = new StatusClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // 打开用户界面
                    }
                };
                spannableString.setSpan(statusClickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (topic != null) {
                int start = matcher.start(2);
                int end = start + topic.length();
                StatusClickableSpan statusClickableSpan = new StatusClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // 打开话题
                    }
                };
                spannableString.setSpan(statusClickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (url != null) {
                int start = matcher.start(3);
                int end = start + url.length();
                StatusClickableSpan statusClickableSpan = new StatusClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // 打开url
                    }
                };
                spannableString.setSpan(statusClickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (emoji != null) {
                int start = matcher.start(4);
                int end = start + emoji.length();
                //setEmotion(context, emoji, spannableString, start, end);


                String emojiName = Emoji.getImgName(emoji);
                if (emojiName != null) {
                    // 通过图片的名字获得对应的redId,从而获得drawable
                    int resId = context.getResources().getIdentifier(emojiName, "drawable", context.getPackageName());
                    Drawable drawable = null;
                    try {
                        drawable = context.getResources().getDrawable(resId);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                    if (drawable != null) {
                        drawable.setBounds(0, 0, 50, 50);
                        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                        spannableString.setSpan(imageSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }


            }
        }
        textView.setText(spannableString);
    }

    static abstract class StatusClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.BLUE);
            int rgb = Color.rgb(80, 125, 175);
            ds.setColor(rgb);
        }

        @Override
        abstract public void onClick(View widget);
    }

    public static void fillBottomBar(final Context context, final Status status, TextView retweet, TextView comment, TextView like) {
        if (status.reposts_count != 0) {
            retweet.setText(status.reposts_count + "");
        } else {
            retweet.setText("转发");
        }

        if (status.comments_count != 0) {
            comment.setText(status.comments_count + "");
        } else {
            comment.setText("评论");
        }

        if (status.attitudes_count != 0) {
            like.setText(status.attitudes_count + "");
        } else {
            like.setText("赞");
        }
    }

    public static void fillImageList(Context context, Status status, RecyclerView recyclerView) {
        List<String> data = status.pic_urls;
        if (data == null || data.size() == 0) {
            // 整个status的recyclerview会出现复用的请客,其他的组件由于只要出现就一定会被改变,所以无需关心,
            // 但是图片的recyclerview一直都有,但是有些没有图片,就不会改它,那么就会出现复用之前的图片,所以如果没有图片,设置null
            recyclerView.setAdapter(null);
            return;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(context, getSpanCount(data.size()));
        ImageListAdapter adapter = new ImageListAdapter(context, data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private static int getSpanCount(int size) {
        if (size < 3) {
            return size;
        }
        return 3;
    }

    public static void setBottomClickListener(final Context context, final Status status, LinearLayout retweet, LinearLayout comment, LinearLayout like) {
        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("type", PostActivity.TYPE_REPOST);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WeiboDetailActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });
    }

    public static void fillCommentOrRepost(Context context, User user, String time, String content, ImageView imageView, TextView name, TextView timeText, TextView contentText) {
        Glide.with(context).load(user.profile_image_url).transform(new CircleTransformation(context)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        name.setText(user.name);
        timeText.setText(buildTime(time));
        fillStatusContent(context, contentText, content);
    }
}

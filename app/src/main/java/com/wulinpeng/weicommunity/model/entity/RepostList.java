package com.wulinpeng.weicommunity.model.entity;

import android.util.Log;

import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/25 上午12:41
 * @description:
 */
public class RepostList {

    private List<Repost> repostList;

    public List<Repost> getRepostList() {
        return repostList;
    }

    public void setRepostList(List<Repost> repostList) {
        this.repostList = repostList;
    }

    public static RepostList parse(String s) {
        RepostList list = new RepostList();
        List<Repost> reposts = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("reposts");
            Log.d("Debug", "json array length = " + jsonArray.length());
            for (int i = 0; i != jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Repost repost = new Repost();
                repost.setUser(User.parse(object.getString("user")));
                repost.setCreate_at(object.getString("created_at"));
                repost.setText(object.getString("text"));
                repost.setId(object.getString("id"));
                reposts.add(repost);
            }
            list.setRepostList(reposts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

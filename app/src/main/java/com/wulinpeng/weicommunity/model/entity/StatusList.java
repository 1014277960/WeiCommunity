package com.wulinpeng.weicommunity.model.entity;

import android.text.TextUtils;

import com.sina.weibo.sdk.openapi.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author wulinpeng
 * @datetime: 16/10/15 下午12:21
 * @description: 在sina StatusList基础上加入since_id, max_id
 */
public class StatusList implements Serializable {

    public ArrayList<Status> statusList;
    public Status statuses;
    public boolean hasvisible;
    public String previous_cursor;
    public String next_cursor;
    public int total_number;
    public Object[] advertises;
    public long since_id;
    public long max_id;

    public static StatusList parse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        StatusList statuses = new StatusList();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            statuses.hasvisible      = jsonObject.optBoolean("hasvisible", false);
            statuses.previous_cursor = jsonObject.optString("previous_cursor", "0");
            statuses.next_cursor     = jsonObject.optString("next_cursor", "0");
            statuses.total_number    = jsonObject.optInt("total_number", 0);
            statuses.since_id        = jsonObject.optLong("since_id", 0);
            statuses.max_id          = jsonObject.optLong("max_id", 0);

            JSONArray jsonArray      = jsonObject.optJSONArray("statuses");
            if (jsonArray != null && jsonArray.length() > 0) {
                int length = jsonArray.length();
                statuses.statusList = new ArrayList<Status>(length);
                for (int ix = 0; ix < length; ix++) {
                    statuses.statusList.add(Status.parse(jsonArray.getJSONObject(ix)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return statuses;
    }
}
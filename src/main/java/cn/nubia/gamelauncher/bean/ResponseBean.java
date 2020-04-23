package cn.nubia.gamelauncher.bean;

import cn.nubia.gamelauncher.util.LogUtil;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseBean {
    private ArrayList<GameItemBean> mGameItemBean = new ArrayList<>();
    private int mStateCode;
    private String mStateMsg;

    public ResponseBean(JSONObject jsonObject, int type) {
        try {
            this.mStateCode = jsonObject.getInt("StateCode");
            if (type == 200) {
                JSONArray jSONArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jSONArray.length(); i++) {
                    this.mGameItemBean.add(new GameItemBean((JSONObject) jSONArray.get(i)));
                }
                return;
            }
            this.mGameItemBean.add(new GameItemBean(jsonObject.getJSONObject("Data")));
        } catch (Exception e) {
            LogUtil.e("ResponseBean", "ResponseBean init Error!!");
            throw new RuntimeException(e);
        }
    }

    public int getStateCode() {
        return this.mStateCode;
    }

    public void setStateCode(int stateCode) {
        this.mStateCode = stateCode;
    }

    public String getStateMsg() {
        return this.mStateMsg;
    }

    public void setStateMsg(String stateMsg) {
        this.mStateMsg = stateMsg;
    }

    public ArrayList<GameItemBean> getGameItemBean() {
        return this.mGameItemBean;
    }

    public void setGameItemBean(ArrayList<GameItemBean> gameItemBean) {
        this.mGameItemBean = gameItemBean;
    }

    public String toString() {
        return "ResponseBean{StateCode=" + this.mStateCode + ", StateMsg='" + this.mStateMsg + '\'' + ", GameItemBean=" + this.mGameItemBean + '}';
    }
}

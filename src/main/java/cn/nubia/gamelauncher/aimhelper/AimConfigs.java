package cn.nubia.gamelauncher.aimhelper;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AimConfigs {
    private static final String PREFS_NAME = "aim_config";
    private static AimConfigs sInstance;
    private Map<String, AimConfig> configMap = new HashMap();
    /* access modifiers changed from: private */
    public SharedPreferences prefs = null;

    private class AimConfig {
        private static final int DEFAULT_COLOR = -1;
        private static final boolean DEFAULT_IS_AUTO = true;
        private static final boolean DEFAULT_IS_ON = false;
        private static final int DEFAULT_SIZE = 70;
        private static final int DEFAULT_STYLE = 1;
        private static final String KEY_COLOR = "color";
        private static final String KEY_IS_AUTO = "isAuto";
        private static final String KEY_IS_ON = "isOn";
        private static final String KEY_SIZE = "size";
        private static final String KEY_STYLE = "style";
        private JSONObject jsonObject;
        private final String packageName;

        /* access modifiers changed from: 0000 */
        public String toJson() {
            return this.jsonObject != null ? this.jsonObject.toString() : "";
        }

        AimConfig(String packageName2, String json) {
            this.packageName = packageName2;
            try {
                this.jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                this.jsonObject = new JSONObject();
            }
        }

        public boolean isOn() {
            if (this.jsonObject != null) {
                return this.jsonObject.optBoolean(KEY_IS_ON, false);
            }
            return false;
        }

        public void setOn(boolean isOn) {
            try {
                if (isOn() != isOn) {
                    this.jsonObject.put(KEY_IS_ON, isOn);
                    save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public boolean isAuto() {
            if (this.jsonObject != null) {
                return this.jsonObject.optBoolean(KEY_IS_AUTO, DEFAULT_IS_AUTO);
            }
            return DEFAULT_IS_AUTO;
        }

        public void setAuto(boolean isAuto) {
            try {
                if (isAuto() != isAuto) {
                    this.jsonObject.put(KEY_IS_AUTO, isAuto);
                    save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getStyle() {
            if (this.jsonObject != null) {
                return this.jsonObject.optInt(KEY_STYLE, 1);
            }
            return 1;
        }

        public void setStyle(int style) {
            try {
                if (!this.jsonObject.has(KEY_STYLE) || getStyle() != style) {
                    this.jsonObject.put(KEY_STYLE, style);
                    save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getColor() {
            if (this.jsonObject != null) {
                return this.jsonObject.optInt(KEY_COLOR, -1);
            }
            return -1;
        }

        public void setColor(int color) {
            try {
                if (!this.jsonObject.has(KEY_COLOR) || getColor() != color) {
                    this.jsonObject.put(KEY_COLOR, color);
                    save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getSize() {
            if (this.jsonObject != null) {
                return this.jsonObject.optInt(KEY_SIZE, 70);
            }
            return 70;
        }

        public void setSize(int size) {
            try {
                if (!this.jsonObject.has(KEY_SIZE) || getSize() != size) {
                    this.jsonObject.put(KEY_SIZE, size);
                    save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void save() {
            AimConfigs.this.prefs.edit().putString(this.packageName, toJson()).apply();
        }
    }

    public static AimConfigs getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AimConfigs.class) {
                if (sInstance == null) {
                    sInstance = new AimConfigs(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private AimConfigs(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, 0);
        load();
    }

    private void load() {
        this.configMap.clear();
        Map all = this.prefs.getAll();
        for (String packageName : this.prefs.getAll().keySet()) {
            this.configMap.put(packageName, new AimConfig(packageName, this.prefs.getString(packageName, "")));
        }
    }

    public boolean isOn(String packageName) {
        return getConfig(packageName).isOn();
    }

    public void setOn(String packageName, boolean on) {
        getConfig(packageName).setOn(on);
        NubiaGameTrackManager.updateValue(packageName);
    }

    public boolean isAuto(String packageName) {
        return getConfig(packageName).isAuto();
    }

    public void setAuto(String packageName, boolean auto) {
        getConfig(packageName).setAuto(auto);
    }

    public int getStyle(String packageName) {
        return getConfig(packageName).getStyle();
    }

    public void setStyle(String packageName, int styleCode) {
        getConfig(packageName).setStyle(styleCode);
        NubiaGameTrackManager.updateValue(packageName);
    }

    public int getSize(String packageName) {
        return getConfig(packageName).getSize();
    }

    public void setSize(String packageName, int size) {
        getConfig(packageName).setSize(size);
        NubiaGameTrackManager.updateValue(packageName);
    }

    public int getColor(String packageName) {
        return getConfig(packageName).getColor();
    }

    public void setColor(String packageName, int color) {
        getConfig(packageName).setColor(color);
        NubiaGameTrackManager.updateValue(packageName);
    }

    private AimConfig getConfig(String packageName) {
        AimConfig config = (AimConfig) this.configMap.get(packageName);
        if (config == null) {
            return new AimConfig(packageName, this.prefs.getString(packageName, ""));
        }
        return config;
    }
}

package cn.nubia.commonui.actionbar.app;

import cn.nubia.commonui.actionbar.view.ActionMode;

public interface AppCompatCallback {
    void onSupportActionModeFinished(ActionMode actionMode);

    void onSupportActionModeStarted(ActionMode actionMode);
}

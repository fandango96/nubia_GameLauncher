package cn.nubia.commonui.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import cn.nubia.commonui.R;

public class NubiaLoadingDialog extends ProgressDialog {
    private Context mContext;
    private CharSequence mLoadingMessage;
    private TextView mMessageTextView;

    public NubiaLoadingDialog(Context context) {
        this(context, 0);
    }

    public NubiaLoadingDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this.mContext);
    }

    private void init(Context context) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.nubia_loading_dialog);
        this.mMessageTextView = (TextView) findViewById(R.id.loading_message);
        if (this.mLoadingMessage == null) {
            this.mLoadingMessage = this.mContext.getString(R.string.nubia_loading_dialog_please_waiting);
        }
        this.mMessageTextView.setText(this.mLoadingMessage);
        LayoutParams params = getWindow().getAttributes();
        params.width = -2;
        params.height = -2;
        getWindow().setAttributes(params);
    }

    public CharSequence getLoadingMessage() {
        return this.mLoadingMessage;
    }

    public void setLoadingMessage(CharSequence message) {
        this.mLoadingMessage = message;
        if (this.mMessageTextView != null) {
            this.mMessageTextView.setText(this.mLoadingMessage);
        }
    }

    public void setLoadingMessage(int messageId) {
        this.mLoadingMessage = this.mContext.getString(messageId);
        if (this.mMessageTextView != null) {
            this.mMessageTextView.setText(this.mLoadingMessage);
        }
    }
}

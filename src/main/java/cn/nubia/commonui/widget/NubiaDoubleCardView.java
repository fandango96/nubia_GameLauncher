package cn.nubia.commonui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import cn.nubia.commonui.R;

public class NubiaDoubleCardView extends LinearLayout {
    private static final String TAG = "NubiaWidget";
    private final int PhoneConstants_SUB1 = 0;
    private final int PhoneConstants_SUB2 = 1;
    private ImageView mCard1View;
    private ImageView mCard2View;
    /* access modifiers changed from: private */
    public int mCheckedItem;
    private View mDoubleCardView;
    protected OnCardClickListener mOnCardClickListener = null;
    private final OnClickListener mOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.nubia_card1) {
                NubiaDoubleCardView.this.setCardInUse(0);
            }
            if (id == R.id.nubia_card2) {
                NubiaDoubleCardView.this.setCardInUse(1);
            }
            if (NubiaDoubleCardView.this.mOnCardClickListener != null) {
                NubiaDoubleCardView.this.mOnCardClickListener.onCardClick(NubiaDoubleCardView.this.mCheckedItem);
            }
        }
    };

    public interface OnCardClickListener {
        void onCardClick(int i);
    }

    public NubiaDoubleCardView(Context context) {
        super(context);
        init();
    }

    public NubiaDoubleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NubiaDoubleCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        initView();
        initCheckdItem();
    }

    private void initView() {
        this.mDoubleCardView = LayoutInflater.from(getContext()).inflate(R.layout.nubia_double_card_view, null);
        this.mCard1View = (ImageView) this.mDoubleCardView.findViewById(R.id.nubia_card1);
        this.mCard2View = (ImageView) this.mDoubleCardView.findViewById(R.id.nubia_card2);
        addView(this.mDoubleCardView, 0, new LayoutParams(-2, -1, 16.0f));
        this.mCard1View.setOnClickListener(this.mOnClickListener);
        this.mCard2View.setOnClickListener(this.mOnClickListener);
    }

    private void initCheckdItem() {
        this.mCheckedItem = -1;
        setCardInUseDefault();
    }

    public final void setCardInUse(int useItem) {
        if (useItem == 0 || useItem == 1 || useItem == -1) {
            if (this.mCheckedItem != useItem) {
                this.mCheckedItem = useItem;
            }
            if (useItem == -1) {
                updateCardIndicationDefault();
            } else {
                updateCardIndication();
            }
        } else {
            throw new IllegalArgumentException("Illegal useItem:" + useItem);
        }
    }

    public final void setCardInUseDefault() {
        updateCardIndicationDefault();
    }

    public final int getCardInUse() {
        return this.mCheckedItem;
    }

    /* access modifiers changed from: protected */
    public void updateCardIndication() {
        if (this.mCheckedItem == 0) {
            this.mCard1View.setImageResource(R.drawable.nubia_ic_double_card_card1_primary);
            this.mCard2View.setImageResource(R.drawable.nubia_ic_double_card_card2_secondary);
        } else if (this.mCheckedItem == 1) {
            this.mCard1View.setImageResource(R.drawable.nubia_ic_double_card_card1_secondary);
            this.mCard2View.setImageResource(R.drawable.nubia_ic_double_card_card2_primary);
        }
    }

    private void updateCardIndicationDefault() {
        this.mCard1View.setImageResource(R.drawable.nubia_ic_double_card_card1_secondary);
        this.mCard2View.setImageResource(R.drawable.nubia_ic_double_card_card2_secondary);
    }

    public final void setOnCardClickeListener(OnCardClickListener l) {
        this.mOnCardClickListener = l;
    }
}

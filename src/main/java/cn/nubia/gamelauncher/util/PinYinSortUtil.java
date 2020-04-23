package cn.nubia.gamelauncher.util;

import android.icu.text.Collator;
import cn.nubia.gamelauncher.bean.AppListItemBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class PinYinSortUtil {

    public static class CollatorComparator implements Comparator {
        Collator collator = Collator.getInstance(Locale.CHINA);

        public int compare(Object element1, Object element2) {
            return this.collator.getCollationKey(((AppListItemBean) element1).getName()).compareTo(this.collator.getCollationKey(((AppListItemBean) element2).getName()));
        }
    }

    public static ArrayList<AppListItemBean> sortByPinYinFirstChar(ArrayList<AppListItemBean> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new CollatorComparator());
        }
        return list;
    }
}

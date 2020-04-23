package cn.nubia.commonui.widget;

import android.content.Context;
import android.support.v4.internal.view.SupportMenu;
import cn.nubia.commonui.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LunarUtil {
    public static final int ADD_20_IF_LEAPMONTH = 20;
    public static String DAY = null;
    public static int DEFAULT_END_YEAR = 2037;
    public static int DEFAULT_START_YEAR = 1970;
    public static String EIGHT = null;
    public static String FIVE = null;
    public static String FOUR = null;
    public static final int IS_DAY = 2;
    public static final int IS_MONTH = 1;
    public static final int IS_YEAR = 0;
    public static String LEAP = null;
    public static String LUNAR_ELEVENTH_MONTH = null;
    public static String LUNAR_FIRST_DAY = null;
    public static String LUNAR_FIRST_MONTH = null;
    private static final int[] LUNAR_INFO = {19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42448, 83315, 21200, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46496, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 21952, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19415, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448};
    public static String LUNAR_TWELVETH_MONTH = null;
    public static String LUNAR_TWENTHIETH_DAY = null;
    private static final int MAX_YEAR = 2050;
    private static final int MIN_YEAR = 1900;
    public static String MONTH = null;
    public static final String[] MONTHS_LONG = new String[12];
    public static final String[] MONTHS_SHORT = new String[12];
    public static String NINE = null;
    public static String ONE = null;
    public static String SEVEN = null;
    public static String SIX = null;
    private static final String START_DATE = "19000130";
    public static String TEN;
    public static String THREE;
    public static String TWO;
    public static final String[] WEEKDAYS_LONG = new String[7];
    public static final String[] WEEKDAYS_SHORT = new String[7];
    public static String YEAR;
    public static String ZERO;
    public static String am;
    public static ArrayList<String> arrayList = new ArrayList<>();
    private static boolean isLeapYear;
    static int[] months = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static String pm;
    static int sum = 0;

    public static int getLeapMonth(int year) {
        return LUNAR_INFO[year - 1900] & 15;
    }

    private static int getLeapMonthDays(int year) {
        if (getLeapMonth(year) == 0) {
            return 0;
        }
        if ((LUNAR_INFO[year - 1900] & 983040) == 0) {
            return 29;
        }
        return 30;
    }

    private static int getMonthDays(int lunarYeay, int month) throws Exception {
        if (month > 31 || month < 0) {
            throw new Exception("month error");
        }
        if ((LUNAR_INFO[lunarYeay - 1900] & SupportMenu.USER_MASK & (1 << (16 - month))) == 0) {
            return 29;
        }
        return 30;
    }

    private static int getYearDays(int year) {
        int sum2 = 348;
        for (int i = 32768; i >= 8; i >>= 1) {
            if ((LUNAR_INFO[year - 1900] & 65520 & i) != 0) {
                sum2++;
            }
        }
        return getLeapMonthDays(year) + sum2;
    }

    private static int daysBetween(Date startDate, Date endDate) {
        return Integer.parseInt(String.valueOf((endDate.getTime() - startDate.getTime()) / 86400000));
    }

    private static void checkLunarDate(int lunarYear, int lunarMonth, int lunarDay, boolean leapMonthFlag) throws Exception {
        if (lunarYear < MIN_YEAR || lunarYear > MAX_YEAR) {
            throw new Exception("lunar year error");
        } else if (lunarMonth < 1 || lunarMonth > 12) {
            throw new Exception("lunar month error");
        } else if (lunarDay < 1 || lunarDay > 30) {
            throw new Exception("lunar day error");
        } else {
            int leap = getLeapMonth(lunarYear);
            if (leapMonthFlag && lunarMonth != leap) {
                throw new Exception("leap month error");
            }
        }
    }

    public static boolean isLeapMonth(int lunarYear, int lunarMonth) {
        return lunarMonth == getLeapMonth(lunarYear);
    }

    public static String lunarToSolar(String lunarDate, boolean leapMonthFlag) throws Exception {
        int lunarYear = Integer.parseInt(lunarDate.substring(0, 4));
        int lunarMonth = Integer.parseInt(lunarDate.substring(4, 6));
        int lunarDay = Integer.parseInt(lunarDate.substring(6, 8));
        checkLunarDate(lunarYear, lunarMonth, lunarDay, leapMonthFlag);
        int offset = 0;
        for (int i = MIN_YEAR; i < lunarYear; i++) {
            offset += getYearDays(i);
        }
        int leapMonth = getLeapMonth(lunarYear);
        if ((leapMonth != lunarMonth) && leapMonthFlag) {
            throw new Exception("the leapmonth flag error");
        }
        if (leapMonth == 0 || lunarMonth < leapMonth) {
            for (int i2 = 1; i2 < lunarMonth; i2++) {
                offset += getMonthDays(lunarYear, i2);
            }
            if (lunarDay > getMonthDays(lunarYear, lunarMonth)) {
                throw new Exception("lunar date error");
            }
            offset += lunarDay;
        } else {
            for (int i3 = 1; i3 < lunarMonth; i3++) {
                offset += getMonthDays(lunarYear, i3);
            }
            if (lunarMonth > leapMonth) {
                int offset2 = offset + getLeapMonthDays(lunarYear);
                if (lunarDay > getMonthDays(lunarYear, lunarMonth)) {
                    throw new Exception("lunar date error");
                }
                offset = offset2 + lunarDay;
            } else if (lunarMonth == leapMonth) {
                if (leapMonthFlag) {
                    offset += getMonthDays(lunarYear, lunarMonth);
                    if (lunarDay > getLeapMonthDays(lunarYear)) {
                        throw new Exception("lunar date error");
                    }
                } else if (lunarDay > getMonthDays(lunarYear, lunarMonth)) {
                    throw new Exception("lunar date error");
                }
                offset += lunarDay;
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date myDate = formatter.parse(START_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(myDate);
        c.add(5, offset);
        return formatter.format(c.getTime());
    }

    public static String solarToLunar(String solarDate) {
        return SolarToLunar.calendarSolarToLundar(Integer.parseInt(solarDate.substring(0, 4)), Integer.parseInt(solarDate.substring(4, 6)), Integer.parseInt(solarDate.substring(6, 8)));
    }

    private static boolean isLessTen(int day) {
        return day <= 10;
    }

    public static String[] solarYearTolunarYear() {
        String[] lunarYear = new String[LUNAR_INFO.length];
        for (int i = 0; i < LUNAR_INFO.length; i++) {
            lunarYear[i] = numConvert(i + 1970, 0);
        }
        return lunarYear;
    }

    public static String[] solarMonthTolunarMonth(int year) {
        String[] lunarMonth = new String[12];
        int leapMonth = getLeapMonth(year);
        if (leapMonth != 0) {
            lunarMonth = new String[13];
        }
        for (int i = 0; i < lunarMonth.length; i++) {
            if (leapMonth <= 0) {
                lunarMonth[i] = numConvert(i + 1, 1);
            } else if (leapMonth == i) {
                lunarMonth[i] = LEAP + numConvert(i, 1);
            } else if (i > leapMonth) {
                lunarMonth[i] = numConvert(i, 1);
            } else {
                lunarMonth[i] = numConvert(i + 1, 1);
            }
        }
        return lunarMonth;
    }

    public static String[] solarDayTolunarDay(int year, int month, boolean isLeapMonth) {
        String[] lunarDay = new String[30];
        if (!isLeapMonth) {
            try {
                if (getMonthDays(year, month) != 30) {
                    lunarDay = new String[29];
                }
            } catch (Exception e) {
            }
        } else if (getLeapMonthDays(year) != 30) {
            lunarDay = new String[29];
        }
        for (int i = 0; i < lunarDay.length; i++) {
            lunarDay[i] = numConvert(i + 1, 2);
        }
        return lunarDay;
    }

    public static String[] timePickerSolMonthDay(int solarYear) {
        Object valueOf;
        ArrayList<String> list = new ArrayList<>();
        boolean isSolLeapYear = isSolarLeapYear(solarYear);
        int month = 1;
        while (month <= 12) {
            int maxDay = month == 2 ? isSolLeapYear ? 29 : 28 : (month == 4 || month == 6 || month == 9 || month == 11) ? 30 : 31;
            String str = "";
            for (int day = 1; day <= maxDay; day++) {
                StringBuilder append = new StringBuilder().append(month < 10 ? "0" + month : Integer.valueOf(month)).append(MONTH);
                if (day < 10) {
                    valueOf = "0" + day;
                } else {
                    valueOf = Integer.valueOf(day);
                }
                list.add(append.append(valueOf).append(DAY).toString());
            }
            month++;
        }
        return (String[]) list.toArray(new String[0]);
    }

    public static String[] timePickerUSMonthDay(int USYear) {
        Object valueOf;
        ArrayList<String> list = new ArrayList<>();
        boolean isSolLeapYear = isSolarLeapYear(USYear);
        int month = 1;
        while (month <= 12) {
            int maxDay = month == 2 ? isSolLeapYear ? 29 : 28 : (month == 4 || month == 6 || month == 9 || month == 11) ? 30 : 31;
            String str = "";
            for (int day = 1; day <= maxDay; day++) {
                StringBuilder append = new StringBuilder().append(MONTHS_SHORT[month - 1]);
                if (day < 10) {
                    valueOf = "0" + day;
                } else {
                    valueOf = Integer.valueOf(day);
                }
                list.add(append.append(valueOf).toString());
            }
            month++;
        }
        return (String[]) list.toArray(new String[0]);
    }

    public static boolean isSolarLeapYear(int solarYear) {
        if ((solarYear % 4 != 0 || solarYear % 100 == 0) && solarYear % 400 != 0) {
            return false;
        }
        return true;
    }

    public static String formatDate(int year, int month, int day) {
        StringBuilder sb = new StringBuilder("");
        sb.append(year);
        if (month > 9) {
            StringBuilder append = sb.append(month);
        } else {
            StringBuilder append2 = sb.append("0" + month);
        }
        return (day > 9 ? sb.append(day) : sb.append("0" + day)).toString();
    }

    public static String numConvert(int num, int type) {
        String[] big = {ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, LUNAR_TWENTHIETH_DAY, LUNAR_FIRST_MONTH, LUNAR_ELEVENTH_MONTH, LUNAR_TWELVETH_MONTH};
        StringBuilder sb = new StringBuilder("");
        if (type == 0) {
            if (num >= MAX_YEAR || num <= MIN_YEAR) {
                return null;
            }
            int bai = (num % 1000) / 100;
            int shi = ((num % 1000) % 100) / 10;
            int ge = ((num % 1000) % 100) % 10;
            sb.append(big[num / 1000]);
            sb.append(big[bai]);
            sb.append(big[shi]);
            sb.append(big[ge]);
            return sb.toString();
        } else if (type != 1) {
            if (type == 2) {
                if (num <= 0 || num > 31) {
                    return null;
                }
                if (num <= 10) {
                    return sb.append(LUNAR_FIRST_DAY).append(big[num]).toString();
                }
                int shi2 = num / 10;
                int ge2 = num % 10;
                if (ge2 == 0) {
                    return sb.append(big[shi2]).append(big[10]).toString();
                }
                if (shi2 == 1) {
                    return sb.append(big[10]).append(big[ge2]).toString();
                }
                if (shi2 == 2) {
                    return sb.append(big[11]).append(big[ge2]).toString();
                }
            }
            return "**";
        } else if (num <= 0 || num > 13) {
            return null;
        } else {
            if (num == 1) {
                return sb.append(big[12]).toString();
            }
            if (num <= 10) {
                return sb.append(big[num]).toString();
            }
            if (num == 11) {
                return sb.append(big[13]).toString();
            }
            return sb.append(big[14]).toString();
        }
    }

    public static String computeWeekday(String date) {
        int c = Integer.parseInt(date.substring(0, 2));
        int y = Integer.parseInt(date.substring(2, 4));
        int m = Integer.parseInt(date.substring(4, 6));
        int d = Integer.parseInt(date.substring(6, 8));
        if (m < 3) {
            y--;
            if (y < 0) {
                c--;
                y = 99;
            }
            m = m == 1 ? 13 : 14;
        }
        int temp = (((((((y / 4) + y) + (c / 4)) - (c * 2)) + (((m + 1) * 26) / 10)) + d) - 1) % 7;
        if (temp < 0) {
            temp += 7;
        }
        return WEEKDAYS_LONG[temp];
    }

    public static int solarSumDays(int mm, int dd, int yyyy) {
        months[1] = isSolarLeapYear(yyyy) ? 29 : 28;
        if (mm == 1) {
            return sum + dd;
        }
        sum += solarSumDays(mm - 1, months[mm - 2], yyyy);
        return sum + dd;
    }

    public static void clearSum() {
        sum = 0;
    }

    public static String daysTommdd(String md_str, String[] md_arr, boolean isSolarLeapYear) {
        int day = 0;
        int j = 0;
        String mmdd = new String("");
        int i = 0;
        while (true) {
            if (i >= md_arr.length) {
                break;
            } else if (md_str.equals(md_arr[i])) {
                day = i + 1;
                break;
            } else {
                i++;
            }
        }
        if (day == 0) {
            return null;
        }
        months[1] = isSolarLeapYear ? 29 : 28;
        while (day - months[j] > 0) {
            day -= months[j];
            j++;
        }
        return mmdd + (j + 1 < 10 ? "0" + (j + 1) : "" + (j + 1)) + (day < 10 ? "0" + day : "" + day);
    }

    public static String daysToLunmmdd(String md_str, String[] md_arr, int lunYear) {
        String str;
        String str2;
        String str3;
        int monthDays;
        int day = 0;
        boolean isLeap = false;
        String mmdd = new String("");
        int i = 0;
        while (true) {
            if (i >= md_arr.length) {
                break;
            } else if (md_str.equals(md_arr[i])) {
                day = i + 1;
                break;
            } else {
                i++;
            }
        }
        if (day == 0) {
            return null;
        }
        int leapMonth = getLeapMonth(lunYear);
        int j = 1;
        while (true) {
            if (j > 12) {
                break;
            }
            if (j >= leapMonth) {
                if (j != leapMonth) {
                    if (day - getMonthDays(lunYear, j) <= 0) {
                        break;
                    }
                    monthDays = getMonthDays(lunYear, j);
                } else if (day - getMonthDays(lunYear, j) <= 0) {
                    break;
                } else {
                    day -= getMonthDays(lunYear, j);
                    if (day - getLeapMonthDays(lunYear) <= 0) {
                        isLeap = true;
                        break;
                    }
                    monthDays = getLeapMonthDays(lunYear);
                }
            } else {
                try {
                    if (day - getMonthDays(lunYear, j) <= 0) {
                        break;
                    }
                    monthDays = getMonthDays(lunYear, j);
                } catch (Exception e) {
                }
            }
            day -= monthDays;
            j++;
        }
        StringBuilder append = new StringBuilder().append(mmdd);
        if (isLeap) {
            str = "0";
        } else {
            str = "";
        }
        StringBuilder append2 = append.append(str);
        if (j < 10) {
            str2 = "0" + j;
        } else {
            str2 = "" + j;
        }
        StringBuilder append3 = append2.append(str2);
        if (day < 10) {
            str3 = "0" + day;
        } else {
            str3 = "" + day;
        }
        return append3.append(str3).toString();
    }

    public static String[] timePickerLunMonthDay(int lunYear) {
        int maxDay;
        String str;
        ArrayList<String> list = new ArrayList<>();
        int leapMonth = getLeapMonth(lunYear);
        int maxMonth = leapMonth > 0 ? 13 : 12;
        new StringBuilder("");
        int month = 1;
        while (month <= maxMonth) {
            if (month <= leapMonth) {
                try {
                    maxDay = getMonthDays(lunYear, month);
                } catch (Exception e) {
                }
            } else if (leapMonth > 0 && month == leapMonth + 1) {
                maxDay = getLeapMonthDays(lunYear);
            } else if (leapMonth <= 0 || month <= leapMonth + 1) {
                maxDay = getMonthDays(lunYear, month);
            } else {
                maxDay = getMonthDays(lunYear, month - 1);
            }
            String month_str = "";
            if (leapMonth == 0) {
                month_str = (month < 10 ? "0" : "") + month;
            } else if (month <= leapMonth) {
                month_str = (month < 10 ? "0" : "") + month;
            } else if (leapMonth > 0 && month == leapMonth + 1) {
                month_str = "0" + (month + -1 < 10 ? "0" : "") + (month - 1);
            } else if (leapMonth > 0 && month > leapMonth + 1) {
                month_str = (month + -1 < 10 ? "0" : "") + (month - 1);
            }
            int day = 1;
            while (day <= maxDay) {
                String lunarDate = "" + lunYear + month_str + (day < 10 ? "0" : "") + day;
                String str2 = "";
                if (lunarDate.length() == 8) {
                    int parseInt = Integer.parseInt(lunarDate.substring(0, 4));
                    str = numConvert(Integer.parseInt(lunarDate.substring(4, 6)), 1) + MONTH + numConvert(Integer.parseInt(lunarDate.substring(6, 8)), 2);
                } else {
                    int parseInt2 = Integer.parseInt(lunarDate.substring(0, 4));
                    str = LEAP + numConvert(Integer.parseInt(lunarDate.substring(5, 7)), 1) + MONTH + numConvert(Integer.parseInt(lunarDate.substring(7, 9)), 2);
                }
                list.add(str);
                day++;
            }
            month++;
        }
        return (String[]) list.toArray(new String[0]);
    }

    public static void getTextRes(Context context) {
        YEAR = context.getResources().getString(R.string.nubia_date_year);
        MONTH = context.getResources().getString(R.string.nubia_date_month);
        DAY = context.getResources().getString(R.string.nubia_date_day);
        LEAP = context.getResources().getString(R.string.nubia_leap);
        LUNAR_FIRST_DAY = context.getResources().getString(R.string.nubia_lunar_first_day);
        LUNAR_TWENTHIETH_DAY = context.getResources().getString(R.string.nubia_lunar_twentieth_day);
        LUNAR_FIRST_MONTH = context.getResources().getString(R.string.nubia_lunar_first_month);
        LUNAR_ELEVENTH_MONTH = context.getResources().getString(R.string.nubia_lunar_eleventh_month);
        LUNAR_TWELVETH_MONTH = context.getResources().getString(R.string.nubia_lunar_twelveth_month);
        ZERO = context.getResources().getString(R.string.nubia_zero);
        ONE = context.getResources().getString(R.string.nubia_one);
        TWO = context.getResources().getString(R.string.nubia_two);
        THREE = context.getResources().getString(R.string.nubia_three);
        FOUR = context.getResources().getString(R.string.nubia_four);
        FIVE = context.getResources().getString(R.string.nubia_five);
        SIX = context.getResources().getString(R.string.nubia_six);
        SEVEN = context.getResources().getString(R.string.nubia_seven);
        EIGHT = context.getResources().getString(R.string.nubia_eight);
        NINE = context.getResources().getString(R.string.nubia_nine);
        TEN = context.getResources().getString(R.string.nubia_ten);
        am = context.getResources().getString(R.string.nubia_time_am);
        pm = context.getResources().getString(R.string.nubia_time_pm);
        String[] weekdaysFull = context.getResources().getStringArray(R.array.nubia_weeks_full);
        for (int i = 0; i < weekdaysFull.length; i++) {
            WEEKDAYS_LONG[i] = weekdaysFull[i];
        }
        String[] weekdaysShort = context.getResources().getStringArray(R.array.nubia_weeks_short);
        for (int i2 = 0; i2 < weekdaysShort.length; i2++) {
            WEEKDAYS_SHORT[i2] = weekdaysShort[i2];
        }
        String[] monthsFull = context.getResources().getStringArray(R.array.nubia_months_full);
        for (int i3 = 0; i3 < monthsFull.length; i3++) {
            MONTHS_LONG[i3] = monthsFull[i3];
        }
        String[] monthsShort = context.getResources().getStringArray(R.array.nubia_months_short);
        for (int i4 = 0; i4 < monthsShort.length; i4++) {
            MONTHS_SHORT[i4] = monthsShort[i4];
        }
    }
}

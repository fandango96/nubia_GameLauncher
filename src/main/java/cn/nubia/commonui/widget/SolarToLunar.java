package cn.nubia.commonui.widget;

public class SolarToLunar {
    private static final char[] LunarLeapMonthTable = {0, 'P', 4, 0, ' ', '`', 5, 0, ' ', 'p', 5, 0, '@', 2, 6, 0, 'P', 3, 7, 0, '`', 4, 0, ' ', 'p', 5, 0, '0', 128, 6, 0, '@', 3, 7, 0, 'P', 4, 8, 0, '`', 4, 10, 0, '`', 5, 0, '0', 128, 5, 0, '@', 2, 7, 0, 'P', 4, 9, 0, '`', 4, 0, ' ', '`', 5, 0, '0', 176, 6, 0, 'P', 2, 7, 0, 'P', 3};
    private static final int[] LunarMonthDaysTable = {19168, 42352, 21096, 53856, 55632, 27304, 22176, 39632, 19176, 19168, 42200, 42192, 53840, 54600, 46416, 22176, 38608, 38320, 18872, 18864, 42160, 45656, 27216, 27968, 44456, 11104, 38256, 18808, 18800, 25776, 54432, 59984, 27976, 23248, 11104, 37744, 37600, 51560, 51536, 54432, 55888, 46416, 22176, 43736, 9680, 37584, 51544, 43344, 46248, 27808, 46416, 21928, 19872, 42416, 21176, 21168, 43344, 59728, 27296, 44368, 43856, 19296, 42352, 42352, 21088, 59696, 55632, 23208, 22176, 38608, 19176, 19152, 42192, 53864, 53840, 54568, 46400, 46752, 38608, 38320, 18864, 42168, 42160, 45656, 27216, 27968, 44448, 43872, 38256, 18808, 18800, 25776, 27216, 59984, 27432, 23232, 43872, 37736, 37600, 51552, 54440, 54432, 55888, 23208, 22176, 43736, 9680, 37584, 51544, 43344, 46240, 46416, 46416, 21928, 19360, 42416, 21176, 21168, 43312, 29864, 27296, 44368, 19880, 19296, 38256, 42208, 53856, 59696, 54576, 23200, 27472, 38608, 19176, 19152, 42192, 53848, 53840, 54560, 55968, 46496, 22224, 19160, 18864, 42168, 42160, 43600, 46376, 27936, 44448, 21936};
    private static final char[] SolarLunarOffsetTable = {'1', '&', 28, '.', '\"', 24, '+', ' ', 21, '(', 29, '0', '$', 25, ',', '!', 22, ')', 31, '2', '&', 27, '.', '#', 23, '+', ' ', 22, '(', 29, '/', '$', 25, ',', '\"', 23, ')', 30, '1', '&', 26, '-', '#', 24, '+', ' ', 21, '(', 28, '/', '$', 26, ',', '!', 23, '*', 30, '0', '&', 27, '-', '#', 24, '+', ' ', 20, '\'', 29, '/', '$', 26, '-', '!', 22, ')', 30, '0', '%', 27, '.', '#', 24, '+', ' ', '2', '\'', 28, '/', '$', 26, '-', '\"', 22, '(', 30, '1', '%', 27, '.', '#', 23, '*', 31, 21, '\'', 28, '0', '%', 25, ',', '!', 22, '(', 30, '1', '&', 27, '.', '#', 24, '*', 31, 21, '(', 28, '/', '$', 25, '+', '!', 22, ')', 30, '1', '&', 27, '-', '\"', 23, '*', 31, 21, '(', 29, '/', '$', 25, ',', ' ', 22};

    static String calendarSolarToLundar(int solarYear, int solarMonth, int solarDay) {
        int lunarYear;
        int lunarDay;
        int lunarMonth;
        String str;
        String str2;
        String str3;
        boolean isLeapMonth = false;
        int offsetDays = getSolarNewYearOffsetDays(solarYear, solarMonth, solarDay);
        int iLeapMonth = getLunarLeapMonth(solarYear);
        if (offsetDays < SolarLunarOffsetTable[solarYear - 1901]) {
            lunarYear = solarYear - 1;
            int offsetDays2 = SolarLunarOffsetTable[solarYear - 1901] - offsetDays;
            int lunarDay2 = offsetDays2;
            lunarMonth = 12;
            while (offsetDays2 > getLunarMonthDays(lunarYear, lunarMonth)) {
                lunarDay2 = offsetDays2;
                offsetDays2 -= getLunarMonthDays(lunarYear, lunarMonth);
                lunarMonth--;
            }
            if (lunarDay2 == 0) {
                lunarDay = 1;
            } else {
                lunarDay = (getLunarMonthDays(lunarYear, lunarMonth) - offsetDays2) + 1;
            }
        } else {
            lunarYear = solarYear;
            int offsetDays3 = offsetDays - SolarLunarOffsetTable[solarYear - 1901];
            lunarDay = offsetDays3 + 1;
            int lunarMonth2 = 1;
            while (true) {
                if (offsetDays3 < 0) {
                    break;
                }
                lunarDay = offsetDays3 + 1;
                offsetDays3 -= getLunarMonthDays(lunarYear, lunarMonth2);
                if (iLeapMonth == lunarMonth2 && offsetDays3 >= 0) {
                    lunarDay = offsetDays3 + 1;
                    offsetDays3 -= getLunarMonthDays(lunarYear, lunarMonth2 + 12);
                    if (offsetDays3 <= 0) {
                        lunarMonth2 += 13;
                        break;
                    } else if (offsetDays3 == 0) {
                        lunarDay = 1;
                    }
                }
                lunarMonth2++;
            }
            lunarMonth = lunarMonth2 - 1;
        }
        if (lunarMonth > 12) {
            isLeapMonth = true;
            lunarMonth -= 12;
        }
        StringBuilder append = new StringBuilder().append("").append(lunarYear);
        if (isLeapMonth) {
            str = "0";
        } else {
            str = "";
        }
        StringBuilder append2 = append.append(str);
        if (lunarMonth > 9) {
            str2 = "" + lunarMonth;
        } else {
            str2 = "0" + lunarMonth;
        }
        StringBuilder append3 = append2.append(str2);
        if (lunarDay > 9) {
            str3 = "" + lunarDay;
        } else {
            str3 = "0" + lunarDay;
        }
        return append3.append(str3).toString();
    }

    static int getSolarNewYearOffsetDays(int solarYear, int solarMonth, int solarDay) {
        int offsetDays = 0;
        for (int i = 1; i < solarMonth; i++) {
            offsetDays += getSolarYearMonthDays(solarYear, i);
        }
        return offsetDays + (solarDay - 1);
    }

    static int getLunarLeapMonth(int solarYear) {
        char month = LunarLeapMonthTable[(solarYear - 1901) / 2];
        if (solarYear % 2 == 0) {
            return month & 15;
        }
        return (month & 240) >> 4;
    }

    static int getLunarMonthDays(int solarYear, int solarMonth) {
        int leapMonth = getLunarLeapMonth(solarYear);
        if ((solarMonth > 12 && solarMonth - 12 != leapMonth) || solarMonth < 0) {
            return -1;
        }
        if (solarMonth - 12 != leapMonth || leapMonth <= 0) {
            if (leapMonth > 0 && solarMonth > leapMonth) {
                solarMonth++;
            }
            if ((LunarMonthDaysTable[solarYear - 1901] & (32768 >> (solarMonth - 1))) != 0) {
                return 30;
            }
            return 29;
        } else if ((LunarMonthDaysTable[solarYear - 1901] & (32768 >> leapMonth)) != 0) {
            return 30;
        } else {
            return 29;
        }
    }

    static int getSolarYearMonthDays(int solarYear, int solarMonth) {
        if (solarMonth == 1 || solarMonth == 3 || solarMonth == 5 || solarMonth == 7 || solarMonth == 8 || solarMonth == 10 || solarMonth == 12) {
            return 31;
        }
        if (solarMonth == 4 || solarMonth == 6 || solarMonth == 9 || solarMonth == 11) {
            return 30;
        }
        if (solarMonth != 2) {
            return 0;
        }
        if (isSolarLeapYear(solarYear)) {
            return 29;
        }
        return 28;
    }

    static boolean isSolarLeapYear(int solarYear) {
        return (solarYear % 4 == 0 && solarYear % 100 != 0) || solarYear % 400 == 0;
    }
}

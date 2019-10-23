package com.example.friendbook.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 11972 on 2017/10/6.
 */

public class Util {
    public static String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 hh:mm");
        return format.format(new Date());
    }
}

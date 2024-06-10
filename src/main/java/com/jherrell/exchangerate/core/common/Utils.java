package com.jherrell.exchangerate.core.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String generateDateWithFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }
}

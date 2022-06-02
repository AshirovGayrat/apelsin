package com.company.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static boolean checkExpiredDate(String expDate, LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MM/yy")).equals(expDate);
    }
}

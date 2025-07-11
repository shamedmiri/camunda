package com.example.demo.utils;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertDate {

    public static String currentDateShamsi() {
        DateConverter dateConverter = new DateConverter();
        String date = dateConverter.nowAsJalali().toString();
        String[] parts = date.split("-");
        String year = parts[0];
        String month = String.format("%02d", Integer.parseInt(parts[1]));
        String day = String.format("%02d", Integer.parseInt(parts[2]));
        return year + month + day;
    }
    public static String currentTimeShamsi() {
        ZonedDateTime tehranTime = ZonedDateTime.now(ZoneId.of("Asia/Tehran"));
        return tehranTime.format(DateTimeFormatter.ofPattern("HHmmss"));
    }
    public static String gregorianToJalali(int year,int month, int day){
        DateConverter dateConverter = new DateConverter();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(year, month, day);
        return jalaliDate.toString();
    }
    public static String jalaliToGregorian(int year,int month, int day){
        DateConverter dateConverter = new DateConverter();
        LocalDate localdate = dateConverter.jalaliToGregorian(year, month, day);
        return localdate.toString();
    }
}

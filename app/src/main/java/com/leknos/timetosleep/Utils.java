package com.leknos.timetosleep;

public class Utils {

    public static String timeWithNull(int number){
        if(number < 10){
            return "0"+number;
        }else{
            return String.valueOf(number);
        }
    }
}

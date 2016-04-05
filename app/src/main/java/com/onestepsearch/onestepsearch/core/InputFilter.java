package com.onestepsearch.onestepsearch.core;

import java.util.Random;

/**
 * Created by mdislam on 12/22/15.
 */
public abstract class InputFilter {

    public static boolean checkNumCharacters(String input, int chars){
        return (input.length() > chars);
    }


    public static boolean checkPassMatch(String password, String confirm){
        return password.equals(confirm);
    }


    public static String generateRandomString(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String encodeFileName(String rawFileName){

        String newFileName = rawFileName.replaceAll("\\s", "-");
        newFileName = newFileName.replace("\"", "");
        newFileName = newFileName.replace("\'", "");
        newFileName = newFileName.replaceAll("[^a-zA-Z0-9]+","");

        return newFileName;
    }

}

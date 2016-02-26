package com.mdislam.onestep.CORE;

/**
 * Created by mdislam on 12/22/15.
 */
public class InputFilter {

    public static boolean checkNumCharacters(String input, int chars){
        return (input.length() > chars);
    }


    public boolean checkPassMatch(String password, String confirm){
        return password.equals(confirm);
    }

}

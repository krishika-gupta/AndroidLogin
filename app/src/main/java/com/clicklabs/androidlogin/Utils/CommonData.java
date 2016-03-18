package com.clicklabs.androidlogin.Utils;

import android.content.Context;

import com.clicklabs.androidlogin.Models.UserInformation;

import io.paperdb.Paper;

/**
 * Created by hp- on 11-03-2016.
 */
public class CommonData implements AppConstants {

    private static UserInformation userInformation;
    public static UserInformation getUserInfo(Context context) {
        if (userInformation == null) {
            userInformation = Paper.book().read(APP_USER_INFO);

        }


        return userInformation;

    }


    public static void saveUserInfo(Context context, UserInformation information) {
        userInformation = information;
        Paper.book().write(APP_USER_INFO, information);
    }


    public static void clearAllAppData() {

        userInformation=null ;
        Paper.book().destroy();
    }






}

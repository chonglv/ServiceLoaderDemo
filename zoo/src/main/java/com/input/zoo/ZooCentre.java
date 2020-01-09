package com.input.zoo;


import android.util.Log;

import com.input.pet.IPet;

import java.util.ServiceLoader;

public class ZooCentre {
    private static final String TAG = "ZooCentre";
    public static void loadPet(){
        ServiceLoader<IPet> petServiceLoader = ServiceLoader.load(IPet.class);
        for (IPet pet: petServiceLoader){
            pet.say();
        }
        Log.d(TAG, "loadPet: ");
    }
}

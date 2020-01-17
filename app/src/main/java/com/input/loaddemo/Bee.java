package com.input.loaddemo;


import com.input.pet.Pet;
import com.input.pet.PetFood;

@Pet(Name = "bee")
public class Bee {
    @PetFood("flower")
    public static String food = "flower";
}

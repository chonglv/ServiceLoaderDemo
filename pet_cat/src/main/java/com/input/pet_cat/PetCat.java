package com.input.pet_cat;


import com.input.pet.Pet;
import com.input.pet.PetFood;

@Pet(Name = "cat")
public class PetCat {
    @PetFood("fish")
    public static String food = "fish";
}

package com.input.pet_dog;

import com.input.pet.Pet;
import com.input.pet.PetFood;

@Pet(Name = "dog")
public class PetDog {
    @PetFood("bone")
    public static String food = "bone";
}

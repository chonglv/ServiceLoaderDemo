package com.pet.register

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PetRegisterPlugin implements Plugin<Project> {
    private static final String TAG = "PetRegisterPlugin"

    @Override
    void apply(Project project) {
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        appExtension.registerTransform(new RegisterTransform());
    }
}
package com.pet.register

import org.gradle.api.Plugin
import org.gradle.api.Project

class PetRegister implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task('testTask') {
            println "Hello gradle plugin"
        }
    }
}
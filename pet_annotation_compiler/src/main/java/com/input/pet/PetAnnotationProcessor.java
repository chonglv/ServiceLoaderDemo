package com.input.pet;

import com.google.auto.service.AutoService;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class PetAnnotationProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;
    private int times = 0;
    private static int entry = 0;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        System.out.println("getSupportedAnnotationTypes");

        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(Pet.class.getCanonicalName());
        supportTypes.add(PetFood.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    protected synchronized boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            messager.printMessage(Diagnostic.Kind.NOTE,"process:"+annotations);
            messager.printMessage(Diagnostic.Kind.NOTE,"process:"+roundEnv);
            messager.printMessage(Diagnostic.Kind.NOTE,"process: entry:"+entry);
            messager.printMessage(Diagnostic.Kind.NOTE,"process: times:"+times);
            entry++;
            times++;
            if (annotations == null || annotations.size() == 0){
                return false;
            }
            messager.printMessage(Diagnostic.Kind.NOTE,"process: +++:");
            Set<? extends Element> foodAnnotationSet = roundEnv.getElementsAnnotatedWith(PetFood.class);
            messager.printMessage(Diagnostic.Kind.NOTE,"process: food: set:"+foodAnnotationSet);
            ArrayList<String> petFoodNames = new ArrayList<>();
            messager.printMessage(Diagnostic.Kind.NOTE,"process: food");

            for (Element element: foodAnnotationSet){
                messager.printMessage(Diagnostic.Kind.NOTE,"process:food 1:"+element);
                messager.printMessage(Diagnostic.Kind.NOTE,"process:food 2:"+element.getEnclosingElement());
                messager.printMessage(Diagnostic.Kind.NOTE,"process:food 3:"+element.getEnclosingElement().getSimpleName());
                PetFood foodAnnotation = element.getAnnotation(PetFood.class);
                String name = foodAnnotation.value();
                petFoodNames.add(name);

            }

            Set<? extends Element> annotationSet = roundEnv.getElementsAnnotatedWith(Pet.class);
            messager.printMessage(Diagnostic.Kind.NOTE,"process: pet: set:"+annotationSet);

            if (annotationSet == null || annotationSet.size() == 0){
                return false;
            }
            messager.printMessage(Diagnostic.Kind.NOTE,"process: pet");
            ArrayList<String> petNames = new ArrayList<>();
            for (Element element: annotationSet){
                messager.printMessage(Diagnostic.Kind.NOTE,"process:pet:"+element);
                messager.printMessage(Diagnostic.Kind.NOTE,"process:pet:"+element.getEnclosingElement());

                Pet petAnnotation = element.getAnnotation(Pet.class);
                String name = petAnnotation.Name();
                petNames.add(name);
            }
            generateFile(petNames);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    private void generateFile(ArrayList<String> petNames) {
        for(String name:petNames){
            generatePetFile(name);
        }
    }

    private void generatePetFile(String name) {
        MethodSpec sayMethod = MethodSpec.methodBuilder("say")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("$T.out.println(\"say:\"+$S)",System.class, name)
                .build();

        AnnotationSpec autoService = AnnotationSpec.builder(AutoService.class)
                .addMember("value","$T.class", ClassName.get(IPet.class))
                .build();

//        TypeSpec pet = TypeSpec.classBuilder("Pet$"+name)
        TypeSpec pet = TypeSpec.classBuilder("Pet$"+System.currentTimeMillis())
                .addAnnotation(autoService)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IPet.class)
                .addMethod(sayMethod)
                .build();
        JavaFile javaFile = JavaFile.builder("com.input.pet", pet).build();
        try {
//            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (Throwable ex){
            ex.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE,"process: ------------");
        }
    }
}

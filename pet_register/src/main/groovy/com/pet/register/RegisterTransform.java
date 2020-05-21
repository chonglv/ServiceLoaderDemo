package com.pet.register;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.gradle.internal.impldep.org.apache.http.util.TextUtils;
import org.gradle.util.TextUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class RegisterTransform extends Transform {

    public static final String TAG = "RegisterTransform";

    public RegisterTransform() {
        super();
    }

    @Override
    public String getName() {
        return "RegisterTransform";
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        boolean isIncremental = transformInvocation.isIncremental();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                System.out.println("====jar====");
                System.out.println(jarInput.getFile().getAbsolutePath());
                System.out.println(jarInput.getContentTypes());
                System.out.println(jarInput.getScopes());
                System.out.println(dest.getAbsolutePath());
                scanJar(jarInput.getFile());
                FileUtils.copyFile(jarInput.getFile(), dest);
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                System.out.println("====dir====");
                System.out.println("====dir====");

                System.out.println(directoryInput.getFile().getAbsolutePath());
                System.out.println(directoryInput.getContentTypes());
                System.out.println(directoryInput.getScopes());
                System.out.println(dest.getAbsolutePath());

                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }

    }

    static void scanJar(File jarFile) {
        if (jarFile!=null && jarFile.exists()) {
            JarFile file = null;
            try {
                file = new JarFile(jarFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Enumeration enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith("com/input/pet/Pet$")) {
                    System.out.println(entryName);
                    InputStream inputStream = null;
                    try {
                        inputStream = file.getInputStream(jarEntry);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    scanClass(inputStream);
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void scanClass(File file) {
        try {
            scanClass(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void scanClass(InputStream inputStream) {
        ClassReader cr = null;
        try {
            cr = new ClassReader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassWriter cv) {
            super(api, cv);
        }

        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            System.out.println("name="+name+" super="+superName+" interfaces="+interfaces);
            for (String face: interfaces){
                System.out.println("face="+face);
                if ("com/input/pet/IPet".equals(face)){
                    System.out.println("This is:"+name);
                }
            }
        }


    }



    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public Set<QualifiedContent.ContentType> getOutputTypes() {
        return super.getOutputTypes();
    }

    @Override
    public Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return TransformManager.EMPTY_SCOPES;
    }


    @Override
    public Map<String, Object> getParameterInputs() {
        return super.getParameterInputs();
    }

    @Override
    public boolean isCacheable() {
        return true;
    }


    @Override
    public boolean isIncremental() {
        return true;
    }

}
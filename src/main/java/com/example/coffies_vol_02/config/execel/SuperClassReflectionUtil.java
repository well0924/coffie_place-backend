package com.example.coffies_vol_02.config.execel;

import lombok.NoArgsConstructor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class SuperClassReflectionUtil {
    public static List<Field> getAllFields(Class<?>clazz){
        List<Field> fields = new ArrayList<>();

        for(Class<?>clazzInClasses : getAllClassesIncludingSuperClasses(clazz,true)) {
            fields.addAll(Arrays.asList(clazzInClasses.getDeclaredFields()));
        }

        return fields;
    }

    public static Annotation getAnnotation(Class<?>clazz,Class<? extends Annotation>targetAnnotation) {

        for(Class<?>clazzInClasses : getAllClassesIncludingSuperClasses(clazz, false)) {

            if(clazzInClasses.isAnnotationPresent(targetAnnotation)) {
                return clazzInClasses.getAnnotation(targetAnnotation);
            }
        }
        return null;
    }

    public static Field getField(Class<?>clazz,String name)throws Exception{

        for(Class<?>clazzInClasses : getAllClassesIncludingSuperClasses(clazz,true)) {

            for(Field field : clazzInClasses.getDeclaredFields()) {

                if(field.getName().equals(name)) {
                    return clazzInClasses.getDeclaredField(name);
                }
            }
        }
        throw new Exception();
    }

    private static List<Class<?>> getAllClassesIncludingSuperClasses(Class<?>clazz,boolean fromSuper){
        List<Class<?>> classes = new ArrayList<>();

        while(clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }
        if(fromSuper) Collections.reverse(classes);
        return classes;
    }
}

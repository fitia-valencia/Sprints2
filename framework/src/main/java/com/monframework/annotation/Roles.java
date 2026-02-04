package com.monframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Roles { //pour plusieurs roles
    String[] value(); // Liste des rôles acceptés
    boolean requireAll() default false; // true = tous les rôles, false = au moins un
}
package id.co.babe.entityextractor.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by aditya on 30/09/16.
 */
@BindingAnnotation
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface BabeNlpDbContext {
}

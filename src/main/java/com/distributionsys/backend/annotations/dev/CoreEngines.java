package com.distributionsys.backend.annotations.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * README: This annotation is used to make clarify method's meaning because it's used to be re-used by the others.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreEngines {
}

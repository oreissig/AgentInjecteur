package com.github.oreissig.agentinjecteur.rt;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Agent Injecteur removes {@link javax.inject.Inject} annotations to avoid double injection, but marks those elements
 * with this annotation instead.
 */
@Target({METHOD, CONSTRUCTOR, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Injected {
}

package io.github.xypercode.craftyconfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a config entry in the {@link CraftyConfig}.
 * This is automatically processed.
 *
 * @see CraftyConfig
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntry {
    /**
     * This is the path to the Json5 object.
     * Examples:
     * <pre>
     *  "foo"
     *  "foo.bar"
     *  "foo.bar.baz"
     * </pre>
     *
     * @return the path to the Json5 object
     */
    String path();

    String comment() default "";

    boolean defaulted() default false;
}

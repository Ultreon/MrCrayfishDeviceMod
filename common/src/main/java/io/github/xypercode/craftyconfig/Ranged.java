package io.github.xypercode.craftyconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a config entry in the {@link CraftyConfig}.
 * This is automatically processed.
 *
 * @see CraftyConfig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Ranged {
    double min();

    double max();
}

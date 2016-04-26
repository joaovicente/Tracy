package com.apm4all.tracy.extensions.annotations;

import java.lang.annotation.*;

/**
 * Marks the POJO representing the request body and allows for the use of custom parsers to parse the relevant
 * information out of incoming request bodies and annotate it on the root level Tracy frame.
 * <p>
 * <b>API note:</b> This annotation enables the use of {@link RequestProfiling#parser()}
 *
 * @author Jakub Stas
 * @see RequestProfiling#parser()
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}

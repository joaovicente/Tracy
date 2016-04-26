package com.apm4all.tracy.extensions.annotations;

import java.lang.annotation.*;

/**
 * Marks the instance of the request to be profiled and allows {@link RequestProfiling} to automatically
 * pick the request up and profile it.
 * <p>
 * <b>API note:</b> This annotation enables the use of {@link RequestProfiling}
 *
 * @author Jakub Stas
 * @see RequestProfiling
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {
}

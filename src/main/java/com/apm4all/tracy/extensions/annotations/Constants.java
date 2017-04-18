package com.apm4all.tracy.extensions.annotations;

/**
 * A few constants used in Tracy extensions.
 *
 * @author Jakub Stas
 */
public final class Constants {

    /**
     * A wild card string used to specify that all of the method arguments should be annotated.
     */
    public static final String ALL_PARAMETERS = "..";

    /**
     * The name of the attribute annotated for method returns.
     */
    public static final String METHOD_RESULT = "returned";

    /**
     * The name of the attribute annotated for an HTTP code returned by a profiled endpoint.
     */
    public static final String HTTP_CODE = "httpCode";

    /**
     * The name of the attribute annotated for an error code in case of an exception being thrown.
     */
    public static final String ERROR_CODE = "errorCode";

    /**
     * The name of the attribute annotated for an error message in case of an exception being thrown.
     */
    public static final String ERROR_MESSAGE = "errorMessage";
}
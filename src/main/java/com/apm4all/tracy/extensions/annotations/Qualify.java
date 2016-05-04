package com.apm4all.tracy.extensions.annotations;

/**
 * Enumeration determining qualification of any frame name to be used by Tracy.
 *
 * @author Jakub Stas
 * @see Profiled
 * @since 4.0.0
 */
public enum Qualify {

    /**
     * Constant that indicates no qualification at all.
     */
    NO,

    /**
     * Constant that indicates qualification using simple class name.
     */
    CLASS,

    /**
     * Constant that indicates qualification using canonical class name.
     */
    PACKAGE
}
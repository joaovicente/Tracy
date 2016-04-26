package com.apm4all.tracy.extensions.annotations;

/**
 * Enumeration determining handling of any exception thrown from a profiled method that gets processed.
 *
 * @author Jakub Stas
 * @see Profiled
 */
public enum OnException {

    /**
     * Constant that indicates no action at all.
     */
    DO_NOTHING,
    /**
     * Constant that indicates annotating and closing of a current frame and handing over the execution back
     * to the client code.
     */
    POP,
    /**
     * Constant that indicates annotating of a current frame without closing it and handing over the execution
     * back to the client code.
     */
    MARK_FRAME,
    /**
     * Constant that indicates annotating of a current frame as well as any other frame on the path to the root
     * frame (root frame included) closing them along the way.
     */
    MARK_UP_TO_THE_ROOT_FRAME;
}

package com.apm4all.tracy.extensions;

/**
 * The family of headers used by Tracy to help with request profiling.
 *
 * @author Jakub Stas
 */
public enum XTracyHeader {

    /**
     * The Tracy {@code X-Tracy-Parent-Task-Id} header field name. This header stores the ID of the ancestor Tracy task in a Tracy graph.
     */
    X_TRACY_PARENT_TASK_ID("X-Tracy-Parent-Task-Id"),

    /**
     * The Tracy {@code X-Tracy-Task-Id} header field name. This header stores the task ID used by Tracy to bind log frames together.
     */
    X_TRACY_TASK_ID("X-Tracy-Task-Id"),

    /**
     * The Tracy {@code X-Tracy-Parent-Operation-Id} header field name. This header stores the ID of the ancestor Tracy event in a Tracy graph.
     */
    X_TRACY_PARENT_OPERATION_ID("X-Tracy-Parent-Operation-Id"),

    /**
     * The Tracy {@code X-Tracy-Annotations} header field name. This header stores comma separated list of key-value pairs used by the application
     * in further processing.
     */
    X_TRACY_ANNOTATIONS("X-Tracy-Annotations");

    private final String headerName;

    XTracyHeader(final String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}

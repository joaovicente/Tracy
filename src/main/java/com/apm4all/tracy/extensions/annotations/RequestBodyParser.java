package com.apm4all.tracy.extensions.annotations;

/**
 * Interface for custom parser implementations allowing developer to parse the request body and annotate
 * the relevant information on the root level Tracy frame opened for given endpoint method.
 *
 * @param <T> the type of request body (POJO representing deserialized request body by the JAX-RS framework)
 * @author Jakub Stas
 * @see RequestProfiling
 * @see RequestBody
 */
public interface RequestBodyParser<T> {

    /**
     * Parses out the relevant data from the request body and annotates it on the root level Tracy
     * frame opened for given endpoint method.
     *
     * @param requestBody POJO representing the request body
     */
    void parseAndAnnotate(final T requestBody);
}

package com.apm4all.tracy.extensions.annotations;

import java.lang.annotation.*;

/**
 * {@code @RequestProfiling} decorates {@code @Profiled} with features specific to the Tracy context management.
 * <p>
 * <b>API note:</b> This annotation must be used together with {@code @Profiled} to take effect.
 *
 * <h3>Tracy context initialization</h3>
 *
 * <p>To initialize a Tracy context the {@code @RequestProfiling} annotation needs to specify at least three attributes.
 * {@link #component() component} and {@link #parentOptId() parentOptId} attributes represent their counterparts from Tracy
 * and are mandatory for the annotation to take effect. The task ID is resolved or generated randomly by Tracy. While the
 * {@code @RequestProfiling} annotation handles context related operations, the {@link Profiled} annotation specifies what
 * are the specifics of the profiling. Keep in mind that this annotation only handles the initialization of the Tracy context.
 * It still needs to be torn down after the method execution.
 *
 * <pre class="code">
 * &#064;GET
 * &#064;Produces(MediaType.APPLICATION_JSON)
 * &#064;Profiled
 * &#064;RequestProfiling(parentOptId = "1", component = "eshop")
 * public Response listProducts(@Context @Request HttpServletRequest request) {
 *      return Response.ok().build();
 * }</pre>
 *
 * <h3>Task ID resolution</h3>
 *
 * <p>While a {@link #taskId() taskId} attribute is available, the default strategy for the task ID resolution is to look for a
 * specific header 'X-Tracy-Task-Id' on the request and use its value. This is convenient, but if you want to use a custom header
 * you can specify the header name in the {@link #taskId() taskId} attribute and it will be used instead of the default one.
 *
 * <pre class="code">
 * &#064;GET
 * &#064;Produces(MediaType.APPLICATION_JSON)
 * &#064;Profiled
 * &#064;RequestProfiling(taskId="X-Tracy-Task-Id", parentOptId = "1", component = "eshop")
 * public Response listProducts(@Context @Request HttpServletRequest request) {
 *      return Response.ok().build();
 * }</pre>
 * <p>
 * Note that if you don't want to tie the API call to a larger process view, you can use different task ID resolution strategies
 * by using the {@link #resolution() resolution} attribute. Combining the {@link #taskId()} and {@link #resolution()} attributes
 * allows you to specify the task ID by hand or instruct Tracy to generate it for you.
 *
 * <pre class="code">
 * &#064;GET
 * &#064;Produces(MediaType.APPLICATION_JSON)
 * &#064;Profiled
 * &#064;RequestProfiling(taskId="listProductsAPI", resolution=Resolution.STRING, parentOptId = "1", component = "eshop")
 * public Response listProducts(@Context @Request HttpServletRequest request) {
 *      return Response.ok().build();
 * }</pre>
 *
 * <h3>Request processing</h3>
 *
 * <p>It is allowed for the implementation of this contract to include an additional profiling as a part of request processing.
 * This section is intentionally left open for interpretation since the logic contained here is specific to the needs of the
 * implementer. Typical use-cases would include parsing and annotation of the full request URI or parsing of various identifiers
 * to be passed around as a part of the overall architecture. All these specifics must be captured in the implementation's documentation.
 *
 * <h4>Request body processing</h4>
 *
 * <p>It is also allowed to parse and annotate data sent in the body of the request. Since the request body parsing is highly domain
 * or implementer specific task, it is crucial to reduce the coupling between the library and the code using it. By implementing the
 * {@link RequestBodyParser} interface and linking it to the {@code @RequestProfiling} annotation using the {@link #parser()} attribute,
 * custom parsing and annotation logic can be introduced into the request processing flow.
 *
 * <pre class="code">
 * &#064;POST
 * &#064;Produces(MediaType.APPLICATION_JSON)
 * &#064;Consumes(MediaType.APPLICATION_JSON)
 * &#064;Profiled
 * &#064;RequestProfiling(taskId="createProductAPI", resolution=Resolution.STRING, parentOptId = "1", component = "eshop")
 * public Response createProduct(@Context @Request HttpServletRequest request, @RequestBody Product product) {
 *      return Response.ok(product).build();
 * }</pre>
 *
 * @author Jakub Stas
 * @see Profiled
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestProfiling {

    /**
     * String attribute uniquely identifying Tracy log frame which allows correlating Tracy events. The value
     * provided will be handled according to the behaviour specified by selected resolution strategy.
     *
     * @see Resolution
     */
    String taskId() default "";

    /**
     * Strategy for task ID resolution describing where to get the task ID from and how to process it.
     * <p>
     * <b>API note:</b> If left unspecified the headers of the request linked using {@code forRequest} attribute will
     * be inspected according to the specification of {@link Resolution#HEADER} strategy.
     *
     * @see Resolution
     */
    Resolution resolution() default Resolution.HEADER;

    /**
     * <b>Required</b> string attribute identifying the parent operation invoking logic on a local component.
     * <p>
     * <b>API note:</b> If left unspecified the annotated method will not be profiled.
     */
    String parentOptId() default "";

    /**
     * <b>Required</b> string attribute identifying the name of the component the Tracy logs belong to.
     * <p>
     * <b>API note:</b> If left unspecified the annotated method will not be profiled.
     */
    String component() default "";

    /**
     * Type of the parser to be used to parse and annotate data from request body.
     * <p>
     * <b>API note:</b> If left unspecified the request body will not be profiled or annotated.
     *
     * @see RequestBodyParser
     * @see RequestBody
     */
    Class parser() default DEFAULT.class;

    /**
     * Class representing the default request processing strategy (does nothing).
     */
    final class DEFAULT {
    }
}

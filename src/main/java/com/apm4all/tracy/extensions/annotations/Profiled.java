package com.apm4all.tracy.extensions.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a method produces a log frame to be handled by Tracy.
 * <p>
 * <b>API note:</b> The use of {@code @Profiled} implies:
 * <ul>
 * <li>the implicit opening of a new Tracy log frame before method execution and closing it right after the method finished it's execution.</li>
 * <li>that the Tracy context has been previously created either manually or using {@code @RequestProfiling}</li>
 * </ul>
 *
 * <h3>Frame names</h3>
 *
 * <p>While a {@link #name() name} attribute is available, the default strategy for
 * determining the name of a Tracy frame is to use the name of the {@code @Profiled}
 * method. This is convenient and intuitive, but if explicit naming is desired, the
 * {@code name} attribute may be used.
 *
 * <p>Also note that the default behavior of the {@code name} attribute can cause
 * several Tracy frames to bare the same name. In these cases, the {@link #qualify() qualify}
 * attribute should be used to qualify the name of the frame. No qualification is performed
 * by default.
 *
 * <pre class="code">
 *     &#064;Profiled(name = "reportGeneration", qualify = Qualify.CLASS)
 *     public Report generateReport() {
 *         return report;
 *     }</pre>
 *
 * <h3>Method output capturing</h3>
 *
 * <p>Typically, {@code @Profiled} methods return a value that can be captured on the Tracy frame.
 * Use the {@link #captureOutput() captureOutput} attribute to turn this behavior on. The value
 * returned from the method will be annotated under the name {@code returned}. No output capturing
 * is performed by default.
 *
 * <pre class="code">
 *     &#064;Profiled(captureOutput = true)
 *     public Report generateReport() {
 *         return report;
 *     }</pre>
 *
 * <h3>Argument annotations</h3>
 *
 * <p>{@code @Profiled} methods may also capture method arguments and annotate them on the Tracy
 * frame using the appropriate method's parameter names. Use the parameter names to specify
 * which method argument should by annotated on the Tracy frame.
 *
 * <pre class="code">
 *     &#064;Profiled(annotations = {"id"})
 *     public Product findById(final long id) {
 *         return product;
 *     }</pre>
 *
 * <p>Note that if you want to capture all of the arguments there is a wildcard option {@code ..}
 * available.
 *
 * <pre class="code">
 *     &#064;Profiled(annotations = {".."})
 *     public List&lt;Product&gt; findByNameAndCategory(final String name, final Category category) {
 *         return products;
 *     }</pre>
 *
 * <h3>Exception handling</h3>
 *
 * <p>If a {@code @Profiled} method throws an exception, you can annotate it on the Tracy frame. All
 * exception types are included in the processing by default and you can choose how to process them
 * using the {@link #onException() onException} attribute which allows you to pick the processing strategy.
 *
 * <pre class="code">
 *     &#064;Profiled(onException = OnException.MARK_FRAME)
 *     public Product findById(final long id) throws ProductNotFoundException {
 *         return product;
 *     }</pre>
 *
 * <p>This is convenient, but if the processing of specific exception types or hierarchies is desired,
 * the {@link #exclude() exclude} and {@link #include() include} attributes allow you to specify that.
 * Let's suppose that {@code ProductNotFoundException} is a subtype of {@link java.sql.SQLException}.
 *
 * <pre class="code">
 *     &#064;Profiled(onException = OnException.MARK_FRAME, exclude = {SQLException.class, RuntimeException.class}, include = {ProductNotFoundException.class})
 *     public Product findById(final long id) throws ProductNotFoundException {
 *         return product;
 *     }</pre>
 *
 * @author Jakub Stas
 * @since 1.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Profiled {

    /**
     * String attribute forming the base for uniquely identifying Tracy log frame which allows correlating
     * Tracy events. The base for the name of a log frame created for the {@code @Profiled} method.
     * <p>
     * <b>API note:</b> If left unspecified the name of the method is the base for the name of the log frame.
     * If specified, the method name is ignored.
     */
    String name() default "";

    /**
     * Is the base for the name of a log frame created for the {@code @Profiled} method to be qualified in
     * any way?
     * <p>
     * <b>API note:</b> If left unspecified the base for the name of the log frame will be used as is.
     *
     * @see Qualify
     */
    Qualify qualify() default Qualify.NO;

    /**
     * The names of all arguments to be annotated on a log frame created for the {@code @Profiled} method.
     * To include all available arguments consider using shorthand {@link Constants#ALL_PARAMETERS}.
     * <p>
     * <b>API note:</b> If left unspecified no arguments will be annotated on the log frame.
     */
    String[] annotations() default {};

    /**
     * Is the {@code @Profiled} method's output supposed to be annotated on a log frame?
     * <p>
     * <b>API note:</b> If left unspecified no method output will be annotated on the log frame.
     */
    boolean captureOutput() default false;

    /**
     * The exception processing strategy used in case a thrown exception is to be processed.
     * <p>
     * <b>API note:</b> All exception types are included in the processing by default. If left unspecified the exception message will be annotated on the current frame
     * and this frame will be closed.
     *
     * @see OnException
     * @see Profiled#exclude()
     * @see Profiled#include()
     */
    OnException onException() default OnException.POP;

    /**
     * The class instances of all types or type hierarchies that are to be excluded from exception processing.
     * <p>
     * <b>API note:</b> If left empty, no types will be excluded from processing.
     *
     * @see Profiled#include()
     */
    Class[] exclude() default {};

    /**
     * All exception types are included in the processing by default. Exception type or hierarchy inclusion only
     * makes sense when a broader exclusion has been defined in the {@code exclude} attribute.
     * <p>
     * <b>API note:</b> In order to use this attribute a broader exclusion must be defined in the {@code exclude} attribute.
     * If left empty, no types will be included back into processing.
     *
     * @see Profiled#exclude()
     */
    Class[] include() default {};
}

package bot.inker.ankh.core.api.ioc;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * This class provides a generic implementation of the lazy initialization
 * pattern.
 *
 * <p>
 * Sometimes an application has to deal with an object only under certain
 * circumstances, e.g. when the user selects a specific menu item or if a
 * special event is received. If the creation of the object is costly or the
 * consumption of memory or other system resources is significant, it may make
 * sense to defer the creation of this object until it is really needed. This is
 * a use case for the lazy initialization pattern.
 * </p>
 * <p>
 * This abstract base class provides an implementation of the double-check idiom
 * for an instance field as discussed in Joshua Bloch's "Effective Java", 2nd
 * edition, item 71. The class already implements all necessary synchronization.
 * A concrete subclass has to implement the {@code initialize()} method, which
 * actually creates the wrapped data object.
 * </p>
 * <p>
 * As an usage example consider that we have a class {@code ComplexObject} whose
 * instantiation is a complex operation. In order to apply lazy initialization
 * to this class, a subclass of {@link DcLazy} has to be created:
 * </p>
 *
 * <pre>
 * public class ComplexObjectInitializer extends LazyInitializer&lt;ComplexObject&gt; {
 *     &#064;Override
 *     protected ComplexObject initialize() {
 *         return new ComplexObject();
 *     }
 * }
 * </pre>
 *
 * <p>
 * Access to the data object is provided through the {@code get()} method. So,
 * code that wants to obtain the {@code ComplexObject} instance would simply
 * look like this:
 * </p>
 *
 * <pre>
 * // Create an instance of the lazy initializer
 * ComplexObjectInitializer initializer = new ComplexObjectInitializer();
 * ...
 * // When the object is actually needed:
 * ComplexObject cobj = initializer.get();
 * </pre>
 *
 * <p>
 * If multiple threads call the {@code get()} method when the object has not yet
 * been created, they are blocked until initialization completes. The algorithm
 * guarantees that only a single instance of the wrapped object class is
 * created, which is passed to all callers. Once initialized, calls to the
 * {@code get()} method are pretty fast because no synchronization is needed
 * (only an access to a <b>volatile</b> member field).
 * </p>
 *
 * @since 3.0
 * @param <T> the type of the object managed by this initializer class
 */
public abstract class DcLazy<T> {

    private static final Object NO_INIT = new Object();

    @SuppressWarnings("unchecked")
    // Stores the managed object.
    private volatile T object = (T) NO_INIT;

    /**
     * Returns the object wrapped by this instance. On first access the object
     * is created. After that it is cached and can be accessed pretty fast.
     *
     * @return the object initialized by this {@link DcLazy}
     */
    public T get() {
        // use a temporary variable to reduce the number of reads of the
        // volatile field
        T result = object;

        if (result == NO_INIT) {
            synchronized (this) {
                result = object;
                if (result == NO_INIT) {
                    object = result = callInitialize();
                }
            }
        }

        return result;
    }

    private <E extends Throwable> T callInitialize() throws E{
        try {
            return initialize();
        } catch (Throwable e) {
            throw (E) e;
        }
    }

    /**
     * Creates and initializes the object managed by this {@code
     * LazyInitializer}. This method is called by {@link #get()} when the object
     * is accessed for the first time. An implementation can focus on the
     * creation of the object. No synchronization is needed, as this is already
     * handled by {@code get()}.
     *
     * @return the managed data object
     */
    protected abstract T initialize() throws Throwable;

    public static <T> DcLazy<T> of(Supplier<T> supplier){
        return new DcLazy<T>() {
            @Override
            protected T initialize() throws Throwable {
                return supplier.get();
            }
        };
    }

    public static <T> DcLazy<T> of(Callable<T> supplier){
        return new CallableInitializer<>(supplier);
    }

    private static class CallableInitializer<T> extends DcLazy<T>{
        private final Callable<T> supplier;

        private CallableInitializer(Callable<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected T initialize() throws Throwable {
            return supplier.call();
        }
    }
}
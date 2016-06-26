package utils.decorator

import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/***
 * Dependencies:
 * cglib:cglib (or spring-framework:spring-context) for AOP
 * kotlin:kotlin-reflect for method-invoke
 */

@Suppress("UNCHECKED_CAST")
infix fun<T : Any> T.decorate(decorator: ClassDecorator): T =
        Enhancer.create(this.javaClass, null, InvocationHandler { proxy, method, args ->
            try {
                decorator.onStart(method, args)
                return@InvocationHandler decorator.onResult(method, method.invoke(this, *args), args)
            } catch (error: InvocationTargetException) {
                throw decorator.onError(method, error.targetException, args)
            } finally {
                decorator.onComplete(method, args)
            }
        }) as T

@Suppress("UNCHECKED_CAST")
inline fun<reified T : Any> T.decorateTo(decorator: ClassDecorator): T =
        if (T::class.java.isInterface)
            Enhancer.create(Any::class.java, arrayOf(T::class.java), InvocationHandler { proxy, method, args ->
                try {
                    decorator.onStart(method, args)
                    return@InvocationHandler decorator.onResult(method, method.invoke(this, *args), args)
                } catch (error: InvocationTargetException) {
                    throw decorator.onError(method, error.targetException, args)
                } finally {
                    decorator.onComplete(method, args)
                }
            }) as T
        else
            Enhancer.create(T::class.java, null, InvocationHandler { proxy, method, args ->
                try {
                    decorator.onStart(method, args)
                    return@InvocationHandler decorator.onResult(method, method.invoke(this, *args), args)
                } catch (error: InvocationTargetException) {
                    throw decorator.onError(method, error.targetException, args)
                } finally {
                    decorator.onComplete(method, args)
                }
            }) as T


interface ClassDecorator {
    fun onStart(method: Method, args: Array<Any>) = Unit

    fun onComplete(method: Method, args: Array<Any>) = Unit

    fun onError(method: Method, error: Throwable, args: Array<Any>): Throwable = error

    fun onResult(method: Method, result: Any?, args: Array<Any>): Any? = result
}

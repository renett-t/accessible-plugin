package com.renettt.accessible.di


object DI {

    val factories = mutableMapOf<Class<*>, () -> Any>()
    val storage = mutableMapOf<Class<*>, Any>()

    inline operator fun <reified T> invoke(): T = get()

    inline fun <reified T> get(): T {
        var value = storage[T::class.java]
        if (value == null) {
            val factory = factories[T::class.java]
                ?: error("Instance of type ${T::class.java} is not saved to ServiceLocator.")
            value = factory()
            storage[T::class.java] = value
        }
        return value as T
    }

    inline fun <reified T> single(noinline factory: () -> T) {
        factories[T::class.java] = {
            factory() as Any
        }
    }

}

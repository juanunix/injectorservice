package com.juansanz.injectorservice

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A lightweight ServiceProvider for dependency injection.
 */
class ServiceProvider {
    private val mappings: HashMap<KType, Any> = HashMap()

    /**
     * Registers an instance with a specific type.
     * @param type The type to register the instance for.
     * @param instance The instance to be registered.
     */
    fun add(type: KType, instance: Any) {
        mappings[type] = instance
    }

    /**
     * Retrieves a registered service of the specified type.
     * @return The requested service instance.
     */
    inline fun <reified T : Any> getService(): T {
        return getService(typeOf<T>().classifier as KClass<*>) as T
    }

    /**
     * Retrieves a service by its target type.
     * @param targetType The class type of the desired service.
     * @return The requested service instance or null if unavailable.
     */
    fun getService(targetType: KClass<*>): Any? {
        return createInstance(targetType)
    }

    private fun createInstance(targetType: KClass<*>): Any {
        for (ctor in targetType.constructors) {
            val arguments = canConstruct(ctor.parameters)
            if (arguments != null) {
                return ctor.call(*arguments.toTypedArray())
            }
        }
        throw Exception("Could not find constructor with available arguments")
    }

    /*private fun canConstruct(parameters: List<KParameter>): List<Any?>? {
        val result = mutableListOf<Any?>()
        for (parameter in parameters) {
            val argument = mappings[parameter.type] ?: run {
                val instance = createInstance(parameter.type.classifier as KClass<*>)
                result.add(instance)
                instance
            }
            result.add(argument)
        }
        return result
    }*/

    private fun canConstruct(parameters: List<KParameter>): List<Any?>? {
        val result = mutableListOf<Any?>()
        for (parameter in parameters) {
            val argument = mappings[parameter.type] ?: run {
                val classifier = parameter.type.classifier
                if (classifier is KClass<*>) {
                    createInstance(classifier)
                } else {
                    return null
                }
            }
            result.add(argument)
        }
        return result
    }

}
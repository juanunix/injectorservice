package com.juansanz.injectorservice



import org.junit.Test
import kotlin.reflect.typeOf
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServiceProviderTest {

    @Test
    fun `test service registration and retrieval`() {
        val serviceProvider = ServiceProvider()
        serviceProvider.add(typeOf<String>(), "Hello, ServiceProvider!")

        val result: String = serviceProvider.getService()

        assertNotNull(result)
        assertTrue(result == "Hello, ServiceProvider!")
    }

    @Test
    fun `test service creation via constructor`() {
        class ExampleService(val dependency: String)

        val serviceProvider = ServiceProvider()
        serviceProvider.add(typeOf<String>(), "Dependency")

        val result = serviceProvider.getService(ExampleService::class) as ExampleService

        assertNotNull(result)
        assertTrue(result.dependency == "Dependency")
    }

    @Test
    fun `test multiple services of different types`() {
        class ServiceA
        class ServiceB

        val serviceProvider = ServiceProvider()
        serviceProvider.add(typeOf<ServiceA>(), ServiceA())
        serviceProvider.add(typeOf<ServiceB>(), ServiceB())

        val serviceA = serviceProvider.getService<ServiceA>()
        val serviceB = serviceProvider.getService<ServiceB>()

        assertNotNull(serviceA)
        assertNotNull(serviceB)
    }

    @Test
    fun `test nested dependencies`() {
        class DependencyA
        class DependencyB(val a: DependencyA)
        class ExampleService(val b: DependencyB)

        val serviceProvider = ServiceProvider()
        serviceProvider.add(typeOf<DependencyA>(), DependencyA())

        val exampleService = serviceProvider.getService(ExampleService::class) as ExampleService

        assertNotNull(exampleService)
        assertNotNull(exampleService.b)
        assertNotNull(exampleService.b.a)
    }

    @Test
    fun `test missing dependency throws exception`() {
        class ExampleService(val missingDependency: String)

        val serviceProvider = ServiceProvider()

        try {
            serviceProvider.getService(ExampleService::class)
        } catch (e: Exception) {
            assertTrue(e.message?.contains("Callable expects 1 arguments, but 2 were provided.") == true)
        }
    }


}


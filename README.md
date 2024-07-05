# Monolith Domain Splitter Library

The `monolith-domain-splitter` library is designed to help you organize and monitor different domains within your
monolithic application using Datadog. This guide will help you integrate the library into your project. 

**Note: This
project requires the Datadog agent to be configured** for full functionality. While adding domain and team annotations to
logs may work without Datadog, it has not been tested in such scenarios.

## Step-by-Step Approach to Using Domain Annotations

### 1. Define Domains and Teams

You will use enums to represent different domains and teams within your application. The library will help you tag and
monitor these domains effectively.

```kotlin
enum class DomainValueImpl(
    override val team: Team,
) : DomainValue {
    PROJECT(TeamImpl.LIONS),
    FILE(TeamImpl.SNAILS),
}

enum class TeamImpl : Team {
    LIONS,
    SNAILS,
}
```

### 2. Add Dependencies

Add the library and opentracing dependencies

If you use gradle add to your `build.gradle.kts` file:
```kotlin
dependencies {
    api("io.opentracing:opentracing-api:0.33.0")
    api("io.opentracing:opentracing-util:0.33.0")
    implementation("io.github.feddena.monolith.splitter:monolith-domain-splitter:0.0.2")
}
```

### 3. Annotate Your Application Class

Annotate your main application class to include the monolith-domain-splitter package for component scanning.

```kotlin
@SpringBootApplication(scanBasePackages = ["your.app.pkg", "io.github.feddena.monolith.splitter"])
class StorageApplication

fun main(args: Array<String>) {
    runApplication<StorageApplication>(*args)
}
```

### 4. Annotate Your Classes and Methods

Use the @Domain annotation to mark your classes and methods with specific domains.

```kotlin
@Domain("FILE")
class FileEndpoint {
// Your endpoint logic
}
```

### 5. Handle Unsupported Cases

For cases that cannot be annotated directly, use DomainTagsService to wrap the logic.

```kotlin
fun executeNotSupportedByAnnotationsLogic() {
    domainTagsService.invoke(domain) { executeLogic() }
}
```

### 6. Configure the Library

Ensure you have the necessary configuration classes to set up the domain and team management.
Here are the configurations provided in your project:

```kotlin
package io.github.feddena.monolith.splitter

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfiguration {

    @Bean
    fun domainTagsService(): DomainTagsService {
        return DomainTagsServiceImpl()
    }

    @Bean
    fun domainTraceInterceptorConfiguration(): DomainTraceInterceptorConfiguration {
        return DomainTraceInterceptorConfigurationImpl()
    }

    @Bean
    fun domainValue(): DomainValue {
        return DomainValueImpl()
    }
}
```

```kotlin
@Configuration
class DomainTraceInterceptorConfigurationImpl : DomainTraceInterceptorConfiguration {
    // name of the service in datadog if you wish to override it
    override fun getServicesToOverride(): Set<String> {
        return setOf("real-service-to-override")
    }

    // prefix that will be used to create artificial services names 
    // artificialServiceName = getServiceNamePrefix() + team
    override fun getServiceNamePrefix(): String {
        return "monolith-"
    }
}
```

WebMvcConfigurer.kt

```kotlin
@Component
class WebMvcConfigurer(
    private val domainHandlerInterceptor: DomainHandlerInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(domainHandlerInterceptor)
    }
}
```

### 7. Enable scan of library beans

```kotlin
package your.app.pkg

@SpringBootApplication(scanBasePackages = ["your.app.pkg", "io.github.feddena.monolith.splitter"])
class YourApplication

fun main(args: Array<String>) {
    runApplication<YourApplication>(*args)
}
```

### 8. Monitor with Datadog

Use artificial service filters for monitors, dashboards, and APM traces filtering in Datadog to keep track of different
domains and teams. Make sure your project has the Datadog agent configured for full functionality.

## Monitoring Configuration
Use the provided configuration files to set up domain tracing and monitoring effectively.

By following these steps, you can integrate and use the monolith-domain-splitter library in your project to achieve
improved domain monitoring and management using Datadog. This setup ensures that each domain in your monolithic
application is well-organized and tracked for better observability and accountability.


package com.monolith.splitter

/**
 * Allows adding tags informing about team owning code (and responsible for handling errors)
 * to DataDog spans and logs.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Domain(val value: String)
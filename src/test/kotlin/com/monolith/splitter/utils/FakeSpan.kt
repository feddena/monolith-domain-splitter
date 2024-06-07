package com.monolith.splitter.utils

import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.tag.Tag

class FakeSpan : Span {
    private val _tags = mutableMapOf<String, Any?>()

    val tags: Map<String, Any?> = _tags

    fun getTag(name: String): Any? = _tags[name]

    override fun setTag(key: String, value: String): Span {
        _tags[key] = value
        return this
    }

    override fun setTag(key: String, value: Boolean): Span {
        _tags[key] = value
        return this
    }

    override fun setTag(key: String, value: Number?): Span {
        _tags[key] = value
        return this
    }

    override fun <T : Any?> setTag(key: Tag<T>?, value: T): Span = TODO("Not yet implemented")

    override fun context(): SpanContext = TODO("Not yet implemented")

    override fun log(p0: MutableMap<String, *>?): Span = TODO("Not yet implemented")

    override fun log(p0: Long, p1: MutableMap<String, *>?): Span = TODO("Not yet implemented")

    override fun log(p0: String?): Span = TODO("Not yet implemented")

    override fun log(p0: Long, p1: String?): Span = TODO("Not yet implemented")

    override fun setBaggageItem(p0: String?, p1: String?): Span = TODO("Not yet implemented")

    override fun getBaggageItem(p0: String?): String = TODO("Not yet implemented")

    override fun setOperationName(p0: String?): Span = TODO("Not yet implemented")

    override fun finish() {
    }

    override fun finish(finishMicros: Long) {
    }
}

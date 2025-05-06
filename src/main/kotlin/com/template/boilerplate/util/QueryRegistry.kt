package com.template.boilerplate.util

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class QueryRegistry {
    private val queries = ConcurrentHashMap<String, QuerySpec>()
    private val dynamicQueryCache =
        Caffeine
            .newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(1, java.util.concurrent.TimeUnit.HOURS)
            .build<String, String>()

    fun register(
        name: String,
        spec: QuerySpec,
    ) {
        queries[name] = spec
    }

    fun get(name: String): QuerySpec =
        queries[name]
            ?: throw IllegalArgumentException("Query $name not found")

    fun getDynamicSql(
        baseQueryName: String,
        key: String,
        sqlGenerator: () -> String,
    ): String =
        dynamicQueryCache.get("$baseQueryName:$key") {
            sqlGenerator()
        }
}

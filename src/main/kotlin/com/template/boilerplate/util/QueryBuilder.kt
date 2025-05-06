package com.template.boilerplate.util

import io.r2dbc.spi.Row
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class QueryBuilder(
    private val template: R2dbcEntityTemplate,
) {
    private val logger = LoggerFactory.getLogger(QueryBuilder::class.java)

    suspend fun <T> select(
        spec: QuerySpec,
        mapper: (Row) -> T,
    ): List<T> {
        val inlinedSql = spec.sqlWithParamsInlined()
        logger.debug("Executing command sqlWithParamsInlined: $inlinedSql")

        return template.databaseClient
            .sql(inlinedSql) // .sql(spec.sql)
            // .apply { spec.params.forEach { (key, value) -> bind(key, value) } } // 바인딩 안됨
            // .bind(0, spec.params["category"]) // OK
            .map { row, _ -> mapper(row) }
            .all()
            .collectList()
            .awaitSingle()
    }

    suspend fun <T> selectOne(
        spec: QuerySpec,
        mapper: (Row) -> T,
    ): T? = select(spec, mapper).firstOrNull()
}

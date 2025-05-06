package com.template.boilerplate.util

import com.template.boilerplate.model.dto.ProductDto
import io.r2dbc.spi.Row
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    suspend fun insertProduct(product: ProductDto): ProductDto {
        val insertSql =
            """
            INSERT INTO PRODUCT (brand, category, price)
            VALUES (:brand, :category, :price)
            """.trimIndent()

        val affectedRows =
            template.databaseClient
                .sql(insertSql)
                .bind("brand", product.brand)
                .bind("category", product.category)
                .bind("price", product.price)
                .fetch()
                .rowsUpdated()
                .awaitSingle()

        require(affectedRows.toInt() == 1) { "Insert failed!" }

        val id =
            template.databaseClient
                .sql("SELECT MAX(id) AS id FROM PRODUCT")
                .map { row, _ -> row.get("id", java.lang.Long::class.java)!! }
                .one()
                .awaitSingle()

        val inserted = product.copy(id = id.toLong())
        logger.info("상품 등록 성공: $inserted")
        return inserted
    }

    @Transactional
    suspend fun updateProduct(product: ProductDto): ProductDto {
        val updateSql =
            """
            UPDATE PRODUCT
            SET brand = :brand,
                category = :category,
                price = :price
            WHERE id = :id
            """.trimIndent()

        val affectedRows =
            template.databaseClient
                .sql(updateSql)
                .bind("id", product.id)
                .bind("brand", product.brand)
                .bind("category", product.category)
                .bind("price", product.price)
                .fetch()
                .rowsUpdated()
                .awaitSingle()

        require(affectedRows.toInt() == 1) { "Update failed! (id=${product.id})" }

        val id =
            template.databaseClient
                .sql("SELECT MAX(id) AS id FROM PRODUCT")
                .map { row, _ -> row.get("id", java.lang.Long::class.java)!! }
                .one()
                .awaitSingle()

        val updated = product.copy(id = id.toLong())
        logger.info("상품 업데이트 성공: $updated")
        return updated
    }

    @Transactional
    suspend fun deleteProduct(product: ProductDto): Boolean {
        val updateSql =
            """
            DELETE FROM PRODUCT WHERE id = :id
            """.trimIndent()

        val affectedRows =
            template.databaseClient
                .sql(updateSql)
                .bind("id", product.id)
                .fetch()
                .rowsUpdated()
                .awaitSingle()

        require(affectedRows.toInt() == 1) { "Delete failed! (id=${product.id})" }

        return affectedRows.toInt() == 1
    }
}

package com.template.boilerplate.repository

import com.template.boilerplate.exception.QueryExecutionException
import com.template.boilerplate.model.dto.ProductDto
import com.template.boilerplate.util.QueryBuilder
import com.template.boilerplate.util.QueryCommand
import com.template.boilerplate.util.toDto
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException

data class ProductByCategoryCommand(
    val category: String,
) : QueryCommand<List<ProductDto>> {
    private val logger = LoggerFactory.getLogger(ProductByCategoryCommand::class.java)

    override suspend fun execute(queryBuilder: QueryBuilder): List<ProductDto> {
        val spec = ProductQueries.byCategory(category)
        logger.debug("Executing command query: ${spec.sql} and params: ${spec.params}")

        try {
            return queryBuilder.select(spec) { row ->
                logger.trace("Mapping row for category: $category")
                row.toDto<ProductDto>()
            }
        } catch (e: DataAccessException) {
            logger.error(
                "Query execution failed: ${e.message}, query: ${spec.sql}, params: ${spec.params}",
                e,
            )
            throw QueryExecutionException("Query execution failed: ${e.message}", e)
        } catch (e: IllegalArgumentException) {
            logger.error(
                "Mapping error: ${e.message}, query: ${spec.sql}, params: ${spec.params}",
                e,
            )
            throw QueryExecutionException("Mapping error: ${e.message}", e)
        } catch (e: Exception) {
            logger.error(
                "Unexpected error: ${e.message}, query: ${spec.sql}, params: ${spec.params}",
                e,
            )
            throw QueryExecutionException(
                "Unexpected error: ${e.message}",
                e,
            )
        }
    }
}

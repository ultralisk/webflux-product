package com.template.boilerplate.service

import com.template.boilerplate.model.dto.ProductDto
import com.template.boilerplate.repository.ProductQueries
import com.template.boilerplate.util.QueryBuilder
import com.template.boilerplate.util.QueryCommand
import com.template.boilerplate.util.toDto
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val queryBuilder: QueryBuilder,
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    suspend fun <R> execute(command: QueryCommand<R>): R {
        logger.debug("Executing command: ${command::class.simpleName}")
        return command.execute(queryBuilder)
    }

    @Cacheable(value = ["products"], key = "#category")
    suspend fun getProductByCategory(category: String): List<ProductDto> {
        require(category.isNotBlank()) { "Category cannot be blank" }
        return queryBuilder.select(
            spec = ProductQueries.byCategory(category),
            mapper = { row -> row.toDto<ProductDto>() },
        )
    }
}

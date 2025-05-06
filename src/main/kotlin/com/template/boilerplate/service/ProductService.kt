package com.template.boilerplate.service

import com.template.boilerplate.model.dto.ProductDto
import com.template.boilerplate.repository.ProductQueries
import com.template.boilerplate.util.QueryBuilder
import com.template.boilerplate.util.QueryCommand
import com.template.boilerplate.util.toDto
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigDecimal

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

    private fun validateProduct(product: ProductDto) {
        require(product != null) { "Product cannot be null" }
        // require(product.id > 0L) { "ID는 0보다 큰 값이어야 합니다" }
        require(product.brand.isNotBlank()) { "브랜드는 필수 입력값입니다" }
        require(product.category.isNotBlank()) { "카테고리는 필수 입력값입니다" }
        require(product.price > BigDecimal.ZERO) { "가격은 0보다 커야 합니다" }
    }

    suspend fun insertProduct(product: ProductDto): ProductDto {
        validateProduct(product)

        logger.debug("insertProduct: $product")

        return queryBuilder.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductDto): ProductDto {
        validateProduct(product)
        require(product.id != null && product.id > 0) { "ID는 0보다 큰 값이어야 합니다" }

        logger.debug("updateProduct: $product")

        return queryBuilder.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductDto): Boolean {
        validateProduct(product)
        require(product.id != null && product.id > 0) { "ID는 0보다 큰 값이어야 합니다" }

        logger.debug("deleteProduct: $product")

        return queryBuilder.deleteProduct(product)
    }
}

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
        // require(product.id > 0L) { "ID�� 0���� ū ���̾�� �մϴ�" }
        require(product.brand.isNotBlank()) { "�귣��� �ʼ� �Է°��Դϴ�" }
        require(product.category.isNotBlank()) { "ī�װ��� �ʼ� �Է°��Դϴ�" }
        require(product.price > BigDecimal.ZERO) { "������ 0���� Ŀ�� �մϴ�" }
    }

    suspend fun insertProduct(product: ProductDto): ProductDto {
        validateProduct(product)

        logger.debug("insertProduct: $product")

        return queryBuilder.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductDto): ProductDto {
        validateProduct(product)
        require(product.id != null && product.id > 0) { "ID�� 0���� ū ���̾�� �մϴ�" }

        logger.debug("updateProduct: $product")

        return queryBuilder.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductDto): Boolean {
        validateProduct(product)
        require(product.id != null && product.id > 0) { "ID�� 0���� ū ���̾�� �մϴ�" }

        logger.debug("deleteProduct: $product")

        return queryBuilder.deleteProduct(product)
    }
}

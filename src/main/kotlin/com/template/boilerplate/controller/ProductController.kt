package com.template.boilerplate.controller

import com.template.boilerplate.common.response.ApiResponse
import com.template.boilerplate.service.ProductService
import com.template.boilerplate.util.ProductDataLoader
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class ProductController(
    private val service: ProductService,
) {
    suspend fun getProduct(request: ServerRequest): ServerResponse {
        val categoryName =
            request
                .queryParam("category")
                .orElseThrow { IllegalArgumentException("Query parameter 'category' is required") }

        val product = service.getProductByCategory(categoryName)

        val responseData =
            ApiResponse(
                data = product,
            )
        return ServerResponse.ok().bodyValueAndAwait(responseData)
    }

    suspend fun findCheapestCategoryBrand(request: ServerRequest): ServerResponse {
        val categories =
            ProductDataLoader.findCheapestCategoryBrand()

        val responseData =
            ApiResponse(
                data = categories,
            )
        return ServerResponse.ok().bodyValueAndAwait(responseData)
    }

    suspend fun findCheapestBrand(request: ServerRequest): ServerResponse {
        val product = ProductDataLoader.findCheapestBrand()

        val responseData =
            ApiResponse(
                data = product,
            )
        return ServerResponse.ok().bodyValueAndAwait(responseData)
    }

    suspend fun findCategoryPriceBrand(request: ServerRequest): ServerResponse {
        val categoryName =
            request
                .queryParam("category")
                .orElseThrow { IllegalArgumentException("Query parameter 'category' is required") }

        val product = ProductDataLoader.findCategoryPriceBrand(categoryName)

        val responseData =
            ApiResponse(
                data = product,
            )
        return ServerResponse.ok().bodyValueAndAwait(responseData)
    }
}

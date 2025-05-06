package com.template.boilerplate.repository

import com.template.boilerplate.util.QueryRegistry
import com.template.boilerplate.util.QuerySpec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// object ProductQueries {
//    fun byCategory(category: String) =
//
//        QuerySpec(
//            // sql = "SELECT * FROM product WHERE category = ?", // OK
//            sql = "SELECT * FROM product WHERE category = :category",
//            params = mapOf("category" to category),
//            paramOrder = listOf("category"),
//        )
// }

@Component
class ProductQueries(
    private val queryRegistry: QueryRegistry,
) {
    private val logger = LoggerFactory.getLogger(ProductQueries::class.java)

    init {
        queryRegistry.register(
            "byCategory",
            QuerySpec(
                sql = "SELECT * FROM product WHERE category = :category",
                paramOrder = listOf("category"),
            ),
        )
    }

    companion object {
        @JvmStatic
        fun byCategory(category: String) =

            QuerySpec(
                sql = "SELECT * FROM product WHERE category = :category",
                params = mapOf("category" to category),
                paramOrder = listOf("category"),
            )
    }
}

package com.template.boilerplate.model.dto

import java.math.BigDecimal

data class ProductDto(
    val id: Long,
    val brand: String,
    val category: String,
    val price: BigDecimal,
)

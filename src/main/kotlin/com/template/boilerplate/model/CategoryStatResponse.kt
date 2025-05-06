package com.template.boilerplate.model

data class BrandCategoryPriceResponse(
    val brand: String,
    val categories: List<CategoryPrice>,
    val total: String,
)

data class CategoryPrice(
    val category: String,
    val price: String,
)

data class CategoryMinData(
    val formattedCategories: List<Map<String, String>>,
    val total: String,
)

data class BrandMinData(
    val brand: String,
    val total: String,
    val categories: List<Map<String, String>>,
)

data class CategoryPriceData(
    val category: String,
    val min: List<BrandPrice>,
    val max: List<BrandPrice>,
)

data class BrandPrice(
    val brand: String,
    val price: String,
)

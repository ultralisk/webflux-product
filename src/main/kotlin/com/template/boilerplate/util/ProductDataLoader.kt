package com.template.boilerplate.util

import com.template.boilerplate.model.BrandMinData
import com.template.boilerplate.model.BrandPrice
import com.template.boilerplate.model.CategoryMinData
import com.template.boilerplate.model.CategoryPriceData
import com.template.boilerplate.model.dto.ProductDto
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.forEach

@Component
class ProductDataLoader(
    private val queryBuilder: QueryBuilder,
) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(ProductDataLoader::class.java)

    companion object {
        @Volatile private var _allProduct = listOf<ProductDto>()
        val allProduct: List<ProductDto> get() = _allProduct

        // --------------------------------

        // 1) - 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회
        private val _categoryMinCache = AtomicReference<CategoryMinData>()
        val categoryMinCache: CategoryMinData get() = _categoryMinCache.get()

        fun findCheapestCategoryBrand() = categoryMinCache

        // --------------------------------

        // 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회

        private val _brandMinCache = AtomicReference<BrandMinData>()
        val brandMinCache: BrandMinData get() = _brandMinCache.get()

        fun findCheapestBrand() = brandMinCache

        // --------------------------------

        // 3) - 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
        private val _categoryPriceCache = ConcurrentHashMap<String, CategoryPriceData>()
        val categoryPriceCache: Map<String, CategoryPriceData> get() = _categoryPriceCache

        fun findCategoryPriceBrand(category: String): CategoryPriceData =
            _categoryPriceCache[category] ?: run {
                val calculated = calculateCategoryPriceData(category)
                _categoryPriceCache[category] = calculated
                calculated
            }

        private fun calculateCategoryPriceData(category: String): CategoryPriceData {
            // 해당 카테고리 상품 필터링
            val filteredProducts = allProduct.filter { it.category == category }

            if (filteredProducts.isEmpty()) {
                return CategoryPriceData(category, emptyList(), emptyList())
            }

            // 최저/최고 가격 계산
            val minPrice = filteredProducts.minOf { it.price.toInt() }
            val maxPrice = filteredProducts.maxOf { it.price.toInt() }

            val minBrands =
                filteredProducts
                    .filter { it.price.toInt() == minPrice }
                    .distinctBy { it.brand }
                    .map { BrandPrice(it.brand, "%,d".format(it.price.toInt())) }

            val maxBrands =
                filteredProducts
                    .filter { it.price.toInt() == maxPrice }
                    .distinctBy { it.brand }
                    .map { BrandPrice(it.brand, "%,d".format(it.price.toInt())) }

            return CategoryPriceData(
                category = category,
                min = minBrands,
                max = maxBrands,
            )
        }
    }

    override fun run(args: ApplicationArguments?) {
        logger.info("상품 데이터 로드 시작")
        try {
            runBlocking {
                // 1. 데이터 조회
                val products =
                    queryBuilder.select(
                        QuerySpec(sql = "SELECT * FROM product"),
                    ) { row -> row.toDto<ProductDto>() }

                // 2. 데이터 갱신
                _allProduct = products

                refreshCache()
            }
            logger.info(
                """
                |로드 완료
                |- 전체 상품: ${allProduct.size}개
                """.trimMargin(),
            )
        } catch (e: Exception) {
            logger.error("데이터 로드 실패: ${e.stackTraceToString()}")
        }
    }

    @Scheduled(fixedRate = 300000) // 5분
    fun scheduleCacheRefresh() {
        runBlocking {
            logger.info("--- refreshCache ---")

            refreshCache()
        }
    }

    fun refreshCache() {
        _categoryMinCache.set(calculateCategoryMinData())
        _brandMinCache.set(calculateBrandMinData())
    }

    // 1) - 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회 로직
    // private fun calculateCategoryMinData(): Pair<List<Map<String, String>>, String> {
    private fun calculateCategoryMinData(): CategoryMinData {
        val categoryMinInfo = mutableMapOf<String, Pair<String, Int>>()
        var total = 0

        _allProduct.groupBy { it.category }.forEach { (category, prods) ->
            val minPrice = prods.minOf { it.price.toInt() }
            val minBrands =
                prods
                    .filter { it.price.toInt() == minPrice }
                    .map { it.brand }
                    .distinct()
            val selectedBrand = minBrands.firstOrNull() ?: "N/A"
            categoryMinInfo[category] = selectedBrand to minPrice
            total += minPrice
        }

        val formattedCategories =
            categoryMinInfo.map { (category, data) ->
                mapOf(
                    "category" to category,
                    "brand" to data.first,
                    "price" to "%,d".format(data.second),
                )
            }

        // return Pair(formattedCategories, "%,d".format(total))
        return CategoryMinData(formattedCategories, "%,d".format(total))
    }

    // 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
    private fun calculateBrandMinData(): BrandMinData {
        // 브랜드별: {브랜드 → {카테고리 → 최저가격}}
        val brandCategoryMin: Map<String, Map<String, Int>> =
            _allProduct
                .groupBy { it.brand }
                .mapValues { (_, prods) ->
                    prods
                        .groupBy { it.category }
                        .mapValues { (_, ps) -> ps.minOf { it.price.toInt() } }
                }

        // 브랜드별 총합 계산
        val brandTotals: Map<String, Int> =
            brandCategoryMin.mapValues { (_, categories) ->
                categories.values.sum()
            }

        // 최저가 브랜드 선정
        val cheapestEntry =
            brandTotals.minByOrNull { it.value }
                ?: throw IllegalStateException("No brand data available")

        val formattedCategories =
            brandCategoryMin[cheapestEntry.key]!!.map { (category, price) ->
                mapOf(
                    "category" to category,
                    "price" to "%,d".format(price),
                )
            }

        return BrandMinData(
            brand = cheapestEntry.key,
            total = "%,d".format(cheapestEntry.value),
            categories = formattedCategories,
        )
    }
}

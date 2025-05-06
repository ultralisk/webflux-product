package com.template.boilerplate.model.dto

import com.template.boilerplate.util.RowMapper
import com.template.boilerplate.util.toDto
import io.r2dbc.spi.Row

/*
object ProductMapper : RowMapper<ProductDto> {
    override fun map(row: Row): ProductDto =
        ProductDto(
            id =
                row.get("id", Long::class.javaObjectType)
                    ?: throw QueryExecutionException("ID is null"),
            brand =
                row.get("brand", String::class.java)
                    ?: throw QueryExecutionException("Brand is null"),
            category =
                row.get("category", String::class.java)
                    ?: throw QueryExecutionException("Category is null"),
            price =
                row.get("price", BigDecimal::class.java).toInt()
                    ?: throw QueryExecutionException("Price is null"),
        )
}
*/

object ProductMapperEx : RowMapper<ProductDto> {
    override fun map(row: Row): ProductDto = row.toDto()
}

package com.template.boilerplate.util

import io.r2dbc.spi.Row
import org.slf4j.LoggerFactory
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

interface RowMapper<T> {
    fun map(row: Row): T
}

/*
inline fun <reified T : Any> Row.toDto(): T {
    val constructor =
        T::class.primaryConstructor
            ?: throw IllegalArgumentException(
                "${T::class.simpleName} must have a primary constructor",
            )

    val args =
        constructor.parameters.associate { param ->
            val type = param.type.jvmErasure.javaObjectType
            param to get(param.name!!, type)
        }

    return constructor.callBy(args)
}
*/
inline fun <reified T : Any> Row.toDto(): T {
    val logger = LoggerFactory.getLogger("RowToDto")
    logger.trace("Available columns: ${this.metadata.columnMetadatas.map { it.name }}")
    val constructor =
        T::class.primaryConstructor
            ?: throw IllegalArgumentException(
                "${T::class.simpleName} must have a primary constructor",
            )
    val args =
        constructor.parameters.associate { param ->
            logger.trace("Mapping column: ${param.name}, type: ${param.type.jvmErasure}")
            val type = param.type.jvmErasure.javaObjectType
            val value =
                try {
                    get(param.name!!, type)
                        ?: throw IllegalArgumentException("Column ${param.name} is null or missing")
                } catch (e: Exception) {
                    throw IllegalArgumentException(
                        "Failed to map column ${param.name} to type $type",
                        e,
                    )
                }
            param to value
        }
    return try {
        constructor.callBy(args)
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to create ${T::class.simpleName} from row", e)
    }
}

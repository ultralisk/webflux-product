package com.template.boilerplate.util

data class QuerySpec(
    val sql: String,
    val params: Map<String, Any> = emptyMap(),
    val paramOrder: List<String> = params.keys.toList(),
)

fun QuerySpec.sqlWithParamsInlined(): String {
    var sqlWithParams = sql
    params.forEach { (key, value) ->
        val paramValue =
            when (value) {
                is String -> "'${value.replace("'", "''")}'"
                null -> "NULL"
                else -> value.toString()
            }
        sqlWithParams = sqlWithParams.replace(":$key", paramValue)
    }
    return sqlWithParams
}

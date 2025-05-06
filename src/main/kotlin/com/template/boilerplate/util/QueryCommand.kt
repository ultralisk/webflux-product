package com.template.boilerplate.util

interface QueryCommand<R> {
    suspend fun execute(queryBuilder: QueryBuilder): R
}

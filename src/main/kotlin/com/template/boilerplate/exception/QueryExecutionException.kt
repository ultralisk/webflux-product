package com.template.boilerplate.exception

class QueryExecutionException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

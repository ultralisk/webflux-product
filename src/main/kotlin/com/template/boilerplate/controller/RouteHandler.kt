package com.template.boilerplate.controller

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouteHandler {
    @Bean
    fun endpointRouter(
        demoController: DemoController,
        errorController: ErrorController,
        productController: ProductController,
    ) = coRouter {
        "/api".nest {
            GET("/hello", demoController::handleHello)
            GET("/hello2", demoController::handleHelloByProblemDetail)

//            GET("/info", demoController::handleInfo)
//            POST("/info", demoController::handleInfo)
            "/info".nest {
                method(HttpMethod.GET, demoController::handleInfo)
                method(HttpMethod.POST, demoController::handleInfo)
            }

            "/info2".nest {
                method(HttpMethod.GET, demoController::handleInfo2)
                method(HttpMethod.POST, demoController::handleInfo2)
            }

            GET("/info3", demoController::handleInfo3)

            GET("/monotask", demoController::handleMonotask)
        }

        GET("/error", errorController::handleError)

        // User
        "/product".nest {
            GET("/getProduct", productController::getProduct)

            GET("/findCheapestCategoryBrand", productController::findCheapestCategoryBrand)
            GET("/findCheapestBrand", productController::findCheapestBrand)
            GET("/findCategoryPriceBrand", productController::findCategoryPriceBrand)

            POST("/insertProduct", productController::insertProduct)
            POST("/updateProduct", productController::updateProduct)
            POST("/deleteProduct", productController::deleteProduct)
        }
    }
}

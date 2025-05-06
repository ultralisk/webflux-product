## RouteHandler
Webflux기반으로, coRouter 통해 실제 Handler를 매핑함(MVC의 Controller가 아님.)

### 설정
- Netty의 응답 속도를 EventLoopGroup 전달할 스레드 개수를 pc에 개수를 조정할 수 있도록 함.
- 사용하지 않는 bean의 제거(`org.springframework.boot.autoconfigure.AutoConfiguration.imports`)
- 기본 구성은 `application-local.yml`을 따름.

## 2. h2 관련 작업
- Webflux기반이어서, MVC를 기반으로하는 H2의 API들을 사용할 수 없어, SQL관련 작업은 외부에서 작업해서 생성함.
- R2DBC기반에서 파라메터 바인딩에 오류가 있는지 적용이 되지 않아, Injection의 위험이 있는 것은 알지만 replace하는 것으로 작업
- `util/Query~` 클래스 참조 

## 캐싱
- 어플리케이션 로드시 데이터를 캐싱
- 이 캐싱데이터를 가지고, 노출 데이터를 생성
- 브랜드/카테고리의 최소가격을 얻어오기 위하여 Min/Max Heap 구조를 활용
- `util/ProductDataLoader` 참고

## 에러처리와 Response의 구조
- `filter/GlobalExceptionHandler`을 통해, 모든 에러 처리에 대해서, 아래 Response구조를 따름.
- `ApiResponse` 를 생성하고, ProblemDetail을 따름.
- response 데이터는 `data`필드를 통해 노출함.

## API
### 1) - 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회
- [GET] http://localhost:9218/product/findCheapestCategoryBrand
- Response
```json
{
    "status": 200,
    "timestamp": "2025-05-06T18:06:59.194544300Z",
    "message": "Success",
    "data": {
        "formattedCategories": [
            {
                "category": "상의",
                "brand": "C",
                "price": "10,000"
            },
            {
                "category": "아우터",
                "brand": "E",
                "price": "5,000"
            },
            {
                "category": "바지",
                "brand": "D",
                "price": "3,000"
            },
            {
                "category": "스니커즈",
                "brand": "A",
                "price": "9,000"
            },
            {
                "category": "가방",
                "brand": "A",
                "price": "2,000"
            },
            {
                "category": "모자",
                "brand": "D",
                "price": "1,500"
            },
            {
                "category": "양말",
                "brand": "I",
                "price": "1,700"
            },
            {
                "category": "액세서리",
                "brand": "F",
                "price": "1,900"
            }
        ],
        "total": "34,100"
    }
}
```

### 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
- [GET] http://localhost:9218/product/findCheapestBrand
- Response
```json
{
    "status": 200,
    "timestamp": "2025-05-06T18:08:03.560929400Z",
    "message": "Success",
    "data": {
        "brand": "D",
        "total": "36,100",
        "categories": [
            {
                "category": "상의",
                "price": "10,100"
            },
            {
                "category": "아우터",
                "price": "5,100"
            },
            {
                "category": "바지",
                "price": "3,000"
            },
            {
                "category": "스니커즈",
                "price": "9,500"
            },
            {
                "category": "가방",
                "price": "2,500"
            },
            {
                "category": "모자",
                "price": "1,500"
            },
            {
                "category": "양말",
                "price": "2,400"
            },
            {
                "category": "액세서리",
                "price": "2,000"
            }
        ]
    }
}
```

### 3) - 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
- [GET] http://localhost:9218/product/findCategoryPriceBrand?category=상의
- Response
```json
{
    "status": 200,
    "timestamp": "2025-05-06T18:08:23.734368Z",
    "message": "Success",
    "data": {
        "category": "상의",
        "min": [
            {
                "brand": "C",
                "price": "10,000"
            }
        ],
        "max": [
            {
                "brand": "I",
                "price": "11,400"
            }
        ]
    }
}
```

### 4) Insert/Update/Delete
[POST] http://localhost:9218/product/insertProduct
```json
{
    "id": 225,
    "brand": "J",
    "category": "상의",
    "price": 11201
}
```
[POST] http://localhost:9218/product/updateProduct
```json
{
    "id": 225,
    "brand": "J",
    "category": "상의",
    "price": 11201
}
```

[POST] http://localhost:9218/product/deleteProduct
```json
{
    "id": 225,
    "brand": "J",
    "category": "상의",
    "price": 11201
}
```

- 구현의도: 어플리케이션 시작시 bulk로 data를 캐싱
- `util\ProductDataLoader`클래스내에서 5분마다 한번씩 갱신함.
- 개선필요점(500개단위로 끊어서 backgound에서 로딩하는 등의 처리 필요)
```kotlin
    @Scheduled(fixedRate = 300000) // 5분
    fun scheduleCacheRefresh() {
        runBlocking {
            logger.info("--- refreshCache ---")

            refreshCache()
        }
    }
```
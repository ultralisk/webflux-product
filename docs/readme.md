## RouteHandler
Webflux기반으로, coRouter 통해 실제 Handler를 매핑함(MVC의 Controller가 아님.)

### 설정
- Netty의 응답 속도를 EventLoopGroup 전달할 스레드 개수를 pc에 개수를 조정할 수 있도록 함.
- 사용하지 않는 bean의 제거(`org.springframework.boot.autoconfigure.AutoConfiguration.imports`)
- 기본 구성은 `application-local.yml`을 따름. 

## 2. h2 관련 작업
- Webflux기반이어서, MVC를 기반으로하는 H2의 API들을 사용할 수 없어, SQL관련 작업은 외부에서 작업해서 생성함.

## Response의 구조
- `ApiResponse` 를 생성하고, ProblemDetail을 따름.
- response 데이터는 `data`필드를 통해 노출함.

## API
- http://localhost:9218/product/findCheapestCategoryBrand
- http://localhost:9218/product/findCheapestBrand
- http://localhost:9218/product/findCategoryPriceBrand?category=%EC%83%81%EC%9D%98
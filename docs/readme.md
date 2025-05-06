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
- http://localhost:9218/product/findCheapestCategoryBrand

### 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
- http://localhost:9218/product/findCheapestBrand

### 3) - 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
- http://localhost:9218/product/findCategoryPriceBrand?category=상의
- 새로운 상품이 추가 되었을 때, data캐싱 을 다시 하려고 했지만 이부분은 구현하지 못함.
- 구현의도: 어플리케이션 시작시 bulk로 data를 캐싱하고, `insert/update/delete`시 이 캐시에 데이터를 갱신 
- 누락된 부분: `insert/update/delete`
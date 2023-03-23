package extend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * 
 * @author CZ-WGLEE-N1
 * 
 * 
 * 상속관계 매핑
	• 관계형 데이터베이스는 상속관계가 없다. 
	• 슈퍼타입 서브타입 관계라는 논리 모델링 기법이 객체상속과 유사하다.
	• 상속관계매핑: 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑
	
	• 슈퍼타입 서브타입 논리모델을 실제 물리모델로 구현하는 방법
		• 각각 테이블로 변환 -> 조인 전략
		• 통합 테이블로 변환 -> 단일 테이블 전략
		• 서브타입 테이블로 변환 -> 구현클래스마다 테이블 전략
 *
 *
 * 조인 전략 : 실무 추천, 부모 객체에 @Inheritance(strategy = InheritanceType.JOINED) 선언
	• 장점
		• 테이블 정규화 방식
		• 외래 키 참조 무결성 제약조건 활용가능
		• 저장공간 효율화
	• 단점
		• 조회 시 조인을 많이 사용, 성능저하
		• 조회 쿼리가 복잡함
		• 데이터 저장 시 INSERT SQL 2번 호출 
 *
 * 단일 테이블 전략 : 부모 객체에 @Inheritance(strategy = InheritanceType.SINGLE_TABLE) 선언
	• 장점
		• 조인이 필요없으므로 일반적으로 조회성능이 빠름
		• 조회쿼리가 단순함
	• 단점
		• 자식 엔티티가 매핑한 컬럼은 모두 null 허용
		• 단일 테이블에 모든것을 저장하므로 테이블이 커질 수 있다. 상황에 따라서 조회성능이 오히려 느려질 수 있다.
 *
 * 구현클래스마다 테이블 전략 : 실무 사용 X, @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 선언
	• 이전략은데이터베이스설계자와 ORM 전문가둘다추천X 
	• 장점
		• 서브타입을 명확하게 구분해서 처리할 때 효과적
		• not null 제약조건 사용가능
	• 단점
		• 여러자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL 필요) 
		• 자식테이블을 통합해서 쿼리하기 어려움
 */
public class JpaMainExtend {
	public static void main(String[] args) {
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();

		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		// 트랜잭션 시작
		tx.begin();
		
		try {
			
			/**
			 * 객체간 상속된 경우 부모와 자식 각각 inset SQL을 생성함.
			 */
			Movie movie = new Movie();
			movie.setDirector("aaaa");
			movie.setActor("bbbb");
			movie.setName("바람과함께사라지다");
			movie.setPrice(10000L);
			
			em.persist(movie);

			// 영속성 컨텍스트와 DB 동기화
			em.flush();
			// 영속성 컨텍스트 초기화
			em.clear();
			
			/**
			 * 부모와 자식 객체간 조인 SQL 생성함.
			 */
			Movie findMovie =  em.find(Movie.class, movie.getId());
			System.out.println("movie.name : " + findMovie.getName());
			
			/**
			 * 구현클래스마다 테이블 전략 설정 후 조회하는 경우
			 * ID 값이 서브타입 테이블 어디에 있는 모르기 때문에 union으로 서브타입 테이블을 전부 조회하여 데이터를 찾는다.
			 * 
			 */
//			Item findItem =  em.find(Item.class, movie.getId());
//			System.out.println("movie.name : " + findItem.getName());
			
			
			// 트랜잭션 종료
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			// EntityManager가 DB connection을 물고 있기 때문에 꼭 닫아줘야 한다.
			em.close();
		}
		
		emf.close();
	}
}

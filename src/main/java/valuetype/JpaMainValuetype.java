package valuetype;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * @author wglee
 *
 *
 	JPA의 데이터 타입 분류
	1.  엔티티 타입
		• @Entity로 정의하는 객체
		• 데이터가 변해도 식별자로 지속해서 추적 가능
		• 예) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능
	2. 값 타입
		• int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
		• 식별자가 없고 값만 있으므로 변경시 추적 불가
		• 예) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체
		
		2-1. 값 타입 분류
			가. 기본값 타입
			나. 자바 기본 타입(int, double) 
			다. 래퍼 클래스(Integer, Long) 
			라. String 
			마. 임베디드 타입(embedded type, 복합 값 타입) 
			바. 컬렉션 값 타입(collection value type)
			
				가. 기본값 타입
					• 예): String name, int age 
					• 생명주기를 엔티티의 의존
					• 예) 회원을 삭제하면 이름, 나이 필드도 함께 삭제
					• 값 타입은 공유하면 안된다.
					• 예) 회원 이름 변경시 다른 회원의 이름도 함께 변경되면 안됨

					참고: 자바의 기본 타입은 절대 공유가 안된다.
					• int, double 같은 기본 타입(primitive type)은 절대 공유가 안된다. 
					• 기본 타입은 항상 값을 복사함
					• Integer같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경이 안된다.


					
				나. 임베디드 타입(복합 값 타입)
					• 새로운 값 타입을 직접 정의할 수 있음
					• JPA는 임베디드 타입(embedded type)이라 함
					• 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 함
					• int, String과 같은 값 타입
	
					임베디드 타입 사용법
					• @Embeddable: 값 타입을 정의하는 곳에 표시
					• @Embedded: 값 타입을 사용하는 곳에 표시
					• 기본 생성자 필수

					임베디드 타입의 장점
					• 재사용
					• 높은 응집도
					• Period.isWork()처럼 해당 값 타입만 사용하는 의미 있는 메소드를 만들 수 있음
					• 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존함

					임베디드 타입과 테이블 매핑
					• 임베디드 타입은 엔티티의 값일 뿐이다. 
					• 임베디드 타입을 사용하기 전과 후에 매핑하는 테이블은 같다. 
					• 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능
					• 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음

	
	
	3. 값 타입과 불변 객체
	- 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 
	  따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다

		값 타입 공유 참조
		• 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함
		• 부작용(side effect) 발생


	4. 정리
		• 엔티티 타입의 특징
			• 식별자O 
			• 생명 주기 관리
			• 공유
		• 값 타입의 특징
			• 식별자X 
			• 생명 주기를 엔티티에 의존
			• 공유하지 않는 것이 안전(복사해서 사용) 
			• 불변 객체로 만드는 것이 안전



	• 값 타입은 정말 값 타입이라 판단될 때만 사용

	• 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안됨
	
	• 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티

 *
 */
public class JpaMainValuetype {
	
	public static void main(String[] args) {
		값타입_컬렉션_엔티티변환();
	}
	
	public static void 임베디드타입() {
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
			
			Member member = new Member();
			member.setName("user");
			member.setHomeAddress(new Address("seoul", "테헤란로","10123"));
			member.setWorkPeriod(new Period(LocalDateTime.now(), LocalDateTime.now()));
			
			em.persist(member);
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
	
	/**
	 * 
	 	값 타입 공유 참조
		• 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함
		• 부작용(side effect) 발생
		
		객체 타입의 한계
		• 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다. 
		• 문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입이다. 
		• 자바 기본 타입에 값을 대입하면 값을 복사한다. 
		• 객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다. 
		• 객체의 공유 참조는 피할 수 없다
		
		객체 타입의 한계
		• 기본타입
			int a = 10; 
			int b = a;//기본 타입은 값을 복사
			b = 4;
			
		• 객체타입	
			Address a = new Address(“Old”); 
			Address b = a; //객체 타입은 참조를 전달
			b. setCity(“New”
		
	 */
	public static void 값타입공유참조_부작용() {
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
			
			Address address = new Address("city", "street","zipcode");
			
			Member member1 = new Member();
			member1.setName("user");
			member1.setHomeAddress(address);
			em.persist(member1);
			
			Member member2 = new Member();
			member2.setName("user");
			member2.setHomeAddress(address);
			em.persist(member2);
			
			/**
			 * 값타입으로 설정된 참조객체를 공유해서 사용하는 경우, 의도와는 다르게 부작용이 발생한다.
			 * 의도 : member1 의 city 만 newCity로 변경하자.
			 * 결과 : member1, member2 모두 newCity로 바뀌게 된다.
			 * 
			 * 만약 애초에 member1, member2 모두를 변경하고자 한다면 값타입을 사용하지 말고 엔티티로 선언된 객체를 공유해야 한다.
			 */   
			member1.getHomeAddress().setCity("newCity");
			
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
	
	/**
	 * 
	 * 
	 	1. 값 타입 복사
		• 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험
		• 대신 값(인스턴스)를 복사해서 사용
			
			하지만
	 	• 객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다. 
		• 객체의 공유 참조는 피할 수 없다
	 	
	 	2. 불변 객체
		• 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단
		• 값 타입은 불변 객체(immutable object)로 설계해야함
		• 불변 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체
		• 생성자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 됨
		• 참고: Integer, String은 자바가 제공하는 대표적인 불변 객체
	 */
	public static void 값타입공유참조_해결방법() {
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
			
			Address address = new Address("city", "street","zipcode");
			
			Member member1 = new Member();
			member1.setName("user");
			member1.setHomeAddress(address);
			em.persist(member1);
			
			/**
			 * 아래와 같이 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
			 * 하지만 개발자가 만약 복사하지 않고 코딩했다면, 이를 막을 방법이 없다. 
			 * 
			 * 따라서 값타입을 사용하는 참조객체를 불변객체로 만들어야 한다.
			 */ 
			Address address2 = new Address(address.getCity(), address.getStreet(), address.getZipcode()); 
			
			Member member2 = new Member();
			member2.setName("user");
			member2.setHomeAddress(address2);
			em.persist(member2);
			
			member1.getHomeAddress().setCity("newCity");
			
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
	
	/**
	 * 
	 * 
	 	값 타입의 비교
		• 값 타입: 인스턴스가 달라도 그 안에 값이 같으면 같은 것으로 봐야 함
		
		값 타입의 비교
		• 동일성(identity) 비교: 인스턴스의 참조 값을 비교, == 사용
		• 동등성(equivalence) 비교: 인스턴스의 값을 비교, equals() 사용
		• 값 타입은 a.equals(b)를 사용해서 동등성 비교를 해야 함
		• 값 타입의 equals() 메소드를 적절하게 재정의(주로 모든 필드 사용)

		임베디드 타입의 경우 동등성 비교를 사용해야한다.
		임베더블객체의 equals 메소드 선언해서 사용하자.
		Shift + Alt + S > Generate hashcoced() and equals..  자동완성 기능 활용 하자.

	 */
	public static void 값타입_비교() {
			
			int a = 10;
			int b = 10;
			
			System.out.println("a == b : " + (a == b));
			
			Address address1 = new Address("city", "street","zipcode");
			Address address2 = new Address("city", "street","zipcode");
			
			System.out.println("address1 == address2 : " + (address1.equals(address2)));
			
	}
	
	/**
	 * 
	 * 
	 	값 타입 컬렉션
		• 값 타입을 하나 이상 저장할 때 사용
		• @ElementCollection, @CollectionTable 사용
		• 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없다. 
		• 컬렉션을 저장하기 위한 별도의 테이블이 필요함
 		
 		참고: 값 타입 컬렉션은 영속성 전에(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.


		값 타입 컬렉션의 제약사항
		• 값 타입은 엔티티와 다르게 식별자 개념이 없다. 
		• 값은 변경하면 추적이 어렵다. 
		• 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 
			값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다. 
		• 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야 함: null 입력X, 중복 저장X
		
		값 타입 컬렉션 대안
		• 실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려
		• 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
		• 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션 처럼 사용
		• EX) AddressEntity
		
	 */
	public static void 값타입_컬렉션() {
		
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
			
			System.out.println("=========== 저장 =============");
			
			Member member = new Member();
			member.setName("user1");
			member.setHomeAddress(new Address("city1", "street1","zipcode1"));
			
			member.getFavoriteFoods().add("치킨");
			member.getFavoriteFoods().add("족발");
			member.getFavoriteFoods().add("피자");
			
			member.getAddresseHistory().add(new Address("city2", "street2","zipcode2"));
			member.getAddresseHistory().add(new Address("city3", "street3","zipcode3"));

			em.persist(member);
			
			
			em.flush();
			em.clear();
			
			
			System.out.println("=========== 조회 =============");
			
			/**
			 * 값 타입 컬렉션은 지연로딩이 기본값이다.
			 * 따라서 실제 식별자 조회 시 DB를 호출하게 된다.
			 */
			Member findMember =  em.find(Member.class, member.getId());
			
			List<Address> addresses =  findMember.getAddresseHistory();
			
			
			for (Address address : addresses) {
				System.out.println("address city : " + address.getCity());
			}
			
			
			
			System.out.println("=========== 변경 =============");
			
			// 치킨 -> 한식으로 변경해보자.
			findMember.getFavoriteFoods().remove("치킨");
			findMember.getFavoriteFoods().add("한식");
			
			/**
			 * city2 -> city4로 변경해 보자.
			 * 1. remove : 주인 엔티티와 연관된 모든 데이터를 삭제 즉, member id에 해당되는 모든 address 데이터를 모두 삭제한다.
			 * 2. add : 기존에 남아있는 값 + 변경된 값을 다시 모두 저장한다.
			 * 
			 * 
			 * 참고
			 * - 자바 컬렉션의 경우 remove 시 오브젝트 비교를 equals로 한다.
			 * - remove(new Address("city2", "street2","zipcode2") 이렇게 객체 자체를 넣고 삭제하는 경우 equals가 제대로 동작하기 위해서는
			 *   address 객체에 equals와 hashcode 를 제대로 만들어 줘야 한다.(자동으로 생성되는 equals, hascode사용할 것 -> Shift + Alt + S > Generate hashcoced() and equals..  자동완성 기능 활용)
			 *   - 컬렉션을 다루는데 중요하다.
			 *  
			 */
			findMember.getAddresseHistory().remove(new Address("city2", "street2","zipcode2"));
			findMember.getAddresseHistory().add(new Address("city4", "street4","zipcode4"));
			
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
	
	/**
	 * 값 타입 컬렉션은 실무에서 사용하지 말자. 
	 * 정말 정말 단순한 경우에만 사용하는데, 그래도 왠만하면 사용하지 말자.
	 */
	public static void 값타입_컬렉션_엔티티변환() {
		
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
			
			System.out.println("=========== 저장 =============");
			
			Member member = new Member();
			member.setName("user1");
			member.setHomeAddress(new Address("city1", "street1","zipcode1"));
			
			member.getFavoriteFoods().add("치킨");
			member.getFavoriteFoods().add("족발");
			member.getFavoriteFoods().add("피자");
			
			member.getAddresseEntities().add(new AddressEntity("city2", "street2","zipcode2"));
			member.getAddresseEntities().add(new AddressEntity("city3", "street3","zipcode3"));
			
			em.persist(member);
			
			
			
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

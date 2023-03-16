package proxy;

import org.hibernate.Hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMainProxy {

	public static void main(String[] args) {
//		craeteEntity();
		proxyLearn4();
	}
	
	/**
	 * 
	   프록시 객체에 대한 매커니즘은 JAP 표준 스펙에는 존재하지 않는다.
	   JPA 구현체 라이브러리에 존재하며, 각 라이브러리에 따라 매커니즘을 다를 수 있다.
	   즉, 하이버네이트의 매커니즘이다. 
	   
	   프록시 기초
	 	• em.ﬁnd() vs em.getReference() 
		• em.ﬁnd(): 데이터베이스를 통해서 실제 엔티티 객체 조회
		• em.getReference(): 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회
		
	   프록시특징1
		• 실제 클래스를 상속 받아서 만들어짐
		• 실제 클래스와 겉모양이 같다.
		• 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지않고 사용하면 됨(이론상)
		• 프록시 객체는 실제 객체의 참조(target)를 보관
		• 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드호출
		
	   프록시 객체의 초기화
		1. 사용자가 getName()을 호출 한다.
		2. MemberProxy 의 target에 실제 객체가 참조되어 있는 않은경우 3번 진행. 참조되어 있는 경우 아래 절차 진행하지 않고 참조된 실제 객체의 정보를 전달해 준다.
		3. 영속성 컨텍스트에 초기화 요청을 한다.
		4. 영속성 컨텍스트는 DB를 조회한 후
		5. 실제 Member 엔티티를 생성한다.
		6. MemberProxy의 target에 실제 Member 엔티티를 연결해 준다.
		7. 실제 Member 엔티티에서 getName()를 호출하여 MemberProxy를 통해 사용자에게 전달해 준다.  
		
	   프록시의 특징2
		• 프록시 객체는 처음 사용할 때 한 번만 초기화
		• 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는것은 아님, 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
		• 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야 함 (== 비교실패, 대신 instance of 사용)
		• 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
		• 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생
		  (하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트림)
		
	 */
	public static void proxyLearn1() {
		
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();
		
		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		
		try {
			
			/**
			 * getReference()
			    프록시 객체에 대한 매커니즘은 JAP 표준 스펙에는 존재하지 않는다.
			    JPA 구현체 라이브러리에 존재하며, 각 라이브러리에 따라 매커니즘을 다를 수 있다.
			    즉, 하이버네이트의 매커니즘이다. 
			 */
			Member findMember = em.getReference(Member.class, 1L);
			System.out.println("Member class : " + findMember.getClass());	// 출력 포맷 : class proxy.Member$HibernateProxy$U0DlV3XL
			System.out.println("Member id : " + findMember.getId());		// id를 호출할 때는 DB를 조회하지 않는다. em.getReference(Member.class, 1L) 할 때 id 값을 매개변수로 넘겨줬기 때문에 DB를 조회하지 않고 출력해 준다.
			System.out.println("Member name : " + findMember.getName());	// name를 최초 호출 : Proxy Target에 실제 엔티티가 참조되어 있지 않기 때문에 DB를 조회하여 출력한다.
			System.out.println("Member name : " + findMember.getName());	// name를 두번째 호출 : 최초 호출 시 Target에 실제 엔티티가 참조되어 DB 조회 없이 출력된다.
			
			
			// 쓰기 지연 SQL 저장소에 있는 SQL를 DB에 전송한다.
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		
		emf.close();
	}
	
	/**
	 * 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야 함 (== 비교실패, 대신 instance of 사용)
	 * 
	 */
	public static void proxyLearn2() {
		
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();
		
		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		
		try {
			
			Team team = em.find(Team.class, 1L);
			
			System.out.println("Member1 team.name : " + team.getName());
			Member member1 = new Member();
			member1.setName("user2");
			member1.setTeam(team);
			em.persist(member1);
			
			System.out.println("Member2 team.name : " + team.getName());
			Member member2 = new Member();
			member2.setName("user3");
			member2.setTeam(team);
			em.persist(member2);
			
			em.flush();
			em.clear();
			
			/**
			 * class type를 비교하는 경우
			 * 아래 상황은 당연히 같은 Member.class 이므로 true가 나온다. 
			 */
//			Member findMember1 = em.find(Member.class, member1.getId());
//			Member findMember2 = em.find(Member.class, member2.getId());
//			System.out.println("findMember1 == findMember2 : " + (findMember1.getClass() == findMember2.getClass()));
			
			
			/**
			 * 아래 상황은 둘다 Member.class 임에도 불구하고 false가 나온다.
			 * proxy는 가짜 이기 때문에 아래와 같이 다른 클래스이다.
			 * findMember3 => class proxy.Member
			 * findReference1 => class proxy.Member$HibernateProxy$NqVXDuCs
			 * 
			 * 그런데 우리가 실무에서 해당 상황을 맞이하는 경우에는 보통 아래와 같이 class를 메소드의 매개변수로 받는다.
			 	public void logic(Member m1, Member m2){
			 		System.out.println(m1.getClass() == m2.getClass());
			 	}
			 
			   위왁 같은 상황일 경우 해당 member 객체가 실제 엔티티인지 proxy인지 구분하지 못한다.
			   따라서 아래 괕이 해줘야 한다.
			   public void logic(Member m1, Member m2){
			 		System.out.println(m1 instanceof Mmeber);
			 		System.out.println(m2 instanceof Mmeber);
			   } 
			 */
			Member findMember3 = em.find(Member.class, member1.getId());
			Member findReference1 = em.getReference(Member.class, member2.getId());
			System.out.println("findMember3 == findReference1 : " + (findMember3.getClass() == findReference1.getClass()));	
			
			logic(findMember3, findReference1);
			
			// 쓰기 지연 SQL 저장소에 있는 SQL를 DB에 전송한다.
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		
		emf.close();
	}
	
	/**
	* 타입 비교시 == 대신 instanceof 사용할 것.
	* @param m1
	* @param m2
	*/
	public static void logic(Member m1, Member m2){
 		System.out.println(m1 instanceof Member);
 		System.out.println(m2 instanceof Member);
	}
	
	
	/**
	 * 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
	 * 이유
	 * 	1. 영속성 컨텍스트에 이미 존재하는 객체를 다시 DB 조회 할 이유가 없다.
	 * 	2. JPA는 동일한 트랜잭션 내의 같은 영속성 컨텍스트 안에서는 PK가 돌일하다면 == 에 대해 항상 true를 보장해 준다.
	 *  
	 */
	public static void proxyLearn3() {
		
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();
		
		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		
		try {
			
			Team team = em.find(Team.class, 1L);
			
			System.out.println("Member1 team.name : " + team.getName());
			Member member1 = new Member();
			member1.setName("user2");
			member1.setTeam(team);
			em.persist(member1);
			
			
			em.flush();
			em.clear();
			
			/**
			 * find() 실행 후 getReference() 실행
			 * 개념상 출력 예상 : find => class proxy.Member / getReference => class proxy.Member$HibernateProxy$uTECW8tx
			 * 
			 * 그러나..
			 * 
			 * 출력 결과 
			 * - findMember.getClass() / findReference.getClass() : class proxy.Member / class proxy.Member
			 * - findMember == findReference : true
			 * 
			 * getReference()를 했음에도 불구하고 class proxy.Member 를 출력한다.
			 */ 
//			Member findMember = em.find(Member.class, member1.getId());
//			Member findReference = em.getReference(Member.class, member1.getId());
//			System.out.println("findMember.getClass() / findReference.getClass() : " + findMember.getClass() +" / "+ findReference.getClass());	
//			System.out.println("findMember == findReference : " + (findMember.getClass() == findReference.getClass()));
			
			
			/**
			 * 위와 반대로 해보자.
			 * getReference() 실행 후 find() 실행
			 * 개념상 출력 예상 : getReference => class proxy.Member$HibernateProxy$uTECW8tx / find => class proxy.Member
			 * 
			 * 그러나..
			 * 
			 * 출력 결과
			 * - findMember.getClass() / findReference.getClass() : class proxy.Member$HibernateProxy$uTECW8tx / class proxy.Member$HibernateProxy$uTECW8tx
			 * - - findMember == findReference : true
			 * 
			 * find()을 했음에도 불구하고 class proxy.Member$HibernateProxy$uTECW8tx 를 출력한다.
			 */
			Member findReference = em.getReference(Member.class, member1.getId());
			Member findMember = em.find(Member.class, member1.getId());
			System.out.println("findMember.getClass() / findReference.getClass() : " + findMember.getClass() +" / "+ findReference.getClass());	
			System.out.println("findMember == findReference : " + (findMember.getClass() == findReference.getClass()));	
			
			
			// 쓰기 지연 SQL 저장소에 있는 SQL를 DB에 전송한다.
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		
		emf.close();
		
	}
	
	/**
	 * 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생
	 * (하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트림)
	 * 
	 * 프록시 확인 Util 확인
	 * 
	 */
	public static void proxyLearn4() {
		
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();
		
		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		
		try {
			
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);
			
			Member member1 = new Member();
			member1.setName("user1");
			member1.setTeam(team);
			em.persist(member1);
			
			
			em.flush();
			em.clear();
			
			/**
			 * 영속성 컨텍스트에 이미 실제 엔티티가 존재하는 경우 getReference()로 조회해도 class Type은 proxy가 아닌 Member.class 이다.
			 * 
			 * 	flush와 clear를 통해 DB 동기화 및 영속성 컨텍스트를 지워 준 경우에는 class Type이 proxy로 출력된다. 
			 	em.flush();
				em.clear();
			 */ 
			Member findReference = em.getReference(Member.class, member1.getId());
			System.out.println("************* findReference.getClass() : " +  findReference.getClass());	
			
			/**
			 * 프록시인스턴스의초기화여부확인 PersistenceUnitUtil.isLoaded(Object entity)
			 */ 
			System.out.println("isLoad : " + emf.getPersistenceUnitUtil().isLoaded(findReference));
			
			/**
			 * 프록시클래스확인방법
			   entity.getClass().getName() 출력(..javasist.. or HibernateProxy…)
			 */
			System.out.println("findReference class : " + findReference.getClass());
			
			/**
			 * 프록시강제초기화
			   - org.hibernate.Hibernate.initialize(entity);
			   
			   • 참고: JPA 표준은강제초기화없음 강제호출: member.getName()
			 */
			Hibernate.initialize(findReference);
			
			/**
			 * 준영속
			 * - detach() : 영속성 컨텍스트에서 findReference 객체를 지운다.
			 * - clear() : 영속성 컨텍스트 전체 객체를 지운다.
			 * - close() : 엔티티메니저를 닫는다.
			 */ 
//			em.detach(findReference); // org.hibernate.LazyInitializationException: could not initialize proxy [proxy.Member#552] - no Session
//			em.clear();	// org.hibernate.LazyInitializationException: could not initialize proxy [proxy.Member#602] - no Session 
//			em.close();	// 강의에서는 에러가 나온다고 했는데 에러 발생하지 않음.
			
			/**
			 * 이 시점에 프록시를 초기화한다.(실제 DB로 조회 해온다)
			 * 그런데 위에서 영속성 컨텍스트를 지우거나 엔티티매니저를 종료하거나 등을 하면 프록시 초기화 시 에러가 발생한다.
			 * - close()는 에러 발생하지 않음.
			 * org.hibernate.LazyInitializationException
			 */ 
//			System.out.println("************* findReference.getName : " + findReference.getName());
			
			
			// 쓰기 지연 SQL 저장소에 있는 SQL를 DB에 전송한다.
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		
		emf.close();
	}
	
	/**
	 * Member를 조회하면 참조되고 있는 Team 객체도 같이 조회 된다.
	 * DB에서 Member와 Team을 조인한 SQL로 조회함.
	 * 
	 * 그런데 Team을 제외하고 Member만 조회하고 싶은 경우에도 어쩔수 없이 Team을 조회할 수 밖에 없는 상황이다.
	 * 이렇게 되면 아무래도 불필요한 트랜잭션이 생기기 마련이다.
	 * 
	 * 그래서 이를 해결하고자 지연로딩으로 해결할 수 있다. 이를 알기 위해서 우선 Proxy에 대해서 알아보자. 
	 */
	public static void proxy를알아야하는이유() {
		
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();
		
		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		
		try {
			
			Member findMember = em.find(Member.class, 1L);
			printMemberAndTeam(findMember);
			
			
			// 쓰기 지연 SQL 저장소에 있는 SQL를 DB에 전송한다.
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		
		emf.close();
		
	}
	
	
	private static void printMemberAndTeam(Member member) {
		String name = member.getName();
		System.out.println("Member.name : " + name);
		
		Team team = member.getTeam();
		System.out.println("Team name : " + team.getName());
		
	}



	/**
	 * entity 생성 및 조인 테이블 FK 설정 
	 */
	public static void craeteEntity() {
		
		/**
		 * 애플리케이션 로딩 시점에 한번만 만든다. 데이터베이스당 1개만 실행한다.
		 * persistence.xml의 persistence-unit name 과 맵핑된다.
		 */
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		
		// 트랜젝션이 일어나는 시점에 계속 만들어 준다.
		EntityManager em = emf.createEntityManager();
		
		// JPA에서는 데이터 변경 관련 모든 작업은 트랙젝션 안에서 실행되어야 한다. 
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		
		try {
			
			/**
			 * Team 1 : Member N 관계
			 */
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);
			// persist 영속성 컨텍스트에 객체 정보를 초기화 해주고, SQL생성 후 쓰기 지연 SQL 저장소에 넣는다.
			
			Member member = new Member();
			member.setName("user1");
			member.setTeam(team);	// team의 PK를 member에 fk로 넣어줌.
			em.persist(member);
			// persist 영속성 컨텍스트에 객체 정보를 초기화 해주고, SQL생성 후 쓰기 지연 SQL 저장소에 넣는다.
			
			
			// 쓰기 지연 SQL 저장소에 있는 SQL를 DB에 전송한다.
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		
		emf.close();
	}
}

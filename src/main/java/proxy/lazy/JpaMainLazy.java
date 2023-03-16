package proxy.lazy;


import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Persistence;

/**
 * 
 * @author wglee
 *
 *
 	지연 로딩 활용 - 실무
	• 모든 연관관계에 지연 로딩을 사용해라! 
	• 실무에서 즉시 로딩을 사용하지 마라! 
	• JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라! 
	• 즉시 로딩은 상상하지 못한 쿼리가 나간다
 *
 */
public class JpaMainLazy {

	public static void main(String[] args) {
		
		문제점();
	}
	
	/**
	 * 지연로딩
	 */
	public static void lazyLoading() {
		
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
			 * Member 클래스에 참조 객체인 Team 선언 시 필드 옵션을 아래와 같이 fetch 옵션 중 LAZY를 설정하면 
			 * em.find(Member.class, member1.getId()) 조회 시 Team 객체는 Proxy로 조회 된다.
			 * - @ManyToOne(fetch = FetchType.LAZY)
			 * 
			 	출력 결과
			 	- findMember.getClass() : class lazy.Member
				- findMember.getTeam().getClass : class lazy.Team$HibernateProxy$EYqtoZBy
				
				
				Proxy를 알고있다면 당연히 Team을 조회하는 시점은 Team내의 필드를 호출하는 시점이 된다.
				findMember.getTeam().getName()
				
			 */ 
			Member findMember = em.find(Member.class, member1.getId());
			System.out.println("************* findMember.getClass() : " +  findMember.getClass());	
			System.out.println("*************  findMember.getTeam().getClass : " +  findMember.getTeam().getClass());
			
			// Proxy 객체의 초기화 : 이 시점에 DB에서 조회 한다.
			System.out.println("Team : " + findMember.getTeam().getName());
			
			
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
	 * 즉시로딩
	 */
	public static void eagerLoading() {
		
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
			 * Member 클래스에 참조 객체인 Team 선언 시 필드 옵션을 아래와 같이 fetch 옵션 중 EAGER를 설정하면 
			 * em.find(Member.class, member1.getId()) 조회 시 Team 객체도 같이 조회한다.
			 * - @ManyToOne(fetch = FetchType.EAGER)
			 * 
			 	출력 결과
			 	- findMember.getClass() : class lazy.Member
				- findMember.getTeam().getClass : class lazy.Team
				
			 */ 
			Member findMember = em.find(Member.class, member1.getId());
			System.out.println("************* findMember.getClass() : " +  findMember.getClass());	
			System.out.println("*************  findMember.getTeam().getClass : " +  findMember.getTeam().getClass());
			
			// Proxy 객체의 초기화 : 이 시점에 DB에서 조회 한다.
			System.out.println("Team : " + findMember.getTeam().getName());
			
			
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
	 * 
	 * 프록시와 즉시로딩 주의
		1. 가급적 지연 로딩만 사용(특히 실무에서) 
		2. 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
		3. 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다. 
		4. @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY로 설정
			• @OneToMany, @ManyToMany는 기본이 지연 로딩
	 * 
	 */
	public static void 문제점() {
		
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
			 * 1. 즉시로딩은 무조건 조인쿼리가 실행되기 때문에 실무에서 사용 시 
			 *     조회되는 수많은 객체가 다 조인되서 조회되기 때문에 성능이슈가 발생할 수 있다.
			 * 
			 */ 
//			Member findMember = em.find(Member.class, member1.getId());
			
			
			/**
			 * 2.즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.  
			 *  - JPQL로 조회 시 작성한 SQL 그대로 조회 후 해당 객체에 즉시로딩이 걸려있으면 해당 참조 객체를 한번 더 조회한다.
			 *  - 즉, 쿼리가 두번 실행된다. 그런데 만약 Member가 수십, 수백개 이고 Member에 참조된 Team이 별개로 존재한다면 수많은 쿼리가 실행되는 참사가 발생한다.
			 *  - 그래서 지연로딩을 사용해야 한다. But, 지연로딩을 사용해도 발생할 수 있는 문제는 Team를 Loop를 돌리면 쿼리가 Loop 사이즈 만큼 생성되는 이슈가 있다. 
			 * 
			 */
//			List<Member> members =  em.createQuery("select m from Member m", Member.class).getResultList();
			
			
			/**
			 * 2번 해결방안
			 * - 우선 모든 연관관계를 지연로딩으로 설정한다.
			 * 	1. 패치조인
			 * 	- 두 객체를 조인해서 한번에 조회된다. 이렇게 하면 Loop 시 발생하는 문제 해결 할 수 있다.
			 * 	- "select m from Member m join fetch m.team"
			 */  

			List<Member> members =  em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
			
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
}

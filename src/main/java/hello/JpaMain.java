package hello;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

public class JpaMain {
	
	public static void main(String[] args) {
		
		JpaMain jpaMain = new JpaMain();
		jpaMain.동일성보장();
	}
	
	public void JPACRUD() {
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
			// 1. 생성
//			Member member = new Member();
//			member.setId(10L);
//			member.setName("HI");
//			em.persist(member);
			
			// 2. 조회
//			Member member = em.find(Member.class, 10L);
//
//			System.out.println("member.getId : "+member.getId());
//			System.out.println("member.getName : "+member.getName());
			
			// 3. 삭제
//			Member member = em.find(Member.class, 10L);
//			em.remove(member);
			
			
			// 4. 수정
			/**
			 * JPA를 통해서 엔티티를 가지고 오면 JPA에서 엔티티를 관리를 한다. 
			 * 트랜잭션 안에서는 데이터 변경된 경우 JPA가 엔티티 변경 여부를 체크하여 변경이 된 경우 트랜잭션 commit 시 업데이트 쿼리를 날림. 대박 신기..
			 */
//			Member member = em.find(Member.class, 10L);
//			member.setName("HIHI");
			
			
			// 5. JPQL : 엔티티 객체를 대상으로 하는 '객체지향 SQL' 언어
			// 대상이 테이블이 아니고 객체를 대상으로 조회한다.
//			List<Member> listMember = em.createQuery("select m from Member m", Member.class)
//					.setFirstResult(5)	// 페이징
//					.setMaxResults(8)	// 페이징
//					.getResultList();
//			
//			for (Member member : listMember) {
//				System.out.println("memeber.name : " + member.getName());
//			}
			
			
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
	 * Desc : 영속성 컨켁스트(PersistenceContext) 이해하기
	 * @author wglee
	 * 
	 * 영속성 컨텍스트의 이점
	 * 1. 1차 캐시 : 동일한 트랜잭션 내에서만 사용된다.
	 *  - 영속 상태가 되면 PK가 생성 된 후 영속 상태가 된다. 
	 * 2. 동일성(identity) 보장 : 동일한 트랜잭션 내에서
	 * : 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공
	 * 3. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
	 * 
	 * 
	 */
	public void 영속성컨텍스트() {
		
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
			 * 엔티티의 생명주기
			 */
			
			// 1. 비영속 (new/transient) : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
			Member member = new Member();
			member.setId(20L);
			member.setName("Hello");
			
			// 2. 영속 (managed) : 영속성 컨텍스트(1차 캐시)에 관리되는 상태.
			// INSERT SQL를 DB에 보내지 않고 쌓고 있는다. 
			em.persist(member);
			
			// 1차 캐시에 저장된 엔티티를 조회한다. 즉 DB에서 조회 하지 않는다.
			Member mb =  em.find(Member.class, 20L);
			System.out.println("memeber.name : " + mb.getName());
			
			// 3. 준영속 (detached) : 영속성 컨텍스트에 저장되었다가 분리된 상태
			// 준영속을 하게 되면 이후 commit을 해도 SQL 생성되지 않음. 
//			em.detach(member);
			
			// 4. 삭제 (removed) : 삭제된 상태
//			em.remove(member);
			
			
			// 트랜잭션 종료
			// 5. SQL를 DB에 넘어가는 시점
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			// EntityManager가 DB connection을 물고 있기 때문에 꼭 닫아줘야 한다.
			em.close();
		}
		
		emf.close();
		
	}
	
	public void 일차캐시_1() {
		
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
			
			
			// 1. 비영속 (new/transient) : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
			Member member = new Member();
			member.setId(20L);
			member.setName("Hello");
			
			// 2. 영속 (managed) : 1차 캐시(영속성 컨텍스트)에 쌓인다.
			em.persist(member);
			
			// 1차 캐시에 저장된 엔티티를 조회한다. 즉 DB에서 조회 하지 않는다.
			Member mb =  em.find(Member.class, 20L);
			System.out.println("memeber.name : " + mb.getName());
			
			
			// 트랜잭션 종료
			// 3. SQL를 DB에 넘어가는 시점
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
	 * Java의 collection 처럼 동일 엔티티를 보장한다.
	 * 1차 캐시의 이점으로 인해 가능함.
	 */
	public void 동일성보장() {
		
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
			
			Member member1 = new Member();
			member1.setName("user1");
			em.persist(member1);
			
//			em.flush();
//			em.clear();
			
			Member findMember1 =  em.find(Member.class, member1.getId());	 // DB에서 조회 한 후 1차 캐시에 저장한다.
			Member findMember2 =  em.find(Member.class, member1.getId());	 // 1차 캐시에서 조회 한다. 
			System.out.println("memeber1 memeber2 비교 : " + (findMember1 == findMember2));
			
			
			// 트랜잭션 종료
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			// EntityManager가 DB connection을 물고 있기 때문에 꼭 닫아줘야 한다.
			em.close();
		}
		
		emf.close();
		
		
	}
	
	/**
	 *  persistence.xml의 hibernate.jdbc.batch_size 옵션으로 SQL를 최대 얼마나 쌓을지 사이즈 설정 가능함.
	 *  JPA 성능의 이점이 되는 기능. 잘 사용하면.
	 *  버퍼링 사용 가능
	 */
	public void 쓰기지연() {
		
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
			
			
			Member member1 = new Member(30L, "WGLEE");
			Member member2 = new Member(40L, "MINAE");
			
			em.persist(member1);
			em.persist(member2);
			// 여기까지 INSERT SQL를 DB에 보내지 않고 쌓고 있는다. 쓰기 지연 SQL 저장소에 SQL을 쌓는다.
			
			// 1차 캐시에 저장된 엔티티를 조회한다. 즉 DB에서 조회 하지 않는다.
			Member mb =  em.find(Member.class, 20L);
			System.out.println("memeber.name : " + mb.getName());
			
			
			
			// 트랜잭션 종료
			// 커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다. 
			tx.commit(); 	// commit()이 실행되면 영속 컨텍스트에 있는 쓰기 지연 SQL 저장소에 쌓인 SQL를 DB로 flush 한다. 그리고 최종 DB에서 commit이 샐행된다.  
		} catch (Exception e) {
			tx.rollback();
		} finally {
			// EntityManager가 DB connection을 물고 있기 때문에 꼭 닫아줘야 한다.
			em.close();
		}
		
		emf.close();
		
	}
	
	/**
	 * Dirty Checking
	 */
	public void 변경감지() {
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
			
			
			Member member = em.find(Member.class, 30L);
			member.setName("WGLEE2");
			
			
			// 트랜잭션 종료
			/**
			 * 1차 캐시(영속 컨텍스트)에 최초 저장되는 시점에 스냅샷을 저장해 놓는다.
			 *  그리고 commit()이 실행되는 시점에 flush가 호출이 되면서 1차 캐시 엔티티와 스냅샷을 비교한다.
			 *  이때 변경된 내용이 있는 경우 update sql를 쓰기지연 SQL 저장소에 저장해 놓는다.
			 *  그리고 쓰기지연 SQL 저장소에 있는 SQL를 DB에 보낸 후 commit 한다.
			 */
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
	 * 플러시가 발생하면
	 * 1. 변경 감지 
	 * 2. 수정된 엔티티 쓰기 지연 SQL 저장소에 등록 
	 * 3. 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송 (등록, 수정, 삭제 쿼리)
	 * 
	 * 플러시 모드 옵션
	 *  em.setFlushMode(FlushModeType.COMMIT)
	 *	
	 *  1. FlushModeType.AUTO
	 *	- 커밋이나 쿼리를 실행할 때 플러시 (기본값) 
	 *	2. FlushModeType.COMMIT 
	 *  - 커밋할 때만 플러시
	 *  
	 * 플러시 특징
	 * 1. 영속성 컨텍스트를 비우지 않음 
	 * 2. 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 
	 * 3. 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화 하면 됨
	 * 
	 */
	public void 플러시() {
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
			
			
			Member member = new Member(30L, "WGLEE");
			// 1차 캐시에 저장. 쓰기지연 SQL 저장소에 쿼리 저장
			em.persist(member);

			// 쓰기지연 SQL 저장소에 있는 쿼리를 DB에 강제로 보낸다.
			em.flush();
			
			System.out.println("===========");
			
			// 위에서 강제로 flush를 해 줬기 때문에 아래 commit() 시점에는 flush가 일어나지 않는다? 암튼 중복해서 발생하지는 않음.
			// 즉, flush가 일어날때 쿼리를 DB에 전송하는 걸 중복하지 않는다는 말. 
			tx.commit(); 	  
		} catch (Exception e) {
			tx.rollback();
		} finally {
			// EntityManager가 DB connection을 물고 있기 때문에 꼭 닫아줘야 한다.
			em.close();
		}
		
		emf.close();
		
		
	}
	
	public void JPQL_플러시() {
		
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
			
			
			Member member1 = new Member(40L, "WGLEE1");
			Member member2 = new Member(50L, "WGLEE2");
			Member member3 = new Member(60L, "WGLEE3");
			// 1차 캐시에 저장. 쓰기지연 SQL 저장소에 쿼리 저장
			em.persist(member1);
			em.persist(member2);
			em.persist(member3);
			// 여기까지는 DB에 SQL를 전달하지 않는다.
			
			// JPQL 쿼리 실행 시 플러시가 자동으로 호출된다.
			Query query = em.createQuery("select m from Member m", Member.class);
			List<Member> listMember = query.getResultList();
			
			for (Member member : listMember) {
				System.out.println("memeber.name : " + member.getName());
			}
			
			System.out.println("===========");
			
			// 위에서 강제로 flush를 해 줬기 때문에 아래 commit() 시점에는 flush가 일어나지 않는다? 암튼 중복해서 발생하지는 않음.
			// 즉, flush가 일어날때 쿼리를 DB에 전송하는 걸 중복하지 않는다는 말. 
			tx.commit(); 	  
		} catch (Exception e) {
			tx.rollback();
		} finally {
			// EntityManager가 DB connection을 물고 있기 때문에 꼭 닫아줘야 한다.
			em.close();
		}
		
		emf.close();
		
	}
	
	
	public void 준영속() {
		
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
			
			
			Member member = em.find(Member.class, 30L);
			member.setName("WGLEE2");
			
			// 준영속 상태로 만드는 방법
			// 1. 특정 객체만 준영속
			em.detach(member);
			
			// 2. 1차 캐시 전체를 준영속 상태로 만드는 방법
			em.clear();
			
			// 3. 엔티티매니저를 종료해 버리면 준영속 상태가 된다.
			em.close();
			
			
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
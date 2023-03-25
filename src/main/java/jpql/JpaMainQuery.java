package jpql;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class JpaMainQuery {

	/**
	 * 
	 	JPA는 다양한 쿼리 방법을 지원
		• JPQL 
		• JPA Criteria
		• QueryDSL 
		• 네이티브 SQL
		• JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용
	 */
	public static void main(String[] args) {
		
		
	}
	
	/**
	 * 
	 * 
	 	JPA에서의 DB 조회란?
		• 가장 단순한 조회 방법으로 EntityManager.find()가 있다.
		• 그래서 결과적으로 객체 그래프 탐색(a.getB().getC())을 통해 객체를 조회한다.
		• 그런데, 나이가 18살 이상인 회원을 모두 검색하고 싶다면?
		
		• JPA를 사용하면 엔티티 객체를 중심으로 개발한다.
		• 문제는 검색 쿼리
		• 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색하기 때문에
			모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
		• 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요하게 된다.
		
		JPQL(Java Persistence Query Language)
		• JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
		• SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
		• SQL은 데이터베이스 테이블을 대상으로 쿼리
		• JPQL은 테이블이 아닌 엔티티 객체를 대상으로 검색하는 객체 지향 쿼리
		• SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.
		• JPQL을 한마디로 정의하면 객체 지향 SQL이라고 할 수 있다.
	 */
	public static void JPQL() {
		
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
			
			// 아래 SQL의 Member는 테이블이 아니라 Member 객체를 바라보고 있다.
			List<Member> members = em.createQuery("select m from Member m where m.name Like '%kim%'", Member.class).getResultList();
			
			
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
	 	Criteria 소개
		• 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
		• JPQL 빌더 역할
		• JPA 공식 기능
		• 단점: 너무 복잡하고 실용성이 없다. 
		• Criteria 대신에 QueryDSL 사용 권장

	 */
	public static void JpaCriteria() {
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
				//Criteria 사용 준비
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Member> query = cb.createQuery(Member.class); 
				//루트 클래스 (조회를 시작할 클래스)
				Root<Member> m = query.from(Member.class); 
				//쿼리 생성 \
				CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
				List<Member> resultList = em.createQuery(cq).getResultList();
					
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
	 	QueryDSL 소개
		• 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
		• JPQL 빌더 역할
		• 컴파일 시점에 문법 오류를 찾을 수 있음
		• 동적쿼리 작성 편리함
		• 단순하고 쉬움
		• 실무 사용 권장

	 */
	public static void QueryDSL() {
		
		/**
		 * 
		 * JPAFactoryQuery query = new JPAQueryFactory(em);
			 QMember m = QMember.member;
			 List<Member> list = 
			 query.selectFrom(m)
			 .where(m.age.gt(18))
			 .orderBy(m.name.desc())
			 .fetch();
		 * 
		 * 
		 */
		
	}
	
	
	/**
	 * 
		네이티브 SQL 소개
		• JPA가 제공하는 SQL을 직접 사용하는 기능
		• JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능
		• 예) 오라클 CONNECT BY, 특정 DB만 사용하는 SQL 힌트

	 */
	public static void 네이티브_SQL() {
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
			
			String sql = "SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = 'kim'"; 
			List<Member> findMembers = em.createNativeQuery(sql, Member.class).getResultList();
			
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
	 	JDBC 직접 사용, SpringJdbcTemplate 등
		• JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스등을 함께 사용 가능
		• 단 영속성 컨텍스트를 적절한 시점에 강제로 플러시 필요
		• 예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시
	 */
	public static void JDBC_SpringJdbcTemplate() {
		
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
			member.setName("member1");
			em.persist(member);
			
			
			/**
			 *  
			 *  JPA는 flush를 해야 DB에 쿼리가 날라간다..
			 *  - flush 되는 경우 : commit, JPQL, 네이티브SQL 실행 시.
			 */
			em.flush();
			
			/**
			 * SpringJdbcTemplate 를 사용해서 아래와 같이 조회 했다고 치자.
			 * dbconn.executeQuery("select * from member");
			 * 
			 * 위 쿼리는 JPA 하고는 아무상관이 없기 때문에 위에 member 객체를 조회 할 수 없다.
			 * 
			 * 왜냐면, persist 에서는 실제 DB에 전송하지 않고 영속성 컨텍스트에만 존재하기 때문에
			 * 그래서 강제플러시 해줘야 한다.
			 * 
			 * 이해했지? 까먹은거 아닌지? 미래의 나에게.. 
			 * 
			 */ 
			
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

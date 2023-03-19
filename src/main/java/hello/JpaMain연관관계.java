package hello;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * 
 * @author wglee
 * 
 * 양방향 매핑 정리
	• 단방향 매핑만으로도 이미 연관관계 매핑은 완료
	• 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색) 기능이 추가된 것 뿐
	• JPQL에서 역방향으로 탐색할 일이 많음
	• 단방향 매핑을 잘 하고 양방향은 필요할 때 추가해도 됨(테이블에 영향을 주지 않음
	• 연관관계의 주인은 외래 키의 위치를 기준으로 정해야함
 * 
 * 
 *
 * 양방향 맵핑 시 주의 사항
 	• 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자
 	• 연관관계 편의 메소드를 생성하자
 	    - 한쪽에만 추가해줘야 한다.
 	• 양방향 매핑시에 무한 루프를 조심하자
 		• 예: toString(), lombok, JSON 생성 라이브러리
 		- lombok 등으로 toString 메소드 자동생성해서 사용하는 경우 주의하자. 객체끼리 참조 되는 부분에서 무한루프 발생 한다.
 		  참조 되는 객체는 삭제 후 사용해야 함.
 		- controller에서 엔티티 객체를 JSON 맵핑하여 response 하는 경우 무한루프가 발생한다.
 		  따라서 JSON을 생성할 경우에는 DTO를 만들어서 해결하자. 
 		
 		
 *
 * JPA 모델링 시 
 * 1. 단방향 맵핑으로 설계를 끝내야 한다. 양방향 맵핑은 고려하지 않는다.
 * 2. 애플리케이션 개발 시 필요한 경우에 추가하면 됨.
 *
 */
public class JpaMain연관관계 {
public static void main(String[] args) {
		
		JpaMain연관관계 jpaMain = new JpaMain연관관계();
		jpaMain.객체_단방향연관관계();
	}


	/**
	 * 객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다.
     * • 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾는다. 
     * • 객체는 참조를 사용해서 연관된 객체를 찾는다. 
     * • 테이블과 객체 사이에는 이런 큰 간격이 있다 
	 */
	public void 테이블연관관계() {
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
			
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);
			// persist를 하게 되면 1차 캐시에 저장되는데, 저장 전 PK가 생성된 후 저장 된다.
			// 그래서 아래 team.getId()를 셋팅할 수 있다.
			
			// 등록 : 외래키를 사용하여 등록
//			Member member = new Member();
//			member.setName("memeber1");
//			member.setTeamId(team.getId());
//			em.persist(member);
			
			
			// 조회 : 외래키를 사용하여 조회
//			Member findMember =  em.find(Member.class, member.getId());
//			Team findTeam =  em.find(Team.class, findMember.getTeamId());
			
			
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
	
	public void 객체_단방향연관관계() {
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
			
			Team team = new Team();
			team.setName("TeamB");
			em.persist(team);
			// persist를 하게 되면 1차 캐시에 저장되는데, 저장 전 PK가 생성된 후 저장 된다.
			
			// 등록 : 외래키를 사용하여 등록
			Member member = new Member();
			member.setName("memeber1");
//			member.setTeamId(team.getId());
			member.setTeam(team);	// team 객체만 넣어주면 JPA가 Team의 PK를 꺼내서 Member에 FK(team_id)로 알아서 넣어준다. 
			em.persist(member);
			
			em.flush(); // 영속성 컨텍스트에 있는 SQL를 DB와 동기화 해 준다.
			em.clear(); // 영속성 컨텍스트를 초기화 해준다.
			
			// 조회 : 외래키를 사용하여 조회
			Member findMember =  em.find(Member.class, member.getId());
//			Team findTeam =  em.find(Team.class, findMember.getTeamId());
			Team findTeam = findMember.getTeam();
			System.out.println("findTeam : "+findTeam.getName());
			
			
			// 수정 : 조회 한 member의 팀을 변경해 주는 방법
			Team newTeam = em.find(Team.class, 2);
			findMember.setTeam(newTeam);
			
			
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
	 * 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단뱡향 관계 2개다
	 * 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
	 * 그러면 외래키를 어느 객체에서 관리(등록, 수정)해야 되는지에 대한 문제가 생긴다. 그래서 연관관계의 주인을 정해야 한다.
	 * 
	 * 양방향 매핑 규칙
	 * • 객체의 두 관계중 하나를 연관관계의 주인으로 지정
     * • 연관관계의 주인만이 외래 키를 관리(등록, 수정) 
     * • 주인이 아닌쪽은 읽기만 가능
     * • 주인은 mappedBy 속성 사용X 
     * • 주인이 아니면 mappedBy 속성으로 주인 지정
     * 
	 * • 외래 키가 있는 있는 곳(외래키가 존재하는 객체)을 주인으로 정해라
	 * 
	 */
	public void 객체_양방향연관관계() {
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
			
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);
			// persist를 하게 되면 1차 캐시에 저장되는데, 저장 전 PK가 생성된 후 저장 된다.
			
			// 등록 :
			Member member = new Member();
			member.setName("memeber1");
			member.setTeam(team);	// team 객체만 넣어주면 JPA가 Team의 PK를 꺼내서 Member에 FK(team_id)로 알아서 넣어준다. 
			em.persist(member);
			
			em.flush(); // 영속성 컨텍스트에 있는 SQL를 DB와 동기화 해 준다.
			em.clear(); // 영속성 컨텍스트를 초기화 해준다.
			
			// 조회 
			Member findMember =  em.find(Member.class, member.getId());
			List<Member> members = findMember.getTeam().getMembers();
			
			for (Member m : members) {
				System.out.println("member : " + m.getName());
			}
			
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
	 * 잘못된 예제
	 * - 연관관계의 주인에 값(참조객체)을 입력하지 않음. 외래키 Null
	 */
	public void 양방향연관관계_실수() {
		
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
			 * 잘못된 예제
			 * - 연관관계의 주인에 값을 입력하지 않음. 외래키 Null
			 */
			Member member = new Member();
			member.setName("memeber1");
			em.persist(member);
			
			Team team = new Team();
			team.setName("TeamA");
			team.getMembers().add(member);	// 연관관계가 주인이 아닌 역방향에만 값을 넣어 실제 memeber 테이블의 외래키 값은 Null이 된다.
			em.persist(team);
			
			
			em.flush(); // 영속성 컨텍스트에 있는 SQL를 DB와 동기화 해 준다.
			em.clear(); // 영속성 컨텍스트를 초기화 해준다.
			
			
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
	 * 좋은 예제
	 * 순수한 객체 관계를 고려하면 항상 양쪽다 값을 입력해야 한다.
	 * 1. 연관관계 주인 객체
	 * 2. 역방향 객체
	 * 
	 */
	public void 양방향연관관계_좋은예() {
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
			 * 좋은 예제
			 * 순수한 객체 관계를 고려하면 항상 양쪽다 값을 입력해야 한다.
			 */
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);
			
			
			Member member = new Member();
			member.setName("memeber1");
			member.setTeam(team);		// 참조하는 Team 객체 주입 : 외래키 삽입됨. 1. 연관관계 주인 객체
			em.persist(member);
			 
			team.getMembers().add(member);		// 참조하는 Member 객체 주입. : 조회용 2. 역방향 객체
			
			
			em.flush(); // 영속성 컨텍스트에 있는 SQL를 DB와 동기화 해 준다.
			em.clear(); // 영속성 컨텍스트를 초기화 해준다.
			
			/**
			 * 연관관계 주인에만 값을 넣는 경우 : Team 객체에 Member 객체를 주입하지 않는 경우 - getMembers().add(member1)
			 * 
			 * 1. flush와 clear를 해주는 경우
			 * 조회 전 DB에서 직접 조회를 하기 때문에 JPA가 Member 객체를 조회 시 참조 객체인 Team 객체 안에 List<Member>에 데이터를 주입해 준다.
			 * 따라서 Member를 조회 시 getTeam().getMembers 를 통해 Member의 조회가 가능하다.
			 * 
			 * 2.	flush와 clear를 해주지 않은 경우	  
			 * 1차 캐시 저장 시 Team객체 안에 Member를 주입해 주지 않을 경우 조회 시 memeber가  되지 않는다. 이해하지? 1차캐시 개념 필요. 
			 * 
			 * 따라서 양쪽 다 주입해주는게 좋다. 
			 */
			
			// 조회 
			Member findMember =  em.find(Member.class, member.getId());
			List<Member> members = findMember.getTeam().getMembers();
			
			for (Member m : members) {
				System.out.println("member : " + m.getName());
			}
			
			//-------------------------------------------------------------------------------------------------------------------
			
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
	 * 연관관계 편의 메소드를 생성하자
	 * 1. 연관관계 주인 객체에 편의 메소드 생성
	 * 2. 역방향 객체에 편의 메소드 생성
	 * 
	 * 단, 둘중에 하나만 셋팅해야 한다.!!! 무한루프 걸릴 수 있음.
	 * 
	 */
	public void 양방향연관관계_좋은예_개선안() {
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
			
			
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);
			
			
			Member member = new Member();
			member.setName("memeber1");
			/**
			 * 1. 연관관계 주인 객체에 편의 메소드 생성
			 * Memeber 객체 안에 changeTeam 메소드(연관관계 편의 메소드)를 생성해서 관리
			 * 
			 	public void changeTeam(Team team) {
					this.team = team;
					team.getMembers().add(this);		
				}
			 * 
			 */
			member.changeTeam(team);
			em.persist(member);
			
			/**
			 * 2. 역방향 객체에 편의 메소드 생성
			 * 
			 	public void addMemebers(Member member) {
					member.setTeam(this);
					members.add(member);
				}
			 */
			team.addMemebers(member);
			
			
			em.flush(); // 영속성 컨텍스트에 있는 SQL를 DB와 동기화 해 준다.
			em.clear(); // 영속성 컨텍스트를 초기화 해준다.
			
			
			// 조회 
			Member findMember =  em.find(Member.class, member.getId());
			List<Member> members = findMember.getTeam().getMembers();
			
			for (Member m : members) {
				System.out.println("member : " + m.getName());
			}
			
			//-------------------------------------------------------------------------------------------------------------------
			
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
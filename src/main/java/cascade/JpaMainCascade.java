package cascade;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


public class JpaMainCascade {
	public static void main(String[] args) {
		고아객체();
	}
	
	
	/**
	 *
	 	영속성 전이: CASCADE
		• 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들도 싶을 때
		• 예: 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장.


		영속성 전이: CASCADE - 주의!
		• 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
		• 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐
		
		
		사용 시 주의
		- 자식 객체가 하나의 부모 객체로만 관리되는 경우에만 사용해야 한다. 
		- 즉, 자식 객체의 소유자가 하나일 경우에만 사용
	 *
	 */
	public static void cascade() {
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
			 * cascade 사용 전
			 */
			Child child1 = new Child();
			child1.setName("child1");
			em.persist(child1);
			
			Child child2 = new Child();
			child2.setName("child2");
			em.persist(child2);
			
			Parent parent1 = new Parent();
			parent1.setName("parent1");
			parent1.addChilds(child1);
			parent1.addChilds(child2);
			
			
			em.persist(parent1);
			
			
			
			/**
			 * cascade 사용한 경우
			 * 자식 객체의 persist를 해주지 않아도 JPA가 자동으로 해줌.
			 * - 자식 객체도 모두 insert 된다.
			 */
			Child child3 = new Child();
			child3.setName("child1");

			Child child4 = new Child();
			child4.setName("child2");
			
			Parent parent2 = new Parent();
			parent2.setName("parent1");
			parent2.addChilds(child3);
			parent2.addChilds(child4);

			em.persist(parent2);
			
			
			
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
	 * \
	 	고아 객체 
		• 고아 객체 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
		• orphanRemoval = true 
		• Parent parent1 = em.find(Parent.class, id); 
			parent1.getChildren().remove(0);  <- 자식 엔티티를 컬렉션에서 제거 하면 아래와 같이 Delete 쿼리 생성
		• DELETE FROM CHILD WHERE ID=?

		
		고아 객체 - 주의
		• 참조가 제거된(컬렉션에서 제거) 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
		• 참조하는 곳이 하나일 때 사용해야함! 
		• 특정 엔티티가 개인 소유할 때 사용
		• @OneToOne, @OneToMany만 가능
		• 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화 하면, 부모를 제거할 때 자식도 함께 
			제거된다. 이것은 CascadeType.REMOVE처럼 동작한다
			
			
			
		영속성 전이 + 고아 객체, 생명주기
		• CascadeType.ALL + orphanRemoval=true 
		• 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
		• 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있음
		• 도메인 주도 설계(DDD)의 Aggregate Root개념을 구현할 때 유용
			
	 */
	public static void 고아객체() {
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
			
			

			Child child3 = new Child();
			child3.setName("child1");
			
			Child child4 = new Child();
			child4.setName("child2");
			
			Parent parent = new Parent();
			parent.setName("parent1");
			parent.addChilds(child3);
			parent.addChilds(child4);

			
			em.persist(parent);
			em.persist(child3);
			em.persist(child4);
			
			em.flush();
			em.clear();
			
			
			Parent findParent = em.find(Parent.class, parent.getId());
			// 컬렉션에서 자식 객체를 삭제하게 되면 DELETE 쿼리가 실행된다.
			findParent.getChilds().remove(0);
			
			// orphanRemoval = true 인 경우 부모 객체를 삭제하면 자식 객체는 자동으로 삭제 된다. 
			em.remove(findParent);
			
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

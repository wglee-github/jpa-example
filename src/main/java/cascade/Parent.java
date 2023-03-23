package cascade;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

//@Entity
public class Parent {
	
	@Id @GeneratedValue
	@Column(name = "PARENT_ID")
	private Long id;
	
	private String name;
	
	/**
	 * 영속성 전이 옵션 cascade
	 * 
	 	CASCADE의 종류
		• ALL: 모두 적용(실무사용)
		• PERSIST: 영속, 저장할 때 만 적용된다.(실무사용)
		• REMOVE: 삭제
		• MERGE: 병합
		• REFRESH: REFRESH
		• DETACH: DETACH
		
		
		orphanRemoval = true
		• 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
		
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Child> childs = new ArrayList<Child>();
	
	/**
	 * Method Desc : 양뱡향 연관관계 편의 메소드
	 *  
	 * <pre>{@code
	 * childs.add(child); 
	 * child.setParent(this);
	 * }</pre>
	 * @param child
	 * 
	 */
	public void addChilds(Child child) {
		childs.add(child);
		child.setParent(this);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Child> getChilds() {
		return childs;
	}

	public void setChilds(List<Child> childs) {
		this.childs = childs;
	}
	
}

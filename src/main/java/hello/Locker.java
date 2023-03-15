package hello;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

//@Entity
public class Locker {

	@Id @GeneratedValue
	@Column(name = "LOCKER_ID")
	private Long id;
	
	private String name;
	
	@OneToOne(mappedBy = "locker")
	private Member members;
	
	
	/**
	 * 1:1 양방향 연관관계 편의 메소드
	 * @return
	 */
	public void addMembers(Member member) {
		member.setLocker(this);
		this.members = member;
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
	
	
}

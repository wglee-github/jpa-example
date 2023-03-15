package hello;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

//@Entity
public class Team {

	@Id @GeneratedValue
	@Column(name = "TEAM_ID")
	private Long id;
	
	private String name;

	/**
	 * 양방향 연관관계를 위해서 @OneToMany 선언 후 양방향 할 변수명을 mappedBy에 맵핑해준다.
	 *  mappedBy는 읽기 전용이다.
	 */
	@OneToMany(mappedBy = "team")	
	private List<Member> members = new ArrayList<Member>();
	
	
	/**
	 * 연관관계 편의 메소드
	 * @param member
	 */
	public void addMemebers(Member member) {
		member.setTeam(this);
		members.add(member);
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

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
}
package hello;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;


/**
 * DDL 생성 기능은 DDL을 자동생성할 때만 사용됨. 옵션이 추가되어 있다고 해도 아무일도 일어나지 않음. JPA 실행로직에는 영향을 주지 않는다.
 * @author CZ-WGLEE-N1
 *
 */
//@Entity	// Class 객체 관리를 JPA에게 일임
//@Table(name = "Member") // cㅣass 객체와 DB 테이블 맵핑
//@Table(uniqueConstraints = {@UniqueConstraint( name = "ID_NAME_UNIQUE", columnNames = {"id", "name"} )}) // 유니크 제약조건 추가. * DDL 생성 기능
@SequenceGenerator( name = "MEMBER_SEQ_GENERATOR", sequenceName = "MEMBER_SEQ", initialValue = 1, allocationSize = 1)
public class Member {

	@Id @GeneratedValue(strategy = GenerationType.AUTO , generator = "MEMBER_SEQ_GENERATOR")
	@Column(name = "MEMBER_ID")
	private long id;
	
//	@Column(unique = true, length = 10) 		// unique 제약조건을 추가해준다. * DDL 생성 기능
//	@Column(nullable = false, length = 10)    // * DDL 생성 기능
	@Column(name = "USERNAME")
	private String name;
	
	// 테이블 연관관계 : 외래키 사용
//	@Column(name = "TEAM_ID")
//	private Long teamId;
	
	// 객체 연관관계
	@ManyToOne  // N : 1 -> Member class 기준(연관관계 주인)으로 판단하면 된다.
	@JoinColumn(name = "TEAM_ID") // 조인할 컬럼. (FK)
	private Team team;
	
	/**
	 * 1:1 관계 - FK에 유니크 제약조건도 같이 걸어줘야 한다. 당연히 1:1 이니까 여러개 생길 수 있는 여지를 막아야 하기 때문에
	 */
	@OneToOne
	@JoinColumn(name = "LOCKER_ID")
	private Locker locker;
	
	/**
	 * N:M 관계
	 * 실무에서는 사용하지 않는다.
	 */
	@ManyToMany
	@JoinTable(name = "MEMBER_PRODUCT")
	private List<Product> products = new ArrayList<Product>();
	
	// JPA는 꼭 기본 생성자를 넣어줘야 한다.
	public Member() {}
	
	public Member(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * 연관관계 편의 메소드
	 * @param team
	 */
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);		
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public Long getTeamId() {
//		return teamId;
//	}
//
//	public void setTeamId(Long teamId) {
//		this.teamId = teamId;
//	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}
	
}
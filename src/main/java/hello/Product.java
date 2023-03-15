package hello;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

/**
 * N:M 연관관계를 설명하기 위한 클래스
 * @author wglee
 *
 */
//@Entity
public class Product {

	@Id @GeneratedValue
	@Column(name = "PRODUCT_ID")
	private Long id;
	
	private String name;


	/**
	 * N:M 관계 - 양방향 연관관계 설정
	 * 실무에서 사용하지 말자.
	 */
	@ManyToMany(mappedBy = "products")
	private List<Member> members = new ArrayList<Member>();

	
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

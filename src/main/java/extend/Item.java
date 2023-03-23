package extend;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * 
 * 상속관계 맵핑
 * @author CZ-WGLEE-N1
 *
 * 1.단일 테이블 전략 : JPA 기본 전략
 * @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
 * - 객체간 상속이 이루어진 경우 통합테이블 하나를 생성한다.
 * - 해당 전략은 @DiscriminatorColumn 를 선언하지 않아도 DType이 자동으로 생성된다.
 * 
 * 2.조인 테이블 전략 
 * @Inheritance(strategy = InheritanceType.JOINED)
 * - 테이블 생성 시 부모와 자식 테이블 각각 생성한다.
 * - 부모의 PK가 자식의 FK로 생성된다.
 * - 연관관계 맵핑 해주지 않아도 된다.
 *
 * 3. 구현클래스마다 테이블 전략
 * @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
 * - 부모객체 테이블을 만들지 않고 자식객체(서브타입) 테이블들만 생성한다.
 * - 단, 부모 객체는 abstract로 만들어야 한다. 
 * - 자식 객체 각각 만들어 졌기 때문에 @DiscriminatorColumn 옵션은 필요 없음.
 * 
 * ** 아무 설정 없이 테이블을 생성하면 JPA에서는 통합테이블을 생성한다.
 *
 * @DiscriminatorColumn
 * - 최초 테이블 생성 시 자식 객체를 구분하기 위한 컬럼을 자동 생성해 준다. name 옵션으로 변경 가능.
 *   default 컬럼 : DTYPE varchar(31) not null
 * - 데이터 입력 시 자식 객체의 class 명이 자동 입력된다. 
 *   입력되는 값을 지정하고 싶은 경우 자식 객체 class 위에 @DiscriminatorValue("A")를 선언하고 "" 안에 정의하고 싶은 값 넣으면 된다.  
 * 
 */
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "cd_type_item")
//@Entity
public abstract class Item {

	@Id @GeneratedValue
	@Column(name = "ITEM_ID")
	private Long id;
	
	private String name;
	
	private Long price;

	
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

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}
	
}

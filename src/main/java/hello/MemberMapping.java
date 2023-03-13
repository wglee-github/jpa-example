package hello;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.annotation.processing.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;


/**
 * 기본키 맵핑에 사용되는 어노테이션
 * 
 * @SequenceGenerator
 * 1. name : 식별자생성기이름 / default : 필수
 * 2. sequenceName : 데이터베이스에등록되어있는시퀀스이름 / default : hibernate_sequence
 * 3. initialValue : DDL 생성시에만사용됨, 시퀀스 DDL을생성할때처음 1 시작하는 수를지정한다. / default : 1
 * 4. allocationSize : 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨) 데이터베이스 시퀀스값이 하나씩 증가하도록 설정 되어있으면 이값을 반드시 1로 설정해야한다 / default : 50
 * 5. catalog, schema : 데이터베이스 catalog, schema 이름
 * Ex. @SequenceGenerator( name = "MEMBER_SEQ_GENERATOR", sequenceName = "MEMBER_SEQ", initialValue = 1, allocationSize = 1)
 * 
 * 
 * @TableGenerator
 * 1. name : 식별자 생성기 이름 / default : 필수
 * 2. table : 키 생성 테이블명 / default : hibernate_sequences
 * 3. pkColumnName : 시퀀스 컬럼명 / default : sequence_name
 * 4. valueColumnNa : 시퀀스 값 컬럼명 / default : next_val 
 * 5. pkColumnValue : 키로사용할값이름 / default : 엔티티이름
 * 6. initialValue : 초기값, 마지막으로 생성된 값이 기준이다. / default : 0
 * 7. allocationSize : 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨) / default : 50
 * 8. catalog, schema : 데이터베이스 catalog, schema 이름
 * 9. uniqueConstraint s(DDL) : 유니크 제약조건을 지정할 수 있다. 
 * Ex. @TableGenerator(name = "MEMBER_SEQ_GENERATOR", table = "MY_SEQUENCES", pkColumnValue = "MEMBER_SEQ", allocationSize = 1)
 */
//@Entity
public class MemberMapping {

	/**
	 * 기본기 맵핑에 사용되는 어노테이션
	 * 
	 * @GeneratedValue : default auto 
	 * 1. IDENTITY : 데이터베이스에 위임 (Mysql)
	 *    Ex. @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * 2. SEQUENCE : 데이터베이스 시퀀스오브젝트 사용(Oracle). Class 위에 @SequenceGenerator 선언하여 시퀀스를 지정이 가능하고, 필드위의 @GeneratedValue에 generator를 @SequenceGenerator에 선언한 name으로 맵핑해 준다.
	 *               필드 타입을 Long, Integer
	 *    Ex. @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
	 * 
	 * 3. TABLE : 키생성 전용테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략. Class 위에 @TableGenerator 선언하여 시퀀스 테이블을 지정이 가능하고,  필드위의 @GeneratedValue에 generator를 @TableGenerator에 선언한 name으로 맵핑해 준다.
	 *    Ex. @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR") 
	 * 
	 * 4. AUTO : default. DB에 따라 자동 지정  
	 * 
	 * @Id : 사용자가 필드 값을 직접 할당.
	 */
	@Id
	private Long id;
	
	/**
	 * 컬럼 맵핑
	 * @Column(insertable = false)	// 맵핑 컬럼에 데이터 추가 여부 defautl true
	 * @Column(updatable = false) 	// 맵핑 컬럼에 데이터 변경 여부 defautl true
	 * @Column(nullable = false) 	// null 허용 여부, false면 not null
	 * @Column(unique = true) 		// 유니크 제약조건 추가 여부. 자동생된 유니크 제약조전 명이 랜덤하게 생성됨 주의
	 * @Column(columnDefinition = "varchar(100) default 'EMPTY'") // 직접 컬럼 정보를 줄 수 있다.
	 * @Column(length = 10) 		// 컬럼 길이
	 * @Column(precision = 19, scale = 2) // BigDecimal 타입에서사용한다(BigInteger도사용할수있다). precision은소수점을포함한전체자릿수를, scale은소수의자릿수 다. 참고로 double, ﬂoat 타입에는적용되지않는다. 아주큰숫자나 정밀한소수를다루어야할때만사용한다.
	 * @Column(name = "name") 
	 * 
	 * 
	 */
	@Column(name = "name")
	private String username;
	
	private Integer age;
	
	/**
	 * default EnumType.ORDINAL 
	 * EnumType.ORDINAL: enum 순서를 데이터베이스에 저장 <- 사용하지 말자.
     * EnumType.STRING: enum 이름을 데이터베이스에 저장 
	 */
	@Enumerated(EnumType.STRING)	// enum 맵핑
	private RoleType roleType;

	@Temporal(TemporalType.TIMESTAMP)	// 날짜 맵핑
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)	// 날짜 맵핑
	private Date lastModifiedDate;
	
	// java 8 이후 @Temporal 대신 아래 필드타입 사용하면 됨.(최신 하이버네이트 지원됨)
	private LocalDate cDate;			// 날짜
	private LocalDateTime cDateTime;	// 날짜+시간
	
	@Lob	// CLOB, BLOB 맵핑. 필드 타입이 String 이면 CLOB, 그 외에 BLOB 
	private String description;
	
	@Transient // 특정 필드를 컬럼에 맵핑하지 않음(맵핑 무시)
	private int temp;
	
}
package valuetype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

//@Entity
public class Member {

	@Id @GeneratedValue
	@Column(name = "MEMBER_ID")
	private Long id;
	
	private String name;
	 
	 //기간 : Period class로 분리 
//	 private LocalDateTime startDate;
//	 private LocalDateTime endDate;
	@Embedded
	private Period workPeriod;
	 
	 //주소 : Address class로 분리
//	 private String city;
//	 private String streets;
//	 private String zipcode;
	@Embedded
	private Address homeAddress;
	 
	 /**
	  	@AttributeOverride: 속성 재정의
		• 한 엔티티에서 같은 값 타입을 사용하면? @Embeddable 로 정의한 객체의 컬럼명이 종복되기 때문에 안된다.
		• 따라서 아래와 같이 @AttributeOverrides, @AttributeOverride를 사용해서 컬러 명 속성을 재정의
		• 임베디드 타입이 null 이면 맵핑한 컬럼 값은 모두 null이다.
			 private Address workAddress = null; 이면 Address 객체의 컬럼값도 모두 null이다.
	  */
	 @Embedded
	 @AttributeOverrides({
         @AttributeOverride(name="city",  column=@Column(name="WORK_CITY")),
         @AttributeOverride(name="street", column=@Column(name="WORK_STREET")),
         @AttributeOverride(name="zipcode", column=@Column(name="WORK_ZIPCODE"))
	 })
	private Address workAddress;
	 
	 /**
	  * 값 타입 컬렉션
	  */
	@ElementCollection
	@CollectionTable(name = "FAVORITE_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
	@Column(name = "FOOD_NAME")
	private Set<String> favoriteFoods = new HashSet<String>();
	
	/**
	 * 값 타입 컬렉션
	 */
	@ElementCollection
	@CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
	private List<Address> addresseHistory = new ArrayList<Address>();
	 
	
	/**
	 * 값 타입 컬렉션을 엔티티로 변환하여 사용
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "MEMBER_ID")
	private List<AddressEntity> addresseEntities = new ArrayList<AddressEntity>();
	 
	
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
	public Period getWorkPeriod() {
		return workPeriod;
	}
	public void setWorkPeriod(Period workPeriod) {
		this.workPeriod = workPeriod;
	}
	public Address getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;
	}
	public Address getWorkAddress() {
		return workAddress;
	}
	public void setWorkAddress(Address workAddress) {
		this.workAddress = workAddress;
	}
	/**
	 * @return the favoriteFoods
	 */
	public Set<String> getFavoriteFoods() {
		return favoriteFoods;
	}
	/**
	 * @param favoriteFoods the favoriteFoods to set
	 */
	public void setFavoriteFoods(Set<String> favoriteFoods) {
		this.favoriteFoods = favoriteFoods;
	}
	/**
	 * @return the addresseHistory
	 */
//	public List<Address> getAddresseHistory() {
//		return addresseHistory;
//	}
	/**
	 * @param addresseHistory the addresseHistory to set
	 */
//	public void setAddresseHistory(List<Address> addresseHistory) {
//		this.addresseHistory = addresseHistory;
//	}
	/**
	 * @return the addresseHistory
	 */
	public List<Address> getAddresseHistory() {
		return addresseHistory;
	}
	/**
	 * @param addresseHistory the addresseHistory to set
	 */
	public void setAddresseHistory(List<Address> addresseHistory) {
		this.addresseHistory = addresseHistory;
	}
	/**
	 * @return the addresseEntities
	 */
	public List<AddressEntity> getAddresseEntities() {
		return addresseEntities;
	}
	/**
	 * @param addresseEntities the addresseEntities to set
	 */
	public void setAddresseEntities(List<AddressEntity> addresseEntities) {
		this.addresseEntities = addresseEntities;
	}
	
}

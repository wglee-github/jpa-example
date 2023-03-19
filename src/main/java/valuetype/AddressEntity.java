package valuetype;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class AddressEntity {

	@Id @GeneratedValue
	private Long id;
	
	private Address address;

	public AddressEntity() {
		super();
	}

	public AddressEntity(String city, String street, String zipdode) {
		this.address = new Address(city, street, zipdode);
	}
	
}

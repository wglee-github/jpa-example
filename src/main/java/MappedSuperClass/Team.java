package MappedSuperClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * 
 * @author wglee
 *
 *
 	create table Team (
       TEAM_ID bigint not null,
        createBy varchar(255),
        createDate timestamp(6),
        lastModifiedBy varchar(255),
        lastModifiedDate timestamp(6),
        name varchar(255),
        primary key (TEAM_ID)
)
 */
//@Entity
public class Team extends BaseEntity{
	
	@Id @GeneratedValue
	@Column(name = "TEAM_ID")
	private Long id;
	
	private String name;

	
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

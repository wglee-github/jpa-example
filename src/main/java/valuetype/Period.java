package valuetype;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

@Embeddable
public class Period {

	 private LocalDateTime startDate;
	 private LocalDateTime endDate;
	 
	 
	 
	public Period() {
		super();
	}
	
	public Period(LocalDateTime startDate, LocalDateTime endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public boolean isWork() {
		// 현지 시간이 작업시간인지 등의 로직이 들어갈 수 있다.
		return true;
	}
	
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	public LocalDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	 
	 
	  
}

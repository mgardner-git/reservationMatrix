package dto;

import model.Reservable;

public class ReservableDto {

	private int id;
	private String name;
	
	public ReservableDto() {
		
	}
	
	public ReservableDto(Reservable reservableEntity) {
		this.id = reservableEntity.getId();
		this.name = reservableEntity.getName();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

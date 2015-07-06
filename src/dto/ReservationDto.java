package dto;

import java.util.ArrayList;
import java.util.List;

import model.Reservable;
import model.ReservableReservation;
import model.Reservation;


public class ReservationDto {

	private List<ReservableDto> reservables;
	private Long beginDate;
	private Long endDate;
	
	public ReservationDto() {
		
	}
	
	public ReservationDto(Reservation reservation) {
		this.beginDate = reservation.getBeginDate().getTime();
		this.endDate = reservation.getEndDate().getTime();
		this.reservables = new ArrayList<ReservableDto>();
		for (ReservableReservation linker : reservation.getReservableReservations()) {
			Reservable r = linker.getReservable();
			ReservableDto reservableForm = new ReservableDto(r);
			this.reservables.add(reservableForm);		
		}
	}

	public List<ReservableDto> getReservables() {
		return reservables;
	}

	public void setReservables(List<ReservableDto> reservables) {
		this.reservables = reservables;
	}

	public Long getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Long beginDate) {
		this.beginDate = beginDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
}

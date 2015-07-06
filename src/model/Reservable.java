package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the reservable database table.
 * 
 */
@Entity
@NamedQuery(name="Reservable.findAll", query="SELECT r FROM Reservable r")
public class Reservable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String name;

	//bi-directional many-to-one association to ReservableReservation
	@OneToMany(mappedBy="reservable")
	private List<ReservableReservation> reservableReservations;

	public Reservable() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ReservableReservation> getReservableReservations() {
		return this.reservableReservations;
	}

	public void setReservableReservations(List<ReservableReservation> reservableReservations) {
		this.reservableReservations = reservableReservations;
	}

	public ReservableReservation addReservableReservation(ReservableReservation reservableReservation) {
		getReservableReservations().add(reservableReservation);
		reservableReservation.setReservable(this);

		return reservableReservation;
	}

	public ReservableReservation removeReservableReservation(ReservableReservation reservableReservation) {
		getReservableReservations().remove(reservableReservation);
		reservableReservation.setReservable(null);

		return reservableReservation;
	}

}
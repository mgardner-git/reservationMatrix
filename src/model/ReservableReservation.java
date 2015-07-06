package model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the reservable_reservation database table.
 * 
 */
@Entity
@Table(name="reservable_reservation")
@NamedQuery(name="ReservableReservation.findAll", query="SELECT r FROM ReservableReservation r")
public class ReservableReservation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	//bi-directional many-to-one association to Reservation
	@ManyToOne
	@JoinColumn(name="fk_reservation_id")
	private Reservation reservation;

	//bi-directional many-to-one association to Reservable
	@ManyToOne
	@JoinColumn(name="fk_reservable_id")
	private Reservable reservable;

	public ReservableReservation() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Reservation getReservation() {
		return this.reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public Reservable getReservable() {
		return this.reservable;
	}

	public void setReservable(Reservable reservable) {
		this.reservable = reservable;
	}

}
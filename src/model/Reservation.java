package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the reservation database table.
 * 
 */
@Entity
@NamedQuery(name="Reservation.findAll", query="SELECT r FROM Reservation r")
public class Reservation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date beginDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	//bi-directional many-to-one association to ReservableReservation
	@OneToMany(mappedBy="reservation", cascade = CascadeType.PERSIST)	
	private List<ReservableReservation> reservableReservations;

	public Reservation() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<ReservableReservation> getReservableReservations() {
		return this.reservableReservations;
	}

	public void setReservableReservations(List<ReservableReservation> reservableReservations) {
		this.reservableReservations = reservableReservations;
	}

	public ReservableReservation addReservableReservation(ReservableReservation reservableReservation) {
		getReservableReservations().add(reservableReservation);
		reservableReservation.setReservation(this);

		return reservableReservation;
	}

	public ReservableReservation removeReservableReservation(ReservableReservation reservableReservation) {
		getReservableReservations().remove(reservableReservation);
		reservableReservation.setReservation(null);

		return reservableReservation;
	}

}
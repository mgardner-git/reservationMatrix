package reservationMatrix.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Reservable;
import model.ReservableReservation;
import model.Reservation;

import org.codehaus.jackson.map.ObjectMapper;

import dto.ReservableDto;
import dto.ReservationDto;

/**
 * Servlet implementation class ReservablesServlets
 */
@WebServlet("/ReservationsServlet")
public class ReservationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public ReservationsServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		Long beginDateT = Long.parseLong(request.getParameter("beginDate"));
		Long endDateT = Long.parseLong(request.getParameter("endDate"));
		Date beginDate = new Date(beginDateT);
		Date endDate = new Date(endDateT);
	
		try {
			List<Reservation> reservations = performSearch(beginDate, endDate);
			List<ReservationDto> reservationForms = new ArrayList<ReservationDto>();
			for (Reservation reservation : reservations) {
				reservationForms.add(new ReservationDto(reservation));			
			}
		
			
			ObjectMapper mapper = new ObjectMapper();
			String resultsInJson = mapper.writeValueAsString(reservationForms);	
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);			
			response.getWriter().write(resultsInJson);	
		}catch (Exception e) {
			e.printStackTrace(); //TODO Error handling
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null){
				sb.append(line);
			}
			String reservationJson = sb.toString();
			ObjectMapper mapper = new ObjectMapper();
			ReservationDto reservationForm = mapper.readValue(reservationJson, ReservationDto.class);
			createReservation(reservationForm);
		 
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(e.getMessage());
		}
	}
	
	private void createReservation(ReservationDto reservationForm) {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("ReservationMatrix");
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Reservation reservationEntity = new Reservation();
			reservationEntity.setBeginDate(new Date(reservationForm.getBeginDate()));
			reservationEntity.setEndDate(new Date(reservationForm.getEndDate()));
			em.persist(reservationEntity);
			em.flush();
			
			reservationEntity.setReservableReservations(new ArrayList<ReservableReservation>());
			for (ReservableDto reservableForm : reservationForm.getReservables()) {
				ReservableReservation linkEntity = new ReservableReservation();
				Reservable reservableEntity = new Reservable();
				reservableEntity.setId(reservableForm.getId());
				reservableEntity.setName(reservableForm.getName());
				linkEntity.setReservable(reservableEntity);
				linkEntity.setReservation(reservationEntity);
				reservationEntity.getReservableReservations().add(linkEntity);
				em.persist(linkEntity);
			}
			
			em.getTransaction().commit();			
		

	}
	private List<Reservation> performSearch(Date search1, Date search2) throws Exception{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("ReservationMatrix");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
	
		CriteriaQuery<Reservation> cq = builder.createQuery(Reservation.class);		
	    Root<Reservation> root = cq.from(Reservation.class);	    
	  	cq.select(root);
	  	
	  	Path<Date> endDatePath = root.get("endDate");
	  	Path<Date> beginDatePath = root.get("beginDate");
	  	
		Predicate b1 = builder.greaterThan(endDatePath, search1);		
		Predicate b2 = builder.lessThan(endDatePath, search2);
		Predicate beginPredicate = builder.and(b1,b2);
		
		
		Predicate e1 = builder.greaterThan(beginDatePath, search1);
		Predicate e2 = builder.lessThan(beginDatePath, search2);
		Predicate endPredicate = builder.and(e1,e2);
		
		Predicate finalPredicate = builder.or(beginPredicate, endPredicate);
		
		cq.where(finalPredicate);
		Query rQuery = em.createQuery(cq);
		@SuppressWarnings("unchecked")
		List<Reservation> results = rQuery.getResultList();
		return results;
		
	}

}

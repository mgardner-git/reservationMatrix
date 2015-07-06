package reservationMatrix.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import model.Reservable;
import dto.ReservableDto;

/**
 * Servlet implementation class ReservablesServlets
 */
@WebServlet("/ReservablesServlets")
public class ReservablesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public ReservablesServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("ReservationMatrix");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
			TypedQuery<Reservable> query = em.createNamedQuery("Reservable.findAll", Reservable.class);

			List<Reservable> reservableEntities = query.getResultList();
			List<ReservableDto> reservableForms = new ArrayList<ReservableDto>();
			for (Reservable reservableEntity : reservableEntities) {
				reservableForms.add(new ReservableDto(reservableEntity));
			}
		em.getTransaction().commit();
		
		ObjectMapper mapper = new ObjectMapper();
		String resultsInJson = mapper.writeValueAsString(reservableForms);	
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);			
		response.getWriter().write(resultsInJson);	

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

package srv;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mBankExceptions.MBankException;
import mbank.MBank;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import wrapClasses.ClientBeanWrapper;
import actions.ClientActions;
import beans.AccountsBean;
import beans.ActivityBean;
import beans.ClientBean;
import beans.DepositBean;

import com.sun.jersey.api.Responses;
import com.sun.jersey.spi.resource.Singleton;
import com.util.SummaryDetails;
import com.util.WrappedActivities;
import com.util.WrappedDeposits;

/**
 * This class defines the control of MBank RESTful services 
 * @author Simon Mor
 */

@Path(value = "/MBankRoot")
@Singleton
public class MBankRestSrvRoot {

	private MBank master = MBank.getMBank();
	
	public MBankRestSrvRoot() {super();}
	
	@Path("/populateDBData")
	@GET
	public Response populateDBData() {
		MBank.populateData();
		return Response.status(201).build();
	}

	/*
	 * Login handling section
	 */
	/**
	 * @param json
	 * @param req
	 * @return OK Response if success, and ClientBean as JSON Object
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(String json, @Context HttpServletRequest req){
		HttpSession session = null;
		ClientBean client = null;
		try {
			JSONObject js  = new JSONObject(json);
			client = new ClientBean();
			client.setId(js.getInt("id"));
			client.setPassword(js.getString("password"));
			client.setEmail(js.getString("email"));
		} catch (JSONException | MBankException e) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) master.login(client);
		if (ca == null) {
			return Response.status(Responses.NO_CONTENT).build();
		}
		client = ca.getClientObjectById(client.getId());
		session = req.getSession(true);
		session.setMaxInactiveInterval(15*60);
		session.setAttribute("clientAction", ca);
		session.setAttribute("id", client.getId());
		client = ca.getClientObjectById(client.getId());
		return Response.ok(new ClientBeanWrapper(client)).build();
	}
	
	/**
	 * @param req
	 * @return Created Response if success
	 */
	@GET
	@Path("/logout")
	public Response logout(@Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.removeAttribute("clientAction");
			session.removeAttribute("id");
			session.invalidate();
			return Response.status(201).build();
		}
		return Response.status(Responses.NOT_ACCEPTABLE).build();
	}
	
	/**
	 * @param req
	 * @return OK Response if success, and summaryDetails as JSON Object
	 */
	@Path("/summaryDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSummaryDetails(@Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		SummaryDetails summaryDetails = new SummaryDetails();
		if (session == null) {
			return Response.status(Responses.NOT_ACCEPTABLE).build();
		}
		ClientActions ca = null;
		int id = 0;
		try {
			ca = (ClientActions) session.getAttribute("clientAction");
			id = (int) session.getAttribute("id");
		} catch (Exception e) {
			return Response.status(Responses.NOT_ACCEPTABLE).build();
		}
		if (ca == null || id == 0) {
			return Response.status(Responses.NOT_ACCEPTABLE).build();
		}
		AccountsBean account = ca.viewAccountDetails(ca.getClientObjectById(id));
		summaryDetails.setAccountBalance(account.getBalance());
		List<DepositBean> deposits = ca.viewClientDeposits(ca.getClientObjectById(id));
		double depositsTotalBalance = 0;
		for (DepositBean depositBean : deposits) {
			depositsTotalBalance+=depositBean.getBalance();
		}
		summaryDetails.setDepositsBalance(depositsTotalBalance);
		summaryDetails.setAccountType(ca.getClientObjectById(id).getType());
		summaryDetails.setCreditLimit(account.getCreditLimit());
		return Response.ok(summaryDetails).build();
		
	}
	
	/**
	 * @return OK Response if success, and activates MBank desktop application.
	 */
	@GET
	@Path("/adminPanel")
	public Response loadAdminPanel() {
		master.start();
		return Response.ok().build();
	}

	/**
	 * @param json
	 * @param req
	 * @return OK Response if success
	 */
	@Path("/updateClientDestails")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateClientDestails(String json, @Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		ClientBean client = null;
		try {
			JSONObject js  = new JSONObject(json);
			client = new ClientBean();
			client.setId((int) session.getAttribute("id"));
			client.setEmail(js.getString("email"));
			client.setAddress(js.getString("address"));
			client.setName(js.getString("name"));
			client.setPassword(js.getString("password"));
			client.setPhone(js.getString("phone"));
		} catch (JSONException | MBankException e) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		ca.updateClientDetails(client);
		ClientBean refClient = ca.getClientObjectById(client.getId());
		if (!refClient.equals(client)) {
			return Response.status(Responses.CONFLICT).build();
		}
		return Response.ok().build();
	}
	
	/**
	 * @param req
	 * @return OK Response with AccountsBean Obj.
	 */
	@Path("/viewAccountDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewAccountDetaias(@Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		AccountsBean account = ca.viewAccountDetails(ca.getClientObjectById((int) session.getAttribute("id")));
		if (account == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		return Response.ok(account).build();
	}
	
	/**
	 * @param req
	 * @return OK Response with wrappedDeposits Obj.
	 */
	@Path("/viewClientDeposits")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewClientDeposits(@Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		List<DepositBean> depositsBeans = ca.viewClientDeposits(ca.getClientObjectById((int) session.getAttribute("id")));
		WrappedDeposits wrappedDeposits = new WrappedDeposits();
		wrappedDeposits.setWrappedDepositsFromDepositsBeans(depositsBeans);
		return Response.ok(wrappedDeposits).build(); 
	}
	
	/**
	 * @param amount
	 * @param req
	 * @return OK Response if deposit to account success.
	 */
	@Path("/depositToAccount")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response depositToAccount(String amount, @Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientBean client = ca.getClientObjectById((int) session.getAttribute("id"));
		try {
			ca.depositToAccount(client, Double.parseDouble(amount));
		} catch (NumberFormatException | MBankException e) {
			return Response.status(Responses.CONFLICT).build();
		}
		return Response.ok().build(); 
	}
	
	/**
	 * @param amount
	 * @param req
	 * @return OK Response if deposit to account success.
	 */
	@Path("/withdrawFromAccount")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response withdrawFromAccount(String amount, @Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientBean client = ca.getClientObjectById((int) session.getAttribute("id"));
		ca.withdrawFromAccount(client, Double.parseDouble(amount));
		return Response.ok().build();
	}
	
	/**
	 * @param newDepositInfo
	 * @param req
	 * @return OK Response if create new account successful.
	 */
	@Path("/createNewDeposit")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewDeposit(String newDepositInfo, @Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		int balance;
		int yearsToAdd;
		try {
			JSONObject js = new JSONObject(newDepositInfo);
			balance = js.getInt("amount");
			yearsToAdd = js.getInt("duration");
		} catch (JSONException e) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientBean client = ca.getClientObjectById((int) session.getAttribute("id"));
		Calendar cal = Calendar.getInstance();
		boolean typeIndicator = (yearsToAdd > 1) ? true:false;
		Date openingDate = new java.sql.Date(cal.getTimeInMillis()); 
		cal.add(Calendar.YEAR, yearsToAdd);
		Date closingDate = new java.sql.Date(cal.getTimeInMillis());
		DepositBean deposit = new DepositBean(client.getId(), balance, openingDate, closingDate);
		ca.createNewDeposit(client, deposit, typeIndicator);
		return Response.ok().build();
	}
	
	/**
	 * @param depositId
	 * @param req
	 * @return OK Response if pre open deposit successful.
	 */
	@Path("/preOpendeposit")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response preOpendeposit(String depositId, @Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		DepositBean deposit = ca.getDepositById(Integer.parseInt(depositId));
		ClientBean client = ca.getClientObjectById((int) session.getAttribute("id"));
		ca.preOpenDeposit(client, deposit);
		return Response.status(201).build();
	}
	
	/**
	 * @param req
	 * @return OK Response if view activities successful.
	 */
	@Path("/viewClientActivities")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewClientActivities(@Context HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		if (ca == null) {
			return Response.status(Responses.CONFLICT).build();
		}
		ClientBean client = ca.getClientObjectById((int) session.getAttribute("id"));
		List<ActivityBean> activities = ca.viewClientActivities(client);
		WrappedActivities wrappedActivities = new WrappedActivities();
		wrappedActivities.setWrappedActvitiesFromActvityBeans(activities);
		return Response.ok(wrappedActivities).build();
	}


}

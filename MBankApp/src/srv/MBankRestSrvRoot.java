package srv;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.RequestWrapper;

import mBankExceptions.MBankException;
import managers.DepositDBManager;
import mbank.MBank;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import parsers.DataExchanger;
import wrapClasses.ClientBeanWrapper;
import actions.AdminActions;
import actions.ClientActions;
import beans.AccountsBean;
import beans.ClientBean;
import beans.DepositBean;

import com.oracle.jrockit.jfr.DataType;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.xml.internal.stream.Entity;
import com.util.SummaryDetails;

/**
 * This class defines the control of MBank RESTful services 
 * @author Simon Mor
 */

@Path(value = "/MBankRoot")
@Singleton
public class MBankRestSrvRoot {

	private MBank master = MBank.getMBank();
	
	private final DataExchanger dataExchanger = new DataExchanger();

	public MBankRestSrvRoot() {}
	
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
	 * 
	 * @param id
	 * @param email
	 * @param password
	 * @return JSon object. null if failed.
	 * @throws JSONException 
	 * @throws MBankException 
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
	
	@Path("/summaryDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSummaryDetails(@Context HttpServletRequest req) {
		HttpSession session = req.getSession();
		SummaryDetails summaryDetails = new SummaryDetails();
		if (session == null) {
			return Response.status(Responses.NOT_ACCEPTABLE).build();
		}
		ClientActions ca = (ClientActions) session.getAttribute("clientAction");
		int id = (int) session.getAttribute("id");
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

	@GET
	@Path("/adminPanel")
	public Response loadAdminPanel() {
		master.start();
		return Response.ok().build();
	}


//	/*
//	 * Client handling section
//	 */
//	//	private HashMap<String, Object> getActionAndClientFromSession(HttpSession sess) {
//	//	sess = (HttpSession) req.getSession();
//	//	ClientActions ca = (ClientActions) session.getAttribute("action");
//	//	int clientId = (int) session.getAttribute("id");
//	//	ClientBean client = ca.getClientObjectById(clientId);
//	//	Map<String, Object> tempMap = new HashMap<>();
//	//	tempMap.put("action", ca);
//	//	tempMap.put("client", client);
//	//	tempMap.put("clientId", clientId);
//	//	return (HashMap<String, Object>) tempMap;
//	//}
//
//	/**
//	 * This method provide the current client's info. 
//	 * @return JSON description of current client
//	 */
//	@Path("/viewClientDetails")
//	@GET
//	//@Produces(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public String viewClientDetails() {
//		//	session = req.getSession();
//		//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//		//	ClientBean client = (ClientBean) sessionAttMap.get("client");
//		//	List<ClientBean> clientsList = new ArrayList<>();
//		//	clientsList.add(client);
//
//		List<ClientBean> testList = null;
////		jsonObject; = dataExchanger.marshallClients(testList);
//
//		AdminActions a = new AdminActions();
//		testList = new ArrayList<ClientBean>();
//		testList.add(a.getClientObjectById(108)); testList.add(a.getClientObjectById(109)); testList.add(a.getClientObjectById(110));
//		StringBuilder str = new StringBuilder();
//		str.append("{\"Clients\":["); 
//		for (ClientBean client : testList) {
//			str.append( "{\"name\":"+"\""+client.getName()+"\""+","+"\"password\":"+"\""+client.getPassword()+"\""+"}"+"," );
//		}
//		str.delete(str.length()-1, str.length());
//		str.append("]}");
//
//		return str.toString();
//	}
//
//	///**
//	// * This method updates a client's info.
//	// * @param password
//	// * @param address
//	// * @param email
//	// * @param phone
//	// * @return JSON description of an updated client
//	// */
//	//@Path("/updateClientDestails")
//	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String updateClientDestails(@FormParam("password") String password, 
//	//		@FormParam("address") String address, @FormParam("email") String email, @FormParam("phone") String phone) {
//	//	session = req.getSession(false);
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	ClientBean existingClient = (ClientBean) sessionAttMap.get("client");
//	//	ClientBean clientToUpdate = new ClientBean();
//	//	clientToUpdate.setId((int) sessionAttMap.get("clientId")); 
//	//	try {
//	//		clientToUpdate.setName(existingClient.getName());
//	//		clientToUpdate.setPassword(password);
//	//		clientToUpdate.setType(AccountType.getTypeFromString(existingClient.getType()));
//	//		clientToUpdate.setAddress(address);
//	//		clientToUpdate.setEmail(email);
//	//		clientToUpdate.setPhone(phone);
//	//		ca.updateClientDetails(clientToUpdate);
//	//		List<ClientBean> clientsList = new ArrayList<>();
//	//		clientsList.add(clientToUpdate);
//	//		
//	//		List<ClientBean> testList = null;
//	//		
//	//		jsonObject = util.xml2jason.XML.toJSONObject(dataExchanger.marshallClients(testList));
//	//	} catch (MBankException e) {
//	//		jsonObject.append("message", "Updating Failed");
//	//	}
//	//	return jsonObject.toString();
//	//}
//	//
//	///**
//	// * This method returns the information of current client's account.
//	// * @return JSON description of client's account
//	// */
//	//@Path("/viewAccountDetaias")
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String viewAccountDetaias() {
//	//	session = req.getSession();
//	//	ClientActions ca = (ClientActions) session.getAttribute("action");
//	//	int clientId = (int) session.getAttribute("id");
//	//	ClientBean client = ca.getClientObjectById(clientId);
//	//	AccountsBean account = ca.viewAccountDetails(client);
//	//	List<AccountsBean> accountsList = new ArrayList<>();
//	//	accountsList.add(account);
//	//	
//	//	List<AccountsBean> testList = null;
//	//	
//	//	jsonObject = util.xml2jason.XML.toJSONObject(dataExchanger.marshallAccounts(testList));
//	//	return jsonObject.toString();
//	//	
//	//}
//	//
//	///**
//	// *This method returns current client account's information 
//	// * @return JSON description of client's deposits
//	// */
//	//@Path("/viewClientDeposits")
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String viewClientDeposits() {
//	//	session = req.getSession();
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	List<DepositBean> depositsList =  ca.viewClientDeposits((ClientBean) sessionAttMap.get("client"));
//	//	
//	//	List<DepositBean> testList = null;
//	//	
//	//	jsonObject = XML.toJSONObject(dataExchanger.marshallDeposits(testList));
//	//	return jsonObject.toString();
//	//}
//	//
//	///**
//	// * This method returns the activities information of current client. 
//	// * @return JSON description of client's activities
//	// */
//	//@Path("/viewClientActivities")
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String viewClientActivities() {
//	//	session = req.getSession();
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	List<ActivityBean> activitiesList = ca.viewClientActivities((ClientBean) sessionAttMap.get("client"));
//	//	
//	//	List<DepositBean> testList = null;
//	//	
//	//	jsonObject = XML.toJSONObject(dataExchanger.marshallDeposits(testList));
//	//	return jsonObject.toString();
//	//}
//	//
//	///**
//	// * This method withdraws an amount from a client's account
//	// * @param stringAmount
//	// * @return JSON true/false of withdrawal attempt from a client's account
//	// */
//	//@Path("/withdrawFromAccount")
//	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String withdrawFromAccount(@FormParam("amount") String stringAmount) {
//	//	session = req.getSession();
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	double amount = Double.parseDouble(stringAmount);
//	//	boolean flag = ca.withdrawFromAccount((ClientBean) sessionAttMap.get("client"), amount);
//	//	
//	//	jsonObject = XML.toJSONObject(Boolean.toString(flag));
//	//	return jsonObject.toString();
//	//}
//	//
//	///**
//	// * This method deposits an amount to a client's account
//	// * @param stringAmount
//	// * @return JSON true/false of deposit attempt to a client's account
//	// */
//	//@Path("/depositToAccount")
//	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String depositToAccount(@FormParam("amount") String stringAmount) {
//	//	session = req.getSession();
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	double amount = Double.parseDouble(stringAmount);
//	//	boolean flag = false;
//	//	try {
//	//		ca.depositToAccount((ClientBean) sessionAttMap.get("client"), amount);
//	//		flag = true;
//	//	} catch (MBankException e) {
//	//		flag = false;
//	//	}
//	//	
//	//	jsonObject = XML.toJSONObject(Boolean.toString(flag));
//	//	return jsonObject.toString();
//	//}
//	//
//	///**
//	// * This method will create a new deposit for a current client.
//	// * @param stringAmount
//	// * @param duration
//	// * @return JSON description of client's newly opened deposit.
//	// */
//	//@Path("/createNewDeposit")
//	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String createNewDeposit(@FormParam("amount") String stringAmount,@FormParam("duration") String duration) {
//	//	session = req.getSession();
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	double amount = Double.parseDouble(stringAmount);
//	//	int yearsToAdd = Integer.parseInt(duration);
//	//	Calendar cal = Calendar.getInstance();
//	//	Date openingDate = new java.sql.Date(cal.getTimeInMillis()); 
//	//	cal.add(Calendar.YEAR, yearsToAdd);
//	//	Date closingDate = new java.sql.Date(cal.getTimeInMillis());
//	//	DepositBean deposit = new DepositBean((int) sessionAttMap.get("clientId"), amount, openingDate, closingDate);
//	//	DepositBean depositToReturn = ca.createNewDeposit((ClientBean) sessionAttMap.get("client"), deposit, ((yearsToAdd > 1) ? true:false));
//	//	List<DepositBean> depositsList = new ArrayList<>();
//	//	depositsList.add(depositToReturn);
//	//	
//	//	jsonObject = XML.toJSONObject(dataExchanger.marshallDeposits(depositsList));
//	//	return jsonObject.toString();
//	//}
//	//
//	///**
//	// * This method pre opens a specific deposit of current client.
//	// * @param depId
//	// * @return JSON true/false if a client's deposit successfully pre opened
//	// */
//	//@Path("/preOpendeposit")
//	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	//@Produces(MediaType.APPLICATION_JSON)
//	//public String preOpendeposit(@FormParam("depId") String depId) {
//	//	session = req.getSession();
//	//	Map<String, Object> sessionAttMap = getActionAndClientFromSession(session);
//	//	ClientActions ca = (ClientActions) sessionAttMap.get("action");
//	//	int depositId = Integer.parseInt(depId);
//	//	DepositBean deposit = ca.getDepositById(depositId);
//	//	ca.preOpenDeposit((ClientBean) sessionAttMap.get("client"), deposit);
//	//	DepositBean testDeposit = null;
//	//	testDeposit = ca.getDepositById(depositId);
//	//	boolean flag = testDeposit == null ? true:false;
//	//	
//	//	jsonObject = XML.toJSONObject(Boolean.toString(flag));
//	//	return jsonObject.toString();
//	//}
//
//
//
//
//
//


}

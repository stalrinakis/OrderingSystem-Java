<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.sun.jersey.api.client.Client"%>
<%@ page import="com.sun.jersey.api.client.ClientResponse"%>
<%@ page import="com.sun.jersey.api.client.WebResource"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.text.ParseException"%>

<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
div.scrollmenu {
	background-color: #333;
	overflow: auto;
	white-space: nowrap;
	position: fixed;
	bottom: 0;
	width: 100%;
}

div.scrollmenu a {
	display: inline-block;
	color: white;
	text-align: center;
	padding: 14px;
	text-decoration: none;
}

div.scrollmenu a:hover {
	background-color: #777;
}
</style>
</head>
<body>


	<%
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String USER = "admin";
	final String PASS = "admin";
	final String dbName = "orders";
	final String DB_URL = "jdbc:mysql://localhost/" + dbName
			+ "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";

	Connection conn = null;
	String isAd = null;
	try {
		Class.forName(JDBC_DRIVER);

		System.out.println("Connecting to databse...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		PreparedStatement psExists = conn.prepareStatement("SELECT * FROM LogUser ");
		ResultSet rs = psExists.executeQuery();
		while (rs.next()) {
			isAd = rs.getString("admin");
		}
	} catch (SQLException | ClassNotFoundException e) {
		e.printStackTrace();
	}

	if (isAd.equalsIgnoreCase("yes")) {
			%>

		  <div class="scrollmenu">
		  <a href="Orders.jsp" >Orders</a>
		  <a href="CancelOrder.jsp">CancelOrder</a>
		  <a href="OrdersHistory.jsp">OrdersHistory</a>
		  <a href="Checkout.jsp">Checkout</a>
		  <a href="CheckoutHistory.jsp">CheckoutHistory</a>
		  <a href="InsertItem.jsp">InsertItem</a>
		  <a href="DeleteItem.jsp">DeleteItem</a>
		  <a href="SearchItem.jsp">SearchItem</a>
		  <a href="Complaints.jsp">Complaints</a>
		  <a href="ComplaintsHistory.jsp">ComplaintsHistory</a>
		  <a href="SearchUser.jsp">SearchUser</a>
		  <a href="ChangeUserData.jsp">ChangeUserData</a>
		  <a href="DeleteUser.jsp">DeleteUser</a>
		  <a href="SelfDelivery.jsp">SelfDelivery</a>  
		  <a href="SelfDeliveryHistory.jsp">SelfDeliveryHistroy</a> 
		  <a href="Stock.jsp">Stock</a>
		  <a href="Transit.jsp">Transit</a>
		  <a href="TransitHistory.jsp">TransitHistory</a>
		  <a href="LogIn.jsp">SignOut</a>
		</div>
		<%
		} else {
		%>

		  <div class="scrollmenu">
		  <a href="Orders.jsp" >Orders</a>
		  <a href="CancelOrder.jsp">CancelOrder</a>
		  <a href="Checkout.jsp">Checkout</a>
		  <a href="Complaints.jsp">Complaints</a>
		  <a href="SearchItem.jsp">SearchItem</a>
		  <a href="SearchUser.jsp">SearchUser</a>
		  <a href="SelfDelivery.jsp">SelfDelivery</a>  
		  <a href="Stock.jsp">Stock</a>
		  <a href="Transit.jsp">Transit</a>
		  <a href="LogIn.jsp">SignOut</a>
		</div>
		<%
		}
	%>
	<%
		if (request.getParameter("btn") != null) {

		String date = request.getParameter("date");
		String store = request.getParameter("store");
		Client client = Client.create();

		WebResource webResource = client
		.resource("http://localhost:8080/WebOrderingSystem/rest/OrderingSystem/OrdersHistory/"+store+"/"+date);
		ClientResponse myresponse = webResource.accept("text/plain").get(ClientResponse.class);

		if (myresponse.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + myresponse.getStatus());
		}
		String output = myresponse.getEntity(String.class);
		out.println(output);
	}
	%>

	<h1>Orders History</h1>
	<form METHOD="POST">
		<h1>Select Date:</h1>
		<label for="start">Date:</label> <input type="date" id="start"
			name="date" value="" placeholder="dd-mm-yyyy" min="12-02-2022"
			max="31-12-2022" required>
			
		<br> <br>Store:
		<input type="radio" name="store"  value="A" required><label >Store A</label>
    	<input type="radio" name="store"  value="B" required><label >Store B</label>
    	<input type="radio" name="store"  value="C" required><label >Store C</label><br> <br>
    	
    	<input type="submit" name="btn" value="Submit">
	</form>

</body>
</html>
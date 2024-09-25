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
String isAd=null;
try {
	Class.forName(JDBC_DRIVER);

	conn = DriverManager.getConnection(DB_URL, USER, PASS);
	
	PreparedStatement ps = conn.prepareStatement(
			"CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), surname VARCHAR(255), email VARCHAR(255), password VARCHAR(255), admin VARCHAR(10), store VARCHAR(10))");
	ps.executeUpdate();
	 ps = conn.prepareStatement("SELECT * FROM users ");
	ResultSet rs = ps.executeQuery();
	if (rs.next() == false){
		ps = conn.prepareStatement(
				"INSERT INTO users (name, surname, email, password, admin, store) VALUES (?, ?, ?, ?, ?, ?)");
		ps.setString(1, "admin");
		ps.setString(2, "admin");
		ps.setString(3, "1@2");
		ps.setString(4, "12345");
		ps.setString(5, "YES");
		ps.setString(6, "A");
		ps.executeUpdate();
		ps.close();
	}

} catch (SQLException | ClassNotFoundException e) {
	e.printStackTrace();
}
%>


<%



			if (request.getParameter("btn") != null) {
				
				String email = request.getParameter("email");
				String password = request.getParameter("pwd");

				
				
				Client client = Client.create();
				
				WebResource webResource = client.resource("http://localhost:8080/WebOrderingSystem/rest/OrderingSystem/LogIn/"+email+"/"+password);
				ClientResponse myresponse = webResource.accept("text/plain").get(ClientResponse.class);
	
				if (myresponse.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + myresponse.getStatus());
				}
				String output = myresponse.getEntity(String.class);
				
				if (output.equalsIgnoreCase("ok")){
					
					response.sendRedirect("Menu.jsp");
				}
				
				out.println(output);
			}

		%>
		
		
		
	<h1>User Login</h1>
	<form METHOD="POST">
		<h2>Sign in</h2>
		Email: <input type="email" id="email" name="email" required><br> <br>
		Password: <input type="password" id="pwd" name="pwd" minlength="5" required><br><br> 

		
		<input type="submit" name="btn" value="Submit">

		</form>
		<div class="scrollmenu">
  		<a href="Register.jsp">Register</a>

</div>
</body>
</html>
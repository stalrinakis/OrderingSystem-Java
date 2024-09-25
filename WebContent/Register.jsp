<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.sun.jersey.api.client.Client"%>
<%@ page import="com.sun.jersey.api.client.ClientResponse"%>
<%@ page import="com.sun.jersey.api.client.WebResource"%>


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




  <div class="scrollmenu">
  <a href="LogIn.jsp" >LogIn</a>

</div>
<%
			if (request.getParameter("btn") != null) {
				
				String name = request.getParameter("name");
				String surname = request.getParameter("surname");
				String email = request.getParameter("email");
				String password = request.getParameter("pwd");
				String operator = request.getParameter("admin");
				String store = request.getParameter("store");
				
				Client client = Client.create();
				
				WebResource webResource = client.resource("http://localhost:8080/WebOrderingSystem/rest/OrderingSystem/Register/"+name+"/"+surname+"/"+email+"/"+password);
				ClientResponse myresponse = webResource.accept("text/plain").post(ClientResponse.class);
	
				if (myresponse.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + myresponse.getStatus());
				}
				String output = myresponse.getEntity(String.class);
				out.println(output);
			}

		%>
		
	<h1>User Registration</h1>
	<form METHOD="POST">
		<h1>Register:</h1>
		Name: <input type="text" name="name" value="" required><br> <br>
		Surname: <input type="text" name="surname" value="" required><br> <br>
		Email: <input type="email" id="email" name="email" required><br> <br>
		Password (5 characters minimum): <input type="password" id="pwd" name="pwd" minlength="5" required><br><br> 
		
		<input type="submit" name="btn" value="Submit">

		</form>
		
</body>
</html>
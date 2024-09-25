package my.web.order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("OrderingSystem")

public class Start {
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String USER = "admin";
	final String PASS = "admin";
	final String dbName = "orders";
	final String DB_URL = "jdbc:mysql://localhost/" + dbName
			+ "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";

	String isAd = null;

	@POST
	@Path("/Register/{name}/{surname}/{email}/{password}")
	@Produces(MediaType.TEXT_PLAIN)
	public String regi(@PathParam("name") String name, @PathParam("surname") String surname,
			@PathParam("email") String email, @PathParam("password") String password) {

		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to databse...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS users (name VARCHAR(255), surname VARCHAR(255), email VARCHAR(255), password VARCHAR(255), admin VARCHAR(10), store VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM users WHERE email=" + "'" + email + "'");
			ResultSet rs = psExists.executeQuery();

			if (rs.next() == false) {
				PreparedStatement psTableIns = conn.prepareStatement(
						"INSERT INTO users (name, surname, email, password, admin, store) VALUES (?, ?, ?, ?, ?, ?)");
				psTableIns.setString(1, name);
				psTableIns.setString(2, surname);
				psTableIns.setString(3, email);
				psTableIns.setString(4, password);
				psTableIns.setString(5, "NO");
				psTableIns.setString(6, "A");
				psTableIns.executeUpdate();
				psTableIns.close();
				return ("User registered succefully!");
			} else {

				return "A user with this email already exists!";
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return "error";

	}

	@GET
	@Path("/LogIn/{email}/{password}")
	@Produces(MediaType.TEXT_PLAIN)
	public String LogIn(@PathParam("email") String email, @PathParam("password") String password) {
		Connection conn = null;
		String isPass = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM users WHERE email=?");
			psExists.setString(1, email);
			ResultSet rs = psExists.executeQuery();
			while (rs.next()) {
				isPass = rs.getString("password");
				if (password.equalsIgnoreCase(isPass) == true) {
					PreparedStatement psTableCre = conn.prepareStatement(
							"CREATE TABLE IF NOT EXISTS LogUser (name VARCHAR(255), surname VARCHAR(255), email VARCHAR(255), admin VARCHAR(10), store VARCHAR(1))");
					psTableCre.executeUpdate();

					psTableCre = conn.prepareStatement("SELECT * FROM LogUser");
					ResultSet rsIn = psTableCre.executeQuery();

					String name = rs.getString("name");
					String surname = rs.getString("surname");
					String admin = rs.getString("admin");
					String store = rs.getString("store");

					if (rsIn.next() == false) {
						PreparedStatement psTableIns = conn.prepareStatement(
								"INSERT INTO LogUser (name, surname, email, admin, store) VALUES (?, ?, ?, ?, ?)");
						psTableIns.setString(1, name);
						psTableIns.setString(2, surname);
						psTableIns.setString(3, email);
						psTableIns.setString(4, admin);
						psTableIns.setString(5, store);
						psTableIns.executeUpdate();
						psTableIns.close();
					} else {
						psTableCre.close();
						PreparedStatement psUp = conn.prepareStatement(
								"UPDATE LogUser SET name = ?, surname = ?, email = ?, admin = ?, store = ?");
						psUp.setString(1, name);
						psUp.setString(2, surname);
						psUp.setString(3, email);
						psUp.setString(4, admin);
						psUp.setString(5, store);
						psUp.executeUpdate();
						psUp.close();
					}
					return "ok";
				} else {
					return "Wrong Password!";
				}
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return "User is not registered in database!";
		}
		return "User is not registered in database!";
	}

	@POST
	@Path("/ChangeUserData/{ex_email}/{operator}/{store}")
	@Produces(MediaType.TEXT_PLAIN)
	public String ChaneUserData(@PathParam("ex_email") String ex_email, @PathParam("operator") String operator, @PathParam("store") String store) {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM users WHERE email=?");
			psExists.setString(1, ex_email);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == false) {
				return "User is not registered in database!";

			}
			
			rs = psExists.executeQuery();
			while (rs.next()) {
				
					 PreparedStatement psUp =
					 conn.prepareStatement("UPDATE users SET admin = ?, store = ? WHERE email=?");
					 psUp.setString(1, operator); 
					 psUp.setString(2, store);
					 psUp.setString(3, ex_email);
					 psUp.executeUpdate();
					return "User's data updated succefully!";
				

			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error!";
	}

	@DELETE
	@Path("/DeleteUser/{ex_email}")
	@Produces(MediaType.TEXT_PLAIN)
	public String DeleteUser(@PathParam("ex_email") String ex_email) {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM users WHERE email=?");
			psExists.setNString(1, ex_email);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == true) {
				psExists = conn.prepareStatement("DELETE FROM users WHERE email=?");
				psExists.setNString(1, ex_email);
				psExists.executeUpdate();
				return "User's data deleted successfully!";
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "User is not registered in database!";
	}

	@GET
	@Path("/SearchUser/{ex_email}")
	@Produces(MediaType.TEXT_PLAIN)
	public String SearchUser(@PathParam("ex_email") String ex_email) {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM " + "users" + " WHERE email=?");
			psExists.setNString(1, ex_email);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == true) {
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String admin = rs.getString("admin");
				String isAd = "No";
				String store = rs.getString("store");
				if (admin.equalsIgnoreCase("yes")) {
					isAd = "Yes";
				}
				String html = " Full name: " + name + " " + surname + "\n" + "Email: " + ex_email + "\n" + "Admin:"
						+ isAd + "\n" + "Store:" + store;
				return html.replaceAll("(\r\n|\n)", "<br>");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "User is not registered in database!";
	}

	@POST
	@Path("/InsertItem/{ex_name}/{num}")
	@Produces(MediaType.TEXT_PLAIN)
	public String InsertItem(@PathParam("ex_name") String ex_name, @PathParam("num") int num) {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS items (name VARCHAR(255), qA INT, qB INT, qC INT, storage INT )");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM items WHERE name=?");
			psExists.setNString(1, ex_name);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == true) {

				return ("An item with this name already exists!");

			} else {
				PreparedStatement psTableIns = conn
						.prepareStatement("INSERT INTO items (name, qA, qB, qC, storage) VALUES (?, ?, ?, ?, ?)");
				psTableIns.setString(1, ex_name);
				psTableIns.setInt(2, 0);
				psTableIns.setInt(3, 0);
				psTableIns.setInt(4, 0);
				psTableIns.setInt(5, num);
				psTableIns.executeUpdate();

			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Item registered successfully!";
	}

	@DELETE
	@Path("/DeleteItem/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String DeleteItem(@PathParam("name") String name) {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM items WHERE name=?");
			psExists.setNString(1, name);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == true) {
				psExists = conn.prepareStatement("DELETE FROM items WHERE name=?");
				psExists.setNString(1, name);
				psExists.executeUpdate();
				psExists.close();
				return "Item deleted successfully!";
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Item is not registered in database!";
	}

	@POST
	@Path("/Stock/{name}/{num}")
	@Produces(MediaType.TEXT_PLAIN)
	public String Stock(@PathParam("name") String name, @PathParam("num") String quantity) {
		Connection conn = null;
		String store = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psExists = conn.prepareStatement("SELECT name FROM items WHERE name=?");
			psExists.setNString(1, name);
			ResultSet rs = psExists.executeQuery();

			PreparedStatement ps = conn.prepareStatement("SELECT store FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				store = rss.getString("store");
			}

			if (rs.next() == true) {
				if (store.equalsIgnoreCase("A")) {
					psExists.close();
					psExists = conn.prepareStatement("UPDATE items SET qA = ? WHERE name=?");
					psExists.setString(1, quantity);
					psExists.setString(2, name);
					psExists.executeUpdate();
					psExists.close();
					return "StoreA's stock was updated successfully!";
				} else if (store.equalsIgnoreCase("B")) {
					psExists.close();
					psExists = conn.prepareStatement("UPDATE items SET qB = ? WHERE name=?");
					psExists.setString(1, quantity);
					psExists.setString(2, name);
					psExists.executeUpdate();
					psExists.close();
					return "StoreB's stock was updated successfully!";
				} else {
					psExists.close();
					psExists = conn.prepareStatement("UPDATE items SET qC = ? WHERE name=?");
					psExists.setString(1, quantity);
					psExists.setString(2, name);
					psExists.executeUpdate();
					psExists.close();
					return "StoreC's stock was updated successfully!";
				}

			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Item is not registered in database!";
	}

	@POST
	@Path("/Checkout/{total_cash}/{total_cards}/{total_invoice}")
	@Produces(MediaType.TEXT_PLAIN)
	public String Checkout(@PathParam("total_cash") String total_cash, @PathParam("total_cards") String total_cards,
			@PathParam("total_invoice") String total_invoice) {
		Connection conn = null;
		java.util.Date dt = new java.util.Date();
		String store = null;
		String dateDB = "empty";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cD = sdf.format(dt);

		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement ps = conn.prepareStatement("SELECT store FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				store = rss.getString("store");
			}

			if (store.equalsIgnoreCase("A")) {
				PreparedStatement psExists = conn.prepareStatement(
						"CREATE TABLE IF NOT EXISTS checka (cash VARCHAR(255), cards VARCHAR(255), invoice VARCHAR(255), date  VARCHAR(255))");
				psExists.executeUpdate();
				PreparedStatement psZ = conn.prepareStatement("SELECT date FROM  checka ");
				ResultSet rsZ = psZ.executeQuery();
				if (rsZ.next() == true) {
					dateDB = rsZ.getString("date");
				}

				if (!dateDB.equalsIgnoreCase(cD)) {
					psExists = conn
							.prepareStatement("INSERT INTO checka (cash, cards, invoice, date) VALUES (?, ?, ?, ?)");
					psExists.setString(1, total_cash);
					psExists.setString(2, total_cards);
					psExists.setString(3, total_invoice);
					psExists.setString(4, cD);
					psExists.executeUpdate();
				} else {
					return "Todays checkout has already been uploaded!";
				}
				return "StoreA's checkout was uploaded successfully!";
			} else if (store.equalsIgnoreCase("B")) {
				PreparedStatement psExists = conn.prepareStatement(
						"CREATE TABLE IF NOT EXISTS checkb (cash VARCHAR(255), cards VARCHAR(255), invoice VARCHAR(255), date  VARCHAR(255))");
				psExists.executeUpdate();
				PreparedStatement psZ = conn.prepareStatement("SELECT date FROM  checkb ");
				ResultSet rsZ = psZ.executeQuery();
				if (rsZ.next() == true) {
					dateDB = rsZ.getString("date");
				}
				if (!dateDB.equalsIgnoreCase(cD)) {
					psExists = conn
							.prepareStatement("INSERT INTO checkb (cash, cards, invoice, date) VALUES (?, ?, ?, ?)");
					psExists.setString(1, total_cash);
					psExists.setString(2, total_cards);
					psExists.setString(3, total_invoice);
					psExists.setString(4, cD);
					psExists.executeUpdate();
				} else {
					return "Todays checkout has already been uploaded!";
				}
				return "StoreB's checkout was uploaded successfully!";
			} else {
				PreparedStatement psExists = conn.prepareStatement(
						"CREATE TABLE IF NOT EXISTS checkc (cash VARCHAR(255), cards VARCHAR(255), invoice VARCHAR(255), date  VARCHAR(255))");
				psExists.executeUpdate();
				PreparedStatement psZ = conn.prepareStatement("SELECT date FROM  checkc ");
				ResultSet rsZ = psZ.executeQuery();
				if (rsZ.next() == true) {
					dateDB = rsZ.getString("date");
				}

				if (!dateDB.equalsIgnoreCase(cD)) {
					psExists = conn
							.prepareStatement("INSERT INTO checkc (cash, cards, invoice, date) VALUES (?, ?, ?, ?)");
					psExists.setString(1, total_cash);
					psExists.setString(2, total_cards);
					psExists.setString(3, total_invoice);
					psExists.setString(4, cD);
					psExists.executeUpdate();
				} else {
					return "Todays checkout has already been uploaded!";
				}
				return "StoreC's checkout was uploaded successfully!";
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error!";
	}

	@POST
	@Path("/SelfDelivery/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String SelfDelivery(@PathParam("name") String name) {
		Connection conn = null;
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cD = sdf.format(dt);
		String store = null;
		String user = null;
		int old = 0;

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psx = conn.prepareStatement("SELECT * FROM items WHERE name=?");
			psx.setNString(1, name);
			ResultSet rs = psx.executeQuery();
			if (rs.next() == false) {

				return ("This item does not exist!");
			}
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				store = rss.getString("store");
				user = rss.getString("email");
			}

			PreparedStatement psExists = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS self (email VARCHAR(255), store VARCHAR(10), date  VARCHAR(255), name VARCHAR(255))");
			psExists.executeUpdate();
			psExists.close();

			PreparedStatement tod = conn.prepareStatement("SELECT email FROM self WHERE date=?");
			tod.setNString(1, cD);
			ResultSet rsTod = tod.executeQuery();

			if (rsTod.next() == false) {

				if (store.equalsIgnoreCase("A")) {
					PreparedStatement psfa = conn.prepareStatement("SELECT * FROM items WHERE name=?");
					psfa.setString(1, name);
					ResultSet test = psfa.executeQuery();
					while (test.next()) {
						old = test.getInt("qA");
						if (old < 1) {
							return "Self delivery can not be completed, storage stock: " + old;
						}
					}

					PreparedStatement Up = conn.prepareStatement("UPDATE items SET qA = ?  WHERE name=?");
					Up.setInt(1, old - 1);
					Up.setString(2, name);
					Up.executeUpdate();
				} else if (store.equalsIgnoreCase("B")) {
					PreparedStatement psfb = conn.prepareStatement("SELECT * FROM items WHERE name=?");
					psfb.setString(1, name);
					ResultSet test = psfb.executeQuery();
					while (test.next()) {
						old = test.getInt("qB");
						if (old < 1) {
							return "Self delivery can not be completed, storage stock: " + old;
						}
					}

					PreparedStatement Up = conn.prepareStatement("UPDATE items SET qB = ?  WHERE name=?");
					Up.setInt(1, old - 1);
					Up.setString(2, name);
					Up.executeUpdate();
				} else {
					PreparedStatement psfc = conn.prepareStatement("SELECT * FROM items WHERE name=?");
					psfc.setString(1, name);
					ResultSet test = psfc.executeQuery();
					while (test.next()) {
						old = test.getInt("qC");
						if (old < 1) {
							return "Self delivery can not be completed, storage stock: " + old;
						}
					}

					PreparedStatement Up = conn.prepareStatement("UPDATE items SET qC = ?  WHERE name=?");
					Up.setInt(1, old - 1);
					Up.setString(2, name);
					Up.executeUpdate();
				}
				PreparedStatement psIn = conn
						.prepareStatement("INSERT INTO self (email, store, date, name) VALUES (?, ?, ?, ?)");
				psIn.setString(1, user);
				psIn.setString(2, store);
				psIn.setString(3, cD);
				psIn.setString(4, name);
				psIn.executeUpdate();

				return "Self delivery registered successfully!";
			} else {
				System.out.println("bhke");
				String temp = rsTod.getString("email");
				if (!temp.equalsIgnoreCase(user)) {
					PreparedStatement psIn = conn
							.prepareStatement("INSERT INTO self (email, store, date, name) VALUES (?, ?, ?, ?)");
					psIn.setString(1, user);
					psIn.setString(2, store);
					psIn.setString(3, cD);
					psIn.setString(4, name);
					psIn.executeUpdate();

					return "Self delivery registered successfully!";
				} else {
					return "Today's self delivery has already been registered!";
				}

			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error!";
	}

	@GET
	@Path("/SearchItem/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String SearchItem(@PathParam("name") String name) {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM items WHERE name = ?");
			psExists.setNString(1, name);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == true) {
				String qa = rs.getString("qA");
				String qb = rs.getString("qB");
				String qc = rs.getString("qC");
				String st = rs.getString("storage");

				String html = " Item Name: " + name + "\n" + "Quantity on store A: " + qa + "\n"
						+ "Quantity on store B: " + qb + "\n" + "Quantity on store C: " + qc+ "\n" + "Quantity on storage: " + st;
				return html.replaceAll("(\r\n|\n)", "<br>");

			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Item is not registered in database!";
	}

	@POST
	@Path("/Orders/{name}/{num}")
	@Produces(MediaType.TEXT_PLAIN)
	public String Orders(@PathParam("name") String name, @PathParam("num") int num) {
		int stock = 0;
		Connection conn = null;

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cD = sdf.format(dt);
		String store = null;
		String user = null;
		int qa = 0;
		int qb = 0;
		int qc = 0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS orders (name VARCHAR(255), q INT, date VARCHAR(255), store VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				store = rss.getString("store");
				user = rss.getString("email");
			}
			PreparedStatement pser = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS items (name VARCHAR(255), qA INT, qB INT, qC INT, storage INT )");
			pser.executeUpdate();
			pser.close();

			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM items WHERE name=?");
			psExists.setString(1, name);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == false) {

				return ("This item does not exist in the database!");

			} else {

				PreparedStatement psEx = conn.prepareStatement("SELECT * FROM items WHERE name=?");
				psEx.setString(1, name);
				ResultSet rsEx = psEx.executeQuery();
				while (rsEx.next()) {
					stock = rsEx.getInt("storage");
					qa = rsEx.getInt("qA");
					qb = rsEx.getInt("qB");
					qc = rsEx.getInt("qC");
					if (stock < num) {
						return "Order can not be completed, storage stock: " + stock;
					}
				}

				PreparedStatement Up = conn.prepareStatement("UPDATE items SET storage = ?  WHERE name=?");
				Up.setInt(1, stock - num);
				Up.setString(2, name);
				Up.executeUpdate();
				Up.close();

				PreparedStatement psTableIns = conn
						.prepareStatement("INSERT INTO orders (name, q, date, store) VALUES (?, ?, ?, ?)");
				psTableIns.setString(1, name);
				psTableIns.setInt(2, num);
				psTableIns.setString(3, cD);
				psTableIns.setString(4, store);
				psTableIns.executeUpdate();

				if (store.equalsIgnoreCase("A")) {
					PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qA = ? WHERE name = ?");
					psIns.setInt(1, num + qa);
					psIns.setString(2, name);
					psIns.executeUpdate();
				} else if (store.equalsIgnoreCase("B")) {
					PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qB = ? WHERE name = ?");
					psIns.setInt(1, num + qb);
					psIns.setString(2, name);
					psIns.executeUpdate();
				} else {
					PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qC = ? WHERE name = ?");
					psIns.setInt(1, num + qc);
					psIns.setString(2, name);
					psIns.executeUpdate();
				}

				return "Order registered succefully";
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	@GET
	@Path("/OrdersHistory/{store}/{date}")
	@Produces(MediaType.TEXT_PLAIN)
	public String OrdersHistory(@PathParam("store") String store, @PathParam("date") String date)
			throws ParseException {

		Connection conn = null;

		String s1 = date.substring(0, 4);
		String s2 = date.substring(5, 7);
		String s3 = date.substring(8, 10);
		date = s3 + "-" + s2 + "-" + s1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS orders (name VARCHAR(255), q INT, date VARCHAR(255), store VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  orders WHERE date = ? AND store = ? ");
			ps.setString(1, date);
			ps.setString(2, store);
			ResultSet rs = ps.executeQuery();
			ArrayList<Integer> li = new ArrayList<Integer>();
			ArrayList<String> li2 = new ArrayList<String>();
			Iterator<Integer> itr = null;
			Iterator<String> itr2 = null;
			if (rs.next() == false) {
				return "No orders were registered this date!";
			}
			rs = ps.executeQuery();
			while (rs.next()) {

				li.add(rs.getInt("q"));
				li2.add(rs.getString("name"));
			}
			String temp = "";
			itr = li.listIterator();
			itr2 = li2.listIterator();
			while (itr.hasNext()) {
				temp = temp + "Item: " + itr2.next() + " Quantity : " + itr.next() + "\n";
			}
			return temp.replaceAll("(\r\n|\n)", "<br>");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	@DELETE
	@Path("/CancelOrder/{name}/{num}")
	@Produces(MediaType.TEXT_PLAIN)
	public String CancelOrder(@PathParam("name") String name, @PathParam("num") int num) {

		Connection conn = null;

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cD = sdf.format(dt);
		String store = null;
		// String user = null;
		int q = 0;
		int qa = 0;
		int qb = 0;
		int qc = 0;
		int qs = 0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS orders (name VARCHAR(255), q INT, date VARCHAR(255), store VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				store = rss.getString("store");
				// user = rss.getString("email");
			}

			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM orders WHERE name=? AND date=? AND q=?");
			psExists.setString(1, name);
			psExists.setString(2, cD);
			psExists.setInt(3, num);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == false) {

				return ("This order is not registered!");

			} else {
				rs = psExists.executeQuery();
				while (rs.next()) {
					q = q + rs.getInt("q");
				}

				PreparedStatement psEx = conn.prepareStatement("SELECT * FROM items WHERE name=?");
				psEx.setString(1, name);
				ResultSet rsEx = psEx.executeQuery();
				while (rsEx.next()) {
					qs = rsEx.getInt("storage");
					qa = rsEx.getInt("qA");
					qb = rsEx.getInt("qB");
					qc = rsEx.getInt("qC");
				}
				PreparedStatement psDel = conn.prepareStatement("DELETE FROM orders WHERE name=? AND date=? AND q=?");
				psDel.setString(1, name);
				psDel.setString(2, cD);
				psDel.setInt(3, num);
				psDel.executeUpdate();

				/*
				 * PreparedStatement psEx =
				 * conn.prepareStatement("SELECT * FROM items WHERE name=?"); psEx.setString(1,
				 * name); ResultSet rsEx = psEx.executeQuery(); while (rsEx.next()) { stock =
				 * rsEx.getInt("storage"); qa = rsEx.getInt("qA"); qb = rsEx.getInt("qB"); qc =
				 * rsEx.getInt("qC"); if (stock < num) { return
				 * "Order can not be completed, storage stock: " + stock; } }
				 */

				if (store.equalsIgnoreCase("A")) {
					PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qA = ? WHERE name = ?");
					psIns.setInt(1, qa - q);
					psIns.setString(2, name);
					psIns.executeUpdate();
				} else if (store.equalsIgnoreCase("B")) {
					PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qB = ? WHERE name = ?");
					psIns.setInt(1, qb - q);
					psIns.setString(2, name);
					psIns.executeUpdate();
				} else {
					PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qC = ? WHERE name = ?");
					psIns.setInt(1, qc - q);
					psIns.setString(2, name);
					psIns.executeUpdate();
				}

				PreparedStatement psSto = conn.prepareStatement("UPDATE items SET storage = ? WHERE name = ?");
				psSto.setInt(1, qs + q);
				psSto.setString(2, name);
				psSto.executeUpdate();

				return "Order deleted succefully";
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	@GET
	@Path("/CheckoutHistory/{store}/{date}")
	@Produces(MediaType.TEXT_PLAIN)
	public String CheckoutHistory(@PathParam("store") String store, @PathParam("date") String date)
			throws ParseException {

		Connection conn = null;

		String s1 = date.substring(0, 4);
		String s2 = date.substring(5, 7);
		String s3 = date.substring(8, 10);
		date = s3 + "-" + s2 + "-" + s1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psExists = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS checka (cash VARCHAR(255), cards VARCHAR(255), invoice VARCHAR(255), date  VARCHAR(255))");
			psExists.executeUpdate();
			psExists = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS checkb (cash VARCHAR(255), cards VARCHAR(255), invoice VARCHAR(255), date  VARCHAR(255))");
			psExists.executeUpdate();
			psExists = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS checkc (cash VARCHAR(255), cards VARCHAR(255), invoice VARCHAR(255), date  VARCHAR(255))");
			psExists.executeUpdate();
			if (store.equalsIgnoreCase("A")) {
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM  checka WHERE date = ?");
				ps.setString(1, date);
				ResultSet rs = ps.executeQuery();
				/*
				 * ArrayList<String> li = new ArrayList<String>(); ArrayList<String> li2 = new
				 * ArrayList<String>(); ArrayList<String> li3 = new ArrayList<String>();
				 * Iterator<String> itr = null; Iterator<String> itr2 = null; Iterator<String>
				 * itr3 = null;
				 */
				if (rs.next() == false) {
					return "No checkouts we registered this date!";
				}
				rs = ps.executeQuery();
				String temp = "";
				while (rs.next()) {
					/*
					 * li.add(rs.getString("cash")); li2.add(rs.getString("cards"));
					 * li2.add(rs.getString("invoice"));
					 */
					temp = temp + "Cash: " + rs.getString("cash") + " Cards : " + rs.getString("cards") + " Invoice : "
							+ rs.getString("invoice") + "\n";
				}
				/*
				 * itr = li.listIterator(); itr2 = li2.listIterator(); itr3 =
				 * li3.listIterator(); while (itr.hasNext() ) { temp = temp + "Cash: " +
				 * itr.next() + "Cards : " + itr2.next() + "Invoice : " + itr3.next() + "\n"; }
				 */
				return temp.replaceAll("(\r\n|\n)", "<br>");
			} else if (store.equalsIgnoreCase("B")) {
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM  checkb WHERE date = ?");
				ps.setString(1, date);
				ResultSet rs = ps.executeQuery();
				/*
				 * ArrayList<String> li = new ArrayList<String>(); ArrayList<String> li2 = new
				 * ArrayList<String>(); ArrayList<String> li3 = new ArrayList<String>();
				 * Iterator<String> itr = null; Iterator<String> itr2 = null; Iterator<String>
				 * itr3 = null;
				 */
				if (rs.next() == false) {
					return "No checkouts we registered this date!";
				}
				rs = ps.executeQuery();
				String temp = "";
				while (rs.next()) {
					/*
					 * li.add(rs.getString("cash")); li2.add(rs.getString("cards"));
					 * li2.add(rs.getString("invoice"));
					 */
					temp = temp + "Cash: " + rs.getString("cash") + " Cards : " + rs.getString("cards") + " Invoice : "
							+ rs.getString("invoice") + "\n";
				}
				/*
				 * itr = li.listIterator(); itr2 = li2.listIterator(); itr3 =
				 * li3.listIterator(); while (itr.hasNext() ) { temp = temp + "Cash: " +
				 * itr.next() + "Cards : " + itr2.next() + "Invoice : " + itr3.next() + "\n"; }
				 */
				return temp.replaceAll("(\r\n|\n)", "<br>");
			} else {

			}
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  checkc WHERE date = ?");
			ps.setString(1, date);
			ResultSet rs = ps.executeQuery();
			/*
			 * ArrayList<String> li = new ArrayList<String>(); ArrayList<String> li2 = new
			 * ArrayList<String>(); ArrayList<String> li3 = new ArrayList<String>();
			 * Iterator<String> itr = null; Iterator<String> itr2 = null; Iterator<String>
			 * itr3 = null;
			 */
			if (rs.next() == false) {
				return "No checkouts were registered this date!";
			}
			rs = ps.executeQuery();
			String temp = "";
			while (rs.next()) {
				/*
				 * li.add(rs.getString("cash")); li2.add(rs.getString("cards"));
				 * li2.add(rs.getString("invoice"));
				 */
				temp = temp + "Cash: " + rs.getString("cash") + " Cards : " + rs.getString("cards") + " Invoice : "
						+ rs.getString("invoice") + "\n";
			}
			/*
			 * itr = li.listIterator(); itr2 = li2.listIterator(); itr3 =
			 * li3.listIterator(); while (itr.hasNext() ) { temp = temp + "Cash: " +
			 * itr.next() + "Cards : " + itr2.next() + "Invoice : " + itr3.next() + "\n"; }
			 */
			return temp.replaceAll("(\r\n|\n)", "<br>");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
		
	@POST
	@Path("/Transit/{name}/{num}/{store}")
	@Produces(MediaType.TEXT_PLAIN)
	public String Transit (@PathParam("name") String name, @PathParam("num") int num, @PathParam("store") String store ) {
		int stock = 0;
		Connection conn = null;

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cD = sdf.format(dt);
		String from = null;
		String user = null;
		int qa = 0;
		int qb = 0;
		int qc = 0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS rs (name VARCHAR(255), q INT, date VARCHAR(255), fstore VARCHAR(10), tstore VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				from = rss.getString("store");
				user = rss.getString("email");
			}

			PreparedStatement psExists = conn.prepareStatement("SELECT * FROM items WHERE name=?");
			psExists.setString(1, name);
			ResultSet rs = psExists.executeQuery();
			if (rs.next() == false) {

				return ("This item does not exist in the database!");

			} else {

				PreparedStatement psEx = conn.prepareStatement("SELECT * FROM items WHERE name=?");
				psEx.setString(1, name);
				ResultSet rsEx = psEx.executeQuery();
				while (rsEx.next()) {
					qa = rsEx.getInt("qA");
					qb = rsEx.getInt("qB");
					qc = rsEx.getInt("qC");
				}
				
				if (from.equalsIgnoreCase("A")) {
					if (qa < num) {
						return "Transition can not be completed, store stock: " + qa;
					}
					
					if (store.equalsIgnoreCase("B")) {
						PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qA = ?, qB = ? WHERE name = ?");
						psIns.setInt(1, qa-num);
						psIns.setInt(2, qb+num);
						psIns.setString(3, name);
						psIns.executeUpdate();
						psIns.close();
					}else if(store.equalsIgnoreCase("C")){
						PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qA = ?, qC = ? WHERE name = ?");
						psIns.setInt(1, qa-num);
						psIns.setInt(2, qc+num);
						psIns.setString(3, name);
						psIns.executeUpdate();
						psIns.close();
					}
					else {
						return "Can not make a transition to the same store";
					}
					PreparedStatement psUp = conn.prepareStatement("INSERT INTO RS (name, q, date, fstore, tstore) VALUES (?, ?, ?, ?, ?)");
					psUp.setString(1, name);
					psUp.setInt(2, num);
					psUp.setString(3, cD);
					psUp.setString(4, from);
					psUp.setString(5, store);
					psUp.executeUpdate();
					psUp.close();
				} else if (from.equalsIgnoreCase("B")) {
					if (qb < num) {
						return "Transition can not be completed, store stock: " + qb;
					}
					
					if (store.equalsIgnoreCase("A")) {
						PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qC = ?, qA = ? WHERE name = ?");
						psIns.setInt(1, qb-num);
						psIns.setInt(2, qa+num);
						psIns.setString(3, name);
						psIns.executeUpdate();
						psIns.close();
					}else if(store.equalsIgnoreCase("C")){
						PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qC = ?, qC = ? WHERE name = ?");
						psIns.setInt(1, qb-num);
						psIns.setInt(2, qc+num);
						psIns.setString(3, name);
						psIns.executeUpdate();
						psIns.close();
					}
					else {
						return "Can not make a transition to the same store";
					}
					PreparedStatement psUp = conn.prepareStatement("INSERT INTO RS (name, q, date, fstore, tstore) VALUES (?, ?, ?, ?, ?)");
					psUp.setString(1, name);
					psUp.setInt(2, num);
					psUp.setString(3, cD);
					psUp.setString(4, from);
					psUp.setString(5, store);
					psUp.executeUpdate();
					psUp.close();
				} else {
					if (qc < num) {
						return "Transition can not be completed, store stock: " + qc;
					}
					
					if (store.equalsIgnoreCase("A")) {
						PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qC = ?, qA = ? WHERE name = ?");
						psIns.setInt(1, qc-num);
						psIns.setInt(2, qa+num);
						psIns.setString(3, name);
						psIns.executeUpdate();
						psIns.close();
					}else if(store.equalsIgnoreCase("B")){
						PreparedStatement psIns = conn.prepareStatement("UPDATE items SET qC = ?, qB = ? WHERE name = ?");
						psIns.setInt(1, qc-num);
						psIns.setInt(2, qb+num);
						psIns.setString(3, name);
						psIns.executeUpdate();
						psIns.close();
					}
					else {
						return "Can not make a transition to the same store";
					}
					PreparedStatement psUp = conn.prepareStatement("INSERT INTO RS (name, q, date, fstore, tstore) VALUES (?, ?, ?, ?, ?)");
					psUp.setString(1, name);
					psUp.setInt(2, num);
					psUp.setString(3, cD);
					psUp.setString(4, from);
					psUp.setString(5, store);
					psUp.executeUpdate();
					psUp.close();
					
				}

				return "Transition registered succefully";
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	@POST
	@Path("/Complaints/{name}/{surname}/{phone}/{comp}")
	@Produces(MediaType.TEXT_PLAIN)
	public String Complaints(@PathParam("name") String name, @PathParam("surname") String surname, @PathParam("phone") String phone, @PathParam("comp") String comp) {

		Connection conn = null;

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cD = sdf.format(dt);
		String store = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS comp (name VARCHAR(255), surname VARCHAR(255),phone VARCHAR(12),comp VARCHAR(255), date VARCHAR(255), store VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  logUser ");
			ResultSet rss = ps.executeQuery();
			while (rss.next()) {
				store = rss.getString("store");
				// user = rss.getString("email");
			}
			ps = conn.prepareStatement("INSERT INTO comp (name, surname, phone, comp, date, store) VALUES (?,?,?,?,?,?)");
			ps.setString(1, name);
			ps.setString(2, surname);
			ps.setString(3, phone);
			ps.setString(4, comp.replace('-', ' '));
			ps.setString(5, cD);
			ps.setString(6, store);
			ps.executeUpdate();
			ps.close();

				return "Comlpaint registered succefully";
			

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	@GET
	@Path("/ComplaintsHistory/{store}/{date}")
	@Produces(MediaType.TEXT_PLAIN)
	public String ComplaintsHistory(@PathParam("store") String store, @PathParam("date") String date)
			throws ParseException {

		Connection conn = null;

		String s1 = date.substring(0, 4);
		String s2 = date.substring(5, 7);
		String s3 = date.substring(8, 10);
		date = s3 + "-" + s2 + "-" + s1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS comp (name VARCHAR(255), surname VARCHAR(255),phone VARCHAR(12),comp VARCHAR(255), date VARCHAR(255), store VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  comp WHERE date = ? AND store = ? ");
			ps.setString(1, date);
			ps.setString(2, store);
			ResultSet rs = ps.executeQuery();
			ArrayList<String> li = new ArrayList<String>();
			ArrayList<String> li2 = new ArrayList<String>();
			ArrayList<String> li3 = new ArrayList<String>();
			ArrayList<String> li4 = new ArrayList<String>();
			Iterator<String> itr = null;
			Iterator<String> itr2 = null;
			Iterator<String> itr3 = null;
			Iterator<String> itr4 = null;
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return "No complaints were registered this date!";
			}
			rs = ps.executeQuery();
			String temp = "";
			while (rs.next()) {

				li.add(rs.getString("name"));
				li2.add(rs.getString("surname"));
				li3.add(rs.getString("phone"));
				li4.add(rs.getString("comp"));
			}
			itr = li.listIterator();
			itr2 = li2.listIterator();
			itr3 = li3.listIterator();
			itr4 = li4.listIterator();
			while (itr.hasNext()) {
				temp = temp + "Name: " + itr.next() + " Surname: " + itr2.next() +  " Phone: " + itr3.next() +"\n" + "Comp:" +itr4.next()+"\n";
			}
			return temp.replaceAll("(\r\n|\n)", "<br>");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	@GET
	@Path("/SelfDeliveryHistory/{store}/{date}")
	@Produces(MediaType.TEXT_PLAIN)
	public String SelfDeliveryHistory(@PathParam("store") String store, @PathParam("date") String date)
			throws ParseException {

		Connection conn = null;

		String s1 = date.substring(0, 4);
		String s2 = date.substring(5, 7);
		String s3 = date.substring(8, 10);
		date = s3 + "-" + s2 + "-" + s1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psExists = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS self (email VARCHAR(255), store VARCHAR(10), date  VARCHAR(255), name VARCHAR(255))");
			psExists.executeUpdate();
			psExists.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  self WHERE date = ? AND store = ? ");
			ps.setString(1, date);
			ps.setString(2, store);
			ResultSet rs = ps.executeQuery();
			ArrayList<String> li = new ArrayList<String>();
			ArrayList<String> li2 = new ArrayList<String>();
			Iterator<String> itr = null;
			Iterator<String> itr2 = null;
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return "No self deliveries were registered this date!";
			}
			rs = ps.executeQuery();
			String temp = "";
			while (rs.next()) {

				li.add(rs.getString("email"));
				li2.add(rs.getString("name"));
			}
			itr = li.listIterator();
			itr2 = li2.listIterator();
			while (itr.hasNext()) {
				temp = temp + "User: " + itr.next() + " got for self-delivery an : " + itr2.next() + "\n";
			}
			return temp.replaceAll("(\r\n|\n)", "<br>");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	@GET
	@Path("/TransitHistory/{store}/{date}")
	@Produces(MediaType.TEXT_PLAIN)
	public String TransitHistory(@PathParam("store") String store, @PathParam("date") String date)
			throws ParseException {

		Connection conn = null;

		String s1 = date.substring(0, 4);
		String s2 = date.substring(5, 7);
		String s3 = date.substring(8, 10);
		date = s3 + "-" + s2 + "-" + s1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			PreparedStatement psTableCre = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS rs (name VARCHAR(255), q INT, date VARCHAR(255), fstore VARCHAR(10), tstore VARCHAR(10))");
			psTableCre.executeUpdate();
			psTableCre.close();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM  rs WHERE date = ? AND fstore = ? ");
			ps.setString(1, date);
			ps.setString(2, store);
			ResultSet rs = ps.executeQuery();
			ArrayList<String> li = new ArrayList<String>();
			ArrayList<String> li2 = new ArrayList<String>();
			ArrayList<Integer> li3 = new ArrayList<Integer>();
			Iterator<String> itr = null;
			Iterator<String> itr2 = null;
			Iterator<Integer> itr3 = null;
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return "No transitions were registered this date!";
			}
			rs = ps.executeQuery();
			String temp = "";
			while (rs.next()) {

				li.add(rs.getString("name"));
				li2.add(rs.getString("tstore"));
				li3.add(rs.getInt("q"));
			}
			itr = li.listIterator();
			itr2 = li2.listIterator();
			itr3 = li3.listIterator();
			while (itr.hasNext()) {
				temp = temp + itr3.next() +" Pieces of : " + itr.next() + " were sent from store: " + store + " to store: " + itr2.next() +"\n";
			}
			return temp.replaceAll("(\r\n|\n)", "<br>");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
}
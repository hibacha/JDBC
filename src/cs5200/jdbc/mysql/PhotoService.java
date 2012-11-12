package cs5200.jdbc.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class PhotoService {
	public static String STAFF_NAME = "Staff";
	public static String LIVING_CITY = "Boston";

	public static String URL = "jdbc:mysql://localhost:3306/photo";

	public static int generateID(String tableName, Connection conn,
			String idName) throws SQLException {
		Statement statement = conn.createStatement();
		ResultSet rst = statement.executeQuery("select max(" + idName
				+ ") as mid from " + tableName);
		int mId = 0;
		while (rst.next()) {
			try {
				mId = Integer.parseInt(rst.getString(1));
			} catch (NumberFormatException e) {
				mId = 0;
			}
		}
		return mId;

	}

	public static int getPhotographerOrPersonId(String phername,
			Boolean isPhotographer, Connection conn) throws SQLException {
		Statement statement = conn.createStatement();

		String queryTable = isPhotographer ? "Photographer ph join Person p on ph.id=p.id"
				: "Person p";

		ResultSet rst = statement
				.executeQuery("select count(*) as phNum , p.id as myID from "
						+ queryTable + " where p.name='" + phername + "'");
		int count = 0;

		rst.next();
		count = Integer.parseInt(rst.getString(1));

		if (isPhotographer) {
			if (count == 0) {
				throw new SQLException(isPhotographer ? "Photographer"
						: "Person" + " table doesn't have this [" + phername
								+ "] !");
			} else if (count > 1) {
				throw new SQLException(
						"There are more than 1 Photographer named[" + phername
								+ "]");
			} else {
				rst = statement
						.executeQuery("select p.id from Photographer ph join Person p on ph.id=p.id where p.name='"
								+ phername + "'");
			}
		} else {// person logic
				// create a person
			if (count == 0) {
				int newId = generateID("Person", conn, "id") + 1;
				PreparedStatement preStat = conn
						.prepareStatement("insert into person(id,name) values(?,?)");
				preStat.setInt(1, newId);
				preStat.setString(2, phername);
				preStat.executeUpdate();
				preStat.close();

				return newId;

			} else if (count == 1) {
				return Integer.parseInt(rst.getString(2));
			} else {
				throw new SQLException(
						"There are more than 1 person have the name["
								+ phername + "]");
			}

		}
		rst.next();
		return Integer.parseInt(rst.getString(1));
	}

	private static int getLocationId(String city, String state, String country,
			Connection conn) throws SQLException {
		String query = "select count(*) as locNum from Location where city='"
				+ city + "' and state='" + state + "' and country='" + country
				+ "'";
		Statement statement = conn.createStatement();
		ResultSet rst = statement.executeQuery(query);
		int count = 0;
		while (rst.next()) {
			count = Integer.parseInt(rst.getString(1));
		}
		int locId = 0;
		if (count == 0) {
			int newId = generateID("Location", conn, "id") + 1;
			PreparedStatement preStat = conn
					.prepareStatement("insert into Location(id,city,state,country) values(?,?,?,?)");
			preStat.setInt(1, newId);
			preStat.setString(2, city);
			preStat.setString(3, state);
			preStat.setString(4, country);
			preStat.executeUpdate();
			preStat.close();
			locId = newId;
		} else {
			rst = statement
					.executeQuery("select id as ID from Location where city='"
							+ city + "' and state='" + state
							+ "' and country='" + country + "'");
			rst.next();
			locId = Integer.parseInt(rst.getString(1));
		}
		return locId;
	}

	public static void addNewPhoto(String[] args) throws SQLException {
		Connection conn = getConn();
		PreparedStatement preStat = null;
		//
		String timestamp = args[0];
		String namePher = args[1];
		String type = args[2];
		String city = args[3];
		String state = args[4];
		String country = args[5];

		try {
			preStat = conn
					.prepareStatement("insert into Photo(id,takenAt,takenBy,photographedAt,type) values(?,?,?,?,?)");
			int photoId = generateID("Photo", conn, "id") + 1;
			int takenBy = getPhotographerOrPersonId(namePher, true, conn);
			int photographedAt = getLocationId(city, state, country, conn);

			Timestamp ts = Timestamp.valueOf(timestamp);
			preStat.setInt(1, photoId);
			preStat.setTimestamp(2, ts);
			preStat.setInt(3, takenBy);
			preStat.setInt(4, photographedAt);
			preStat.setString(5, type);
			preStat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (preStat != null) {
				preStat.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

	}

	public static Connection getConn() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, "photo", "photo");

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return conn;
	}

	public static void addNewAppearance(String[] args) throws SQLException {

		Connection conn = getConn();
		PreparedStatement preStat = null;
		int photoId = Integer.parseInt(args[0]);
		String personName = args[1];

		int personId = getPhotographerOrPersonId(personName, false, conn);
		try {

			preStat = conn
					.prepareStatement("insert into Appearance(shows,isShownIn) values(?,?)");
			preStat.setInt(1, personId);
			preStat.setInt(2, photoId);
			preStat.executeUpdate();
			preStat.close();

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			if (preStat != null) {
				preStat.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static boolean createCityIfNecessary(String city, ReturnObj obj,
			Connection conn) throws SQLException {
		Statement state = conn.createStatement();
		ResultSet rst = state
				.executeQuery("select count(*) as countNum, id as locId from Location where city='"
						+ city + "'");
		// get only first one
		rst.first();
		int count = Integer.parseInt(rst.getString(1));

		if (count >= 1) {
			// get First one id
			int locId = Integer.parseInt(rst.getString(2));
			// store existed one into an object
			obj.setLocId(locId);
			// return false to mark it existing
			return false;
		} else {
			// create new location id
			int newLocId = generateID("Location", conn, "id") + 1;
			PreparedStatement preStat = conn
					.prepareStatement("insert into Location(id,city,state,country) values(?,?,?,?)");
			preStat.setInt(1, newLocId);
			preStat.setString(2, city);
			preStat.setString(3, "MA");
			preStat.setString(4, "USA");
			preStat.executeUpdate();
			// store the newly created id in returned object
			obj.setLocId(newLocId);
			return true;
		}

	}

	public static class ReturnObj {

		private int personId;
		private int locId;

		public int getLocId() {
			return locId;
		}

		public void setLocId(int locId) {
			this.locId = locId;
		}

		public int getPersonId() {
			return personId;
		}

		public void setPersonId(int personId) {
			this.personId = personId;
		}

	}

	/**
	 * 
	 * @param conn
	 * @return if there is 0 named "Staff" Person in database
	 * @throws SQLException
	 */
	public static boolean isZeroStaffPerson(Connection conn)
			throws SQLException {

		Statement stat = conn.createStatement();
		ResultSet rst = stat
				.executeQuery("Select count(*) as countNum, id as Myid from Person where name='"
						+ STAFF_NAME + "'");
		rst.first();
		int count = Integer.parseInt(rst.getString(1));
		return count == 0 ? true : false;

	}

	public static boolean isStaffPherNotInBos(Connection conn)
			throws SQLException {

		Statement stat = conn.createStatement();
		ResultSet rst = stat
				.executeQuery("Select count(*) as countNum from Person p join photographer ph on p.id=ph.id join location l on l.id=ph.livesIn where name='"
						+ STAFF_NAME + "' and l.city!='" + LIVING_CITY + "'");
		rst.first();
		int count = Integer.parseInt(rst.getString(1));

		return count >= 1 ? true : false;
	}

	public static boolean isJustStaffPerson(Connection conn, ReturnObj obj)
			throws SQLException {

		Statement stat = conn.createStatement();
		ResultSet rst = stat
				.executeQuery("select count(*) , id from Person where name='"+STAFF_NAME+"' and id not in (select id from photographer)");

		rst.first();
		int count = Integer.parseInt(rst.getString(1));
		if (count >= 1) {
			int pid = Integer.parseInt(rst.getString(2));
			obj.setPersonId(pid);
			return true;
		} else {
			return false;
		}

	}

	public static boolean isSatisfiedAllRequirement(Connection conn, ReturnObj retObj) throws SQLException{
		Statement stat = conn.createStatement();
		ResultSet rst = stat
				.executeQuery("select count(*) as countNum, ph.id as myId,l.id as locId from Person p join Photographer ph on ph.id=p.id join Location l on l.id=ph.livesIn  "
						+ "where p.name='"+STAFF_NAME+"' and l.city='"+LIVING_CITY+"'");

		// if more than one satisfying choose the first one
		rst.first();
		int count = Integer.parseInt(rst.getString(1));
		
		if(count>=1){
			int personId = Integer.parseInt(rst.getString(2));
			retObj.setPersonId(personId);
			int locId = Integer.parseInt(rst.getString(3));
			retObj.setLocId(locId);
			return true;
		}else{
			return false;
		}
		
	}
	/**
	 * 
	 * @param conn
	 * @return byte vector last three bits represent from left to right insert
	 *         location,photographer,person.
	 * @throws SQLException
	 */
	public static ReturnObj CheckIfStaffExist(Connection conn)
			throws SQLException {
		ReturnObj retObj = new ReturnObj();

		if (isSatisfiedAllRequirement(conn,retObj)) {
			return retObj;
		} else {
			// TODO
			createCityIfNecessary("Boston", retObj, conn);

			if (isZeroStaffPerson(conn) || isStaffPherNotInBos(conn)) {
				int newPersonId = generateID("Person", conn, "id") + 1;
				PreparedStatement preStat = conn
						.prepareStatement("insert into person(id,name) values(?,'"+STAFF_NAME+"')");
				preStat.setInt(1, newPersonId);
				preStat.executeUpdate();

				preStat = conn
						.prepareStatement("insert into photographer(id,livesIn) values(?,?)");
				preStat.setInt(1, newPersonId);
				preStat.setInt(2, retObj.getLocId());
				preStat.executeUpdate();
				preStat.close();
				// store it in returned ojbect
				retObj.setPersonId(newPersonId);
				return retObj;

			}
			if (isJustStaffPerson(conn, retObj)) {
				PreparedStatement preStat = conn
						.prepareStatement("insert into photographer(id,livesIn) values(?,?)");
				preStat.setInt(1, retObj.getPersonId());
				preStat.setInt(2, retObj.getLocId());
				preStat.executeUpdate();
				preStat.close();
				return retObj;
			}
			return retObj;
		}
	}

	public static void firePhotographer(String[] args) throws SQLException {
		Connection conn = getConn();
		int firedPerId = Integer.parseInt(args[0]);

		
		/**/
		
		String updateQuery = "update photo set takenBy=null where takenBy=?";
		
		Statement queryStat=conn.createStatement();
		ResultSet rst=queryStat.executeQuery("select id from photo where takenBy="+firedPerId);
		
		
		
		PreparedStatement stat = conn.prepareStatement(updateQuery);
		stat.setInt(1, firedPerId);
		stat.executeUpdate();

		String deleteQuery = "delete from photographer where id=?";
		stat = conn.prepareStatement(deleteQuery);
		stat.setInt(1, firedPerId);
		stat.executeUpdate();

		ReturnObj obj = CheckIfStaffExist(conn);

		while(rst.next()){
			String update2Staff = "update photo set takenBy=? where id="+rst.getInt(1);
			stat = conn.prepareStatement(update2Staff);
			stat.setInt(1, obj.getPersonId());
			stat.executeUpdate();
			
		}
		
		
		stat.close();
		conn.close();

	}

	public static void main(String[] args) throws SQLException {
		
		String method = args[0].trim();
		String[] realArgs = new String[args.length - 1];
		System.arraycopy(args, 1, realArgs, 0, realArgs.length);

		if (method.equals("newPhoto")) {

			addNewPhoto(realArgs);

		} else if (method.equals("newAppearance")) {
			addNewAppearance(realArgs);

		} else if (method.equals("firePhotographer")) {
			firePhotographer(realArgs);
		}
		
	}
}
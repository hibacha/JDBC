package cs5200.jdbc.mysql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.WebRowSet;

public class Result2XML {

	/**
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		Connection conn = PhotoService.getConn();
		Statement stat = conn.createStatement();
		ResultSet rst = stat.executeQuery("select id as PHOTOID, takenBy as PHOTOGRAPHER from photo ");

		File file = new File("./result.xml");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		
		WebRowSet wrs = new com.sun.rowset.WebRowSetImpl();
		wrs.writeXml(rst, fos);

		stat.close();
		conn.close();
	}

}

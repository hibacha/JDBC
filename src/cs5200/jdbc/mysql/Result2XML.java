package cs5200.jdbc.mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.rowset.WebRowSet;

public class Result2XML {
    
    private static void flush2ArrayList(StringBuilder sb,ArrayList<String> list){
    	int currentQueryLen=sb.toString().trim().length();
		//exist a completed query 
		if(currentQueryLen>0 ) list.add(sb.toString());
	   	//TODO
		sb.setLength(0);
    }
	/**
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	public static ArrayList<String> loadQuery() throws IOException{
		File queryFile=new File("/Users/zhouyf/git/JDBC/script/query.sql");
		ArrayList<String> list=new ArrayList<String>(2);
		if(!queryFile.exists()){
			throw new IOException("not exist query file!");
		}
		BufferedReader br=new BufferedReader(new FileReader(queryFile));
		String line="";
		StringBuilder sb=new StringBuilder();
		while((line=br.readLine())!=null){
			
			if(line.trim().length()==0){
				flush2ArrayList(sb,list);
			}else{
				sb.append(line);
			}
		}
		flush2ArrayList(sb, list);
		br.close();
		return list;
	}
	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		Connection conn = PhotoService.getConn();
		Statement stat = conn.createStatement();
		ArrayList<String> list=loadQuery();
		for(int i=0;i<list.size();i++){
			File file = new File("./script/result"+(i+1)+".xml");
			if (!file.exists()) {
				file.createNewFile();
			}
			ResultSet rst = stat
					.executeQuery(list.get(i));
			FileOutputStream fos = new FileOutputStream(file);

			WebRowSet wrs = new com.sun.rowset.WebRowSetImpl();
			wrs.writeXml(rst, fos);

			
		}
		stat.close();
		conn.close();
	}

}

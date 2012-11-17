import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PhotoServiceTest {
	
	Connection conn;
	@Before
	public void setUp() throws Exception {
	     conn= PhotoService.getConn();	
	}

	@After
	public void tearDown() throws Exception {
		conn.close();
	}

	@Test
	public void test() throws SQLException {
//		assertEquals(true, PhotoService.isNeedToCreateCity("Bos",conn));
//	   // assertEquals(true, PhotoService.isNeedToCreatePerson("Staff", conn));
//	    assertEquals(true, PhotoService.isNeedToCreatePhotographer("M4ike", conn));
	}
	
	@Test
	public void testBitCheckForFirePhotographer() throws SQLException {
		assertEquals(1, PhotoService.CheckIfStaffExist(conn));
	    
	}
	
	@Test
	public void testFirePher() throws SQLException {
		PhotoService.firePhotographer(new String[]{"1"});
	    
	}
	
}

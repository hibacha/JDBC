import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Result2XMLTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		ArrayList<String> array=Result2XML.loadQuery();
	   for(String query:array)
		System.out.println(query);
	}

}

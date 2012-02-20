package net.craftstars.general.text;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MessagingTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testSplitLines() {
		String line = "some very long line with (a parenthesis in it somewhere that is not matched until the next line)";
		List<String> split = Messaging.splitLines(line);
		assertEquals(2, split.size());
		int totalLen = 0;
		for(String s : split) totalLen += s.replaceAll("[\r\n]","").length();
		assertEquals(line.length(), totalLen);
	}
}

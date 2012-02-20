package net.craftstars.general.text;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
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
	
	@Test
	public final void testFormatAndSplit() {
		String msg = LanguageText.PERMISSION_LACK.value(
			"action", LanguageText.LACK_TELEPORT.value(
				"target", LanguageText.TARGET_SELF.value(),
				"destination", LanguageText.DESTINATION_HOME_OTHER.value(),
				"world", "test world"
			),
			"permission", "general.teleport.to.home",
			"rose", ChatColor.RED
		);
		assertEquals(ChatColor.RED + "You don't have permission to teleport yourself to other players' homes in world" +
				" 'test world'. (general.teleport.to.home)", msg);
		List<String> split = Messaging.splitLines(msg);
		List<String> expect = Arrays.asList(
			ChatColor.RED + "You don't have permission to teleport yourself to other ",
			ChatColor.RED + "players' homes in world 'test world'. (general.teleport.to.home)"
		);
		for(int i = 0; i < split.size(); i++) assertEquals(expect.get(i), split.get(i));
	}
}

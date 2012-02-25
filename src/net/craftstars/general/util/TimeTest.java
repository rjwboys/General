package net.craftstars.general.util;

import static org.junit.Assert.*;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.craftstars.general.option.Option;
import net.craftstars.general.option.Options;
import net.craftstars.general.util.Time.TimeFormat;

public class TimeTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Option.setConfiguration(new MemoryConfiguration());
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		Options.SHOW_TICKS.set(true);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testFormatTime12() {
		assertEquals("4:12am (22200)", Time.formatTime(22200, TimeFormat.TWELVE_HOUR)); // Dawn
		assertEquals("6:00am (0)", Time.formatTime(0, TimeFormat.TWELVE_HOUR)); // Day
		assertEquals("12:00pm (6000)", Time.formatTime(6000, TimeFormat.TWELVE_HOUR)); // Noon
		assertEquals("6:00pm (12000)", Time.formatTime(12000, TimeFormat.TWELVE_HOUR)); // Dusk
		assertEquals("7:48pm (13800)", Time.formatTime(13800, TimeFormat.TWELVE_HOUR)); // Night
		assertEquals("12:00am (18000)", Time.formatTime(18000, TimeFormat.TWELVE_HOUR)); // Midnight
		Options.SHOW_TICKS.set(false);
		assertEquals("6:00am", Time.formatTime(0, TimeFormat.TWELVE_HOUR));
	}
	
	@Test
	public final void testExtractTime12() {
		assertEquals(22200L, Time.extractTime("4:12am")); // Dawn
		assertEquals(0L, Time.extractTime("06:00am")); // Day
		assertEquals(6000L, Time.extractTime("12:00pm")); // Noon
		assertEquals(12000L, Time.extractTime("6pm")); // Dusk
		assertEquals(13800L, Time.extractTime("7:48pm")); // Night
		assertEquals(18000L, Time.extractTime("12am")); // Midnight
	}
	
	@Test
	public final void testFormatTime24() {
		assertEquals("04:12h (22200)", Time.formatTime(22200, TimeFormat.TWENTY_FOUR_HOUR)); // Dawn
		assertEquals("06:00h (0)", Time.formatTime(0, TimeFormat.TWENTY_FOUR_HOUR)); // Day
		assertEquals("12:00h (6000)", Time.formatTime(6000, TimeFormat.TWENTY_FOUR_HOUR)); // Noon
		assertEquals("18:00h (12000)", Time.formatTime(12000, TimeFormat.TWENTY_FOUR_HOUR)); // Dusk
		assertEquals("19:48h (13800)", Time.formatTime(13800, TimeFormat.TWENTY_FOUR_HOUR)); // Night
		assertEquals("00:00h (18000)", Time.formatTime(18000, TimeFormat.TWENTY_FOUR_HOUR)); // Midnight
		Options.SHOW_TICKS.set(false);
		assertEquals("19:48h", Time.formatTime(13800, TimeFormat.TWENTY_FOUR_HOUR));
	}
	
	@Test
	public final void testExtractTime24() {
		assertEquals(22200L, Time.extractTime("4:12h")); // Dawn
		assertEquals(0L, Time.extractTime("06:00")); // Day
		assertEquals(6000L, Time.extractTime("12:00")); // Noon
		assertEquals(12000L, Time.extractTime("18h")); // Dusk
		assertEquals(13800L, Time.extractTime("19:48h")); // Night
		assertEquals(18000L, Time.extractTime("0:00")); // Midnight
	}
	
	@Test
	public final void testFormatDuration() {
		assertEquals("3 ticks", Time.formatDuration(3));
		assertEquals("3 minutes", Time.formatDuration(50));
		assertEquals("3 hours", Time.formatDuration(3000));
		assertEquals("3 hours and 3 minutes", Time.formatDuration(3050));
	}
	
	@Test
	public final void testExtractDuration() {
		assertEquals(3L, Time.extractDuration("3"));
		assertEquals(50L, Time.extractDuration("3m"));
		assertEquals(3000L, Time.extractDuration("3h"));
		assertEquals(3050L, Time.extractDuration("3h3m"));
	}
	
}

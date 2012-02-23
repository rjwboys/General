/**
 * 
 */
package net.craftstars.general.text;

import static org.hamcrest.core.IsInstanceOf.*;
import static org.junit.Assert.*;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class MappedMessageTest {
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
	public final void testConstruct1() {
		String fmt = "{field}";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("{0}", mmf.getInternal().toPattern());
	}
	
	@Test
	public final void testConstruct2() {
		String fmt = "'quote'";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("{0}quote{0}", mmf.getInternal().toPattern());
	}
	
	@Test
	public final void testConstruct3() {
		String fmt = "`{escapes}`";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("'{'escapes'}'", mmf.getInternal().toPattern());
	}
	
	@Test
	public final void testConstruct4() {
		String fmt = "{year,number}-{month,number}-{day,number}";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("{0,number}-{1,number}-{2,number}", mmf.getInternal().toPattern());
	}
	
	@Test
	public final void testFormat1() {
		String fmt = "'{one}'";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("one", 'A');
		args.put("two", 'Q');
		args.put("3", 42);
		String result = MappedMessageFormat.format(fmt, args);
		assertEquals("'A'", result);
	}
	
	@Test
	public final void testFormat2() {
		String fmt = "`{two}`";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("one", 'A');
		args.put("two", 'Q');
		args.put("3", 42);
		String result = MappedMessageFormat.format(fmt, args);
		assertEquals("{two}", result);
	}
	
	@Test
	public final void testFormat3() {
		String fmt = "{3}";
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("one", 'A');
		args.put("two", 'Q');
		args.put("3", 42);
		args.put("hello", "world");
		String result = MappedMessageFormat.format(fmt, args);
		assertEquals("{3}", result);
	}
	
	@Test
	public final void testFormat4() {
		String fmt = "`an 'escaped' quote`";
		Map<String,Object> args = new HashMap<String,Object>();
		String result = MappedMessageFormat.format(fmt, args);
		assertEquals("an 'escaped' quote", result);
	}
	
	@Test
	public final void testParseString() {
		MappedMessageFormat mmf = new MappedMessageFormat("{year,number}-{month,number}-{day,number}");
		Map<String, Object> vals = mmf.parse("1999-12-25");
		assertNotNull(vals);
		assertNotNull(vals.get("year"));
		assertEquals(1999L, vals.get("year"));
		assertNotNull(vals.get("month"));
		assertEquals(12L, vals.get("month"));
		assertNotNull(vals.get("day"));
		assertEquals(25L, vals.get("day"));
	}
	
	@Test
	public final void testClone() {
		MappedMessageFormat mmf = new MappedMessageFormat("{one} and '{two}' in `{`three`}`");
		MappedMessageFormat mmf2 = mmf.clone();
		assertEquals(mmf.toPattern(), mmf2.toPattern());
	}
	
	@Test
	public final void testGetFormatsByArgument() {
		String fmt = "{one,number} and {two,choice,1#A|2#B} or {three,date} at {four,time}";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		Map<String,Format> fmts = mmf.getFormatsByArgument();
		assertNotNull(fmts);
		assertNotNull(fmts.get("one"));
		assertThat(fmts.get("one"), instanceOf(DecimalFormat.class));
		assertNotNull(fmts.get("two"));
		assertThat(fmts.get("two"), instanceOf(ChoiceFormat.class));
		assertEquals("1.0#A|2.0#B",((ChoiceFormat)fmts.get("two")).toPattern());
		assertNotNull(fmts.get("three"));
		assertThat(fmts.get("three"), instanceOf(DateFormat.class));
		assertNotNull(fmts.get("four"));
		assertThat(fmts.get("four"), instanceOf(DateFormat.class));
	}
	
	@Test
	public final void testSetFormatByArgument() {
		String fmt = "{one,number,¢#000.000#}, {two,date,full}, {three,choice,1#A|2#B}";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		mmf.setFormatByArgument("one", new DecimalFormat("$0.00"));
		mmf.setFormatByArgument("two", DateFormat.getDateInstance(DateFormat.LONG));
		mmf.setFormatByArgument("three", new ChoiceFormat("1#B|2#A"));
		assertEquals("{one,number,$#0.00}, {two,date,long}, {three,choice,1.0#B|2.0#A}", mmf.toPattern());
	}
	
	@Test
	public final void testEqualsObject() {
		MappedMessageFormat mmf = new MappedMessageFormat("{one} and '{two}' in `{`three`}`");
		MappedMessageFormat mmf2 = new MappedMessageFormat("{one} and '{two}' in `{`three`}`");
		MappedMessageFormat mmf3 = mmf.clone();
		assertEquals(mmf, mmf2);
		assertEquals(mmf, mmf3);
		assertEquals(mmf2,mmf3);
	}
	
	@Test
	public final void testToPattern1() {
		String fmt = "{field}";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("{field}", mmf.toPattern());
	}
	
	@Test
	public final void testToPattern2() {
		String fmt = "'quote'";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("'quote'", mmf.toPattern());
	}
	
	@Test
	public final void testToPattern3() {
		String fmt = "`{escapes}`";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("`{`escapes`}`", mmf.toPattern());
	}
	
	@Test
	public final void testToPattern4() {
		String fmt = "{year,number}-{month,number}-{day,number}";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("{year,number}-{month,number}-{day,number}", mmf.toPattern());
	}
	
	@Test
	public final void testSplit1() {
		String fmt = "``````";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("`````", mmf.toPattern());
	}
	
	@Test
	public final void testSplit2() {
		String fmt = "`````";
		MappedMessageFormat mmf = new MappedMessageFormat(fmt);
		assertEquals("`````", mmf.toPattern());
	}
}

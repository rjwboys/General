package net.craftstars.general.util;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ToolboxTest {
	
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
	public final void testEqualsOne() {
		assertTrue(Toolbox.equalsOne("testA", "testA", "testB"));
		assertTrue(Toolbox.equalsOne("testB", "testA", "testB"));
		assertFalse(Toolbox.equalsOne("testC", "testA", "testB"));
	}
	
	@Test
	public final void testRepeat() {
		String rep = Toolbox.repeat('*', 10);
		assertEquals(10, rep.length());
	}
	
	@Test
	public final void testFormatItemNameCamel() {
		assertEquals("Some Item", Toolbox.formatItemName("SomeItem"));
	}
	
	@Test
	public final void testFormatItemNameCameloid() {
		assertEquals("My Abc", Toolbox.formatItemName("MyABC"));
	}
	
	@Test
	public final void testFormatItemNameEnum() {
		assertEquals("Some Item", Toolbox.formatItemName("SOME_ITEM"));
	}
	
	private static String[] joinTester = new String[] {"A", "B", "C", "D"};
	
	@Test
	public final void testJoinStringArrayStringInt() {
		assertEquals("B--C--D", Toolbox.join(joinTester, "--", 1));
	}
	
	@Test
	public final void testJoinStringArrayString() {
		assertEquals("A--B--C--D", Toolbox.join(joinTester, "--"));
	}
	
	@Test
	public final void testJoinStringArrayInt() {
		assertEquals("B C D", Toolbox.join(joinTester, 1));
	}
	
	@Test
	public final void testJoinStringArray() {
		assertEquals("A B C D", Toolbox.join(joinTester));
	}
	
	@Test
	public final void testEnumValue() {
		assertNull(Toolbox.enumValue(Material.class, "NON_EXISTENT_MATERIAL"));
		assertEquals(Material.BLAZE_POWDER, Toolbox.enumValue(Material.class, "BLAZE_POWDER"));
	}
	
	@Test
	public final void testCartesianProduct() {
		String[] left = new String[] {"A", "B", "C"};
		String[] right = new String[] {"1", "2", "3"};
		char joiner = '~';
		String[] expected = new String[] {"A~1", "A~2", "A~3", "B~1", "B~2", "B~3", "C~1", "C~2", "C~3"};
		String[] actual = Toolbox.cartesianProduct(left, right, joiner);
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < expected.length; i++) assertEquals(expected[i], actual[i]);
	}
	
	@Test
	public final void testToRoman() {
		assertEquals("O", Toolbox.toRoman(0));
		assertEquals("I", Toolbox.toRoman(1));
		assertEquals("II", Toolbox.toRoman(2));
		assertEquals("III", Toolbox.toRoman(3));
		assertEquals("IV", Toolbox.toRoman(4));
		assertEquals("V", Toolbox.toRoman(5));
		assertEquals("VI", Toolbox.toRoman(6));
		assertEquals("VII", Toolbox.toRoman(7));
		assertEquals("VIII", Toolbox.toRoman(8));
		assertEquals("IX", Toolbox.toRoman(9));
		assertEquals("X", Toolbox.toRoman(10));
		assertEquals("XIV", Toolbox.toRoman(14));
		assertEquals("XVI", Toolbox.toRoman(16));
		assertEquals("XXX", Toolbox.toRoman(30));
		assertEquals("XL", Toolbox.toRoman(40));
		assertEquals("XLIV", Toolbox.toRoman(44));
		assertEquals("XLIX", Toolbox.toRoman(49));
		assertEquals("L", Toolbox.toRoman(50));
		assertEquals("XCIX", Toolbox.toRoman(99));
		assertEquals("C", Toolbox.toRoman(100));
		assertEquals("CCC", Toolbox.toRoman(300));
		assertEquals("CD", Toolbox.toRoman(400));
		assertEquals("CDIX", Toolbox.toRoman(409));
		assertEquals("CDXCIX", Toolbox.toRoman(499));
		assertEquals("D", Toolbox.toRoman(500));
		assertEquals("CM", Toolbox.toRoman(900));
		assertEquals("M", Toolbox.toRoman(1000));
		assertEquals("MCMXLIX", Toolbox.toRoman(1949));
		assertEquals("MMM", Toolbox.toRoman(3000));
	}
}

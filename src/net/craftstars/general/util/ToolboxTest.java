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
	
}

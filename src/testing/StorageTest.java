package testing;

import java.io.IOException;

import junit.framework.TestCase;
import logger.LogSetup;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import server.storage.Storage;

public class StorageTest extends TestCase {
	Storage storage;

	/**
	 * Initialize storage to interact with it
	 */
	@Before
	public void setUp() throws Exception {
		try {
			new LogSetup("logs/testing/StorageTest.log", Level.DEBUG);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		storage = Storage.init();
	}
	
	/**
	 * Test in the specific order: put, update and then get
	 */
	@Test
	public void testStoreUpdateRetrieve(){
		testStoreUpdate();
		testRetrieve();
	}
	
	@Test
	public void testMissed() {
		String s = storage.get("key1");
		assertNull(s);
	}

	public void testStoreUpdate() {
		String result = storage.put("key", "v1");

		result = storage.put("key", "abc");
		
		assertTrue(result.equals("v1"));
	}
	
	@Test
	public void testRetrieve() {
		String result = storage.get("key");
		String expResult = "abc";
		//assertEquals(result, expResult);
		assertTrue(result.equals("abc"));
	}

}

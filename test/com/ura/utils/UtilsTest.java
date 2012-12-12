package com.ura.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

public class UtilsTest {

	private Logger logger = Logger.getLogger(UtilsTest.class);

	@Test
	/**
	 * Get's xml file with and creates Map object with port's as keys 
	 * and InetSocketAdrress as values.
	 */
	public void xmlToHashMapTest() {
		logger.debug("enter xmlToHashMapTest");
		File xmlFile = new File("test/proxies.xml");
		Map<Integer, InetSocketAddress> actualResult = Utils.mapForXML(xmlFile);
		Map<Integer, InetSocketAddress> expectedResult = getExpectedMap();
		assertEquals(expectedResult, actualResult);
		logger.debug("exit xmlToHashMapTest");
	}

	@Test
	/**
	 * Case when the file dose not exits
	 */
	public void xmlToHashMapReturnsNullTest() {
		logger.debug("enter xmlToHashMapReturnsNullTest");
		File xmlFile = new File("tessdt/pdsroxies.xml");
		Map<Integer, InetSocketAddress> actualResult = Utils.mapForXML(xmlFile);
		assertEquals(actualResult.size(), 0);

	}

	@Test
	/**
	 * Case when the file exists but data is not right
	 */
	public void xmlToHashMapBrokenFile() {
		logger.debug("enter xmlToHashMapReturnsNullTest");
		File xmlFile = new File("test/brokenFile.xml");
		Map<Integer, InetSocketAddress> actualResult = Utils.mapForXML(xmlFile);
		assertEquals(actualResult.size(), 0);

	}

	@Test
	/**
	 * Case when the file exists but data is not parrsable
	 */
	public void xmlToHashMapBrokenFile2() {
		logger.debug("enter xmlToHashMapReturnsNullTest");
		File xmlFile = new File("test/non.txt");
		Map<Integer, InetSocketAddress> actualResult = Utils.mapForXML(xmlFile);
		assertEquals(actualResult.size(), 0);

	}
	
	
	@Test
	public void rewriteXml(){
		Utils.reWriteProxieElementInToXML(7412,"111.2.2.1",4444, new File("test/proxies.xml"));
	}
	
	/**
	 * Utility function that creates and returns map that suppose to be in xml
	 * for testing.
	 */
	private Map<Integer, InetSocketAddress> getExpectedMap() {
		Map<Integer, InetSocketAddress> expectedResult = new HashMap<Integer, InetSocketAddress>();
		expectedResult.put(new Integer(9001), new InetSocketAddress(
				"127.0.0.1", 9002));
		expectedResult.put(new Integer(9321), new InetSocketAddress(
				"127.0.0.1", 9002));
		expectedResult.put(new Integer(1221), new InetSocketAddress(
				"124.0.0.1", 9221));
		expectedResult.put(new Integer(2001), new InetSocketAddress(
				"137.0.0.1", 7342));
		return expectedResult;
	}
}

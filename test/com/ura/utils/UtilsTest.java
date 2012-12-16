package com.ura.utils;

import static org.junit.Assert.assertEquals;

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
		File xmlFile = new File("test/proxies.xml");
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> expectedResult = getExpectedMap();
		assertXMLFileWithMap(xmlFile, expectedResult);
	}

	private void assertXMLFileWithMap(
			File xmlFile,
			Map<String, Pair<InetSocketAddress, InetSocketAddress>> expectedResult) {
		logger.debug("enter xmlToHashMapTest");

		Map<String, Pair<InetSocketAddress, InetSocketAddress>> actualResult = Utils
				.mapForXML(xmlFile);

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
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> actualResult = Utils
				.mapForXML(xmlFile);
		assertEquals(actualResult.size(), 0);

	}

	@Test
	/**
	 * Case when the file exists but data is not right
	 */
	public void xmlToHashMapBrokenFile() {
		logger.debug("enter xmlToHashMapReturnsNullTest");
		File xmlFile = new File("test/brokenFile.xml");
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> actualResult = Utils
				.mapForXML(xmlFile);
		assertEquals(actualResult.size(), 0);

	}

	@Test
	/**
	 * Case when the file exists but data is not parrsable
	 */
	public void xmlToHashMapBrokenFile2() {
		logger.debug("enter xmlToHashMapReturnsNullTest");
		File xmlFile = new File("test/non.txt");
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> actualResult = Utils
				.mapForXML(xmlFile);
		assertEquals(actualResult.size(), 0);

	}

	@Test
	/**
	 * Gets the xml and removes proxy that is already exists in xml.
	 * The result is written in separate file but it can be the same file from which data came from.
	 */
	public void rewriteXmlByRemovingProxy() {
		File fromFile = new File("test/proxies.xml");
		File toFile = new File("test/proxiesRemoved.xml");
		Utils.reWriteProxieElementInToXML("aaaa", 7412, "111.2.2.1", 4444,
				fromFile, toFile);
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> expectedResult = getExpectedMap();
		expectedResult.remove("aaaa");
		assertXMLFileWithMap(toFile, expectedResult);
	}

	@Test
	/**
	 * Gets the xml and adds proxy that is not in xml.
	 * The result is written in separate file but it can be the same file from which data came from.
	 */
	public void rewriteXmlByAddingProxy() {
		File fromFile = new File("test/proxies.xml");
		File toFile = new File("test/proxiesRemoved.xml");
		Utils.reWriteProxieElementInToXML("bbbb", 7412, "111.2.2.1", 4444,
				fromFile, toFile);
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> expectedResult = new HashMap<String, Pair<InetSocketAddress, InetSocketAddress>>(getExpectedMap());
		Pair<InetSocketAddress, InetSocketAddress> pair = new Pair<InetSocketAddress, InetSocketAddress>(new InetSocketAddress(7412),new InetSocketAddress("111.2.2.1",4444));
		expectedResult.put("bbbb", pair);
		assertXMLFileWithMap(toFile, expectedResult);
	}

	/**
	 * Utility function that creates and returns map that suppose to be in xml
	 * for testing.
	 */
	private Map<String, Pair<InetSocketAddress, InetSocketAddress>> getExpectedMap() {
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> expectedResult = new HashMap<String, Pair<InetSocketAddress, InetSocketAddress>>();
		expectedResult.put("ebs",
				new Pair<InetSocketAddress, InetSocketAddress>(
						new InetSocketAddress(new Integer(9001)),
						new InetSocketAddress("127.0.0.1", 9002)));
		expectedResult.put("city",
				new Pair<InetSocketAddress, InetSocketAddress>(
						new InetSocketAddress(new Integer(9321)),
						new InetSocketAddress("127.0.0.1", 9002)));
		expectedResult.put("blabla",
				new Pair<InetSocketAddress, InetSocketAddress>(
						new InetSocketAddress(new Integer(1221)),
						new InetSocketAddress("124.0.0.1", 9221)));
		expectedResult.put("fd",
				new Pair<InetSocketAddress, InetSocketAddress>(
						new InetSocketAddress(new Integer(2001)),
						new InetSocketAddress("137.0.0.1", 7342)));
		expectedResult.put("aaaa",
				new Pair<InetSocketAddress, InetSocketAddress>(
						new InetSocketAddress(new Integer(7412)),
						new InetSocketAddress("111.2.2.1", 4444)));

		return expectedResult;
	}
}

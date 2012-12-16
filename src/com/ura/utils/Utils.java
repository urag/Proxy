package com.ura.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);

	/**
	 * Gets xml file and returns Map object with id's as keys and address pairs
	 * as values.
	 * 
	 * @param xmlFile
	 *            file to read xml from
	 * @return map of port InetSocketAddress pairs
	 */
	public static Map<String, Pair<InetSocketAddress, InetSocketAddress>> mapForXML(
			File xmlFile) {
		Map<String, Pair<InetSocketAddress, InetSocketAddress>> resultMap = new HashMap<String, Pair<InetSocketAddress, InetSocketAddress>>();
		NodeList portAddresPairs;
		logger.info("Creating map with ports and toAddresses");
		portAddresPairs = getRootChildren(xmlFile);
		for (int i = 0; i < portAddresPairs.getLength(); i++) {
			Node child = portAddresPairs.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				Integer port = Integer.valueOf(childElement
						.getAttribute("fromport"));
				Integer toPort = Integer.valueOf(childElement
						.getAttribute("toport"));
				String toIp = childElement.getAttribute("toip");
				String id = childElement.getAttribute("id");
				InetSocketAddress inAddress = new InetSocketAddress(port);
				InetSocketAddress outAddress = new InetSocketAddress(toIp,
						toPort);
				Pair<InetSocketAddress, InetSocketAddress> inOutAddressPair = new Pair<InetSocketAddress, InetSocketAddress>(
						inAddress, outAddress);

				logger.info("Adding pair with in port : " + port
						+ " and out address " + toIp + " " + toPort);
				resultMap.put(id, inOutAddressPair);
			}

		}
		return resultMap;
	}

	/**
	 * Gets a new proxy data and xml file to write it to .If the proxy is
	 * already in the file than it's removed if it's not present in the file
	 * than it's added.
	 * 
	 * @param id
	 *            of the proxy client for example EBS or City
	 * @param port
	 *            on which proxy should listen for messages
	 * @param host
	 *            ip or host name of the recipient
	 * @param outPort
	 *            port to which messages should came to.
	 * @param xmlFile
	 *            file to get data from.
	 * @param toFile
	 *            file to write new xml in to.
	 */
	public static void reWriteProxieElementInToXML(String id, Integer port,
			String host, Integer outPort, File xmlFile, File toFile) {

		Document doc = getDocFromXml(xmlFile);

		NodeList children = getRootChildren(doc);// getRootChildren(xmlFile);
		Node proxyToRemove = findProxy(id, children);
		// If proxy not found than it needs to be added
		if (proxyToRemove == null) {
			Element node = doc.createElement("proxy");
			node.setAttribute("id", id);
			node.setAttribute("fromport", port.toString());
			node.setAttribute("toip", host);
			node.setAttribute("toport", outPort.toString());
			logger.info("Adding proxy to xml with port " + port
					+ " and address " + host + " " + outPort);
			doc.getDocumentElement().appendChild(node);
			logger.info("Proxy to xml with port " + port + " and address "
					+ host + " " + outPort + " is been added to xml");

		} else {
			logger.info("Removing from xml proxy between " + port
					+ " and address " + host + " " + outPort);
			doc.getDocumentElement().removeChild(proxyToRemove);
			logger.info("Proxy between " + port + " and address " + host + " "
					+ outPort + " is removed from xml");

		}

		writeXMLDocToFile(doc, toFile);

	}

	/**
	 * Gets proxy id and finds it in the node list.
	 * 
	 * @param id
	 *            of the proxy
	 * @param children
	 *            list of children of the xml
	 * @return
	 */
	private static Node findProxy(String id, NodeList children) {
		Node proxy = null;
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				String vid = childElement.getAttribute("id");
				if (vid.equals(id))
					proxy = childElement;
			}
		}
		return proxy;
	}

	/**
	 * Write an xml in to the file
	 * 
	 * @param doc
	 *            represents the xml
	 * @param xmlFile
	 */
	public static void writeXMLDocToFile(Document doc, File xmlFile) {
		try {

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);

			logger.info("File saved! To "+xmlFile.getAbsolutePath());
		} catch (TransformerException e) {
			logger.error("Writing in to xml", e);
		}

	}

	/**
	 * For clearance extraction of Document object from xml is in separate
	 * method
	 * 
	 * @param xmlFile
	 *            file from which Document object should be extracted .
	 * @return Document object that represents xml file.
	 */
	private static Document getDocFromXml(File xmlFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(xmlFile);
		} catch (SAXException e) {
			logger.error("", e);
			document = null;
		} catch (IOException e) {
			logger.error("", e);
			document = null;
		} catch (ParserConfigurationException e) {
			logger.error("", e);
			document = null;
		}
		return document;
	}

	/**
	 * Returns all the children of the root of xml.
	 * 
	 * @param xmlFile
	 *            file from which to get the xml.
	 * @return
	 */
	private static NodeList getRootChildren(File xmlFile) {
		Document document = getDocFromXml(xmlFile);
		return getRootChildren(document);
	}

	/**
	 * Returns all the children of the root of xml.
	 * 
	 * @param document
	 *            from which to get the root element
	 * @return
	 */
	private static NodeList getRootChildren(Document document) {
		if (document == null)
			return new EmptyNodeList();

		Element root = document.getDocumentElement();
		NodeList result = root.getChildNodes();
		if (result == null)
			result = new EmptyNodeList();
		return result;
	}

	/**
	 * Empty node list to return in case there is nothing to return
	 * 
	 * @author Ura
	 * 
	 */
	private static class EmptyNodeList implements NodeList {

		@Override
		public Node item(int index) {
			return null;
		}

		@Override
		public int getLength() {
			return 0;
		}

	}

}

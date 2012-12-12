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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);

	/**
	 * Gets xml file and returns Map object with ports as keys and address as
	 * values.
	 * 
	 * @param xmlFile
	 *            file to read xml from
	 * @return map of port InetSocketAddress pairs
	 */
	public static Map<Integer, InetSocketAddress> mapForXML(File xmlFile) {
		Map<Integer, InetSocketAddress> resultMap = new HashMap<Integer, InetSocketAddress>();
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
				resultMap.put(port, new InetSocketAddress(toIp, toPort));
				logger.info("Adding pair with in port : " + port
						+ " and out address " + toIp + " " + toPort);
			}

		}
		return resultMap;
	}

	public static void reWriteProxieElementInToXML(Integer port, String host,
			Integer outPort, File xmlFile) {

		Document doc = getDocFromXml(xmlFile);
		NodeList children = getRootChildren(xmlFile);
		boolean addProxy = removeIfExists(port, host, outPort, children);
		Element node = doc.createElement("proxy");
		node.setAttribute("fromport", port.toString());
		node.setAttribute("toip", host);
		node.setAttribute("toport", outPort.toString());
		if(addProxy)
		{
			logger.info("Adding proxy to xml wtih port "+port+" and address "+host+" "+outPort);
			doc.getDocumentElement().appendChild(node);
		}
		else
		{
			logger.info("Removing from xml proxy on port "+port +" and address "+host+" "+outPort);
			doc.removeChild(node);

		}

		writeXMLDocToFile(doc, xmlFile);

	}

	private static boolean removeIfExists(Integer port, String host,
			Integer outPort, NodeList children) {
		boolean addProxy = true ;
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				Integer vport = Integer.valueOf(childElement
						.getAttribute("fromport"));
				Integer vtoPort = Integer.valueOf(childElement
						.getAttribute("toport"));
				String vtoIp = childElement.getAttribute("toip");
				if (port.equals(vport) && host.equals(vtoIp)
						&& outPort.equals(vtoPort)) {
					child.getParentNode().removeChild(child);
					addProxy = false;
				}
			}
		}
		return addProxy;
	}

	public static void writeXMLDocToFile(Document doc, File xmlFile) {
		try {
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);

			// Output to console for testing
			//StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);

			logger.info("File saved!");
		} catch (TransformerException e) {
			logger.error("Writing in to xml", e);
		}


	}

	private static Document getDocFromXml(File xmlFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(xmlFile);
		} catch (SAXException e) {
			logger.error("",e);
			document = null;
		} catch (IOException e) {
			logger.error("",e);
			document = null;
		} catch (ParserConfigurationException e) {
			logger.error("",e);
			document = null;
		}
		return document;
	}

	private static NodeList getRootChildren(File xmlFile) {
		Document document = getDocFromXml(xmlFile);
		return getRootChildren(document);
	}

	private static NodeList getRootChildren(Document document) {
		if(document == null)
			return  new EmptyNodeList();
		
		Element root = document.getDocumentElement();
		NodeList result = root.getChildNodes();
		if (result == null)
			result = new EmptyNodeList();
		return result;
	}

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

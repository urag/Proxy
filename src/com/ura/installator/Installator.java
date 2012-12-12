package com.ura.installator;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


import com.ura.proxy.HexDumpProxy;
import com.ura.utils.Utils;

public class Installator {

	Set<HexDumpProxy> proxys = new HashSet<HexDumpProxy>();
	Logger logger = Logger.getLogger(Installator.class);
	
	/**
	 * Goes over all running proxys and stops those that are not in the new map 
	 * and starting those in new map but not in all in the end writing to config file new proxys pairs. 
	 */
	public void reInstallProxys(Map<Integer, InetSocketAddress> addresses) {
		logger.info("Reinstaling proxys");
		Set<HexDumpProxy> proxysToKill = new HashSet<HexDumpProxy>(proxys);
		Set<HexDumpProxy> proxysToStart = getProxysSetFromAddressMap(addresses);
		//All proxys that are not present in new proxy group need to be killed
		proxysToKill.removeAll(proxysToStart);
		killProxyGroup(proxysToKill);
		//All proxys that are not present in new proxy group need to be started.
		proxysToStart.removeAll(proxys);
		startProxyGroup(proxysToStart);
	}


	public Installator(){
		Map<Integer, InetSocketAddress> addresses = Utils.mapForXML(new File("src/proxies.xml"));
		installProxys(addresses);
	}

	/**
	 * Goes over all proxys in the group and stops them.
	 */
	private void killProxyGroup(Set<HexDumpProxy> proxysToKill) {
		for(HexDumpProxy proxy: proxysToKill)
			proxy.stop();
	}

	/**
	 * Gets a map of enter ports outAddresses pairs and starts proxys for all this pairs. 
	 */
	public void installProxys(Map<Integer, InetSocketAddress> addresses) {
		logger.info("Instaling proxys");
		proxys = getProxysSetFromAddressMap(addresses);
		startProxyGroup(proxys);
	}

	/**
	 * Goes over all proxys in the group and starts them.
	 */
	private void startProxyGroup(Set<HexDumpProxy> proxysToStart) {
		for(HexDumpProxy proxy: proxysToStart)
			proxy.start();
	}

	/**
	 * Gets map of port InetSocketAddress pairs and creates proxys set based on this pairs 
	 * @param addresses map of port InetSocketAddress pairs 
	 * @return proxys map
	 */
	public Set<HexDumpProxy> getProxysSetFromAddressMap(
			Map<Integer, InetSocketAddress> addresses) {

		Set<HexDumpProxy> result = new HashSet<HexDumpProxy>();
		for (Integer key : addresses.keySet()) {
			HexDumpProxy proxy = new HexDumpProxy(key, addresses.get(key)
					.getHostName(), addresses.get(key).getPort());
			result.add(proxy);
		}

		return result;
	}
	

	public void installProxy(HexDumpProxy proxy) {
		if(proxys.contains(proxy)){
			logger.info("Installing new proxy");
			proxy.start();
			proxys.add(proxy);
		    changeXml(proxy);
		}
		else 
			logger.info("Proxy alredy exists");
	}


	private void changeXml(HexDumpProxy proxy) {
		logger.info("Writing proxy in to the file");
	}


	public void uninstallProxy(HexDumpProxy proxy) {
		logger.info("Uninstalling proxy");		
		if(proxys.contains(proxy)){
			logger.info("Uninstalling proxy");
			proxy.stop();
			proxys.remove(proxy);
		    changeXml(proxy);
		}
		else 
			logger.info("Proxy alredy exists");
	}
	

	public static void main(String []args){
		Installator instalator = new Installator();
		Map<Integer, InetSocketAddress> addresses = Utils.mapForXML(new File("src/proxies.xml"));
		instalator.installProxys(addresses);
		Map<Integer, InetSocketAddress> newAddresses = Utils.mapForXML(new File("src/proxiesT.xml"));
		instalator.reInstallProxys(newAddresses);

	}

}

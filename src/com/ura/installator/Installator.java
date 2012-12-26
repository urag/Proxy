package com.ura.installator;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ura.proxy.HexDumpProxy;
import com.ura.utils.Pair;
import com.ura.utils.Utils;

public class Installator {

	//Set<HexDumpProxy> proxys = new HashSet<HexDumpProxy>();
	Map <String,HexDumpProxy>proxysMap ;
	Logger logger = Logger.getLogger(Installator.class);
	
	/**
	 * Goes over all running proxys and stops those that are not in the new map 
	 * and starting those in new map but not in all in the end writing to config file new proxys pairs. 
	 */
	public void reInstallProxys(Map<String, Pair<InetSocketAddress, InetSocketAddress>> newAddresses) {
		logger.info("Reinstaling proxys ...");
		Set<HexDumpProxy>proxys = new HashSet<HexDumpProxy>(proxysMap.values());
		Set<HexDumpProxy> proxysToKill = new HashSet<HexDumpProxy>(proxys);
		Set<HexDumpProxy> proxysToStart = new HashSet<HexDumpProxy>(getProxysMapFromAddressMap(newAddresses).values());
		//All proxys that are not present in new proxy group need to be killed
		proxysToKill.removeAll(proxysToStart);
		killProxyGroup(proxysToKill);
		//All proxys that are not present in new proxy group need to be started.
		proxysToStart.removeAll(proxys);
		startProxyGroup(proxysToStart);
	}

	public Installator(File file){
		Map<String,Pair<InetSocketAddress,InetSocketAddress>> addresses = Utils.mapForXML(file);
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
	private void installProxys(Map<String,Pair<InetSocketAddress,InetSocketAddress>> addresses) {
		logger.info("Instaling proxys ...");
		proxysMap = getProxysMapFromAddressMap(addresses);
		startProxyGroup(new HashSet<HexDumpProxy>(proxysMap.values()));
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
	private Map<String, HexDumpProxy> getProxysMapFromAddressMap(
			Map<String, Pair<InetSocketAddress, InetSocketAddress>> addresses) {

		Map<String,HexDumpProxy> resultMap = new HashMap<String,HexDumpProxy>();
		for (String key : addresses.keySet()) {
			HexDumpProxy proxy = new HexDumpProxy(key,addresses.get(key).getFirst().getPort(),addresses.get(key).getSecond().getHostName(),addresses.get(key).getSecond().getPort());
			
			resultMap.put(key, proxy);
		}

		return resultMap;
	}
	

	public void installProxy(HexDumpProxy proxy) {
	Set<HexDumpProxy>proxys = new HashSet<HexDumpProxy>(proxysMap.values());
		if(!proxys.contains(proxy)){
			logger.info("Installing new proxy");
			proxy.start();
			proxys.add(proxy);
		    changeXml(proxy);
		    proxys.add(proxy);
		}
		else 
			logger.info("Proxy alredy exists");
	}


	private void changeXml(HexDumpProxy proxy) {
		logger.info("Writing changes in to the file");
		File destFile = new File("src/proxies.xml");
		Utils.reWriteProxieElementInToXML(proxy.getId(), proxy.getLocalPort(),proxy.getRemoteHost(),proxy.getRemotePort(), destFile,destFile);
	}


	public void uninstallProxy(HexDumpProxy proxy) {
		logger.info("Uninstalling proxy");		
		if(proxysMap.containsKey(proxy.getId())){
			logger.info("Uninstalling proxy");
			proxy.stop();
			proxysMap.remove(proxy.getId());
		    changeXml(proxy);
		}
		else 
			logger.info("Proxy is not found");
	}
	

	public static void main(String []args){
		if(args.length != 1){
			System.out.println("Wrong number of arguments !!!");
		}else{
			Installator installator = new Installator(new File(args[0]));
			Map<String, Pair<InetSocketAddress, InetSocketAddress>> newAddresses = Utils.mapForXML(new File("src/proxiesT.xml"));
		}

	}

}

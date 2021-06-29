package com.mojito.caiss.sever.init;


import com.mojito.caiss.sever.context.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.Query;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;

/**
 * @author huangwei10
 * @create 2021/5/17
 */

@Component
public class InitCaissServer implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger("InitCaissServer");

	@Value("${caiss.bizType}")
	private String bizType;


	@Autowired
	private Environment environment;

	@Override
	public void run(String... strings) throws Exception {
		/*读取配置*/
		/*创建初始化的事件*/
		Integer port = null;
		MBeanServer server;
		if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
			server = MBeanServerFactory.findMBeanServer(null).get(0);
			Set names = server.queryNames(new ObjectName("Catalina:type=Connector,*"),
					Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
			Iterator iterator = names.iterator();
			if (iterator.hasNext()) {
				ObjectName name = (ObjectName) iterator.next();
				port = Integer.parseInt(server.getAttribute(name, "port").toString());
			}
		}
		if(null != port){
			String nodeData = NetHandler.getIp()+":"+port;


		} else {
			((ConfigurableApplicationContext)SpringContext.getApplicationContext()).close();
		}

	}

	private static final class NetHandler{

		public static String getIp() throws UnknownHostException {
			InetAddress localHost = Inet4Address.getLocalHost();
			return  localHost.getHostAddress();
		}

	}
}

package com.mojito.caiss.sever.handler;

import com.mojito.caiss.sever.util.BizUtil;
import com.mojito.server.frame.annotation.EventListen;
import com.mojito.server.frame.annotation.Lock;
import com.mojito.server.frame.event.Event;
import com.mojito.server.frame.event.handler.EventHandler;
import com.mojito.server.frame.event.result.EventHandlerResult;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangwei10
 * @create 2021/5/17
 */
@Component
@EventListen("RegisterNode")
public class RegisterNodeHandler implements EventHandler{

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private Environment environment;

	@Value("${caiss.nodeType}")
	private String nodeType;


	private static final String REGISTER_NODE = "registerNode";



	@Lock(lockName = "RegisterNodeLock",lockKey = "RegisterNodeLock", lockType = "dis_lock")
	@Override
	public EventHandlerResult execute(Event event) {
		EventHandlerResult eventHandlerResult = null;
		try{
			RMap<String,Object> registerNodeMap  = redissonClient.getMap(REGISTER_NODE);
			String nodekey =  createRegisterNodeKey("master");
			NodeData ownerMasterNode = new NodeData(NetHandler.getIp(), environment.getProperty("local.server.port"),
					"master");
			if (registerNodeMap.containsKey(nodekey)){
				/*说明其他加载相同模型的已经启动并申请为主节点*/
				List<NodeData> masterDatas  = ((List<NodeData>)registerNodeMap.get(nodekey)).stream().filter(nodeData
						-> nodeData.equals
						(ownerMasterNode)).collect(Collectors.toList());
				if(BizUtil.checkListNotEmpty(masterDatas)){
					/*如果主节点已经有了*/
				} else {
					/*注册从节点*/
					NodeData ownerSlaveNode = new NodeData(NetHandler.getIp(), environment.getProperty("local.server.port"),
							"slave");
					nodekey = createRegisterNodeKey("slave");
					List<NodeData> nodeDatas = (List<NodeData>) registerNodeMap.get(nodekey);
					if(BizUtil.checkListNotEmpty(nodeDatas)){
						List<NodeData> ownerSlaveNodes = nodeDatas.stream().filter(nodeData -> nodeData.equals
								(ownerSlaveNode)).collect(Collectors
								.toList());
						if(BizUtil.checkListNotEmpty(ownerSlaveNodes)){
						} else {
							nodeDatas.add(ownerSlaveNode);
						}
					} else {
						nodeDatas.add(ownerSlaveNode);
					}
					registerNodeMap.put(nodekey, nodeDatas);
				}

			} else {
				/*当前节点注册为主节点*/
				registerNodeMap.put(nodekey,Collections.singletonList(ownerMasterNode));
			}
			eventHandlerResult =  EventHandlerResult.getSuccessResult();
		}catch (Exception e){
			eventHandlerResult = EventHandlerResult.getExceptionResult(e);
		}
		return eventHandlerResult;
	}


	/**
	 * 创建node_
	 * @return
	 */
	private String createRegisterNodeKey(String node){
		StringBuilder nodeKey = new StringBuilder(nodeType);
		nodeKey.append("_").append(node);
		return nodeKey.toString();
	}


	private static final class NetHandler{

		public static String getIp() throws UnknownHostException {
			InetAddress localHost = Inet4Address.getLocalHost();
			return  localHost.getHostAddress();
		}

	}


	public static class NodeData{
		private String nodeIp;

		private String nodePort;

		private String status;

		public NodeData(String nodeIp, String nodePort ,String status){
			this.nodeIp = nodeIp;
			this.nodePort = nodePort;
			this.status = status;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((nodeIp == null) ? 0 : nodeIp.hashCode());
			result = prime * result
					+ ((nodePort == null) ? 0 : nodePort.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(null == obj){
				return false;
			}
			NodeData dto = (NodeData)obj;
			return this.nodeIp.equals(dto.nodeIp) && this.nodePort.equals(dto.nodePort);
		}
	}


}

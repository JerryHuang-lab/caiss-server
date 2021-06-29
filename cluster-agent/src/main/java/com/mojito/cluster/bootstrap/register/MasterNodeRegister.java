package com.mojito.cluster.bootstrap.register;

import com.mojito.cluster.TransferResult;
import com.mojito.cluster.util.BizUtil;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangwei10
 * @create 2021/6/23
 */

public class MasterNodeRegister extends RegisterHandler {

	private final  Logger logger = LoggerFactory.getLogger("MasterNodeRegister");

	@Override
	public TransferResult doRegisterNode(String bizMode, String nodeData) {
		TransferResult result = null;
		try{
			RMap<String,String> registerMasterNodeMap  = getCache().getMap
					(MASTER_NODE);
			RMap<String,List<String>> registerSlaveNodeMap  = getCache().getMap(SLAVE_NODE);
			String nodeKey =  createRegisterNodeKey(bizMode);
			if (registerMasterNodeMap.containsKey(nodeKey)){
				/*说明其他加载相同模型的已经启动并申请为主节点*/
				String data = registerMasterNodeMap.get(nodeKey);
				if(!data.equals(nodeData)){
					/*非自己的主节点已经有了*/
				} else {
					List<String> nodeDatas = registerSlaveNodeMap.get(nodeKey);
					if(BizUtil.checkListNotEmpty(nodeDatas)){
						String finalNodeData = nodeData;
						List<String> ownerSlaveNode  = nodeDatas.stream().filter(slaveNodeData -> slaveNodeData.equals
								(finalNodeData))
								.collect(Collectors.toList());
						if(BizUtil.checkListNotEmpty(ownerSlaveNode)){
							/*本节点注册为从节点*/
						} else {
							nodeDatas.add(nodeData);
						}
					} else {
						nodeDatas = new ArrayList<>();
						nodeDatas.add(nodeData);
					}
					registerSlaveNodeMap.put(nodeKey, nodeDatas);
				}

			} else {
				/*当前节点注册为主节点*/
				registerMasterNodeMap.put(nodeKey, nodeData);
			}
			result = (TransferResult) TransferResult.getSuccessResult();
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			result = (TransferResult) TransferResult.getExceptionResult(e);
		}
		return result;
	}
}

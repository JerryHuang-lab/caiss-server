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

public class SlaveNodeRegister extends RegisterHandler {

	private final Logger logger = LoggerFactory.getLogger("SlaveNodeRegister");


	@Override
	protected TransferResult doRegisterNode(String bizType, String nodeData) {
		TransferResult result = null;
		try{
			RMap<String,List<String>> registerSlaveNodeMap  = getCache().getMap(SLAVE_NODE);
			String nodeKey =  createRegisterNodeKey(bizType);
			List<String> nodeDatas = registerSlaveNodeMap.get(nodeKey);
			if(BizUtil.checkListNotEmpty(nodeDatas)){
				List<String> ownerSlaveNode  = nodeDatas.stream().filter(slaveNodeData -> slaveNodeData.equals
						(nodeData))
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
			registerSlaveNodeMap.put(nodeKey,nodeDatas);
			result = (TransferResult) TransferResult.getSuccessResult();
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			result = (TransferResult) TransferResult.getExceptionResult(e);
		}
		return result;
	}
}

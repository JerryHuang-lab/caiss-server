package com.mojito.cluster.bootstrap.register;

import com.mojito.cluster.TransferResult;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 强制注册主节点方案
 * @author huangwei10
 * @create 2021/6/29
 */

public class ForceMasterNodeRegister extends RegisterHandler {

	private final Logger logger = LoggerFactory.getLogger("ForceMasterRegister");

	@Override
	protected TransferResult doRegisterNode(String bizMode, String nodeData) {
		TransferResult result = null;
		try {
			RMap<String, String> registerMasterNodeMap = getCache().getMap
					(MASTER_NODE);
			String nodeKey =  createRegisterNodeKey(bizMode);
			if (registerMasterNodeMap.containsKey(nodeKey)){
				throw new Exception("this bizMode master node is existed,nodaData:"+registerMasterNodeMap.get(nodeKey));
			} else {
				registerMasterNodeMap.put(nodeKey,nodeData);
				result = (TransferResult) TransferResult.getSuccessResult();
			}

		}catch (Exception e){
			logger.error(e.getMessage(),e);
			result = (TransferResult) TransferResult.getExceptionResult(e);
		}
		return result;
	}
}

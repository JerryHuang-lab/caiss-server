package com.mojito.cluster.bootstrap.register;

import com.mojito.cluster.TransferResult;
import com.mojito.server.frame.config.ManageBeanRegistryFactory;
import org.redisson.api.RedissonClient;

/**
 * @author huangwei10
 * @create 2021/6/29
 */

public abstract class RegisterHandler {

	protected static final String SLAVE_NODE = "register_slave_node";

	protected static final String MASTER_NODE = "register_master_node";

	protected RedissonClient getCache() throws Exception {
		return ManageBeanRegistryFactory.getInstance().getRedissonClient();
	}

	/**
	 * 创建node_
	 * @return
	 */
	protected String createRegisterNodeKey(String bizMode){
		StringBuilder nodeKey = new StringBuilder(bizMode);
		return nodeKey.toString();
	}

	protected abstract TransferResult doRegisterNode(String bizMode, String nodeData);
}

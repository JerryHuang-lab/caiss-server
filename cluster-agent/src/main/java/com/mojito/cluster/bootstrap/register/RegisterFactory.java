package com.mojito.cluster.bootstrap.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangwei10
 * @create 2021/6/28
 */

public class RegisterFactory {

	private final  Logger logger = LoggerFactory.getLogger("RegisterFactory");

	private static volatile  RegisterFactory registerFactory = null;

	private RegisterFactory(){

	}

	public static RegisterFactory getFactory(){
		if(null ==  registerFactory){
			synchronized (RegisterFactory.class){
				if(null == registerFactory){
					registerFactory = new RegisterFactory();
				}
			}
		}
		return registerFactory;
	}


	/**
	 * @param nodeType 注册节点类型
	 * @param force
	 * @return
	 */
	public RegisterHandler createRegisterInstance(String nodeType, boolean force) throws Exception{
		String handlerClassName =  RegisterEnum.getHandler(nodeType,force);
		assert handlerClassName != null;
		return (RegisterHandler) ClassLoader.getSystemClassLoader().loadClass(handlerClassName).newInstance();
	}

	private enum  RegisterEnum{

		MASTER_REGISTER("master",false,"com.mojito.cluster.bootstrap.register.MasterNodeRegister"),
		FORCE_MASTER_REGISTER("master", true,"com.mojito.cluster.bootstrap.register.ForceMasterNodeRegister"),
		SLAVE_REGISTER("slave",false, "com.mojito.cluster.bootstrap.register.SlaveNodeRegister");

		private String  nodeType;

		private boolean force;

		private String handler;

		RegisterEnum(String nodeType, boolean force, String handler){
			this.nodeType = nodeType;
			this.force = force;
			this.handler = handler;
		}

		public static String getHandler(String nodeType, boolean force){
			if(FORCE_MASTER_REGISTER.getNodeType().equals(nodeType) && force){
				return FORCE_MASTER_REGISTER.handler;
			} else if(MASTER_REGISTER.getNodeType().equals(nodeType) && !force){
				return MASTER_REGISTER.handler;
			} else if(SLAVE_REGISTER.getNodeType().equals(nodeType) && !force){
				return FORCE_MASTER_REGISTER.handler;
			}
			return null;
		}


		public String getNodeType() {
			return nodeType;
		}

		public boolean isForce() {
			return force;
		}
	}


}

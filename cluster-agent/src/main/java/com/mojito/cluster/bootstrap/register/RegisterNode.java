package com.mojito.caiss.sever.handler;

import lombok.Data;

/**
 * @author huangwei10
 * @create 2021/6/23
 */
@Data
public class RegisterNode {

	private  String bizType;

	private  String nodeData;

	private  String nodeType;

	private RegisterNode(String bizType, String nodeData, String nodeType){
		this.bizType = bizType;
		this.nodeData = nodeData;
		this.nodeType = nodeType;
	}

	public static RegisterNode build(String bizType, String nodeData, String nodeType){
		return new RegisterNode(bizType,nodeData,nodeType);
	}
}

package com.customer.helper;

public class ShowMessage {

	private String msgContent;
	private String msgType;
	
	public ShowMessage(String msgContent, String msgType) {
		super();
		this.msgContent = msgContent;
		this.msgType = msgType;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContentg) {
		this.msgContent = msgContentg;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	
}

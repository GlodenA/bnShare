package com.ipu.server.core.handle;

import com.ailk.mobile.frame.handle.impl.DefaultSecurityHandler;

public class IpuSecurityHandler extends DefaultSecurityHandler{
	
	/**
	 * 重写此方法设置文件加密的密钥
	 */
	@Override
	public String getResKey() throws Exception {
		// TODO Auto-generated method stub
		return "abcdefgh";
	}
}

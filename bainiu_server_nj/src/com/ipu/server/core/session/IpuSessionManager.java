package com.ipu.server.core.session;

import com.ailk.common.data.IData;
import com.ailk.mobile.frame.context.IContextData;
import com.ailk.mobile.frame.session.impl.AbstractSessionManager;
import com.ailk.mobile.util.MobileUtility;
import com.ipu.server.core.context.IpuContextData;

public class IpuSessionManager extends AbstractSessionManager {

	/**
	 * 自定义校验逻辑:校验客户端的userName和Session中的是否一致
	 */
	
	public void customVerify(String paramString, IData paramIData, IContextData paramIContextData) throws Exception {
		String session = paramIData.getString("SESSION_ID");
		String contextUserName = ((IpuContextData)paramIContextData).getName();
		if(session == null||contextUserName==null){
			MobileUtility.error("非法操作，请重新登陆!", SESSION_ERROR_CODE);
		}
	}
}

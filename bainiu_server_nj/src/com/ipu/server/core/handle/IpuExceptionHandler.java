package com.ipu.server.core.handle;

import com.ailk.mobile.frame.handle.impl.DefaultExceptionHandler;
import com.ailk.mobile.servlet.ServletManager;
import com.ailk.mobile.util.MobileConstant;
import com.ailk.mobile.util.MobileServerException;

public class IpuExceptionHandler extends DefaultExceptionHandler {
	/**
	 * 页面错误时候重定向操作
	 */
	@Override
	public void pageError(Exception e, String pageAction, String data)
			throws Exception {
		if(MobileServerException.class.isInstance(e)){
			if(MobileConstant.Result.SESSION_ERROR_CODE.equals(((MobileServerException)e).getCode())){
				ServletManager.openPage("SessionErr");
				return;
			}
		}
		super.pageError(e, pageAction, data);	//执行父类的逻辑
	}
}

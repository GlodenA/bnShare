package com.ipu.server.core.bean;

import com.ailk.mobile.frame.bean.AbstractBean;
import com.ipu.server.core.context.IpuContextData;

public class IpuAppBean extends AbstractBean {
	@Override
	protected IpuContextData getContextData() throws Exception {
		return (IpuContextData)(getContext().getContextData());
	}
}

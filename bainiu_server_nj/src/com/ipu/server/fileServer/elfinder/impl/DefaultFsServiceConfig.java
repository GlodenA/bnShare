package com.ipu.server.fileServer.elfinder.impl;

import com.ipu.server.fileServer.elfinder.service.FsServiceConfig;

public class DefaultFsServiceConfig implements FsServiceConfig
{
	private int _tmbWidth;

	public void setTmbWidth(int tmbWidth)
	{
		_tmbWidth = tmbWidth;
	}

	
	public int getTmbWidth()
	{
		return _tmbWidth;
	}
}

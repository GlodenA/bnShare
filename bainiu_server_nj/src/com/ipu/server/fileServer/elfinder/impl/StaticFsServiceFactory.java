package com.ipu.server.fileServer.elfinder.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.ipu.server.fileServer.elfinder.service.FsService;
import com.ipu.server.fileServer.elfinder.service.FsServiceFactory;

/**
 * A StaticFsServiceFactory always returns one FsService, despite of whatever it is requested 
 * 
 * @author bluejoe
 *
 */
public class StaticFsServiceFactory implements FsServiceFactory
{
	FsService _fsService;

	
	public FsService getFileService(HttpServletRequest request, ServletContext servletContext)
	{
		return _fsService;
	}

	public FsService getFsService()
	{
		return _fsService;
	}

	public void setFsService(FsService fsService)
	{
		_fsService = fsService;
	}
}

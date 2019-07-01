package com.ipu.server.fileServer.elfinder.impl;

import java.io.IOException;
import java.util.List;

import com.ipu.server.fileServer.elfinder.service.FsItem;
import com.ipu.server.fileServer.elfinder.service.FsSecurityChecker;
import com.ipu.server.fileServer.elfinder.service.FsService;

public class FsSecurityCheckerChain implements FsSecurityChecker
{
	private static final FsSecurityChecker DEFAULT_SECURITY_CHECKER = new FsSecurityCheckForAll();

	List<FsSecurityCheckFilterMapping> _filterMappings;

	private FsSecurityChecker getChecker(FsService fsService, FsItem fsi) throws IOException
	{
		String hash = fsService.getHash(fsi);
		for (FsSecurityCheckFilterMapping mapping : _filterMappings)
		{
			if (mapping.matches(hash))
			{
				return mapping.getChecker();
			}
		}

		return DEFAULT_SECURITY_CHECKER;
	}

	public List<FsSecurityCheckFilterMapping> getFilterMappings()
	{
		return _filterMappings;
	}

	
	public boolean isLocked(FsService fsService, FsItem fsi) throws IOException
	{
		return getChecker(fsService, fsi).isLocked(fsService, fsi);
	}

	
	public boolean isReadable(FsService fsService, FsItem fsi) throws IOException
	{
		return getChecker(fsService, fsi).isReadable(fsService, fsi);
	}

	
	public boolean isWritable(FsService fsService, FsItem fsi) throws IOException
	{
		return getChecker(fsService, fsi).isWritable(fsService, fsi);
	}

	public void setFilterMappings(List<FsSecurityCheckFilterMapping> filterMappings)
	{
		_filterMappings = filterMappings;
	}
}

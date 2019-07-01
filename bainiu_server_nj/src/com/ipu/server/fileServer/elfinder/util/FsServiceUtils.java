package com.ipu.server.fileServer.elfinder.util;

import java.io.IOException;

import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsItem;
import com.ipu.server.fileServer.elfinder.service.FsService;



public abstract class FsServiceUtils
{
	public static FsItemEx findItem(FsService fsService, String hash) throws IOException
	{
		FsItem fsi = fsService.fromHash(hash);
		if (fsi == null)
		{
			return null;
		}

		return new FsItemEx(fsi, fsService);
	}
}

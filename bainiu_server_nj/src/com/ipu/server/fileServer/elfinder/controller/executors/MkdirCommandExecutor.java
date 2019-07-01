package com.ipu.server.fileServer.elfinder.controller.executors;



import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsService;

public class MkdirCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String target = request.getParameter("target");
		String name = request.getParameter("name");

		FsItemEx fsi = super.findItem(fsService, target);
		FsItemEx dir = new FsItemEx(fsi, name);
		dir.createFolder();

		json.put("added", new Object[] { getFsItemInfo(request, dir) });
	}
}

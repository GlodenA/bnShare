package com.ipu.server.fileServer.elfinder.controller.executors;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsService;

public class TreeCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String target = request.getParameter("target");

		Map<String, FsItemEx> files = new HashMap<String, FsItemEx>();
		FsItemEx fsi = super.findItem(fsService, target);
		super.addSubfolders(files, fsi);

		json.put("tree", files2JsonArray(request, files.values()));
	}
}

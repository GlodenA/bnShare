package com.ipu.server.fileServer.elfinder.controller.executors;



import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsService;
import com.ipu.server.fileServer.elfinder.service.FsVolume;

public class OpenCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		boolean init = request.getParameter("init") != null;
		boolean tree = request.getParameter("tree") != null;
		String target = request.getParameter("target");

		Map<String, FsItemEx> files = new LinkedHashMap<String, FsItemEx>();
		if (init)
		{
			json.put("api", 2.1);
			json.put("netDrivers", new Object[0]);
		}

		if (tree)
		{
			for (FsVolume v : fsService.getVolumes())
			{
				FsItemEx root = new FsItemEx(v.getRoot(), fsService);
				files.put(root.getHash(), root);
				addSubfolders(files, root);
			}
		}

		FsItemEx cwd = findCwd(fsService, target);
		files.put(cwd.getHash(), cwd);
		String[] onlyMimes = request.getParameterValues("mimes[]");
		addChildren(files, cwd, onlyMimes);

		json.put("files", files2JsonArray(request, files.values()));
		json.put("cwd", getFsItemInfo(request, cwd));
		json.put("options", getOptions(request, cwd));
	}
}

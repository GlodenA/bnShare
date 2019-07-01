package com.ipu.server.fileServer.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.service.FsService;
import com.ipu.server.fileServer.elfinder.util.FsItemFilterUtils;

public class SearchCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		json.put(
				"files",
				files2JsonArray(request, FsItemFilterUtils.filterFiles(
						fsService.find(FsItemFilterUtils
								.createFileNameKeywordFilter(request
										.getParameter("q"))), super
								.getRequestedFilter(request))));
	}
}

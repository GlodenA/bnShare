package com.ipu.server.fileServer.elfinder.controller.executors;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsService;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;

public class PutCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String target = request.getParameter("target");

		FsItemEx fsi = super.findItem(fsService, target);
		fsi.writeStream(new ByteArrayInputStream(request.getParameter("content").getBytes("utf-8")));
		json.put("changed", new Object[] { super.getFsItemInfo(request, fsi) });
	}
}

package com.ipu.server.fileServer.elfinder.controller.executors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsService;

public class PasteCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String[] targets = request.getParameterValues("targets[]");
		String src = request.getParameter("src");
		String dst = request.getParameter("dst");
		boolean cut = "1".equals(request.getParameter("cut"));

		List<FsItemEx> added = new ArrayList<FsItemEx>();
		List<String> removed = new ArrayList<String>();

		FsItemEx fsrc = super.findItem(fsService, src);
		FsItemEx fdst = super.findItem(fsService, dst);

		for (String target : targets)
		{
			FsItemEx ftgt = super.findItem(fsService, target);
			String name = ftgt.getName();
			FsItemEx newFile = new FsItemEx(fdst, name);
			super.createAndCopy(ftgt, newFile);
			added.add(newFile);

			if (cut)
			{
				ftgt.delete();
				removed.add(target);
			}
		}

		json.put("added", files2JsonArray(request, added));
		json.put("removed", removed);
	}
}

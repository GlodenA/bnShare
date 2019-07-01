package com.ipu.server.fileServer.elfinder.controller.executors;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutor;
import com.ipu.server.fileServer.elfinder.controller.executor.FsItemEx;
import com.ipu.server.fileServer.elfinder.service.FsItemFilter;
import com.ipu.server.fileServer.elfinder.service.FsService;

public class UploadCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		List<FileItemStream> listFiles = (List<FileItemStream>) request
				.getAttribute(FileItemStream.class.getName());
		List<FsItemEx> added = new ArrayList<FsItemEx>();

		String target = request.getParameter("target");
		FsItemEx dir = super.findItem(fsService, target);

		FsItemFilter filter = getRequestedFilter(request);
		for (FileItemStream fis : listFiles)
		{
			String fileName = fis.getName();
			FsItemEx newFile = new FsItemEx(dir, fileName);
			newFile.createFile();
			InputStream is = fis.openStream();
			newFile.writeStream(is);
			if (filter.accepts(newFile))
				added.add(newFile);
		}

		json.put("added", files2JsonArray(request, added));
	}
}

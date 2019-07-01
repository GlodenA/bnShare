package com.ipu.server.fileServer.elfinder.controller.executor;



import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipu.server.fileServer.elfinder.controller.ErrorException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ipu.server.fileServer.elfinder.service.FsService;

public abstract class AbstractJsonCommandExecutor extends AbstractCommandExecutor
{
	@Override
	final public void execute(FsService fsService, HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws Exception
	{
		JSONObject json = new JSONObject();
		try
		{
			execute(fsService, request, servletContext, json);
		}
		catch (ErrorException e)
		{
			if (e.getArgs() == null || e.getArgs().length == 0)
			{
				json.put("error", e.getError());
			}
			else
			{
				JSONArray errors = new JSONArray();
				errors.put(e.getError());
				for (String arg: e.getArgs())
				{
					errors.put(arg);
				}
				json.put("error", errors);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			json.put("error", e.getMessage());
		}
		finally
		{
			//response.setContentType("application/json; charset=UTF-8");
			response.setContentType("text/html; charset=UTF-8");

			PrintWriter writer = response.getWriter();
			json.write(writer);
			writer.flush();
			writer.close();
		}
	}

	protected abstract void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext,
			JSONObject json) throws Exception;

}
package com.ipu.server.fileServer.elfinder.servlet;



import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipu.server.fileServer.elfinder.controller.ConnectorController;
import com.ipu.server.fileServer.elfinder.controller.executor.CommandExecutorFactory;
import com.ipu.server.fileServer.elfinder.controller.executor.DefaultCommandExecutorFactory;
import com.ipu.server.fileServer.elfinder.controller.executors.MissingCommandExecutor;
import com.ipu.server.fileServer.elfinder.impl.DefaultFsService;
import com.ipu.server.fileServer.elfinder.impl.DefaultFsServiceConfig;
import com.ipu.server.fileServer.elfinder.impl.FsSecurityCheckForAll;
import com.ipu.server.fileServer.elfinder.impl.StaticFsServiceFactory;
import com.ipu.server.fileServer.elfinder.localfs.LocalFsVolume;

public class ConnectorServlet extends HttpServlet
{
	//core member of this Servlet
	ConnectorController _connectorController;

	private LocalFsVolume ceateLocalFsVolume(String name, File rootDir)
	{
		LocalFsVolume localFsVolume = new LocalFsVolume();
		localFsVolume.setName(name);
		localFsVolume.setRootDir(rootDir);
		return localFsVolume;
	}

	/**
	 * create a command executor factory
	 * 
	 * @param config
	 * @return
	 */
	protected CommandExecutorFactory createCommandExecutorFactory(
			ServletConfig config)
	{
		DefaultCommandExecutorFactory defaultCommandExecutorFactory = new DefaultCommandExecutorFactory();
		defaultCommandExecutorFactory
				.setClassNamePattern("com.ipu.server.fileServer.elfinder.controller.executors.%sCommandExecutor");
		defaultCommandExecutorFactory
				.setFallbackCommand(new MissingCommandExecutor());
		return defaultCommandExecutorFactory;
	}

	/**
	 * create a connector controller
	 * 
	 * @param config
	 * @return
	 */
	protected ConnectorController createConnectorController(ServletConfig config)
	{
		ConnectorController connectorController = new ConnectorController();

		connectorController.setCommandExecutorFactory(createCommandExecutorFactory(config));
		connectorController.setFsServiceFactory(createServiceFactory(config));

		return connectorController;
	}

	protected DefaultFsService createFsService()
	{
		DefaultFsService fsService = new DefaultFsService();
		fsService.setSecurityChecker(new FsSecurityCheckForAll());

		DefaultFsServiceConfig serviceConfig = new DefaultFsServiceConfig();
		serviceConfig.setTmbWidth(80);

		fsService.setServiceConfig(serviceConfig);

		fsService.addVolume("A",
				ceateLocalFsVolume("我的文件", new File("/tmp/a")));
		fsService.addVolume("B",
				ceateLocalFsVolume("项目文件", new File("/tmp/b")));

		return fsService;
	}

	/**
	 * create a service factory
	 * 
	 * @param config
	 * @return
	 */
	protected StaticFsServiceFactory createServiceFactory(ServletConfig config)
	{
		StaticFsServiceFactory staticFsServiceFactory = new StaticFsServiceFactory();
		DefaultFsService fsService = createFsService();

		staticFsServiceFactory.setFsService(fsService);
		return staticFsServiceFactory;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		_connectorController.connector(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		_connectorController.connector(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		_connectorController.connector(req, resp);
	}
 
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		_connectorController = createConnectorController(config);
	}
}

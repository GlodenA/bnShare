package com.ipu.server.fileServer.elfinder.controller.executor;



public interface CommandExecutorFactory
{
	CommandExecutor get(String commandName);
}
package com.ipu.server.fileServer.elfinder.controller.executor;



public interface CommandExecutor
{
	void execute(CommandExecutionContext commandExecutionContext) throws Exception;
}

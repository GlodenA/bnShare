package com.ipu.server.util;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFactory {

    protected LogFactory()
    {
    }

    protected static synchronized LogFactory getInstance()
    {
        if(instance == null)
            instance = new LogFactory();
        return instance;
    }

    public static synchronized Logger getLog(Class category)
    {
        return getLog(category.getName());
    }

    public static synchronized Logger getLog(String category)
    {
        Logger log = (Logger)loggers.get(category);
        if(log == null)
        {
            log = LoggerFactory.getLogger(category);
            loggers.put(category, log);
        }
        return log;
    }

    public static Map loggers = new HashMap();
    public static LogFactory instance;

}

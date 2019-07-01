package com.ipu.server.fileServer.elfinder.controller;


import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.FileManager;
import com.baidu.ueditor.hunter.ImageHunter;
import com.baidu.ueditor.upload.Uploader;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.ueditor.ConfigManager;

// Referenced classes of package com.baidu.ueditor:
//            ConfigManager

public class ActionEnter
{

    public ActionEnter(HttpServletRequest request, String rootPath)
    {
        this.request = null;
        this.rootPath = null;
        contextPath = null;
        actionType = null;
        configManager = null;
        this.request = request;
        this.rootPath = rootPath;
        actionType = request.getParameter("action");
        contextPath = request.getContextPath();
        configManager = UeditorConfigManager.getInstance(this.rootPath, contextPath, request.getRequestURI());
    }

    public String exec()
    {
        String callbackName = request.getParameter("callback");
        if(callbackName != null)
        {
            if(!validCallbackName(callbackName))
                return (new BaseState(false, 401)).toJSONString();
            else
                return (new StringBuilder(String.valueOf(callbackName))).append("(").append(invoke()).append(");").toString();
        } else
        {
            return invoke();
        }
    }

    public String invoke()
    {
        if(actionType == null || !ActionMap.mapping.containsKey(actionType))
            return (new BaseState(false, 101)).toJSONString();
        if(configManager == null || !configManager.valid())
            return (new BaseState(false, 102)).toJSONString();
        State state = null;
        int actionCode = ActionMap.getType(actionType);
        Map conf = null;
        switch(actionCode)
        {
        case 0: // '\0'
            return configManager.getAllConfig().toString();

        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
            try {
					conf = configManager.getConfig(actionCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            state = (new Uploader(request, conf)).doExec();
            break;

        case 5: // '\005'
            try {
					conf = configManager.getConfig(actionCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            String list[] = request.getParameterValues((String)conf.get("fieldName"));
            state = (new ImageHunter(conf)).capture(list);
            break;

        case 6: // '\006'
        case 7: // '\007'
            try {
					conf = configManager.getConfig(actionCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            int start = getStartIndex();
            state = (new FileManager(conf)).listFile(start);
            break;
        }
        return state.toJSONString();
    }

    public int getStartIndex()
    {
        String start = request.getParameter("start");
        try
        {
            return Integer.parseInt(start);
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    public boolean validCallbackName(String name)
    {
        return name.matches("^[a-zA-Z_]+[\\w0-9_]*$");
    }

    private HttpServletRequest request;
    private String rootPath;
    private String contextPath;
    private String actionType;
    private UeditorConfigManager configManager;
}
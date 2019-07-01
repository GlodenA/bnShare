package com.ipu.server.fileServer.elfinder.controller;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.ueditor.ConfigManager;
public class UeditorConfigManager {

    private UeditorConfigManager(String rootPath, String contextPath, String uri)
        throws FileNotFoundException, IOException
    {
        parentPath = null;
        jsonConfig = null;
        rootPath = rootPath.replace("\\", "/");
        this.rootPath = rootPath;
        this.contextPath = contextPath;
        if(contextPath.length() > 0)
            originalPath = (new StringBuilder(String.valueOf(this.rootPath))).append(uri.substring(contextPath.length())).toString();
        else
            originalPath = (new StringBuilder(String.valueOf(this.rootPath))).append("WEB_INF").toString();
        initEnv();
    }

    public static UeditorConfigManager getInstance(String rootPath, String contextPath, String uri)
    {
        try
        {
            return new UeditorConfigManager(rootPath, contextPath, uri);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public boolean valid()
    {
        return jsonConfig != null;
    }

    public JSONObject getAllConfig()
    {
        return jsonConfig;
    }

    public Map getConfig(int type) throws JSONException
    {
        Map conf = new HashMap();
        String savePath = null;
        switch(type)
        {
        case 4: // '\004'
            conf.put("isBase64", "false");
            conf.put("maxSize", Long.valueOf(jsonConfig.getLong("fileMaxSize")));
            conf.put("allowFiles", getArray("fileAllowFiles"));
            conf.put("fieldName", jsonConfig.getString("fileFieldName"));
            savePath = jsonConfig.getString("filePathFormat");
            break;

        case 1: // '\001'
            conf.put("isBase64", "false");
            conf.put("maxSize", Long.valueOf(jsonConfig.getLong("imageMaxSize")));
            conf.put("allowFiles", getArray("imageAllowFiles"));
            conf.put("fieldName", jsonConfig.getString("imageFieldName"));
            savePath = jsonConfig.getString("imagePathFormat");
            break;

        case 3: // '\003'
            conf.put("maxSize", Long.valueOf(jsonConfig.getLong("videoMaxSize")));
            conf.put("allowFiles", getArray("videoAllowFiles"));
            conf.put("fieldName", jsonConfig.getString("videoFieldName"));
            savePath = jsonConfig.getString("videoPathFormat");
            break;

        case 2: // '\002'
            conf.put("filename", "scrawl");
            conf.put("maxSize", Long.valueOf(jsonConfig.getLong("scrawlMaxSize")));
            conf.put("fieldName", jsonConfig.getString("scrawlFieldName"));
            conf.put("isBase64", "true");
            savePath = jsonConfig.getString("scrawlPathFormat");
            break;

        case 5: // '\005'
            conf.put("filename", "remote");
            conf.put("filter", getArray("catcherLocalDomain"));
            conf.put("maxSize", Long.valueOf(jsonConfig.getLong("catcherMaxSize")));
            conf.put("allowFiles", getArray("catcherAllowFiles"));
            conf.put("fieldName", (new StringBuilder(String.valueOf(jsonConfig.getString("catcherFieldName")))).append("[]").toString());
            savePath = jsonConfig.getString("catcherPathFormat");
            break;

        case 7: // '\007'
            conf.put("allowFiles", getArray("imageManagerAllowFiles"));
            conf.put("dir", jsonConfig.getString("imageManagerListPath"));
            conf.put("count", Integer.valueOf(jsonConfig.getInt("imageManagerListSize")));
            break;

        case 6: // '\006'
            conf.put("allowFiles", getArray("fileManagerAllowFiles"));
            conf.put("dir", jsonConfig.getString("fileManagerListPath"));
            conf.put("count", Integer.valueOf(jsonConfig.getInt("fileManagerListSize")));
            break;
        }
        conf.put("savePath", savePath);
        conf.put("rootPath", rootPath);
        return conf;
    }

    private void initEnv()
        throws FileNotFoundException, IOException
    {
        File file = new File(originalPath);
        if(!file.isAbsolute())
            file = new File(file.getAbsolutePath());
        parentPath = file.getParent();
        String configContent = readFile(getConfigPath());
        try
        {
            JSONObject jsonConfig = new JSONObject(configContent);
            this.jsonConfig = jsonConfig;
        }
        catch(Exception e)
        {
            this.jsonConfig = null;
        }
    }

    private String getConfigPath()
    {
        return (new StringBuilder(String.valueOf(parentPath))).append(File.separator).append("config.json").toString();
    }

    private String[] getArray(String key) throws JSONException
    {
        JSONArray jsonArray = jsonConfig.getJSONArray(key);
        String result[] = new String[jsonArray.length()];
        int i = 0;
        for(int len = jsonArray.length(); i < len; i++)
            result[i] = jsonArray.getString(i);

        return result;
    }

    private String readFile(String path)
        throws IOException
    {
        StringBuilder builder = new StringBuilder();
        try
        {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
            BufferedReader bfReader = new BufferedReader(reader);
            for(String tmpContent = null; (tmpContent = bfReader.readLine()) != null;)
                builder.append(tmpContent);

            bfReader.close();
        }
        catch(UnsupportedEncodingException unsupportedencodingexception) { }
        return filter(builder.toString());
    }

    private String filter(String input)
    {
        return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");
    }

    private final String rootPath;
    private final String originalPath;
    private final String contextPath;
    private static final String configFileName = "config.json";
    private String parentPath;
    private JSONObject jsonConfig;
    private static final String SCRAWL_FILE_NAME = "scrawl";
    private static final String REMOTE_FILE_NAME = "remote";
}

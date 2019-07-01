package com.ipu.server.util;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.bean.DocsCentreBean;
import com.ipu.server.dao.DocsCentreDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class FileDownloadUtil extends HttpServlet {
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //得到要下载的文件名
        String fileID = request.getParameter("DOWNLOAD_DOC_ID");
        DocsCentreBean docsCentreBean = new DocsCentreBean();
        IData param = new DataMap();
        IData resultData = new DataMap();
        param.put("DOC_ID", fileID);
        try {
            resultData = docsCentreBean.queryDocsByID(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IDataset tempDataS = (IDataset) resultData.get("DOCLIST");
        IData tempData = (IData) tempDataS.get(0);
        String fileName = tempData.getString("DOC_NAME");
        //String fileName = resultData.getString ("DOCLIST")//"93309亲情付业务支撑补充需求设计.docx";
        String fileFullName = tempData.getString("DOC_PATH");
        File file = new File(fileFullName);

        //如果文件不存在
        if (!file.exists()) {
            PrintWriter out = response.getWriter();
            out.println("您要下载的资源已被删除");
            return;
        }

            //组织入参，插日志
            param.put("DOC_NAME", fileName);
            param.put("DOC_AUTHOR_NAME", tempData.getString("DOC_AUTHOR_NAME"));
            param.put("DOC_AUTHOR_ACCT", tempData.getString("DOC_AUTHOR_ACCT"));
            param.put("DOC_TYPE", tempData.getString("DOC_TYPE"));
            param.put("DOC_UPLOADER_NAME", tempData.getString("DOC_UPLOADER_NAME"));
        try {
            docsCentreBean.insertDocDownloadLog(param);
            docsCentreBean.updateDocDownloadcnt(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //处理文件名
           // String realname = fileName.substring(fileName.indexOf("_") + 1);
            //设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            //读取要下载的文件，保存到文件输入流
            FileInputStream in = new FileInputStream(fileFullName);
            //创建输出流
            OutputStream out = response.getOutputStream();
            //创建缓冲区
            byte buffer[] = new byte[1024];
            int len = 0;
            //循环将输入流中的内容读取到缓冲区当中
            while ((len = in.read(buffer)) > 0) {
                //输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }
            //关闭文件输入流
            in.close();
            //关闭输出流
            out.close();


    }
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
        System.out.println("doPost");
    }

}

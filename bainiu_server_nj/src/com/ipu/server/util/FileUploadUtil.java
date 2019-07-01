package com.ipu.server.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.bean.DocsCentreBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class FileUploadUtil extends HttpServlet {
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private static final long serialVersionUID = 1L;



    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        FileUpload fileUpload = FileUpload.getInstance();
        String result = fileUpload.upload(request);
        PrintWriter out = response.getWriter();
        out.println(result);

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
        System.out.println("doPost");
    }

}

package com.ipu.server.util;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.bean.GroupBean;
import com.ipu.server.bean.IshareBean;
import com.ipu.server.bean.VacationBean;

public class HttpRequest extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");
		//暂时还没想好怎么实现 20160919 
		if(request.getParameter("ACTION").equals("userAskleave")){
			IData data = new DataMap();
			data.put("REQ_TYPE", request.getParameter("REQ_TYPE"));
			data.put("NT_NAME", request.getParameter("NT_NAME"));
			data.put("REQ_STATE", request.getParameter("REQ_STATE"));
			data.put("USERIDS", request.getParameter("USERIDS"));
			VacationBean bean = new  VacationBean();
			try {
				bean.exportAskleave(data,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(request.getParameter("ACTION").equals("GroupUserList")){
			IData data = new DataMap();
			data.put("GROUP1_ID", request.getParameter("GROUP1_ID"));
			data.put("GROUP2_ID", request.getParameter("GROUP2_ID"));
			data.put("GROUP3_ID", request.getParameter("GROUP3_ID"));
			data.put("GROUP4_ID", request.getParameter("GROUP4_ID"));
			data.put("QUERY_KEY", request.getParameter("QUERY_KEY"));
			data.put("QUERY_STATE", request.getParameter("QUERY_STATE"));
			GroupBean bean = new  GroupBean();
			try {
				bean.exportGroupList(data,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(request.getParameter("ACTION").equals("userIshareList")){
			IData data = new DataMap();
			data.put("USER_ID", request.getParameter("USER_ID"));
			data.put("IS_NAME", request.getParameter("IS_NAME"));
			data.put("IS_STATE", request.getParameter("IS_STATE"));
			IshareBean bean = new  IshareBean();
			try {
				bean.exportIshareList(data,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

}

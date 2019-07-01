<%@ page language="java" contentType="text/html; charset=UTF-8"  
    import="com.baidu.ueditor.ActionEnter"  
    import="java.net.URL"  
    import="java.io.File" 
    pageEncoding="UTF-8"%>  
<%
    request.setCharacterEncoding( "utf-8" );
	response.setHeader("Content-Type" , "text/html");
	
	//String rootPath = request.getSession().getServletContext().getRealPath("/");
	URL url = request.getSession().getServletContext().getResource("/");
		String rootPath = new File(url.toURI()).getAbsolutePath();
      	  if (!rootPath.endsWith("\\") && !rootPath.endsWith("/")) {
			rootPath += File.separator;
		}  
	System.out.println("action="+request.getParameter("action"));
	System.out.println("rootPath="+rootPath);
	System.out.println("contextPath="+request.getContextPath());
	System.out.println("URL="+ request.getRequestURI());
	String s = new ActionEnter( request, rootPath ).exec();
	System.out.println(s);
	out.write(s);
%>
package com.ipu.server.fileServer.elfinder.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ipu.server.dao.GroupDao;
import com.ipu.server.fileServer.elfinder.controller.ActionEnter;

public class UeditorServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static transient Logger log = Logger.getLogger(UeditorServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");

		// String rootPath =
		// request.getSession().getServletContext().getRealPath("/");
		URL url = request.getSession().getServletContext().getResource("/");
		String rootPath = "";
		try {
			rootPath = new File(url.toURI()).getAbsolutePath();
			if (!rootPath.endsWith("\\") && !rootPath.endsWith("/")) {
				rootPath += File.separator;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug("action=" + request.getParameter("action"));
		log.debug("rootPath=" + rootPath);
		log.debug("contextPath=" + request.getContextPath());
		log.debug("URL=" + request.getRequestURI());
		String s = new ActionEnter(request, rootPath).exec();
		log.debug(s);
		response.getWriter().write(s);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");

		// String rootPath =
		// request.getSession().getServletContext().getRealPath("/");
		URL url = request.getSession().getServletContext().getResource("/");
		String rootPath = "";
		try {
			rootPath = new File(url.toURI()).getAbsolutePath();
			if (!rootPath.endsWith("\\") && !rootPath.endsWith("/")) {
				rootPath += File.separator;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug("action=" + request.getParameter("action"));
		log.debug("rootPath=" + rootPath);
		log.debug("contextPath=" + request.getContextPath());
		log.debug("URL=" + request.getRequestURI());
		String s = new ActionEnter(request, rootPath).exec();
		log.debug(s);
		response.getWriter().write(s);
	}

}

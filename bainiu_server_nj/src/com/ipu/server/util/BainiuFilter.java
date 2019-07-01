package com.ipu.server.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;




public class BainiuFilter  implements Filter {
	private String ips="10.20.16.75,bainiu6,218.94.58.199,10.1.241.22,123.59.26.109";
	
	public BainiuFilter(){
		
	}
	
	public void destroy() {
		
		
	}

	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String httphost=httpRequest.getHeader("host");
		String[] hostIps = ips.split(",");
		int rs=0;
		for (String hostIp:hostIps
			 ) {
			if(httphost.toLowerCase().indexOf("bainiu6")>=0){
				rs++;
			}
		}
		if(rs>0){
			filterChain.doFilter(request, response);
		}else{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
//		if(httphost.toLowerCase().indexOf("bainiu6")>=0||httphost.toLowerCase().indexOf("10.20.16.75")>=0||httphost.toLowerCase().indexOf("218.94.58.199")>=0){
//
//			filterChain.doFilter(request, response);
//		}else{
//			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
//			return;
//		}


		
	}

	
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
		 this.ips = config.getInitParameter("HOST");

	}

}

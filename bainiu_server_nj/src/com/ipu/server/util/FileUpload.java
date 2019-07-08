package com.ipu.server.util;
 
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ipu.server.bean.DocsCentreBean;
import com.ipu.server.dao.BookManDao;
import com.ipu.server.dao.DocsCentreDao;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author Administrator
 * 文件上传
 * 具体步骤：
 * 1）获得磁盘文件条目工厂 DiskFileItemFactory 要导包
 * 2） 利用 request 获取 真实路径 ，供临时文件存储，和 最终文件存储 ，这两个存储位置可不同，也可相同
 * 3）对 DiskFileItemFactory 对象设置一些 属性
 * 4）高水平的API文件上传处理  ServletFileUpload upload = new ServletFileUpload(factory);
 * 目的是调用 parseRequest（request）方法  获得 FileItem 集合list ，
 *     
 * 5）在 FileItem 对象中 获取信息，   遍历， 判断 表单提交过来的信息 是否是 普通文本信息  另做处理
 * 6）
 *    第一种. 用第三方 提供的  item.write( new File(path,filename) );  直接写到磁盘上
 *    第二种. 手动处理  
 *
 */
public class FileUpload {

	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

	private static FileUpload instance;


	public static  FileUpload getInstance()
	{
		if(instance==null)
		{
			instance=new FileUpload();
		}
		return instance;
	}


	@SuppressWarnings("unchecked")
	public String upload(HttpServletRequest request)
			throws ServletException, IOException {

		//获得磁盘文件条目工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//获取路径
		//本地
		//String path = request.getContextPath()+File.separator+"bnUpload";
		//String temp = request.getContextPath()+File.separator+"bnUploadTemp";
		//测试
		String path = "/webapp"+File.separator+"bnUpload";
		String temp = "/webapp"+File.separator+"bnUploadTemp";

		//生产
		//String path = "/data/webapp"+File.separator+"bnUpload";
		//String temp = "/data/webapp"+File.separator+"bnUploadTemp";

		System.out.println(temp);
		File file = new File(temp);
		if(!file.exists()&&!file.isDirectory()){
			file.mkdir();
		}
		//如果没以下两行设置的话，上传大的 文件 会占用 很多内存，
		//设置暂时存放的 存储室 , 这个存储室，可以和 最终存储文件 的目录不同
		/**
		 * 原理 它是先存到 暂时存储室，然后在真正写到 对应目录的硬盘上，
		 * 按理来说 当上传一个文件时，其实是上传了两份，第一个是以 .tem 格式的
		 * 然后再将其真正写到 对应目录的硬盘上
		 */
		factory.setRepository(file);
		//设置 缓存的大小，当上传文件的容量超过该缓存时，直接放到 暂时存储室
		factory.setSizeThreshold(1024*1024) ;

		//高水平的API文件上传处理
		ServletFileUpload upload = new ServletFileUpload(factory);
		//手动写的
		OutputStream out = null;

		InputStream in = null;

		try {
			//可以上传多个文件
			List<FileItem> list = (List<FileItem>)upload.parseRequest(request);

			for(FileItem item : list)
			{
				//获取表单的属性名字
				String name = item.getFieldName();

				//如果获取的 表单信息是普通的 文本 信息
				if(item.isFormField())
				{
					//获取用户具体输入的字符串 ，名字起得挺好，因为表单提交过来的是 字符串类型的
					String value = item.getString("utf-8");
					request.setAttribute(name, value);
				}
				//对传入的非 简单的字符串进行处理 ，比如说二进制的 图片，电影这些
				else
				{
					/**
					 * 以下三步，主要获取 上传文件的名字
					 */
					//获取路径名
					String value = item.getName() ;
					//索引到最后一个反斜杠
					int start = value.lastIndexOf("\\");
					//截取 上传文件的 字符串名字，加1是 去掉反斜杠，
					String filename = value.substring(start+1);

					request.setAttribute(name, filename);

					//真正写到磁盘上
					//它抛出的异常 用exception 捕捉
					//得到文件保存的路径
					String savePathStr = mkFilePath(path, filename);
					System.out.println("保存路径为:"+savePathStr);
					//item.write( new File(path,filename) );//第三方提供的

					//手动写的
					out = new FileOutputStream(new File(savePathStr,filename));

					in = item.getInputStream() ;

					int length = 0 ;
					byte [] buf = new byte[1024] ;

					System.out.println("获取上传文件的总共的容量："+item.getSize());

					// in.read(buf) 每次读到的数据存放在   buf 数组中
					while( (length = in.read(buf) ) != -1)
					{
						//在   buf 数组中 取出数据 写到 （输出流）磁盘上
						out.write(buf, 0, length);

					}


					IData inparam = new DataMap();
					IData verParam = new DataMap();
					inparam.put("DOC_NAME",filename);

					//可能是name|acct
					String writer = (String) request.getAttribute("DOC_WRITER");
					String str[] = writer.split("\\|");
					if(str.length>1)
					{
						inparam.put("DOC_AUTHOR_NAME", str[0]);
						inparam.put("DOC_AUTHOR_ACCT", str[1]);
					}
					else
					{
						inparam.put("DOC_AUTHOR_NAME", str[0]);
						inparam.put("DOC_AUTHOR_ACCT", "");
					}
					inparam.put("DOC_LABEL", request.getAttribute("DOC_LABEL"));
					inparam.put("DOC_SUMMARY", request.getAttribute("DOC_SUMMARY"));
					inparam.put("DOC_PATH", savePathStr+File.separator+filename);
					inparam.put("DOC_TYPE",  request.getAttribute("DOC_TYPE"));
					inparam.put("DOC_UPLOADER_ID", request.getAttribute("USER_ID"));
					inparam.put("DOC_UPLOADER_NAME", request.getAttribute("USER_NAME"));
					DocsCentreBean bean = new DocsCentreBean();
					verParam.put("DOC_NAME",filename);
					verParam.put("DOC_AUTHOR_ACCT",inparam.getString("DOC_AUTHOR_ACCT"));
					IData result = bean.queryDocsByID(verParam);
					IDataset doclist= (IDataset)result.get("DOCLIST");

					if (doclist.size()>=1){
						return"您上传的文件已经存在！";
					}else {
						createDoc(inparam);
						return"上传成功！";
					}
				}
			}



		} catch (FileUploadException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{

			try
			{
				out.close();
			}
			catch (Exception e2)
			{
				// TODO: handle exception
			}

			try
			{
				in.close();
			}
			catch (Exception e2)
			{
				// TODO: handle exception
			}

		}
		return "";
	}
	public String mkFilePath(String savePath,String fileName){
		//得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
		//int hashcode = fileName.hashCode();
		//int dir1 = hashcode&0xf;
		//int dir2 = (hashcode&0xf0)>>4;
		//构造新的保存目录
		//String dir = savePath + "\\" + dir1 + "\\" + dir2;
		String dir = savePath ;
		//File既可以代表文件也可以代表目录
		File file = new File(dir);
		if(!file.exists()){
			file.mkdirs();
		}
		return dir;
	}
	public IData createDoc(IData inparam) throws Exception{
		IData resultData = new DataMap();
		DocsCentreDao DocsDao = new DocsCentreDao("bainiu");
		randomNum rand = new randomNum();
		String docID = rand.generateNumber(8);
		inparam.put("DOC_ID", docID);
		resultData = DocsDao.insertDocs(inparam,resultData);
		//doActionLog(inparam);
		return resultData;
	}
}


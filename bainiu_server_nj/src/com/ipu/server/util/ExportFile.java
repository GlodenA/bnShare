package com.ipu.server.util;

import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class ExportFile {
	
	
	/**
	 * 新导出函数，导出多个SHEET页
	 * 
	 * */
	public void exportInfo(HttpServletResponse response,String fileName,IData allInfo){
		IDataset sheetNameSet = (IDataset)allInfo.get("SHEET_NAME");		
		IDataset tabHeadSet = (IDataset)allInfo.get("TABHEAD");
		IDataset tabInfoSet = (IDataset)allInfo.get("TABINFO");					
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
		for(int i=0;i<sheetNameSet.size();i++){
			String sheetName = (String)sheetNameSet.get(i);
			HSSFSheet sheet = wb.createSheet(sheetName);
	        HSSFRow row = sheet.createRow((int) 0);  
	        HSSFCellStyle style = wb.createCellStyle();  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	        
	        HSSFCell cell = row.createCell((short) 0);
	        
	        IDataset tabset =(IDataset)tabHeadSet.get(i);
	        IDataset set = (IDataset)tabInfoSet.get(i);
	        
	        for(int ii = 0;ii<tabset.size();ii++){
	        	IData d = tabset.getData(ii);
	        	cell.setCellValue(d.getString("TAB_TITLE"));  
	        	cell.setCellStyle(style);  
	        	sheet.setColumnWidth(ii, Integer.parseInt(d.getString("TAB_WIDTH")));
	        	cell = row.createCell((short) ii+1);  
	        }
	        
	        for(int iii = 0;iii<set.size();iii++){
	        	IData dataList = set.getData(iii);
	        	row = sheet.createRow((int) iii + 1);
	        	for(int jjj = 0;jjj<tabset.size();jjj++){
	        		IData d = tabset.getData(jjj);
	        		row.createCell((short) jjj).setCellValue(dataList.getString(d.getString("TAB_NAME")));  
	        	}
	        	cell = row.createCell((short) tabset.size());  
	        }    
		}
		
        try  
        {  
            response.reset();
            response.setContentType("application/octet-stream; charset=GBK");
        	response.setHeader("Content-Disposition", "attachment; filename=\"" +  java.net.URLEncoder.encode(fileName,"UTF-8") + "\"");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        	
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }
	}
	/**
	 * 导出
	 * @param response	
	 * @param set	导出的结果集
	 * @param fileName	文件名称
	 * @param sheetName	sheet页名称
	 * @param tabset	导出的列
	 */
	public void exportAskleave(HttpServletResponse response,String fileName,String sheetName,IDataset set,IDataset tabset){
        HSSFWorkbook wb = new HSSFWorkbook();  
        HSSFSheet sheet = wb.createSheet(sheetName);  
        HSSFRow row = sheet.createRow((int) 0);  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
  
        HSSFCell cell = row.createCell((short) 0);  
        for(int i = 0;i<tabset.size();i++){
        	IData d = tabset.getData(i);
        	cell.setCellValue(d.getString("TAB_TITLE"));  
        	cell.setCellStyle(style);  
        	sheet.setColumnWidth(i, Integer.parseInt(d.getString("TAB_WIDTH")));
        	cell = row.createCell((short) i+1);  
        }
        
        for(int i = 0;i<set.size();i++){
        	IData dataList = set.getData(i);
        	row = sheet.createRow((int) i + 1);
        	for(int j = 0;j<tabset.size();j++){
        		IData d = tabset.getData(j);
        		row.createCell((short) j).setCellValue(dataList.getString(d.getString("TAB_NAME")));  
        	}
        	cell = row.createCell((short) tabset.size());  
        }
        
        try  
        {  
            response.reset();
            response.setContentType("application/octet-stream; charset=GBK");
//            response.setContentType("application/vnd.ms-excel;charset=UTF-8");  
        	response.setHeader("Content-Disposition", "attachment; filename=\"" +  java.net.URLEncoder.encode(fileName,"UTF-8") + "\"");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        	
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }
	}
}

package com.ipu.server.dao;

import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ipu.server.util.Pagination;

public class SmartBaseDao extends BaseDAO {
	

	public SmartBaseDao(String connName) throws Exception {
		super(connName);
		// TODO Auto-generated constructor stub
		
	}
		public IData queryPaginationList(String sql, IData param,IData outParam,String ListKey,Pagination paginate)
    	throws Exception
    	{
			return queryPaginationList(sql,param,outParam,ListKey,"ONLYONE",paginate);
    	}
	  public IData queryPaginationList(String sql, IData param,IData outParam,String ListKey,String paginInfoStr,Pagination paginate)
	    throws Exception
	  {
		  
		  IData infoBuf = new DataMap();
		  StringBuffer qryCountBuf = new StringBuffer();
		  qryCountBuf.append("SELECT count(1) SUM FROM ( ");
		  qryCountBuf.append(sql);
		  qryCountBuf.append(" ) COUNT ");
		  
		  IData countBuf = this.queryList(qryCountBuf.toString(), param).first();
		  int count = countBuf.getInt("SUM");
		  int size=paginate.getSize();
		  int showCount=paginate.getShowCount();
		StringBuffer qrySqlBuf = new StringBuffer();
		qrySqlBuf.append("SELECT row_.* FROM ( ");
		qrySqlBuf.append(sql);
		qrySqlBuf.append(" ) row_ LIMIT "+((param.getInt("ROW_INDEX",1)-1)*size)+","+size);
	    IDataset ds = this.queryList(qrySqlBuf.toString(), param);
	    outParam.put(ListKey, ds);
	    double countd= count;
	    double sized= size;
	    double padecountd = countd/sized;
	    infoBuf.put("PAGINATION_COUNT", count);
	    int pageCount= (int)Math.ceil(padecountd);
	    pageCount = pageCount==0?1:pageCount;//如果没有记录,则默认下一页和最后一页都是第一页,add by nisw
	    infoBuf.put("PAGE_COUNT", pageCount);

	    IDataset paginations = new DatasetList();
	    int current=param.getInt("ROW_INDEX",1);
	    int i=1;
	    //例如页的123456789只展示size页
	    while(count>0){
	    	if(current<=showCount&&i<showCount){
	    	
	    	IData buf = new DataMap();
	    	buf.put("ROW_INDEX", i);
	    	
	    	
	    	if(i== current){
	    		buf.put("CURRENT_PAGE", "true");
	    		
	    	}
	    	paginations.add(buf);
	    	
	    	}else{
	    		if(i<current+showCount/2 &&i>=current-(showCount-showCount/2)){
	    			IData buf = new DataMap();
	    	    	buf.put("ROW_INDEX", i);
	    	    	if(i== current){
	    	    		buf.put("CURRENT_PAGE", "true");
	    	    	}
	    	    	paginations.add(buf);
	    		}
	    	}
	    	i++;
	    	count=count-size;
	    }
	    infoBuf.put("CURRENT_PAGE", current);
	    if(current+1>=pageCount){
	    	infoBuf.put("NEXT_PAGE", pageCount);
	    }else{
	    infoBuf.put("NEXT_PAGE", current+1);
	    }
	    if(current-1<=0){
	    	infoBuf.put("LAST_PAGE", 1);
	    }else{
	    	infoBuf.put("LAST_PAGE", current-1);
	    }
	    infoBuf.put("PAGINATIONS", paginations);
	    infoBuf.put("PARAMS", param);
	    outParam.put(paginInfoStr, infoBuf);
	    return outParam;
	  }
	  
}
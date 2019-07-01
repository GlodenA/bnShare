package com.ipu.server.util;

import com.ailk.common.data.IDataset;
import com.ailk.database.session.IDBSession;
import com.ipu.server.core.bean.AppBean;
import com.ipu.server.dao.UtilDao;


/**
 * Created by lxc on 2017/3/7.
 */
public class SeqMaker  extends AppBean {
     private static final long userIdseqCount=0;
     private static final long exIdseqCount=0;
     private static final long logIdseqCount=0;
     private static final long isIdseqCount=0;
     private static final long templetIdseqCount=0;
     private static final long groupIdseqCount=0;
     private static final long smsIdseqCount=0;

     private static SeqMaker sm;
     public static SeqMaker getSeqMaker() throws Exception {
          if(sm==null){
               sm = new  SeqMaker ();
          }
          return sm;
     }

     public String getSeqStr(int mode) throws Exception{
          StringBuffer seqStr = new StringBuffer();
          long countNum =0;
			switch(mode){
                 case 2:
                      countNum+=exIdseqCount;
                      break;
                 case 1:
                      countNum+=userIdseqCount;
                      break;
                 case 3:
                      countNum+=logIdseqCount;
                      break;
            }

          long timeNum = System.currentTimeMillis();
          seqStr.append(timeNum);
          seqStr.append(String.valueOf(countNum));

          return  seqStr.toString();
     }

     public String getSeqStr(String mode) throws Exception{
          StringBuffer seqStr = new StringBuffer();
          long countNum =0;
          if(StringUtil.StrUserIdSeq.equals(mode)){
               countNum+=userIdseqCount;
          }
          if(StringUtil.StrExIdSeq.equals(mode)){
               countNum+=exIdseqCount;
          }
          if(StringUtil.StrLogIdSeq.equals(mode)){
               countNum+=logIdseqCount;
          }
          if(StringUtil.StrIsIdSeq.equals(mode)){
               countNum+= isIdseqCount;
          }
          if(StringUtil.StrTempletIdSeq.equals(mode)){
               countNum+= templetIdseqCount;
          }
          if(StringUtil.StrSmsIdSeq.equals(mode)){
               countNum+= smsIdseqCount;
          }
          if(StringUtil.StrGroupIdSeq.equals(mode)){
               countNum+= groupIdseqCount;
          }
          long timeNum = System.currentTimeMillis();
          seqStr.append(timeNum);
          seqStr.append(String.valueOf(countNum));

          return  seqStr.toString();

     }


}

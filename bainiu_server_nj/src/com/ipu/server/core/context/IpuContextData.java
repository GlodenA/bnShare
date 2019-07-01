package com.ipu.server.core.context;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.mobile.frame.context.impl.DefaultContextData;

public class IpuContextData extends DefaultContextData {
	private static final long serialVersionUID = 1L;
	public String getName() {
		return contextData.getString("USERNAME");
	}
	public void setName(String name) {
		contextData.put("USERNAME", name);
	}
	public String getNickName() {
		return contextData.getString("NICKNAME");
	}
	public void setNickName(String name) {
		contextData.put("NICKNAME", name);
	}
	public String getUserAcct() {
		return contextData.getString("USER_ACCT");
	}
	public void setUserAcc(String userAcc) {
		contextData.put("USER_ACCT", userAcc);
	}
	public String getUserID() {
		return contextData.getString("USER_ID");
	}
	public void setUserID(String id) {
		contextData.put("USER_ID", id);
	}
	public String getEmail() {
		return contextData.getString("EMAIL");
	}
	public void setEmail(String email) {
		contextData.put("EMAIL",email);
	}
	public String getPhone() {
		return contextData.getString("PHONE");
	}
	public void setPhone(String phone) {
		contextData.put("PHONE", phone);
	}
	public String getNTacct() {
		return contextData.getString("NT_ACCT");
	}
	public void setNTacct(String tacct) {
		contextData.put("NT_ACCT", tacct);
	}
	public String getOrg() {
		return contextData.getString("ORG");
	}
	public void setOrg(String org) {
		contextData.put("ORG", org);
	}
	public int getSecurityLevel() {
		return contextData.getInt("SECURITY_LEVEL");
	}
	public void setSecurityLevel(int securityLevel) {
		contextData.put("SECURITY_LEVEL", securityLevel);
	}
	
	public IDataset getRights() {
		return (IDataset)contextData.get("RIGHTS");
	}
	public void setDomain(String org) {
		contextData.put("DOMAIN", org);
	}
	public String getDomain() {
		return contextData.getString("DOMAIN");
	}
	public void setRights(IDataset rights){
		contextData.put("RIGHTS", rights);
		IData right =new DataMap();
		IDataset funRights = new DatasetList();
		for(int i =0;i<rights.size();i++){
			IData buf = (IData) rights.get(i);
			right.put(buf.getString("RIGHT_CODE"), buf.getString("RIGHT_CODE"));
			if("FUN".equals(right.getString("RIGHT_TYPE"))){
				funRights.add(buf);
			}
		}
		contextData.put("RIGHT", right);
		contextData.put("FUNRIGHTS", funRights);
	}
	public IData getRight() {
		return contextData.getData("RIGHT");
	}
	public IDataset getFunRights() {
		return (IDataset)contextData.get("FUNRIGHTS");
	}
	
	public boolean hasRight(String right_code){
		IData rs = contextData.getData("RIGHT");
		return rs.containsKey(right_code);
	}
	
	public boolean isLoginValidate(){
		return contextData.getBoolean("IS_LOGIN_IN");
	}
	public void setLoginInFlag(boolean flag){
		contextData.put("IS_LOGIN_IN", flag);
	}

}

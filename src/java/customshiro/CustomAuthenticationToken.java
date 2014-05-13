package customshiro;


import java.util.Map;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomAuthenticationToken extends UsernamePasswordToken {
	private String customId = null;
	public CustomAuthenticationToken(Map<String, String> map ) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().toString().equals("username")) {
				setUsername(entry.getValue());
			}
			if (entry.getKey().equals("password")) {
				String val=entry.getValue().toString();
				setPassword(val.toCharArray());
				//setPassword(val)
			}
			if (entry.getKey().equals("host")) {
				setHost(entry.getValue());
			}
		
			if (entry.getKey().equals("customId")) {
				setCustomId(entry.getValue());
			}
		}
	}
	/*
	 * 
	 * 	
	 public CustomAuthenticationToken(final String username, final String password,final String host, String customId) {
				setUsername(username);
				setPassword(password.toCharArray());
				setHost(host);
				setCustomId(customId);
	}
	*/

	public String getCustomId() {
		return customId;
	}
	
	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
}




package customshiro;


import java.util.Map;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomAuthenticationToken extends UsernamePasswordToken {
	private String authProvider = null;
	public CustomAuthenticationToken(Map<String, String> map ) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().toString().equals("username")) {
				setUsername(entry.getValue());
			}
			if (entry.getKey().equals("password")) {
				String val=entry.getValue().toString();
				setPassword(val.toCharArray());
			}
			if (entry.getKey().equals("host")) {
				setHost(entry.getValue());
			}
		
			if (entry.getKey().equals("authProvider")) {
				setAuthProvider(entry.getValue());
			}
		}
	}
	public String getAuthProvider() {
		return authProvider;
	}
	
	public void setAuthProvider(String authProvider) {
		this.authProvider = authProvider;
	}
	
}




package customshiro

import org.apache.shiro.authc.AuthenticationToken

class MyToken  implements AuthenticationToken{
	private username;
	
	  public MyToken(String username) {
		this.username = username;
	  }
	
	  public String getPrincipal() {
		return username;
	  }
	  public String getCredentials() {
		return username;
	  }
}

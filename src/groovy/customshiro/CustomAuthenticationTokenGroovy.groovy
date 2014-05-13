package customshiro

import org.apache.shiro.authc.UsernamePasswordToken

class CustomAuthenticationTokenGroovy extends UsernamePasswordToken {
	
	/*public CustomAuthenticationToken(final String username, final String password, final String rememberMe, final String host, String customId) {
		setUsername(username)
		//setPassword(password)
		setPassword(password != null ? password.toCharArray() : null)
		setHost(host)
		setRememberMe(rememberMe)
		setCustomId(customId)
	}*/	
	
	public CustomAuthenticationTokenGroovy(Map map) {
		println "----"+map
		map.each { key, value ->
			if (key.equals('username')) {
				setUsername(value)
			}
			if (key.equals('password')) {
				def val=value as String
				setPassword(val.toCharArray())
				//setPassword(val)
			}
			
			if (key.equals('host')) {
				setHost(value)
			}
			
			if (key.equals('rememberMe')) {
				setRememberMe(value)
			}
			if (key.equals('customId')) {
				setCustomId(value)
			}
			
		}
	}

	public String getCustomId() {
		return customId
	}
	
	public setCustomId(String customId) {
		this.customId = customId
	}
	
}




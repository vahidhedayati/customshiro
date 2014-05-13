import org.apache.shiro.authc.AccountException
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.UsernamePasswordToken

import customshiro.security.*

/*
 * Based on original ShiroDbRealm supplied with Shiro plugin
 */
class MyCompaniesRealm {

	static authTokenClass = UsernamePasswordToken

	def credentialMatcher
	def shiroPermissionResolver
	def ldapService
	def grailsApplication

	/*
	 * This method is invoked when signon is required
	 */
	def authenticate(authToken) {
		def username = authToken.username
		// Null username is invalid
		if (username==null) {
			throw new AccountException("Empty/incomplete usernames are not allowed")
		}
		checkInternal(authToken)
		return username
	}

	private String checkGroup(authToken) {
		if (log.isDebugEnabled()) {
			log.debug "Attempting to get user Groups ${authToken.username}..."
		}
		def ctx,serverInfo,result
		try {
			serverInfo=ldapService.findContext(authToken.authProvider)
			if (!serverInfo) {
				def msg = 'No LDAP server available'
				log.error msg
				throw new AuthenticationException(msg)
			}
			ctx=serverInfo.context
			def url=serverInfo.url
			if (log.isDebugEnabled()) {
				log.debug "Using LDAP server ${url}"
			}
			result = ldapService.findUserInfo(ctx,authToken.username,authToken.authProvider)
			// Ensure context is always closed
		}finally {
			try {
				if (log.isDebugEnabled()) {
					log.debug '--------------------------------> closing context'
				}
				if (ctx) ctx.close()
			} catch (Exception e) {}
		}
		return result
	}

	/**
	 * Internal user authentication (LDAP)
	 */
	private void checkInternal(authToken) {
		if (log.isDebugEnabled()) {
			log.debug "Attempting to authenticate internal user ${authToken.username}..."
		}
		def ctx,serverInfo
		try {
			
			serverInfo=ldapService.findContext(authToken.authProvider)
			if (!serverInfo) {
				def msg = 'No LDAP server available'
				log.error msg
				throw new AuthenticationException(msg)
			}
			ctx=serverInfo.context
			def url=serverInfo.url
			if (log.isDebugEnabled()) {
				log.debug "Using LDAP server ${url}"
			}
			def result = ldapService.findUser(ctx,authToken.username,authToken.authProvider)
			if (!result.hasMore()) {
				throw new UnknownAccountException("No account found for user ${authToken.username}")
			}
			def searchResult = result.next()
			if (log.isDebugEnabled()) {
				log.debug "authenticate for ${searchResult.nameInNamespace}"
			}
			boolean authenticated= ldapService.authenticateUser(url,searchResult.nameInNamespace,authToken.password)
			if (!authenticated) {
				throw new IncorrectCredentialsException("Invalid password for user '${authToken.username}'")
			}
		}

		// Ensure context is always closed
		finally {
			try {
				if (log.isDebugEnabled()) {
					log.debug 'closing context'
				}
				if (ctx) ctx.close()
			} catch (Exception e) {
			}
		}
	}

	/*
	 * We don't currently have the concept of Roles, but we do have types...
	 */
	def hasRole(principal, roleName) {
		return true
	}

	/*
	 * ...but only one at a time
	 */
	def hasAllRoles(principal, roles) {
		return true
	}

	/*
	 * Search the user's permissions
	 */
	def isPermitted(principal, requiredPermission) {
		return true
	}

}

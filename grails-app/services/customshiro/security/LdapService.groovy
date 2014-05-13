package customshiro.security

import javax.naming.Context
import javax.naming.NamingEnumeration
import javax.naming.NamingException
import javax.naming.directory.*

import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.realm.ldap.LdapUtils
import org.springframework.web.context.request.RequestContextHolder

class LdapService {
	def grailsApplication
	def setting
	static transactional = false
	/**
	 * Find an LDAP context
	 * @return context
	 */
	def findContext(String authProvider) {
		def ctx
		def ldapUrls = grailsApplication.config."${authProvider}".servers
		
		def env = new Hashtable()
		env[Context.INITIAL_CONTEXT_FACTORY] = 'com.sun.jndi.ldap.LdapCtxFactory'
		env[Context.SECURITY_AUTHENTICATION] = 'none'
		// Find an LDAP server that we can connect to.
		def foundServer = ldapUrls.find {server ->
			if (log.isDebugEnabled()) {
				log.debug "Trying LDAP server ${server.url} ..."
			}
			env[Context.PROVIDER_URL] = server.url
			if (server.user) {
				// Non-anonymous access for the search.
				env[Context.SECURITY_AUTHENTICATION] = 'simple'
				env[Context.SECURITY_PRINCIPAL] = server.user
				env[Context.SECURITY_CREDENTIALS] = server.pwd
			}
			// If an exception occurs, log it.
			try {
				ctx = new InitialDirContext(env)
				return true
			}
			catch (NamingException e) {
				if (log.isDebugEnabled()) {
					log.debug "Could not connect to ${server.url}: ${e}"
				}
				return false
			}
		}
		if (!foundServer) return null
		return [context:ctx,url:foundServer.url]
	}


	def findUserInfo (Context ctx,  String username,String authProvider) {
		StringBuilder sb=new StringBuilder()
		sb.append('<br>')
		try {
			def roleNames=[]
			def searchBase=grailsApplication.config."${authProvider}".base
			SearchControls searchCtls = new SearchControls()
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE)
			String searchFilter = '(&(objectClass=user)(!(objectClass=computer))(sAMAccountName={0}))'
			//String searchFilter = '(objectClass=*)'
			def searchArguments = [username]
			def currentRequest = RequestContextHolder.requestAttributes
			NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchArguments as Object[], searchCtls)
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next()
				if (log.isDebugEnabled()) {
					log.debug("Retrieving group names for user [" + sr.getName() + "]")
				}
				def usersname,allgroups=""
				def alluser=sr.getName()
				if (alluser.indexOf('CN=')>-1) {
					usersname=alluser.substring(3,alluser.indexOf(","))
				}
				if(currentRequest) {
					sb.append('<div class=tbutton><b>UserID:</b></div><div class=tbutton>'+username+'</div><br/><br/>')
					sb.append('<div class=tbutton><b>NAME:</b></div><div class=tbutton>'+usersname+'</div><br/><br/>')
				}
				Attributes attrs = sr.getAttributes()
				if (attrs != null) {
					NamingEnumeration ae = attrs.getAll()
					//currentRequest.session["usersattr"]=attrs.getAll()
					while (ae.hasMore()) {
						Attribute attr = (Attribute) ae.next()
						if (attr.getID().equals("mail")) {
							LdapUtils.getAllAttributeValues(attr).each {
								sb.append('<div class=tbutton><b>EmailAddress:</b></div><div class=tbutton>'+it+'</div><br/>')
							}
						}
						if (attr.getID().equals("manager")) {
							LdapUtils.getAllAttributeValues(attr).each {
								if (it.indexOf('CN=')>-1) {
									def usersmanager=it.substring(3,it.indexOf(","))

									sb.append('<div class=tbutton><b>Manager:</b></div><div class=tbutton>'+usersmanager+'</div><br/>')
								}
							}
						}
						if (attr.getID().equals("departmentNumber")) {
							LdapUtils.getAllAttributeValues(attr).each {
								sb.append('<div class=tbutton><b>Department ID:</b></div><div class=tbutton>'+it+'</div><br/>')
							}
						}
						if (attr.getID().equals("memberOf")) {
							Collection<String> groupNames = LdapUtils.getAllAttributeValues(attr)
							if (log.isDebugEnabled()) {
								log.debug("Groups found for user [" + username + "]: " + groupNames)
							}
							groupNames.each { groups ->
								if (groups.indexOf('CN=')>-1) {
									allgroups=groups.substring(3,groups.indexOf(","))
									allgroups=allgroups.replaceAll('\\\\# ', '')
									allgroups=allgroups.replaceAll('\\\\#\\\\# ','')
									allgroups=allgroups.replaceAll('\\\\#','')
									roleNames.add(allgroups)
								}
							}
							sb.append('<div class=tbutton><b>Groups:</b></div><div class=tbutton>')
							roleNames.each {
								sb.append(it+", ")
							}
							sb.append("</div><br/>")
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
		}
		//return roleNames
		return sb.toString()
	}

	def findUser(Context ctx,  String username,String authProvider) {
		def searchBase=grailsApplication.config."${authProvider}".base
		SearchControls searchCtls = new SearchControls()
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE)
		String searchFilter = '(&(objectClass=user)(!(objectClass=computer))(sAMAccountName={0}))'
		def searchArguments = [username]
		// Search for the unique account name matching the username
		def result = ctx.search(searchBase, searchFilter, searchArguments as Object[], searchCtls)
		def currentRequest = RequestContextHolder.requestAttributes
		def roleNames=[]
		NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchArguments as Object[], searchCtls)
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult) answer.next()
			if (log.isDebugEnabled()) {
				log.debug(">> | Retrieving group names for user [" + sr.getName() + "]")
			}
			def usersname,allgroups=""
			def alluser=sr.getName()
			if (alluser.indexOf('CN=')>-1) {
				usersname=alluser.substring(3,alluser.indexOf(","))
			}
			if(currentRequest) {
				currentRequest.session["usersname"]=usersname
			}
			Attributes attrs = sr.getAttributes()
			if (attrs != null) {
				NamingEnumeration ae = attrs.getAll()
				currentRequest.session["usersattr"]=attrs.getAll()
				while (ae.hasMore()) {
					Attribute attr = (Attribute) ae.next()
					if (attr.getID().equals("mail")) {
						LdapUtils.getAllAttributeValues(attr).each {
							currentRequest.session["usersemail"]=it
						}
					}
					if (attr.getID().equals("manager")) {
						LdapUtils.getAllAttributeValues(attr).each {
							if (it.indexOf('CN=')>-1) {
								def usersmanager=it.substring(3,it.indexOf(","))
								currentRequest.session["usersmanager"]=usersmanager
							}
						}
					}
					if (attr.getID().equals("departmentNumber")) {
						LdapUtils.getAllAttributeValues(attr).each {
							currentRequest.session["usersdepartment"]=it
						}
					}
					if (attr.getID().equals("memberOf")) {
						Collection<String> groupNames = LdapUtils.getAllAttributeValues(attr)
						if (log.isDebugEnabled()) {
							log.debug("Groups found for user [" + username + "]: " + groupNames)
						}
						groupNames.each { groups ->
							if (groups.indexOf('CN=')>-1) {
								allgroups=groups.substring(3,groups.indexOf(","))
								allgroups=allgroups.replaceAll('\\\\# ', '')
								allgroups=allgroups.replaceAll('\\\\#\\\\# ','')
								allgroups=allgroups.replaceAll('\\\\#','')
								roleNames.add(allgroups)
							}
						}
					}
				}
			}
		}
		if(currentRequest) {
			currentRequest.session["usersgroups"]=roleNames
		}
		return result
	}

	/**
	 * Authenticate a user against an LDAP directory
	 * @param url
	 * @param principal
	 * @param password
	 * @return true=user was authenticated
	 */
	def authenticateUser(String url,String principal,char[] password) {
		def env = new Hashtable()
		env[Context.INITIAL_CONTEXT_FACTORY] = 'com.sun.jndi.ldap.LdapCtxFactory'
		env[Context.PROVIDER_URL] = url
		env[Context.SECURITY_AUTHENTICATION] = 'simple'
		env[Context.SECURITY_PRINCIPAL] = principal
		// n.b. password must be guaranteed never to be empty, otherwise
		// the implementation "helpfully" switches authentication mode to 'None'
		// and validates any principal.
		env[Context.SECURITY_CREDENTIALS] = password?.toString()?:'x'
		def ctx
		if (log.isDebugEnabled()) {
			log.debug 'Authenticating specific user'
		}
		try {
			ctx=new InitialDirContext(env)
			return true
		}
		catch(Exception e) {
			return false
		}
		// Ensure context is always closed
		finally {
			if (log.isDebugEnabled()) {
				log.debug 'Closing specific user context'
			}
			try {
				if (ctx) ctx.close()
				env.remove(Context.SECURITY_CREDENTIALS)
			} catch (Exception e) {
			}
		}
	}

	private String justGroup(String sres) {
		String seperator='CN='
		sres=sres.replace('CN=','\nCN=')
		List<String> list = Arrays.asList(sres.split("\\n"));
		String g=""
		list.each() {
			if (it.indexOf(seperator)>-1) {
				g+=it.substring(it.indexOf(seperator)+seperator.length(),it.indexOf(","))+"<br>"
			}
		}
		return g
	}

}

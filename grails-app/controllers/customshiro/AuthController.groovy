package customshiro


import javax.servlet.http.HttpSession

import org.apache.shiro.SecurityUtils
import org.apache.shiro.web.util.WebUtils



class AuthController {
	def shiroSecurityManager
	def grailsApplication
	
	def loginForm() { 
		render view: 'login'
	}
	static navigation = [
		title:'logout',
		action:'signOut',
		order:900,
		isVisible:{SecurityUtils.subject.principal != null}
	]
	
	def return_ip() {
		def ip=request.getRemoteAddr()
		
		if ((ip==null) || (ip.equals('')) || (ip.equals("127.0.0.1"))) {
			ip=request.getHeader("X-Forwarded-For")
		
		}
		if ((ip==null) || (ip.equals('')) || (ip.equals("127.0.0.1"))) {
			ip=request.getHeader("Client-IP")
		}
		if (ip==null) { ip="127.0.0.1"}
		session.userip=ip
		 return ip
		
	}

	def index = {
		forward action:'loginForm',params:params
	}
	
	/**
	 * Present the login page
	 */
	def login = {
		def model=new LDAPLoginCommand()
		model.authtype=params.authtype
		model.username=params.username
		model.rememberMe=params.rememberMe
		model.targetUri=params.targetUri
		display(model)
	}

	void display(LDAPLoginCommand cmd) {
		render( template:'loginForm', model:[loginCmd:cmd])
	}
	
	/**
	 * Invoked by login page
	 */
	def signIn = {LDAPLoginCommand cmd->
		if (cmd.hasErrors()) {
			flash.message = message(code: "login.failed"+cmd.username)
			display(cmd)
			//saveRecord(cmd.username,false) 
			return
		} 
		String host=request.getHeader('host')
		// Create a password token for captured credentials
		cmd.username=cmd.username?.trim()?.toLowerCase()
		//def authToken = new UsernamePasswordToken(username:cmd.username,password:cmd.password,host:host)
		def authToken = new CustomAuthenticationToken(username:cmd.username,
				password:cmd.password,
				host:host, 
				customId:cmd.authtype
			)
		
		if (cmd.rememberMe) {
			authToken.rememberMe = true
		}
		
		
		def targetUri = cmd.targetUri ?: "/"
		def savedRequest = WebUtils.getSavedRequest(request)
		if (savedRequest) {
			targetUri = savedRequest.requestURI - request.contextPath
			if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
		}
		def msg
		try{
		
			SecurityUtils.subject.login(authToken)// Login was successful so perform session setup
			
			def userid=SecurityUtils.subject.principal
			if (log.isDebugEnabled()) {
				log.debug "principal for ${cmd.username}=${userid}"
			}
			
			/*
			 * 	To get hold of the authProvider at a later point simply execute this
			 * 
			 *	def provider=SecurityUtils.subject.session.getAttribute('authProvider')
			 *	println provider
			 *	
			 *	
			 */


			session.user=userid
			render(template:"welcomeMessage")
			if (log.isDebugEnabled()) {
				log.debug "Redirecting successful authentication"
			}
			return
		}
		catch (Exception ex){
			log.info "Authentication failure for user '${cmd.username}'\n${ex}"
			flash.message = msg?:message(code: "login.failed")
			//saveRecord(cmd.username,false)
		}
		finally {
			// Delete the token for security protection
			authToken.clear()
		}
		// Not authorised - back to login page
		if (log.isDebugEnabled()) {
			log.debug "User ${cmd.username} not authorised"
		}
		display(cmd)
	}

	/**
	 * sign out option
	 */
	def signOut = {
		if (SecurityUtils.subject.principal) { 
			SecurityUtils.subject?.logout()
		}
		
		HttpSession session = request.getSession(false)
		if(session!=null) {
			session.invalidate()
		}
		redirect(uri:'/')
	}

	def unauthorized = {
		render (status:403,text:"${message code:'login.denied',default:'Not authorized'}")
	}
}

class LDAPLoginCommand implements Serializable {
	private static final long serialVersionUID = 1L
	String authtype
	String username
	String password
	Boolean rememberMe
	String targetUri

	// n.b. Constraints are deliberately looser than authentication actually
	// demands. This is to prevent giving too many clues to BlackHats.
	static constraints={
		username(blank:false,minSize:1,maxSize:30,matches:/^[\-A-Za-z0-9 ]+$/)
		password(nullable:false,blank:false,minSize:1,maxSize:256)
	}
	String toString() {
		"${username}:${rememberMe}"
	}
}


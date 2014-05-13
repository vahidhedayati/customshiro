import org.apache.shiro.SecurityUtils


// Security filters - sets the cookie upon detection or removes it when user logs out properly via site.
// The site has been designed not to authenticate all requests 

// instead on application that this is from there is an abstract class that all required controllers extend 
// the abstract class checks if user requires authentication for a given action
// otherwise site loads with no authentication requirements for general usage.
class SecurityFilters {
	def filters = {
		catchRememberMeCookie(url: "/**") {
			before = {
				request.cookies.find({ it.name == "rememberMe" }).each {
					getSession() 
					if ((controllerName.equals('auth'))&&(actionName.equals('signOut'))) {
						log.info "Removing rememberMe cookie: ${it.value}"
						it.maxAge = 0
						it.path = "/"
						it.value=""
						response.addCookie it
						sleep(100)
						def subject = SecurityUtils.subject
						log.info "Logging user '${subject.principal}' out"
						log.info "rememberMe cookie value is: ${it.value} expires ${it.maxAge} "
						subject.logout()
						return
					}else{
						// Remove the rememberMe cookie
						if (!session.user) {
							log.info "Found rememberMe cookie (reauthenticating user): ${it.value}"
							//response.addCookie it
							def subject = SecurityUtils.subject
							def userid=subject.principal
							if (log.isDebugEnabled()) {
								//log.debug "principal ${userid}"
							}
							session.user=userid
						}
					}
					request.accessAllowed = true
				}
			}
		}
	}
}

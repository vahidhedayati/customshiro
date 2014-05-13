<g:if test="${loginCmd?.hasErrors()}">
	<div id="message" class="errorMessage">
		<g:renderErrors bean="${loginCmd}" />
	</div>
</g:if>


<g:formRemote name="loginForm" url="[controller:'auth',action:'signIn']" update="authBox" onSuccess="redirSite()">

<g:javascript>
function redirSite() {
	var cuser="${session.user }";
	$('#userMessage').html('Logged in'); 
	var lastUrl = "${session.lastURL}";
	if (lastUrl) { 
		window.location.href=lastUrl;
	}
}
function redirSite2() { 
	var lastUrl = "${session.lastURL}";
	var lastPlace = "${session.lastPlace}";
	if (lastUrl) { 
		window.alert('LAST: '+lastUrl);
		window.location.href=lastUrl;
		sleep(1000);
		if (lastPlace) {
			window.alert('NEW: '+lastPlace);
			if (!lastPlace.equals(lastUrl)) {
				window.location.href=lastPlace;
			}
		}
	}
}

</g:javascript>
			
<div  role="status">${flash.message}</div>
<g:select id="ldapauth" class="form-control" name="authtype" from="${customshiro.AuthProvider.list()}" optionKey="name" optionValue="friendlyName" required="" value="${fieldValue(bean:loginCmd, field:'authtype')}"/>
<g:textField class="form-control" required="true" placeholder="Username" name="username" value="${fieldValue(bean:loginCmd, field:'username')}" /><br/>
<g:passwordField  class="form-control" required="true" placeholder="Password" name="password" value="${fieldValue(bean:loginCmd, field:'password')}" /><br/>			
<input type="checkbox" name="rememberMe" checked><font size="2">Remember Me</font>
<input class="form-control" type="submit" value="Login" class="loginbtn" id="loginbtn" />	
</g:formRemote>

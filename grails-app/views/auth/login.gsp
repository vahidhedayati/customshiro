<html>
<head>
<meta name="layout" content="main" />
<title><g:message code="customshiro.title" /></title>
</head>
<body>
	<div id="loginPage">
	<g:if test="${! session.user }">
		<g:form action="signIn">
			<input type="hidden" name="targetUri" value="${model?.targetUri}" />
			<table align="center">
			<g:select id="ldapauth" class="form-control" name="authtype" from="${customshiro.AuthProvider.list()}" optionKey="name" optionValue="friendlyName" required="" value="${fieldValue(bean:loginCmd, field:'authtype')}"/>
			
				<tbody id="loginPage">
					<tr>
						<td class="label"><label for="username"><g:message
									code="customshiro.nuid" /></label></td>
						<td class="value"><g:textField name="username"
								autocomplete="off" value="${model?.username}" size="20" /></td>
					</tr>
					<tr>
						<td class="label"><label for="password"><g:message
									code="customshiro.pwd" /></label></td>
						<td class="value"><g:passwordField autocomplete="off"
								name="password" size="20" /></td>
					</tr>
					<tr class="error">
						<td colspan="2"><g:message code="${flash.message}"
								args="${flash.args}" default="${flash.default}" /></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><g:submitButton accesskey="s" class="gsButton"
								name="login" value="${g.message(code:'customshiro.login') }" /></td>
					</tr>
				</tbody>
			</table>
			<input type="checkbox" name="rememberMe" checked><font size="2">Remember Me</font>
		</g:form>
		</g:if>
		<g:else>
			${session.user } already logged in
		</g:else>
	</div>
</body>
</html>

<%@ page import="customshiro.AuthProvider" %>



<div class="fieldcontain ${hasErrors(bean: authProviderInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="authProvider.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${authProviderInstance?.name}"/>

</div>


<div class="fieldcontain ${hasErrors(bean: authProviderInstance, field: 'friendlyName', 'error')} required">
	<label for="friendlyName">
		<g:message code="authProvider.friendlyName.label" default="friendlyName" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="friendlyName" required="" value="${authProviderInstance?.friendlyName}"/>
</div>

CustomShiro is a grails project which shows how to use apache shiro with various LDAP/Active Directory authentication mechanisms. It also shows how to keep user logged in for a defined period and how to remove cookies from users machine 


Changes made to:

## configuration items


#### grails-app/conf/Config.groovy

This contains the list of AD providers and specific cookie age overrides.




#### grails-app/conf/SecurityFilters.groovy

This contains the actions to take upon user visiting site again or logging out




#### grails-app/conf/BootStrap.groovy

This contains the authProviders to create pre launching application which also populates AD/LDAP servers on login page




#### grails-app/domain/customshiro/AuthProvider.groovy

This contains a domainClass which is used to create authentication providers to tally up with authentication providers defined in config.groovy





#### src/java/customshiro/CustomAuthenticationToken.java

This contains a custom Token which now includes our own authProviders being passed around - could have used host and used default usernamePasswordToken but this was an excercise on how to extend existing framework





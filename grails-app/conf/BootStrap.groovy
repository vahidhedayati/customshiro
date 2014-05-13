import customshiro.AuthProvider

class BootStrap {

    def init = { servletContext ->
		AuthProvider.findOrSaveWhere(name: 'ldap_provider1', friendlyName: 'LDAP1')
		AuthProvider.findOrSaveWhere(name: 'ldap', friendlyName: 'LDAP')
		AuthProvider.findOrSaveWhere(name: 'ldap_provider2', friendlyName: 'AD2')
		AuthProvider.findOrSaveWhere(name: 'testad', friendlyName: 'TEST_AD')
    }
    def destroy = {
    }
}

package customshiro

import org.springframework.dao.DataIntegrityViolationException

class AuthProviderController {


	static allowedMethods = [save: "POST",  delete: "POST"]
	def addExcept(list) {
		list << 'index' << 'list' << 'show'
	}
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        [authProviderInstanceList: AuthProvider.list(params), authProviderInstanceTotal: AuthProvider.count()]
    }

    def create() {
        [authProviderInstance: new AuthProvider(params)]
    }

    def save() {
        def authProviderInstance = new AuthProvider(params)
        if (!authProviderInstance.save(flush: true)) {
            render(view: "create", model: [authProviderInstance: authProviderInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), authProviderInstance.id])
        redirect(action: "show", id: authProviderInstance.id)
    }

    def show(Long id) {
        def authProviderInstance = AuthProvider.get(id)
        if (!authProviderInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), id])
            redirect(action: "list")
            return
        }
        [authProviderInstance: authProviderInstance]
    }

    def edit(Long id) {
        def authProviderInstance = AuthProvider.get(id)
        if (!authProviderInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), id])
            redirect(action: "list")
            return
        }
        [authProviderInstance: authProviderInstance]
    }

    def update(Long id, Long version) {
        def authProviderInstance = AuthProvider.get(id)
        if (!authProviderInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), id])
            redirect(action: "list")
            return
        }
        if (version != null) {
            if (authProviderInstance.version > version) {
                authProviderInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'deploymentFileTypes.label', default: 'AuthProvider')] as Object[],
                          "Another user has updated this AuthProvider while you were editing")
                render(view: "edit", model: [authProviderInstance: authProviderInstance])
                return
            }
        }
        authProviderInstance.properties = params
        if (!authProviderInstance.save(flush: true)) {
            render(view: "edit", model: [authProviderInstance: authProviderInstance])
            return
        }
        flash.message = message(code: 'default.updated.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), authProviderInstance.id])
        redirect(action: "show", id: authProviderInstance.id)
    }

    def delete(Long id) {
        def authProviderInstance = AuthProvider.get(id)
        if (!authProviderInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), id])
            redirect(action: "list")
            return
        }
        try {
            authProviderInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'deploymentFileTypes.label', default: 'AuthProvider'), id])
            redirect(action: "show", id: id)
        }
    }
}

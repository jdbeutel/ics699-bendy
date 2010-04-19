import org.springframework.web.context.support.WebApplicationContextUtils
import com.getsu.wcy.User

class BootStrap {

     def init = { servletContext ->
        def appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)

         // init auth events
         def events = appCtx.authenticationService.events // start with defaults
         events.onNewUserObject = { loginID -> User.createInstance(loginID) }
         events.onSignup = { params ->
             params.user.person.properties[ 'preferredName', 'honorific', 'givenNames', 'familyName', 'suffix', 'photo', 'birthDate'] = params.params
             params.user.person.save(failOnError:true)
         }
//         events.onValidatePassword = { password -> return !appCtx.myDictionaryService.containsWord(password) }
     }

     def destroy = {
     }
} 
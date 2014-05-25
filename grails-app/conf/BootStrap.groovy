import org.springframework.web.context.support.WebApplicationContextUtils
import com.getsu.wcy.User
import com.getsu.wcy.Connection.ConnectionType
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import com.getsu.wcy.Person
import com.getsu.wcy.Notification
import com.getsu.wcy.WcyDomainBuilder
import com.getsu.wcy.PhoneNumber.PhoneNumberType

class BootStrap {

     def init = { servletContext ->
        def appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)

         // init auth events
         def events = appCtx.authenticationService.events // start with defaults
         events.onNewUserObject = { loginID -> User.createSignupInstance(loginID) }
         events.onSignup = { params ->
             def includes = [ 'preferredName', 'honorific', 'firstGivenName', 'middleGivenNames', 'familyName',
                     'suffix', 'birthDate',
                     'connections[0].place.addresses[0].city',
                     'connections[0].place.addresses[0].state',
                     'connections[0].place.addresses[0].streetType'
             ]
             params.user.person.properties[includes] = params.params
             params.user.person.save(failOnError:true)
         }
//         events.onValidatePassword = { password -> return !appCtx.myDictionaryService.containsWord(password) }

//         environments {
//             development {
                 addJoe(events.onEncodePassword)
                 addJane(events.onEncodePassword)
                 addCoworker(events.onEncodePassword)
                 addGranny()
                 addPhoneOnly()
                 addGenericPeople()
                 addNotifications()
//             }
//         }
     }

    private static addJoe(Closure passwordEncoder) {
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def joe = builder.user(login:'joe.cool@example.com', password:passwordEncoder('password')) {
            person(firstGivenName:'Joe', middleGivenNames:'B.', familyName:'Cool', preferredName:'J.C.', honorific:'Mr.') {
                photo(fileName:'david-n-ben.JPG', contents:BootStrap.class.getResourceAsStream('dev/david-n-ben.JPG').bytes)
                connection(type:ConnectionType.HOME) {
                    place {
                        address(streetType:true, line1:'123 King St.', city:'Honolulu', state:'HI')
                    }
                }
            }
            settings(dateFormat:'yyyy-MM-dd HH:mm', timeZone:TimeZone.default )
        }
        joe.save(failOnError:true)
    }

    private static addJane(Closure passwordEncoder) {
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def jane = builder.user(login:'jane.cool@rr.net', password:passwordEncoder('password')) {
            person(firstGivenName:'Jane', familyName:'Cool',
                    middleGivenNames:'Minerva', preferredName:'Jane', honorific:'Ms.', suffix:'Ph.D.'
            ) {
                photo(fileName:'ben-tea.JPG', contents:BootStrap.class.getResourceAsStream('dev/ben-tea.JPG').bytes)
                connection(type:ConnectionType.HOME) {
                    place {
                        address(streetType:true, postalType:true, line1:'222 Kapiolani Blvd.', city:'Honolulu', state:'HI')
                    }
                    phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-1111')
                }
                connection(type:ConnectionType.WORK) {
                    place {
                        address(streetType:true, line1:'42 Nuuanu Ave.', city:'Honolulu', state:'HI')
                        address(postalType:true, line1:'P.O.Box 1001', city:'Honolulu', state:'HI')
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-3333')
                        phoneNumber(type:PhoneNumberType.FAX, number:'555-4444')
                    }
                    phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-3333 x123')
                    emailAddress(name:'Jane Cool, V.P. Sales', address:'jane@foo.com')
                }
                phoneNumber(type:PhoneNumberType.MOBILE, number:'555-5555')
                emailAddress(name:'Jane Cool', address:'jane.cool@rr.net')
            }
            settings(dateFormat:'yyyy-MM-dd HH:mm', timeZone:TimeZone.default )
        }
        jane.save(failOnError:true)
    }

    private static addCoworker(Closure passwordEncoder) {
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def jane = builder.user(login:'coworker@example.com', password:passwordEncoder('password')) {
            person(firstGivenName:'Alex', familyName:'McFee', honorific:'Mr.', middleGivenNames:'Trouble') {
                photo(fileName:'ben-korea.JPG', contents:BootStrap.class.getResourceAsStream('dev/ben-korea.JPG').bytes)
                connection(type:ConnectionType.WORK) {
                    place {
                        address(streetType:true, line1:'76 Pensicola Ave.', city:'Honolulu', state:'HI')
                        address(postalType:true, line1:'P.O.Box 2002', city:'Honolulu', state:'HI')
                        phoneNumber(type:PhoneNumberType.FAX, number:'555-7777')
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-6666')
                    }
                    phoneNumber(type:PhoneNumberType.MOBILE, number:'555-3333 x123')
                    emailAddress(name:'Alex McFee, Engineer', address:'coworker@example.com')
                }
            }
            settings(dateFormat:'yyyy-MM-dd HH:mm', timeZone:TimeZone.default )
        }
        jane.save(failOnError:true)
    }

    // not sure why, but had to make this method static to have it found at runtime without any parameters
    private static addGranny() { // no User, only Person
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def granny = builder.person(honorific:'Mrs.', firstGivenName:'Bertha', familyName:'Cool') {
            photo(fileName:'slippers.JPG', contents:BootStrap.class.getResourceAsStream('dev/slippers.JPG').bytes)
            connection(type:ConnectionType.HOME) {
                place {
                    address(streetType:true, line1:'333 Date St.', city:'Honolulu', state:'HI')
                    phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-2222')
                }
            }
        }
        granny.save(failOnError:true)
    }

    private static addPhoneOnly() { // no User, only Person
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def hal = builder.person(firstGivenName:'Hal', familyName:'Homeless') {
            photo(fileName:'slippers.JPG', contents:BootStrap.class.getResourceAsStream('dev/slippers.JPG').bytes)
            phoneNumber(type:PhoneNumberType.MOBILE, number:'555-8888')
        }
        hal.save(failOnError:true)
    }

    private static addGenericPeople() { // no User, only Person
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        (10..55).each { index ->
            def person = builder.person(firstGivenName:"Test$index", familyName:'Smith') {
                photo(fileName:'slippers.JPG', contents:BootStrap.class.getResourceAsStream('dev/slippers.JPG').bytes)
                connection(type:ConnectionType.HOME) {
                    place {
                        address(streetType:true, line1:"$index Citron Ave.", city:'Honolulu', state:'HI', postalCode:'96811')
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:"555-$index$index")
                    }
                }
            }
            person.save(failOnError:true)
        }
    }

    private static Date daysFromNow(days) {
        long DAYS_MILLIS = 1000 * 60 * 60 * 24;
        Date now = new Date()
        now.setTime((long)(now.time + days * DAYS_MILLIS))
        return now
    }

    private static addNotifications() {
        def joe = User.findByLogin('joe.cool@example.com')
        def jane = User.findByLogin('jane.cool@rr.net')
        def coworker = User.findByLogin('coworker@example.com')
        def granny = Person.findByFirstGivenName('Bertha')
        def agent13 = Person.findByFirstGivenName('Test13')

        new Notification(recipient:joe, date:daysFromNow(-5.7), subject:jane, verb:'shared with you', object:granny).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-3.5), subject:jane, verb:'updated home address', object:granny).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-4.5), subject:jane, verb:'added email address', object:joe.person).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-2.05), subject:jane, verb:'added home phone', object:granny).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-2.04), subject:joe, verb:'updated home phone', object:granny).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-2.02), subject:jane, verb:'deleted work phone', object:granny).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-3.3), subject:jane, verb:'added home phone', object:agent13).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-7.3), subject:coworker, verb:'shared with you', object:coworker.person).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-7.2), subject:coworker, verb:'added work phone', object:coworker.person).save(failOnError:true)
        new Notification(recipient:joe, date:daysFromNow(-4.1), subject:coworker, verb:'updated work phone', object:coworker.person).save(failOnError:true)
    }

     def destroy = {
     }
}

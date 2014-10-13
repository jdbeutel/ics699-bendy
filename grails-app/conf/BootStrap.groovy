import com.getsu.wcy.CommunicationLinks
import com.getsu.wcy.Photo
import grails.converters.JSON
import org.hibernate.SessionFactory
import org.springframework.web.context.support.WebApplicationContextUtils
import com.getsu.wcy.User
import com.getsu.wcy.Connection.ConnectionType
import com.getsu.wcy.Person
import com.getsu.wcy.Notification
import com.getsu.wcy.WcyDomainBuilder
import com.getsu.wcy.PhoneNumber.PhoneNumberType

class BootStrap {

    def elasticSearchService
    SessionFactory sessionFactory

    def init = { servletContext ->
        def appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)

        JSON.registerObjectMarshaller(Enum) { e ->
            e.name()
        }
        JSON.registerObjectMarshaller(java.sql.Date) { date ->      // avoid including the time part of the date
            date.toString()
        }
        JSON.registerObjectMarshaller(Photo) { photo ->
            [id: photo.id, fileName: photo.fileName]    // exclude Photo binary contents from JSON
        }

         // init auth events
         def events = appCtx.authenticationService.events // start with defaults
         events.onNewUserObject = { loginID -> User.createSignupInstance(loginID) }
         events.onSignup = { params ->
             params.user.person = params.params.signupForm.invitation.person
             params.user.save(failOnError:true)
         }
//         events.onValidatePassword = { password -> return !appCtx.myDictionaryService.containsWord(password) }

//         environments {
//             development {
                 addJoe(events.onEncodePassword)
                 addJane(events.onEncodePassword)
                 addCoworker(events.onEncodePassword)
                 addGranny()
                 addFujie()
                 addScott()
                 addPhoneOnly()
                 addGenericPeople()
                 addNotifications()
//             }
//         }

        sessionFactory.currentSession.flush()   // because elasticSearchService.index() uses a new session
        elasticSearchService.index()
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
                    middleGivenNames:'Minerva', preferredName:'Jane', honorific:'Ms.', suffix:'Ph.D.',
                    birthDate: java.sql.Date.valueOf('1954-03-25')
            ) {
                photo(fileName:'ben-tea.JPG', contents:BootStrap.class.getResourceAsStream('dev/ben-tea.JPG').bytes)
                connection(type:ConnectionType.HOME) {
                    place {
                        address(streetType:true, postalType:true, line1:'222 Kapiolani Blvd.', city:'Honolulu', state:'HI')
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-1111',
                                level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.HOME)
                    }
                }
                connection(type:ConnectionType.WORK) {
                    place {
                        address(streetType:true, line1:'42 Nuuanu Ave.', city:'Honolulu', state:'HI')
                        address(postalType:true, line1:'P.O.Box 1001', city:'Honolulu', state:'HI')
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-3333',
                                level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.WORK)
                        phoneNumber(type:PhoneNumberType.FAX, number:'555-4444',
                                level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.WORK)
                    }
                    phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-3333 x123',
                            level:CommunicationLinks.Level.DIRECT, connectionType: ConnectionType.WORK)
                    emailAddress(name:'Jane Cool, V.P. Sales', address:'jane@foo.com',
                            level:CommunicationLinks.Level.DIRECT, connectionType: ConnectionType.WORK)
                }
                phoneNumber(type:PhoneNumberType.MOBILE, number:'555-5555',
                        level:CommunicationLinks.Level.PERSONAL, connectionType: null)
                phoneNumber(type:PhoneNumberType.FAX, number:'555-1155',
                        level:CommunicationLinks.Level.PERSONAL, connectionType: null)
                emailAddress(name:'Jane Cool', address:'jane.cool@rr.net',
                        level:CommunicationLinks.Level.PERSONAL, connectionType: null)
                emailAddress(name:'Jane Cool', address:'jane.cool@gmail.com',
                        level:CommunicationLinks.Level.PERSONAL, connectionType: null)
                emailAddress(name:'Jane Cool', address:'jane.cool@facebook.com',
                        level:CommunicationLinks.Level.PERSONAL, connectionType: null)
            }
            settings(dateFormat:'yyyy-MM-dd HH:mm', timeZone:TimeZone.default )
        }
        jane.save(failOnError:true)
    }

    private static addCoworker(Closure passwordEncoder) {
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def jane = builder.user(login:'coworker@example.com', password:passwordEncoder('password')) {
            person(firstGivenName:'Alex', familyName:'McFee', honorific:'Mr.', middleGivenNames:'Trouble',
                    birthDate: java.sql.Date.valueOf('1974-07-04')) {
                photo(fileName:'ben-korea.JPG', contents:BootStrap.class.getResourceAsStream('dev/ben-korea.JPG').bytes)
                connection(type:ConnectionType.WORK) {
                    place {
                        address(streetType:true, line1:'76 Pensicola Ave.', city:'Honolulu', state:'HI')
                        address(postalType:true, line1:'P.O.Box 2002', city:'Honolulu', state:'HI')
                        phoneNumber(type:PhoneNumberType.FAX, number:'555-7777',
                                level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.WORK)
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-6666',
                                level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.WORK)
                    }
                    phoneNumber(type:PhoneNumberType.MOBILE, number:'555-3333 x123',
                            level:CommunicationLinks.Level.DIRECT, connectionType: ConnectionType.WORK)
                    emailAddress(name:'Alex McFee, Engineer', address:'coworker@example.com',
                            level:CommunicationLinks.Level.DIRECT, connectionType: ConnectionType.WORK)
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
                    phoneNumber(type:PhoneNumberType.LANDLINE, number:'555-2222',
                            level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.HOME)
                }
            }
        }
        granny.save(failOnError:true)
    }

    private static addFujie() {
        def builder = new WcyDomainBuilder()
        def email = 'fujie.beutel@example.com'
        builder.classNameResolver = 'com.getsu.wcy'
        def fujie = builder.invitation(email: email) {
            person(firstGivenName:'Fujie', middleGivenNames:'A.', familyName:'Beutel', honorific:'Mrs.') {
                emailAddress(name: 'Fujie Beutel', address: email,
                        level: CommunicationLinks.Level.PERSONAL, connectionType: null)
            }
        }
        fujie.id = 1919     // will be a random number/ticket for security
        fujie.person.save(failOnError:true)
        fujie.save(failOnError:true)
    }

    private static addScott() {
        def builder = new WcyDomainBuilder()
        def email = 'Scott.Robertson@example.com'
        builder.classNameResolver = 'com.getsu.wcy'
        def scott = builder.invitation(email:email) {
            person(firstGivenName:'Scott', familyName:'Robertson', honorific:'Professor', suffix: 'Ph.D.') {
                emailAddress(name:'Scott Roberson', address:email,
                        level:CommunicationLinks.Level.PERSONAL, connectionType: null)
            }
        }
        scott.id = 4242     // will be a random number/ticket for security
        scott.person.save(failOnError:true)
        scott.save(failOnError:true)
    }

    private static addPhoneOnly() { // no User, only Person
        def builder = new WcyDomainBuilder()
        builder.classNameResolver = 'com.getsu.wcy'
        def hal = builder.person(firstGivenName:'Hal', familyName:'Homeless') {
            photo(fileName:'slippers.JPG', contents:BootStrap.class.getResourceAsStream('dev/slippers.JPG').bytes)
            phoneNumber(type:PhoneNumberType.MOBILE, number:'555-8888',
                    level:CommunicationLinks.Level.PERSONAL, connectionType: null)
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
                        phoneNumber(type:PhoneNumberType.LANDLINE, number:"555-$index$index",
                                level:CommunicationLinks.Level.GENERAL, connectionType: ConnectionType.HOME)
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

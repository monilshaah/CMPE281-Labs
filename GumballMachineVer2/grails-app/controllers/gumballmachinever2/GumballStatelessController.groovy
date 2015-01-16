package gumballmachinever2

import gumball.GumballMachine
import org.codehaus.groovy.grails.plugins.codecs.SHA256Codec

class GumballStatelessController {

	def String machineSerialNum = "1234998871109"
	def String machineModelNum = "12345"
	def GumballMachine gumballMachine
	
	def index() {
		
		String VCAP_SERVICES = System.getenv('VCAP_SERVICES')
		
		if (request.method == "GET") {

			// search db for gumball machine
			def gumball = Gumball.findBySerialNumber( machineSerialNum )
			if ( gumball )
			{
				// create a default machine
				gumballMachine = new GumballMachine(gumball.modelNumber, gumball.serialNumber)
				System.out.println(gumballMachine.getAbout())
			}
			else
			{
				gumball = new Gumball()
				gumball.serialNumber = machineSerialNum
				gumball.modelNumber = machineModelNum
				gumball.countGumballs = 5 
				gumball.save(flush: true)
				println '***Gumaball created...!!!'
				gumballMachine = new GumballMachine(gumball.modelNumber, gumball.serialNumber)
				println '***GumballMachine retrieved...'+gumballMachine
				//flash.message = "Error! Gumball Machine Not Found!"
				//render(view: "index")
			}

			// don't save in the session
			// session.machine = gumballMachine
			
			// send machine state to client (instead)
			flash.state = gumballMachine.getCurrentState() ;
			flash.model = gumball.modelNumber ;
			flash.serial = gumball.serialNumber ;
			flash.hash = (flash.state+flash.model+flash.serial).encodeAsSHA256()
			println "***string: "+flash.state+flash.model+flash.serial
			println "***hash: "+flash.hash
			
			// report a message to user
			flash.message = gumballMachine.getAbout()

			// display view
			render(view: "index")

		}
		else if (request.method == "POST") {

			// dump out request object
			request.each { key, value ->
				println( "request: $key = $value")
			}

			// dump out params
			params?.each { key, value ->
				println( "params: $key = $value" )
			}
			
			// don't get machine from session
			// gumballMachine = session.machine

			// restore machine to client state (instead)
			def state = params?.state
			def modelNum = params?.model
			def serialNum = params?.serial
			def hash = params?.hash
			
			def reqHash = (state+modelNum+serialNum).encodeAsSHA256()
			println "string: "+state+modelNum+serialNum
			println "reqHash: "+reqHash
			println "hash   : "+hash
			
			if (hash.equals(reqHash)) {
			gumballMachine = new GumballMachine(modelNum, serialNum) ;
			gumballMachine.setCurrentState(state) ;
			
			System.out.println(gumballMachine.getAbout())
			
			if ( params?.event == "Insert Quarter" )
			{
				gumballMachine.insertCoin()
			}
			if ( params?.event == "Turn Crank" )
			{
				gumballMachine.crankHandle();
				
				if ( gumballMachine.getCurrentState().equals("gumball.CoinAcceptedState") )
				{
					def gumball = Gumball.findBySerialNumber( machineSerialNum )
					if ( gumball )
					{						
						// gumball.lock() // pessimistic lock
						if ( gumball.countGumballs > 0)
							gumball.countGumballs-- ;
						gumball.save(flush: true); // default optimistic lock
					}
				}
				
			}
				gumballMachine._request_status = 'Ok'

			} // hash if ends
			else {
				gumballMachine._request_status = 'Invalid request'
			}
			// send machine state to client
			flash.state = gumballMachine.getCurrentState() ;
			flash.model = modelNum ;
			flash.serial = serialNum ;
			flash.hash = (flash.state+flash.model+flash.serial).encodeAsSHA256()
			
			// report a message to user
			flash.message = gumballMachine.getAbout()

			// render view
			render(view: "index")

		}
		else {
			render(view: "/error")
		}
	}

}


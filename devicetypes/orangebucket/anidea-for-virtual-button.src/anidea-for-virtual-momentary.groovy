/*
 * ---------------------------------------------------------------------------------
 * (C) Graham Johnson (orangebucket)
 *
 * SPDX-License-Identifier: MIT
 * --------------------------------------------------------------------------------- *
 * Anidea for Virtual Momentary
 * =========================
 * Version:	 20.08.09.02 (edited)
 *
 * This device handler implements a virtual button using the Button,
 * Momentary action Contact Sensor, Motion Sensor and Switch.	
 * The capabilities are permanently in place, and the momentary switch action is permanently	
 * enable, but the other two momentary actions are controlled by booleans in the settings	
 * and are not enabled by default.
 */

metadata
{
	definition( name: 'Anidea for Virtual Momentary', namespace: 'orangebucket', author: 'Graham Johnson',
    			ocfDeviceType: 'x.com.st.d.remotecontroller', mnmn: 'SmartThingsCommunity', vid: '18570b53-3a92-34ac-8134-f9c0dc671e5c' )
    {
    	//
	capability 'Button'
        capability 'Momentary'
	capability 'Contact Sensor'	
        capability 'Motion Sensor'	
        capability 'Switch'
	// 
	capability 'Health Check'
        //
        capability 'Actuator'
	capability 'Sensor'
        
	}

	preferences
     {	
        input name: 'momentarycontact', type: 'bool',   title: 'Act as momentary Contact Sensor?', description: 'Enter boolean', required: true	
        input name: 'momentarymotion',  type: 'bool',   title: 'Act as momentary Motion Sensor?',  description: 'Enter boolean', required: true	
        input name: 'momentarydelay',   type: 'number', title: 'Momentary delay in seconds',       description: 'Enter number of seconds (default = 0)', range: '0..60'	
     }
}

// installed() is called when the device is created, and when the device is updated in the IDE.
def installed()
{	
	logger( 'installed', 'info', '' )

    // Health Check is undocumented but this seems to be the common way of creating an untracked
    // device that will appear online when the hub is up.
	sendEvent( name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false )

    // The 'down_6x' attribute value is being used to seed the button attribute. This is something ST
    // seem to do in their handlers, but using 'pushed' seems a bit silly with so many otherwise
    // unused values available.
    def supportedbuttons = [ 'pushed', 'down_6x' ]
    
	sendEvent( name: 'supportedButtonValues', value: supportedbuttons.encodeAsJSON(), displayed: false                      )
	sendEvent( name: 'numberOfButtons',       value: 1,                               displayed: false                      )
    	sendEvent( name: 'button',                value: 'down_6x', 			  displayed: false, isStateChange: true )
	sendEvent( name: 'contact', value: 'closed',   displayed: false )	
    	sendEvent( name: 'motion',  value: 'inactive', displayed: false )	
    	sendEvent( name: 'switch',  value: 'off',      displayed: false )	
}

// updated() seems to be called after installed() when the device is first installed, but not when
// it is updated in the IDE without there having been any actual changes.  It runs whenever settings
// are updated in the mobile app. It often used to be seen running twice in quick succession so was
// debounced in many handlers.
def updated()
{
	logger( 'updated', 'info', '' )
	logger( 'updated', 'debug', 'Momentary Contact Sensor ' + ( momentarycontact ? 'enabled' : 'disabled' ) )	
    	logger( 'updated', 'debug', 'Momentary Motion Sensor '  + ( momentarymotion  ? 'enabled' : 'disabled' ) )
}

def logger( method, level = 'debug', message = '' )
{
	log."${level}" "$device.displayName [$device.name] [${method}] ${message}"
}

// push() is the command for the Momentary capability. Make it press the button once.
def push()
{
	logger( 'push', 'info', '' )
    
    	sendEvent( name: 'button', value: 'pushed', isStateChange: true )
 	
	momentaryactive()	
    	
	    if ( momentarydelay )	
	    {	
		runIn( momentarydelay, momentaryinactive )	
	    }	
	    else	
	    {	
		momentaryinactive()	
	    }	
}	
def on()	
{	
	logger( 'on', 'info', '' )	
    	
    push()	
}	
def off()	
{	
	logger( 'off', 'info', '' )
}

// parse() is called when the hub receives a message from a device.
def parse( String description )
{
    logger( 'parse', 'debug', description )
    
	// Nothing should appear.
}

def momentaryactive()	
{	
	logger( 'momentaryactive', 'info', '' )	
    	
    // Change attributes to the active state.	
    if ( momentarycontact ) sendEvent( name: 'contact', value: 'open'   )	
    if ( momentarymotion  ) sendEvent( name: 'motion',  value: 'active' )	
    sendEvent( name: 'switch',  value: 'on' )	
}	

def momentaryinactive()	
{	
	logger( 'momentaryinactive', 'info', '' )	
	// Return attributes to inactive state.	
	if ( momentarycontact ) sendEvent( name: 'contact', value: 'closed'   )	
    if ( momentarymotion  ) sendEvent( name: 'motion',  value: 'inactive' )	
    sendEvent( name: 'switch',  value: 'off' )	
}

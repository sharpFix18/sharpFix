/* Main.java
   Copyright 2012 Tommy Skodje (http://www.antares.no)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package no.antares.clutil.hitman;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/** Main class for HitMan utility has 3 functions based on command line arguments
 * ( port+message ): send message to hitMan (on port)
 * ( port+cmd ): start hitMan (on port)
 * ( no port ): print help / usage

java -jar "target/clu.hitman-0.6.1-SNAPSHOT-jar-with-dependencies.jar" -port 5555 -msg "HIT ME IN 2"
java -jar "target/clu.hitman-0.6.1-SNAPSHOT-jar-with-dependencies.jar" -port 5555 -cmd "C:\Program Files\Internet Explorer\iexplore.exe"
java -jar "target/clu.hitman-0.6.1-SNAPSHOT-jar-with-dependencies.jar" -port 5555 -cmd /Applications/TextWrangler.app/Contents/MacOS/TextWrangler
 * @author tommy skodje
 */
public class Main {
	private static final Logger logger	= Logger.getLogger( Main.class.getName() );

	/**  */
	public static void main(String[] args) throws Exception {
		CommandLineOptions options	= new CommandLineOptions( args );
		if ( options.portNo != null ) {
			logger.trace( "main() with " + options.toString() );
			if ( ! StringUtils.isBlank( options.command ) ) {
				HitMan.runHitMan( options.portNo, options.command, 5 );
			}
			if ( ! StringUtils.isBlank( options.message ) ) {
				MessageChannel.send( options.portNo, options.message );
			}
		} else {
			options.printHelp( "java -jar HitMan.jar" );
		}
	}

}

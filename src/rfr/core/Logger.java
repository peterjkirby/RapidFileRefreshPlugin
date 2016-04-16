package rfr.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

public class Logger {

	public static void log(int status, String message) {
		Activator activator = Activator.getInstance();
		if (activator == null) return;
		
		ILog log = activator.getLog();
		if (log == null) return;
		
		log.log(new Status(status, Settings.PLUGIN_ID, message));
	}
	
}

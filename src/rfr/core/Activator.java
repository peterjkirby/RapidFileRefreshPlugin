package rfr.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	private static Activator INSTANCE;

	public Activator() {
		super();
		INSTANCE = this;
	}

	public static Activator getInstance() {
		return INSTANCE;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		MonitorManager.getInstance().destroyAll();
	}

}

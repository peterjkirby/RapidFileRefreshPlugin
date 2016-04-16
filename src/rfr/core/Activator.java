package rfr.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	private static Activator INSTANCE;

	public static final String PLUGIN_ID = Settings.PLUGIN_ID;

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
		RefreshMonitorManager.getInstance().destroyAll();
	}

}

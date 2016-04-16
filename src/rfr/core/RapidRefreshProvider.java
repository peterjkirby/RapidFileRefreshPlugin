package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.resources.refresh.RefreshProvider;
import org.eclipse.core.runtime.IStatus;

public class RapidRefreshProvider extends RefreshProvider {
	
	private MonitorEvaluator evaluator = new MonitorEvaluator();
	
	@Override
	public IRefreshMonitor installMonitor(IResource resource, IRefreshResult result) {
		if (!evaluator.canMonitor(resource)) return null;
							
		Integer refreshInterval = PropertyExtractor.getInteger(Settings.REFRESH_INTERVAL_PROP, resource);
				
		RapidRefreshMonitor monitor = RefreshMonitorManager
				.getInstance()
				.create(resource, result, refreshInterval);
		
		if (refreshInterval != null) {
			Logger.log(IStatus.INFO, "Starting monitor for resource: " + resource.getName());
			monitor.start();			
		}
		return monitor;
	}

	@Override
	public void resetMonitors(IResource resource) {
		if (!evaluator.canMonitor(resource)) return;
		
		RapidRefreshMonitor monitor = RefreshMonitorManager.getInstance().getMonitor(resource);
		
		if (monitor == null) return;
		
		if (monitor.isStarted()) {
			monitor.stop();
			monitor.start();	
		}		
	}
	
	

}

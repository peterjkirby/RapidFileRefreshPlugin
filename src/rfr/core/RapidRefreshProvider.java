package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.resources.refresh.RefreshProvider;
import org.eclipse.core.runtime.IStatus;

public class RapidRefreshProvider extends RefreshProvider {
	
	private MonitorEvaluator evaluator = new MonitorEvaluator();
		
	@Override
	protected IRefreshMonitor createPollingMonitor(IResource resource) {
		Logger.log(IStatus.INFO, "-------SOMEBODY REQUESTED A POLLING MONITOR BE CREATED! " + resource.getName());
		return null;
	}

	@Override
	public IRefreshMonitor installMonitor(IResource resource, IRefreshResult result) {
		if (!evaluator.canMonitor(resource)) return null;
							
		Integer refreshInterval = PropertyExtractor.getInteger(Settings.REFRESH_INTERVAL_PROP, resource);
				
		RapidRefreshMonitor monitor = MonitorManager
				.getInstance()
				.create(resource, new RefreshManager(result), refreshInterval);
		
		if (refreshInterval != null && refreshInterval != 0) {
			monitor.start();			
		}
		
		return monitor;
	}

	@Override
	public void resetMonitors(IResource resource) {
		if (!evaluator.canMonitor(resource)) return;
						
		if (evaluator.shouldMonitor(resource)) {
			Integer refreshInterval = PropertyExtractor.getInteger(Settings.REFRESH_INTERVAL_PROP, resource);
			MonitorManager
				.getInstance()
				.restart(resource, refreshInterval);	
		} else {
			MonitorManager
				.getInstance()
				.stop(resource);
		}
		
			
	}
	
	

}

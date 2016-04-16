package rfr.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.runtime.IStatus;

public class RefreshMonitorManager {

	private static RefreshMonitorManager INSTANCE = new RefreshMonitorManager();
	
	private IRefreshResult result;

	private Map<String, RapidRefreshMonitor> monitorMap = new ConcurrentHashMap<>();

	public RapidRefreshMonitor getMonitor(IResource resource) {
		return monitorMap.get(resource.getName());
	}

	public RapidRefreshMonitor create(IResource resource, IRefreshResult result, int refreshInterval) {
		Logger.log(IStatus.INFO, "Creating monitor for resource: " + resource.getName());
		String name = resource.getName();
		if (monitorMap.containsKey(name))
			throw new IllegalStateException("RapidResourceMonitor already exists under this name: " + name);

		RapidRefreshMonitor monitor = new RapidRefreshMonitor(resource, result, refreshInterval);
		monitorMap.put(name, monitor);

		return monitor;
	}

	public static RefreshMonitorManager getInstance() {
		return INSTANCE;
	}

	public void restart(IResource resource, int refreshInterval) {
		RapidRefreshMonitor monitor = monitorMap.get(resource.getName());
		if (monitor == null) return;

		Logger.log(IStatus.INFO, "Restarting monitor for resource: " + resource.getName());

		monitor.stop();
		monitor.setRefreshInterval(refreshInterval);
		
		monitor.start(resource);
	}

	public void stop(IResource resource) {
		RapidRefreshMonitor monitor = monitorMap.get(resource.getName());
		if (monitor != null) {
			Logger.log(IStatus.INFO, "Stopping monitor for resource: " + resource.getName());
			monitor.stop();
		}
	}

	public void stopAll() {
		Collection<RapidRefreshMonitor> monitors = monitorMap.values();

		for (RapidRefreshMonitor monitor : monitors) {
			monitor.stop();
		}
	}
	
	public void destroyAll() {
		stopAll();
		monitorMap.clear();
	}

	public IRefreshResult getResult() {
		return result;
	}

	public void setResult(IRefreshResult result) {
		this.result = result;
	}

}

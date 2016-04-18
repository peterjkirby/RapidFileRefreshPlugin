package rfr.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;

public class MonitorManager {

	private static MonitorManager INSTANCE = new MonitorManager();
		
	private Map<String, RapidRefreshMonitor> monitorMap = new ConcurrentHashMap<>();

	public RapidRefreshMonitor getMonitor(IResource resource) {
		return monitorMap.get(resource.getName());
	}

	/**
	 * Creates a new RapidRefreshMonitor to monitor the specified resource. 
	 * 
	 * In the case where refreshInterval is null, a RapidRefreshMonitor will be created for the resource, but
	 * polling will not be started. If settings are changed at a later time, polling may begin. 
	 *  
	 * @param resource
	 * @param refreshManager
	 * @param refreshInterval a refresh interval in milliseconds or null
	 * @return
	 */
	public RapidRefreshMonitor create(IResource resource, RefreshManager refreshManager, Integer refreshInterval) {
		if (resource == null)
			throw new IllegalArgumentException("resource cannot be null");
		
		if (refreshManager == null)
			throw new IllegalArgumentException("refreshManager cannot be null");
				
		Logger.log(IStatus.INFO, "Creating monitor for resource: " + resource.getName());
		String name = resource.getName();
		if (monitorMap.containsKey(name))
			throw new IllegalStateException("RapidResourceMonitor already exists under this name: " + name);

		RapidRefreshMonitor monitor = new RapidRefreshMonitor(resource, refreshManager, refreshInterval);
		monitorMap.put(name, monitor);

		return monitor;
	}

	public static MonitorManager getInstance() {
		return INSTANCE;
	}

	/**
	 * @see MonitorManager.restart(IResource, Integer)
	 * @param resource
	 */
	public void restart(IResource resource) {
		restart(resource, null);
	}
	
	/**
	 * Restarts the monitor associated with the specified resource.
	 * If refreshInterval is not null, will update the monitors refreshInterval
	 * 
	 * If no monitor is associated with the specified resource, does nothing.
	 *  
	 * 
	 * @param resource
	 * @param refreshInterval
	 */
	public void restart(IResource resource, Integer refreshInterval) {
		RapidRefreshMonitor monitor = monitorMap.get(resource.getName());
		if (monitor == null) return;
		
		Logger.log(IStatus.INFO, "Restarting monitor for resource: " + resource.getName());
		
		// don't restart a monitor that was never started
		if (monitor.isStarted()) {
			monitor.stop();	
		}
		
		if (refreshInterval != null)
			monitor.setRefreshInterval(refreshInterval);
		
		monitor.start();
	}

	public void stop(IResource resource) {
		RapidRefreshMonitor monitor = monitorMap.get(resource.getName());
		if (monitor != null) {
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

}

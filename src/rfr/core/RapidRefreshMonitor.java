package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.runtime.IStatus;

public class RapidRefreshMonitor implements IRefreshMonitor {

	// wait INITIAL_DELAY seconds before starting to track changes
	// in order to allow eclipse to startup faster.
	private static final int INITIAL_DELAY = 1000 * 30;
	private ProjectMonitorJob job;
	private IResource resource;
	private IRefreshResult result;
	private Integer refreshInterval;
	private boolean started = false;

	public RapidRefreshMonitor(IResource resource, IRefreshResult result, Integer refreshIntervalSeconds) {
		this.resource = resource;
		this.result = result;
		if (refreshIntervalSeconds != null)
			this.refreshInterval = refreshIntervalSeconds * 1000;
	}

	public void start() {
		if (started)
			throw new IllegalStateException("RefreshMonitor already started for resource: " + resource.getName());
		
		// don't start if no interval has been set
		if (refreshInterval == null) return;
		
		Logger.log(IStatus.INFO, "Starting monitor for resource: " + resource.getName());
		started = true;
		job = new ProjectMonitorJob(resource, result, refreshInterval);
		job.schedule(INITIAL_DELAY);
	}

	public void start(IResource resource) {
		this.resource = resource;
		start();
	}

	public void stop() {
		if (!started) return;
		Logger.log(IStatus.INFO, "Stopping monitor for resource: " + resource.getName());
		started = false;
		job.cancel();
	}

	public boolean isStarted() {
		return started;
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
		if (job != null)
			job.setRefreshInterval(refreshInterval);
	}

	@Override
	public void unmonitor(IResource resource) {
		if (job != null)
			job.cancel();
	}

}

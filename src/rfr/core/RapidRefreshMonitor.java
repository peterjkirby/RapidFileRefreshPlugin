package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.runtime.IStatus;

public class RapidRefreshMonitor implements IRefreshMonitor {

	// wait INITIAL_DELAY seconds before starting to track changes
	// in order to allow eclipse to startup faster.
	private static final int INITIAL_DELAY = 1000 * 10;
	private static final int SECOND_START_DELAY = 1000;
	
	private ProjectMonitorJob job;
	private IResource resource;
	private RefreshManager refreshManager;
	private boolean firstStart = true;
	
	private Integer refreshInterval;
	private boolean started = false;

	public RapidRefreshMonitor(IResource resource, RefreshManager refreshManager, Integer refreshIntervalMillis) {
		this.resource = resource;
		this.refreshManager = refreshManager;
		if (refreshIntervalMillis != null)
			this.refreshInterval = refreshIntervalMillis;
	}

	public void start() {
		if (started)
			throw new IllegalStateException("RefreshMonitor already started for resource: " + resource.getName());
		
		
		// don't start if no interval has been set
		if (refreshInterval == null) return;
		
		// don't start the job if the refresh interval is 0 - 0 indicates disabled.
		if (refreshInterval == 0) return;
		
		Logger.log(IStatus.INFO, "Starting monitor for resource: " + resource.getName());
		started = true;
		job = new ProjectMonitorJob(resource, refreshManager, refreshInterval);
		
		if (firstStart) {
			job.schedule(INITIAL_DELAY);
		} else {
			job.schedule(SECOND_START_DELAY);
		}
		
		firstStart = false;
	}

	public void stop() {
		if (!started) return;
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
		
		started = false;
	}

}

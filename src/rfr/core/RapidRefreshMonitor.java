package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;

public class RapidRefreshMonitor implements IRefreshMonitor {

	// wait INITIAL_DELAY seconds before starting to track changes 
	// in order to allow eclipse to startup faster.
	private static final int INITIAL_DELAY = 1000 * 20;
	private ProjectMonitorJob job;
	private IResource resource;
	private IRefreshResult result;
	
	public RapidRefreshMonitor(IResource resource, IRefreshResult result) {
		this.resource = resource;
		this.result = result;		
	}
	
	public void start() {
		job = new ProjectMonitorJob(resource, result);
		job.schedule(INITIAL_DELAY);
	}

	
	@Override
	public void unmonitor(IResource resource) {
		job.stop();		
	}

}

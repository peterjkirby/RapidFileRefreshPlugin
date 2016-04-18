package rfr.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class MonitorEvaluator {

	/**
	 * Returns true if we are capable of monitoring the resource and should
	 * create a RefreshMonitor to do so
	 * 
	 * This does not mean that we should monitor it.
	 * @param resource
	 * @return
	 */
	public boolean canMonitor(IResource resource) {
		if (resource == null) return false;
		if (resource.getType() != IResource.PROJECT) return false;
		
		return true;
	}
	
	/**
	 * Returns true if we can monitor the resource and 
	 * the RapidResourceRefresh properties for the resource meet
	 * the conditions necessary for polling to occur.
	 * 
	 * @param resource
	 * @return
	 */
	public boolean shouldMonitor(IResource resource) {
		if (resource == null) return false;
		if (resource.getType() != IResource.PROJECT) return false;
		
		IProject project = (IProject) resource;
			
		Integer refreshInterval = PropertyExtractor.getInteger(Settings.REFRESH_INTERVAL_PROP, project);
		
		if (refreshInterval == null) return false;
		
		if (refreshInterval == 0) return false;
		
		return true;
	}
	
}

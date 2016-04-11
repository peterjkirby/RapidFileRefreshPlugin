package rfr.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.resources.refresh.RefreshProvider;

public class RapidRefreshProvider extends RefreshProvider {

	// add project names here to monitor them
	private static final String[] PROJECTS_NAMES = {
		"YOUR_PROJECT_NAME"
	};
	
	private Set<String> projects = new HashSet<>();
	
	public RapidRefreshProvider() {
		projects.addAll(Arrays.asList(PROJECTS_NAMES));
	}
	
	
	@Override
	public IRefreshMonitor installMonitor(IResource resource, IRefreshResult result) {
		
		// only monitor resources that are projects
		if (resource.getType() != IResource.PROJECT) return null;
		
		IProject project = (IProject) resource;
		
		// only monitor the projects in PROJECT_NAMES
		if (!projects.contains(project.getName())) return null; 
						
		RapidRefreshMonitor monitor = new RapidRefreshMonitor(resource, result);
		monitor.start();
		
		return monitor;
	}

}

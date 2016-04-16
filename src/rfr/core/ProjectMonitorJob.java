package rfr.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class ProjectMonitorJob extends Job {

	private static final String JOB_NAME = "ProjectMonitorJob";

	// add folders here to monitor them (folder that exist within the projects
	// you are monitoring)
	private static final String[] FOLDERS = {
			"src/main/webapp/resources"
	};

	private IResource resource;
	private IRefreshResult result;
	private int refreshInterval;

	public ProjectMonitorJob(IResource resource, IRefreshResult result, int refreshInterval) {
		super(Activator.PLUGIN_ID + " - " + JOB_NAME);
		this.resource = resource;
		this.result = result;
		this.refreshInterval = refreshInterval;

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		
		IProject project = (IProject) resource;

		for (String folderPath : FOLDERS) {
			if (monitor.isCanceled()) return Status.CANCEL_STATUS;
			monitor(project, folderPath, monitor);
		}

		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		} else {
			// schedule the next run
			schedule(refreshInterval);
			return Status.OK_STATUS;
		}	
	}

	private void monitor(IProject project, String folderPath, IProgressMonitor monitor) {
		IFolder folder = project.getFolder(new Path(folderPath));

		if (folder.exists()) {
			ResourceTraverser traverser = new ResourceTraverser(new ResourceChangeEvaluator(), monitor);

			try {
				traverser.traverse(folder, result);
			} catch (CoreException e) {
			}
		}
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	
	

}

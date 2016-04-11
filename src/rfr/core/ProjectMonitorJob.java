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
	private static final String[] FOLDERS = {
			"src/main/webapp/resources"
	};
	// fires about 3 times per second.
	private static final int DELAY = 350;
	private boolean RUNNING = true;
	
	private IResource resource;
	private IRefreshResult result;

	public ProjectMonitorJob(IResource resource, IRefreshResult result) {
		super(Activator.PLUGIN_ID + " - " + JOB_NAME);
		this.resource = resource;
		this.result = result;

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (RUNNING) {

			IProject project = (IProject) resource;

			for (String folderPath : FOLDERS) {
				monitor(project, folderPath);
			}
			
			// schedule the next run
			schedule(DELAY);
		}

		return Status.OK_STATUS;
	}
	
	private void monitor(IProject project, String folderPath) {
		IFolder folder = project.getFolder(new Path(folderPath));
		
		if (folder.exists()) {
			ResourceTraverser traverser = new ResourceTraverser(new ResourceChangeEvaluator());

			try {
				traverser.traverse(folder, result);
			} catch (CoreException e) {}
		}
	}

	public void stop() {
		RUNNING = false;
	}

}

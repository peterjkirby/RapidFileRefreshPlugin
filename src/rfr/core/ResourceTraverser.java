package rfr.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ResourceTraverser {

	private ResourceChangeEvaluator resourceChangeEvaluator;
	private IProgressMonitor monitor;

	public ResourceTraverser(ResourceChangeEvaluator resourceChangeEvaluator, IProgressMonitor monitor) {
		this.resourceChangeEvaluator = resourceChangeEvaluator;
		this.monitor = monitor;
	}

	public IStatus traverse(IFolder folder, RefreshManager refreshManager) throws CoreException {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				
		if (resourceChangeEvaluator.changed(folder)) {
			refreshManager.refresh(folder);
			Logger.log(IStatus.INFO, "Folder/File change detected. Refreshing folder: " + folder.getName());
			return Status.OK_STATUS;
		}

		IResource[] contents = folder.members();
		for (IResource resource : contents) {
			if (monitor.isCanceled()) return Status.CANCEL_STATUS;
			if (resource.getType() == IResource.FOLDER) {
				IStatus status = traverse((IFolder) resource, refreshManager);
				if (status == Status.CANCEL_STATUS) return status;				
			}
		}
		
		return Status.OK_STATUS;
	}

}

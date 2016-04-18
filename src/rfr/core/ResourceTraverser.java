package rfr.core;

import org.eclipse.core.resources.IFile;
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
		
		IResource[] contents = folder.members();
		
		if (resourceChangeEvaluator.changed(folder)) {
			refreshManager.refresh(folder);
			return Status.OK_STATUS;
		}

		for (IResource resource : contents) {
			if (monitor.isCanceled()) return Status.CANCEL_STATUS;
			
			if (resource.getType() == IResource.FILE) {
				IFile file = (IFile) resource;

				if (resourceChangeEvaluator.changed(file)) {
					refreshManager.refresh(file);
					Logger.log(IStatus.INFO, "File change detected. Refreshing " + file.getName());
				}

			} else if (resource.getType() == IResource.FOLDER) {
				IFolder subfolder = (IFolder) resource;

				// only continue traversing if a folder has not changed. If a
				// folder has changed
				// the refresh event at that folder should be enough to force
				// synchronization
				// Plus, other changes at a depth > 1 will be detected on the
				// next pass.
				// This is more a performance optimization than anything else.
				if (resourceChangeEvaluator.changed(subfolder)) {
					refreshManager.refresh(subfolder);
					Logger.log(IStatus.INFO, "Folder change detected. Refreshing " + subfolder.getName());
				} else {
					return traverse((IFolder) resource, refreshManager);
				}
			}
		}
		
		return Status.OK_STATUS;
	}

}

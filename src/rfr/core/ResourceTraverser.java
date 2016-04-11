package rfr.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ResourceTraverser {

	private ResourceChangeEvaluator resourceChangeEvaluator;

	public ResourceTraverser(ResourceChangeEvaluator resourceChangeEvaluator) {
		this.resourceChangeEvaluator = resourceChangeEvaluator;
	}

	public void traverse(IFolder folder, IRefreshResult refreshResult) throws CoreException {

		IResource[] contents = folder.members();
		Activator activator = Activator.getInstance();

		if (resourceChangeEvaluator.changed(folder)) {
			refreshResult.refresh(folder);
		}

		for (IResource resource : contents) {
			if (resource.getType() == IResource.FILE) {
				IFile file = (IFile) resource;

				if (resourceChangeEvaluator.changed(file)) {
					refreshResult.refresh(file);
					activator.getLog().log(
							new Status(IStatus.INFO,
									Activator.PLUGIN_ID,
									"File change detected. Refreshing " + file.getName()));
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
					refreshResult.refresh(subfolder);
					activator.getLog().log(
							new Status(IStatus.INFO,
									Activator.PLUGIN_ID,
									"Folder change detected. Refreshing " + subfolder.getName()));
				} else {
					traverse((IFolder) resource, refreshResult);
				}
			}
		}
	}

}

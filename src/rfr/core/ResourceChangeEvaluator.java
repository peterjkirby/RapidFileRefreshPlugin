package rfr.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ResourceChangeEvaluator {

	public ResourceChangeEvaluator() {}		
		
	public boolean changed(IFolder folder) {
		if (folder == null) return false;
		if (!folder.exists()) return false;
		return !folder.isSynchronized(IResource.DEPTH_ZERO);
	}
	
	public boolean changed(IFile file) throws CoreException {
		if (file == null) return false;
		if (!file.exists()) return false;
		if (file.isSynchronized(IResource.DEPTH_ZERO)) return false;
		
		return true;
	}
	
}

package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshResult;

public class RefreshManager {
	private IRefreshResult result;
	
	public RefreshManager(IRefreshResult result) {
		this.result = result;
	}
	
	public void refresh(IResource resource) {
		result.refresh(resource);
	}
}

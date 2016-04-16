package rfr.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class PropertyExtractor {

	public static Integer getInteger(String name, IResource resource) {
		try {
			String prop = resource.getPersistentProperty(new QualifiedName("", name));
			if (prop != null)
				return Integer.valueOf(prop);
		} catch (CoreException e) {
			
		}
		
		return null;
	}
	
}

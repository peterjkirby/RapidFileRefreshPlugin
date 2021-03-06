package rfr.core.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import rfr.core.MonitorEvaluator;
import rfr.core.MonitorManager;
import rfr.core.PropertyExtractor;
import rfr.core.Settings;

public class RfrPropertiesPage extends PropertyPage {

	private static final String OWNER_TITLE = "Resource polling interval (0 = disabled)";
	
	
	private static final int TEXT_FIELD_WIDTH = 50;
	private MonitorEvaluator evaluator = new MonitorEvaluator();
	private Text ownerText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public RfrPropertiesPage() {
		super();
	}
	
	private IProject getProject() {
		IJavaProject jProject = (IJavaProject) getElement();
		IProject project = jProject.getProject();
		
		return project;
	}
	
	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText(OWNER_TITLE);

		// Owner text field
		ownerText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		ownerText.setLayoutData(gd);

		// Populate owner text field
		try {
			String refreshDelay =
					getProject().getPersistentProperty(
					new QualifiedName("", Settings.REFRESH_INTERVAL_PROP));
			ownerText.setText((refreshDelay != null) ? refreshDelay : Settings.DEFAULT_REFRESH_INTERVAL);
		} catch (CoreException e) {
			ownerText.setText(Settings.DEFAULT_REFRESH_INTERVAL);
		}
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addSecondSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		super.performDefaults();
		// Populate the owner text field with the default value
		ownerText.setText(Settings.DEFAULT_REFRESH_INTERVAL);
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
			getProject().setPersistentProperty(
				new QualifiedName("", Settings.REFRESH_INTERVAL_PROP),
				ownerText.getText());
				
			if (evaluator.shouldMonitor(getProject())) {
				MonitorManager.getInstance()
					.restart(getProject(), PropertyExtractor.getInteger(Settings.REFRESH_INTERVAL_PROP, getProject()));	
			} else {
				MonitorManager.getInstance()
					.stop(getProject());
			}
			
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
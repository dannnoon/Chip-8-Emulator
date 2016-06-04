package gui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PropertiesPanel extends JPanel {

	private static final String TITLE = "Properties";

	private static final String TAB_TITLE_EMULATION = "Emulation";

	Properties properties;

	JPanel thisPanel;
	JPanel emulationTab;

	JTabbedPane propertiesTabPane;

	public PropertiesPanel() {
		thisPanel = this;

		propertiesTabPane = new JTabbedPane();

		initializeEmulationTab();

		propertiesTabPane.addTab(TAB_TITLE_EMULATION, emulationTab);
	}
	
	private void initializeEmulationTab() {
		emulationTab = new JPanel();
		
		
	}
}

package confdb.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import confdb.gui.treetable.CheckBoxTableCellRenderer;

import confdb.data.*;

/**
 * MessageLoggerDialog
 * ------------------
 * @author Vasundhar Chetluru
 *
 * Editor to helps customize Message Logger
 * 
 */
public class MessageLoggerDialog extends JDialog {

	//
	// member data
	//

	private ArrayList<MessageLoggerPanel> messageLoggerPanels = new ArrayList<MessageLoggerPanel>();

	private Configuration config;
	private ServiceInstance serviceMessage;
	private VStringParameter parameterDestination;

	private JButton jButtonCancel = new JButton();
	private JButton jButtonApply = new JButton();
	private JButton jButtonOK = new JButton();

	private JButton jButtonAdd = new JButton();
	private JButton jButtonRemoveCurrent = new JButton();

	private JTabbedPane jTabbedPaneMessageOutStreams = new JTabbedPane();

	// private MessageLoggerPanel []MessageLoggerPanelOutStreams=new
	// MessageLoggerPanel[100];
	private int iMessageLoggerCount;

	//
	// construction
	//

	/** constructor */
	public MessageLoggerDialog(JFrame jFrame, Configuration config) {
		super(jFrame, true);
		this.config = config;

		jButtonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonCancelActionPerformed(e);
			}
		});
		jButtonApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonApplyActionPerformed(e);
			}
		});
		jButtonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonOKActionPerformed(e);
			}
		});
		jButtonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonAddActionPerformed(e);
			}
		});

		jButtonRemoveCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonRemoveCurrentActionPerformed(e);
			}
		});

		setContentPane(initComponents());
		if (!initMessageLogger())
			setVisible(false);
	}

	//
	// private member functions
	//

	// listener callbacks

	private void jButtonCancelActionPerformed(ActionEvent e) {
		setVisible(false);
	}

	private void jButtonApplyActionPerformed(ActionEvent e) {
		commitChanges();
	}

	private void jButtonOKActionPerformed(ActionEvent e) {
		commitChanges();
		setVisible(false);
	}

	private void jButtonAddActionPerformed(ActionEvent e) {

		String stringMesLogDestinationName;
		while (true) {
			stringMesLogDestinationName = JOptionPane.showInputDialog("Enter destination ");
			System.out.println(stringMesLogDestinationName);
			if (stringMesLogDestinationName == null)
				return;
			if (stringMesLogDestinationName.matches("[a-zA-Z0-9]+"))
				break;
			else
				JOptionPane.showMessageDialog(null, "Please enter alpha-numeric charecters for the destination name.",
						"Warning", JOptionPane.ERROR_MESSAGE);

		}

		if (stringMesLogDestinationName.equals("") || stringMesLogDestinationName.equals("MessageLogger"))
			return;
		int iDestVestorSize = parameterDestination.vectorSize();
		for (int i = 0; i < iDestVestorSize; i++) {
			String strCurrent = (String) parameterDestination.value(i);
			if (strCurrent.equals(stringMesLogDestinationName)) {
				return;
			}
		}

		AddMessageLoggerDestination(stringMesLogDestinationName, true);
		String strCurValueOfDestination = parameterDestination.valueAsString();
		strCurValueOfDestination = strCurValueOfDestination + ",\"" + stringMesLogDestinationName + "\"";
		parameterDestination.setValue(strCurValueOfDestination);
		strCurValueOfDestination = parameterDestination.valueAsString();
	}

	private void jButtonRemoveCurrentActionPerformed(ActionEvent e) {
		int iTemp = jTabbedPaneMessageOutStreams.getSelectedIndex();
		MessageLoggerPanel messageLoggerPanelRemove = (MessageLoggerPanel) jTabbedPaneMessageOutStreams
				.getSelectedComponent();
		if (!serviceMessage.removeUntrackedParameter(messageLoggerPanelRemove.pSetDestination))
			return;

		jTabbedPaneMessageOutStreams.setSelectedIndex(iTemp - 1);

		int iDestVestorSize = parameterDestination.vectorSize();
		for (int i = 0; i < iDestVestorSize; i++) {
			String strCurrent = (String) parameterDestination.value(i);
			if (strCurrent.equals(messageLoggerPanelRemove.strDestination)) {
				parameterDestination.removeValue(i);
				break;
			}
		}
		jTabbedPaneMessageOutStreams.removeTabAt(iTemp);
		messageLoggerPanels.remove(iTemp);
		if (messageLoggerPanels.size() > 1)
			jButtonRemoveCurrent.setVisible(true);
		else
			jButtonRemoveCurrent.setVisible(false);

	}

	private void AddMessageLoggerDestination(String strDestination, Boolean bCreateNew) {
		MessageLoggerPanel messageLoggerPanelOutStream = new MessageLoggerPanel(config, serviceMessage, strDestination,
				bCreateNew);
		if (strDestination.equals(""))
			jTabbedPaneMessageOutStreams.addTab("Default", messageLoggerPanelOutStream);
		else
			jTabbedPaneMessageOutStreams.addTab(strDestination, messageLoggerPanelOutStream);
		messageLoggerPanels.add(messageLoggerPanelOutStream);

	}

	private void commitChanges() {
		for (MessageLoggerPanel messageLoggerPanelOutStream : messageLoggerPanels) {
			messageLoggerPanelOutStream.commitTransient();
		}
	}

	private boolean initMessageLogger() {

		serviceMessage = config.service("MessageLogger");
		if (serviceMessage == null)
			return false;
		AddMessageLoggerDestination("", false);
		Iterator<Parameter> itP = serviceMessage.parameterIterator();
		ArrayList<Parameter> alP = new ArrayList<Parameter>();
		Parameter.getParameters(itP, alP);
		for (Parameter parameter : alP) {
			String parameterName = parameter.name();
			String parameterType = parameter.type();
			if (parameterName.equals("destinations")) {
				parameterDestination = (VStringParameter) parameter;
				for (int i = 0; i < parameterDestination.vectorSize(); i++) {
					String strDestination = (String) parameterDestination.value(i);
					if (strDestination.equals(""))
						continue;
					AddMessageLoggerDestination(strDestination, false);
				}
				return true;
			}
		}

		/* instantiate destinations parameter here */
		return true;
	}

	/** init GUI components */
	private JPanel initComponents() {

		JPanel jPanel = new JPanel();
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel);
		jPanel.setLayout(layout);
		jTabbedPaneMessageOutStreams = new JTabbedPane();

		jButtonOK.setText("OK");
		jButtonApply.setText("Apply");
		jButtonCancel.setText("Cancel");

		jButtonAdd.setText("Add Destination");
		jButtonRemoveCurrent.setText("Remove Destination");

		jPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jTabbedPaneMessageOutStreams, javax.swing.GroupLayout.DEFAULT_SIZE, 708,
										Short.MAX_VALUE)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup().addComponent(jButtonCancel)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(jButtonApply)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(jButtonOK)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(jButtonAdd)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(jButtonRemoveCurrent)))

						.addContainerGap()));

		layout.linkSize(SwingConstants.HORIZONTAL,
				new java.awt.Component[] { jButtonApply, jButtonCancel, jButtonOK, jButtonAdd, jButtonRemoveCurrent });

		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(22)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)

								.addComponent(jTabbedPaneMessageOutStreams, javax.swing.GroupLayout.DEFAULT_SIZE, 570,
										Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jButtonAdd).addComponent(jButtonRemoveCurrent)
										.addComponent(jButtonOK).addComponent(jButtonCancel).addComponent(jButtonApply))
								.addContainerGap()));
		jTabbedPaneMessageOutStreams.setBackground(new java.awt.Color(255, 255, 255));
		return jPanel;

	}

}

class MessageLoggerPanel extends JPanel {

	//
	// member data
	//

	/** reference to the configuration */
	private Configuration config;

	private ServiceInstance serviceMessageLogger;

	private VStringParameter parameterSupressInfo;
	private VStringParameter parameterSupressDebug;
	private VStringParameter parameterSupressWarning;
	private VStringParameter parameterSupressError;

	StringParameter parameterThreshold;
	StringParameter parameterLimit;
	StringParameter parameterSpan;

	PSetParameter pSetDestination;

	/** current output module being edited */
	private ModuleInstance outputModule = null;

	/** products */
	private HashMap<String, ProductLogger> products = new HashMap<String, ProductLogger>();

	ArrayList<String> thresholdsArrayList = new ArrayList<String>();
	ArrayList<String> limitArrayList = new ArrayList<String>();
	ArrayList<String> spanArrayList = new ArrayList<String>();

	/** GUI components */

	private JComboBox jComboBoxThreshold = new JComboBox();
	private JComboBox jComboBoxLimit = new JComboBox();
	private JComboBox jComboBoxTimeSpan = new JComboBox();
	private JTable jTableProducts = new JTable();

	private JCheckBox jCheckBoxInfo = new JCheckBox();
	private JCheckBox jCheckBoxDebug = new JCheckBox();
	private JCheckBox jCheckBoxWarning = new JCheckBox();
	private JCheckBox jCheckBoxError = new JCheckBox();

	private JCheckBox jCheckBoxShowSelected = new JCheckBox();

	/** GUI models */
	private ProductTableLoggerModel tmProducts;
	private DefaultComboBoxModel cbmOutputThreshold;
	private DefaultComboBoxModel cbmOutputLimit;
	private DefaultComboBoxModel cbmOutputTimeSpan;
	private Boolean bSupMessage = false;
	private Boolean bSupDebug = false;
	private Boolean bSupError = false;

	String strDestination;

	//
	// construction
	//

	/** standard constructor */
	public MessageLoggerPanel(Configuration config, ServiceInstance serviceMessageLogger, String strDestination,
			Boolean bCreateNew) {

		this.config = config;
		this.serviceMessageLogger = serviceMessageLogger;
		this.strDestination = strDestination;

		initGuiTransientData();
		initGuiComponents();

		Iterator<Parameter> parameterDestination;

		if (bCreateNew) {
			serviceMessageLogger.updateParameter(strDestination, "PSet", "");
			pSetDestination = (PSetParameter) serviceMessageLogger.parameter(strDestination);
			parameterDestination = pSetDestination.parameterIterator();

		} else {

			if (strDestination.equals("")) {
				parameterDestination = serviceMessageLogger.parameterIterator();
			} else {
				pSetDestination = (PSetParameter) serviceMessageLogger.parameter(strDestination);
				parameterDestination = pSetDestination.parameterIterator();
			}
		}

		while (parameterDestination.hasNext()) {

			Parameter param = parameterDestination.next();
			String parameterName = param.name();
			String parameterType = param.type();

			if (parameterName.equals("threshold")) {
				String strThreshold = (String) param.valueAsString();
				strThreshold = strThreshold.replace("\"", "");
				for (int iJ = 0; iJ < jComboBoxThreshold.getItemCount(); iJ++) {
					if (((String) jComboBoxThreshold.getItemAt(iJ)).equals(strThreshold)) {
						jComboBoxThreshold.setSelectedIndex(iJ);
						break;
					}
				}
				parameterThreshold = (StringParameter) param;
			}

			if (parameterName.equals("limit")) {
				String strLimit = (String) param.valueAsString();
				strLimit = strLimit.replace("\"", "");
				for (int iJ = 0; iJ < jComboBoxLimit.getItemCount(); iJ++) {
					if (((String) jComboBoxLimit.getItemAt(iJ)).equals(strLimit)) {
						jComboBoxLimit.setSelectedIndex(iJ);
						break;
					}
				}
				parameterLimit = (StringParameter) param;
			}

			if (parameterName.equals("timespan")) {
				String strSpan = (String) param.valueAsString();
				strSpan = strSpan.replace("\"", "");
				for (int iJ = 0; iJ < jComboBoxTimeSpan.getItemCount(); iJ++) {
					if (((String) jComboBoxTimeSpan.getItemAt(iJ)).equals(strSpan)) {
						jComboBoxTimeSpan.setSelectedIndex(iJ);
						break;
					}
				}
				parameterSpan = (StringParameter) param;
			}

			if (parameterName.equals("suppressInfo")) {
				parameterSupressInfo = (VStringParameter) param;
				for (int i = 0; i < parameterSupressInfo.vectorSize(); i++) {
					String strSupressInfo = (String) parameterSupressInfo.value(i);
					tmProducts.selectRowColumn(products, strSupressInfo, 1);
				}
			}

			if (parameterName.equals("suppressDebug")) {
				parameterSupressDebug = (VStringParameter) param;
				for (int i = 0; i < parameterSupressDebug.vectorSize(); i++) {
					String strSupressDebug = (String) parameterSupressDebug.value(i);
					tmProducts.selectRowColumn(products, strSupressDebug, 2);
				}
			}
			if (parameterName.equals("suppressWarning")) {
				parameterSupressWarning = (VStringParameter) param;
				for (int i = 0; i < parameterSupressWarning.vectorSize(); i++) {
					String strSupressWarning = (String) parameterSupressWarning.value(i);
					tmProducts.selectRowColumn(products, strSupressWarning, 3);
				}
			}
			if (parameterName.equals("suppressError")) {
				parameterSupressError = (VStringParameter) param;
				for (int i = 0; i < parameterSupressError.vectorSize(); i++) {
					String strSupressError = (String) parameterSupressError.value(i);
					tmProducts.selectRowColumn(products, strSupressError, 4);
				}
			}
		}

		if (parameterThreshold == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("threshold", "string", "Info");
				parameterThreshold = (StringParameter) serviceMessageLogger.parameter("threshold");
			} else {
				parameterThreshold = new StringParameter("threshold", "INFO", false);
				pSetDestination.addParameter(parameterThreshold);
			}
			jComboBoxThreshold.setSelectedIndex(1);

		}

		if (parameterLimit == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("limit", "string", "");
				parameterLimit = (StringParameter) serviceMessageLogger.parameter("limit");
			} else {
				parameterLimit = new StringParameter("limit", "", false);
				pSetDestination.addParameter(parameterLimit);
			}

		}

		if (parameterSpan == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("timespan", "string", "");
				parameterSpan = (StringParameter) serviceMessageLogger.parameter("timespan");
			} else {
				parameterSpan = new StringParameter("timespan", "", false);
				pSetDestination.addParameter(parameterSpan);
			}

		}

		if (parameterSupressError == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("suppressError", "vstring", "");
				parameterSupressError = (VStringParameter) serviceMessageLogger.parameter("suppressError");
			} else {
				parameterSupressError = new VStringParameter("suppressError", "", false);
				pSetDestination.addParameter(parameterSupressError);
			}
		}
		if (parameterSupressWarning == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("suppressWarning", "vstring", "");
				parameterSupressWarning = (VStringParameter) serviceMessageLogger.parameter("suppressWarning");
			} else {
				parameterSupressWarning = new VStringParameter("suppressWarning", "", false);
				pSetDestination.addParameter(parameterSupressWarning);
			}
		}
		if (parameterSupressInfo == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("suppressInfo", "vstring", "");
				parameterSupressInfo = (VStringParameter) serviceMessageLogger.parameter("suppressInfo");
			} else {
				parameterSupressInfo = new VStringParameter("suppressInfo", "", false);
				pSetDestination.addParameter(parameterSupressInfo);
			}
		}
		if (parameterSupressDebug == null) {
			if (strDestination.equals("")) {
				serviceMessageLogger.updateParameter("suppressDebug", "vstring", "");
				parameterSupressDebug = (VStringParameter) serviceMessageLogger.parameter("suppressDebug");
			} else {
				parameterSupressDebug = new VStringParameter("suppressDebug", "", false);
				pSetDestination.addParameter(parameterSupressDebug);
			}
		}
	}

	private void initGuiTransientData() {
		thresholdsArrayList.add("");
		thresholdsArrayList.add("INFO");
		thresholdsArrayList.add("DEBUG");
		thresholdsArrayList.add("WARNING");
		thresholdsArrayList.add("ERROR");

		for (int i = 0; i < 10; i++) {
			limitArrayList.add("" + i);
		}
		limitArrayList.add("50");
		limitArrayList.add("100");

		for (int i = 0; i < 10; i++) {
			spanArrayList.add("" + i * 100);
		}

	}

	private void initGuiComponents() {

		cbmOutputThreshold = (DefaultComboBoxModel) jComboBoxThreshold.getModel();
		cbmOutputThreshold.removeAllElements();

		Iterator<String> it = thresholdsArrayList.iterator();
		while (it.hasNext()) {
			String strTemp = it.next();
			cbmOutputThreshold.addElement(strTemp);
		}

		cbmOutputLimit = (DefaultComboBoxModel) jComboBoxLimit.getModel();
		cbmOutputLimit.removeAllElements();
		cbmOutputLimit.addElement("");

		it = limitArrayList.iterator();
		while (it.hasNext()) {
			String strTemp = it.next();
			cbmOutputLimit.addElement(strTemp);
		}

		cbmOutputTimeSpan = (DefaultComboBoxModel) jComboBoxTimeSpan.getModel();
		cbmOutputTimeSpan.removeAllElements();
		cbmOutputTimeSpan.addElement("");
		it = spanArrayList.iterator();
		while (it.hasNext()) {
			String strTemp = it.next();
			cbmOutputTimeSpan.addElement(strTemp);
		}

		jTableProducts.setDefaultRenderer(Boolean.class, new CheckBoxTableCellRenderer());

		jTableProducts.setModel(new ProductTableLoggerModel());

		jTableProducts.getColumnModel().getColumn(0).setPreferredWidth(500);
		jTableProducts.getColumnModel().getColumn(1).setPreferredWidth(100);
		jTableProducts.getColumnModel().getColumn(2).setPreferredWidth(109);
		jTableProducts.getColumnModel().getColumn(3).setPreferredWidth(100);

		tmProducts = (ProductTableLoggerModel) jTableProducts.getModel();

		jCheckBoxShowSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jCheckBoxShowSelectedActionPerformed(e);
			}
		});

		jCheckBoxInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jCheckBoxInfoActionPerformed(e);
			}
		});

		jCheckBoxDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jCheckBoxDebugActionPerformed(e);
			}
		});

		jCheckBoxWarning.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jCheckBoxWarningActionPerformed(e);
			}
		});

		initComponents();
		createProducts();
	}

	public void commitTransient() {

		if (jComboBoxThreshold.getSelectedIndex() > 0) {
			String strThreshold = (String) jComboBoxThreshold.getSelectedItem();
			serviceMessageLogger.updateParameter(parameterThreshold.fullName(), parameterThreshold.type(),
					strThreshold);
		}

		if (jComboBoxLimit.getSelectedIndex() > 0) {
			String strLimit = (String) jComboBoxLimit.getSelectedItem();
			serviceMessageLogger.updateParameter(parameterLimit.fullName(), parameterLimit.type(), strLimit);
		}

		if (jComboBoxTimeSpan.getSelectedIndex() > 0) {
			String strTimeSpan = (String) jComboBoxTimeSpan.getSelectedItem();
			serviceMessageLogger.updateParameter(parameterSpan.fullName(), parameterSpan.type(), strTimeSpan);
		}

		Iterator<ProductLogger> it = products.values().iterator();

		String strSupressInfo = "";
		String strSupressDebug = "";
		String strSupressWarning = "";
		String strSupressError = "";

		while (it.hasNext()) {
			ProductLogger temp = it.next();

			if (temp.info == true) {
				if (strSupressInfo == "") {
					strSupressInfo = "\"" + temp.label + "\"";
				} else {
					strSupressInfo = strSupressInfo + ",\"" + temp.label + "\"";
				}
			}

			if (temp.debug == true) {
				if (strSupressDebug == "") {
					strSupressDebug = "\"" + temp.label + "\"";
				} else {
					strSupressDebug = strSupressDebug + ",\"" + temp.label + "\"";
				}
			}
			if (temp.warning == true) {
				if (strSupressWarning == "") {
					strSupressWarning = "\"" + temp.label + "\"";
				} else {
					strSupressWarning = strSupressWarning + ",\"" + temp.label + "\"";
				}
			}
			if (temp.error == true) {
				if (strSupressError == "") {
					strSupressError = "\"" + temp.label + "\"";
				} else {
					strSupressError = strSupressError + ",\"" + temp.label + "\"";
				}
			}
		}

		serviceMessageLogger.updateParameter(parameterSupressInfo.fullName(), parameterSupressInfo.type(),
				strSupressInfo);
		serviceMessageLogger.updateParameter(parameterSupressDebug.fullName(), parameterSupressDebug.type(),
				strSupressDebug);
		serviceMessageLogger.updateParameter(parameterSupressWarning.fullName(), parameterSupressWarning.type(),
				strSupressWarning);
		serviceMessageLogger.updateParameter(parameterSupressError.fullName(), parameterSupressError.type(),
				strSupressError);

	}

	//
	// private member functions
	//

	// listener callbacks

	private void jButtonMessageActionPerformed(ActionEvent e) {
		if (bSupMessage)
			bSupMessage = false;
		else
			bSupMessage = true;

	}

	private void jButtonErrorActionPerformed(ActionEvent e) {

	}

	private void jButtonDebugActionPerformed(ActionEvent e) {

	}

	private void jCheckBoxShowSelectedActionPerformed(ActionEvent e) {
		if (jCheckBoxShowSelected.isSelected()) {
			tmProducts.update(products, true);
		} else {
			tmProducts.update(products, false);
		}
	}

	private void jCheckBoxInfoActionPerformed(ActionEvent e) {
		if (jCheckBoxInfo.isSelected()) {
			tmProducts.toggleSelectionAll(products, true, 1);
		} else {
			tmProducts.toggleSelectionAll(products, false, 1);
		}
	}

	private void jCheckBoxDebugActionPerformed(ActionEvent e) {
		if (jCheckBoxDebug.isSelected()) {
			tmProducts.toggleSelectionAll(products, true, 2);
		} else {
			tmProducts.toggleSelectionAll(products, false, 2);
		}
	}

	private void jCheckBoxWarningActionPerformed(ActionEvent e) {
		if (jCheckBoxWarning.isSelected()) {
			tmProducts.toggleSelectionAll(products, true, 3);
		} else {
			tmProducts.toggleSelectionAll(products, false, 3);
		}
	}

	private void jCheckBoxErrorActionPerformed(ActionEvent e) {
		if (jCheckBoxError.isSelected()) {
			tmProducts.toggleSelectionAll(products, true, 3);
		} else {
			tmProducts.toggleSelectionAll(products, false, 3);
		}
	}

	// Create products
	private void createProducts() {

		products.clear();

		Iterator<ModuleInstance> itM = config.moduleIterator();
		while (itM.hasNext()) {
			ModuleInstance module = itM.next();
			String moduleName = module.name();
			String moduleType = module.template().type();

			if (!moduleType.equals("EDProducer") && !moduleType.equals("HLTFilter"))
				continue;

			ProductLogger prod = products.get(moduleName);
			if (prod == null) {
				prod = new ProductLogger(moduleName);
				products.put(moduleName, prod);
			}
		}

		tmProducts.update(products, false);
	}

	/** init GUI components */
	private void initComponents() {

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

		JScrollPane jScrollPaneModules = new JScrollPane();

		jComboBoxThreshold.setEditable(false);
		jComboBoxThreshold.setBackground(new java.awt.Color(255, 255, 255));
		JLabel jLabelThreshold = new JLabel();
		jLabelThreshold.setFont(GUIFontConfig.getFont(1));
		jLabelThreshold.setText("Threshold:");

		jComboBoxLimit.setEditable(true);
		jComboBoxLimit.setBackground(new java.awt.Color(255, 255, 255));
		JLabel jLabelLimit = new JLabel();
		jLabelLimit.setFont(GUIFontConfig.getFont(1));
		jLabelLimit.setText("Limit:");

		jComboBoxTimeSpan.setEditable(true);
		jComboBoxTimeSpan.setBackground(new java.awt.Color(255, 255, 255));
		JLabel jLabelTimeSpan = new JLabel();
		jLabelTimeSpan.setFont(GUIFontConfig.getFont(1));
		jLabelTimeSpan.setText("TimeSpan:");

		JLabel jLabelSuppressAll = new JLabel();
		jLabelSuppressAll.setText("Suppress All");

		jCheckBoxInfo.setFont(GUIFontConfig.getFont(1));
		jCheckBoxInfo.setSelected(false);
		jCheckBoxInfo.setText("info");

		jCheckBoxDebug.setFont(GUIFontConfig.getFont(1));
		jCheckBoxDebug.setSelected(false);
		jCheckBoxDebug.setText("debug");

		jCheckBoxWarning.setFont(GUIFontConfig.getFont(1));
		jCheckBoxWarning.setSelected(false);
		jCheckBoxWarning.setText("warning");

		jCheckBoxError.setFont(GUIFontConfig.getFont(1));
		jCheckBoxError.setSelected(false);
		jCheckBoxError.setText("error");

		jCheckBoxShowSelected.setFont(GUIFontConfig.getFont(1));
		jCheckBoxShowSelected.setSelected(false);
		jCheckBoxShowSelected.setText("Show Only Selected");
		jCheckBoxShowSelected.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

		this.setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPaneModules, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
						.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
								layout.createSequentialGroup().addComponent(jLabelThreshold)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jComboBoxThreshold, 0, 200, Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabelLimit)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jComboBoxLimit, 0, 100, Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabelTimeSpan)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jComboBoxTimeSpan, 0, 100, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
								layout.createSequentialGroup().addComponent(jCheckBoxShowSelected)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabelSuppressAll)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jCheckBoxInfo)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jCheckBoxDebug)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jCheckBoxWarning)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jCheckBoxError)))
				.addContainerGap()));

		// layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[]
		// {jButtonApply, jButtonCancel, jButtonOK});

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(22)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabelThreshold)
								.addComponent(jComboBoxThreshold, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabelLimit)
								.addComponent(jComboBoxLimit, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabelTimeSpan).addComponent(jComboBoxTimeSpan,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)

						).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)

						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jCheckBoxShowSelected).addComponent(jLabelSuppressAll)
								.addComponent(jCheckBoxInfo).addComponent(jCheckBoxDebug).addComponent(jCheckBoxWarning)
								.addComponent(jCheckBoxError))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)

						.addComponent(jScrollPaneModules, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addContainerGap()));

		jScrollPaneModules.setBackground(new java.awt.Color(255, 255, 255));
		jScrollPaneModules.setViewportView(jTableProducts);

	}
}

//
// table model
//
class ProductTableLoggerModel extends AbstractTableModel {
	/** all products in the configuration */
	private ArrayList<ProductLogger> products = new ArrayList<ProductLogger>();

	/** column names */
	private static String[] columnNames = { "Module", "Info", "Debug", "Warning", "Error" };

	Boolean bShowSelected;

	/** update the table */
	public void update(HashMap<String, ProductLogger> map, Boolean bShowSelected) {
		products.clear();
		Iterator<ProductLogger> it = map.values().iterator();

		this.bShowSelected = bShowSelected;

		while (it.hasNext()) {
			ProductLogger temp = it.next();
			if (bShowSelected) {
				if (temp.info || temp.debug || temp.warning || temp.error) {
					products.add(temp);
				}
			} else {
				products.add(temp);
			}
		}
		Collections.sort(products);
		fireTableDataChanged();
	}

	public void toggleSelectionAll(HashMap<String, ProductLogger> map, Boolean bToggleSelection, int iCol) {
		products.clear();
		Iterator<ProductLogger> it = map.values().iterator();

		while (it.hasNext()) {
			ProductLogger temp = it.next();
			if (bShowSelected) {
				if (temp.info || temp.debug || temp.warning || temp.error) {
					switch (iCol) {
					case 1:
						temp.info = bToggleSelection;
						break;
					case 2:
						temp.debug = bToggleSelection;
						break;
					case 3:
						temp.warning = bToggleSelection;
						break;
					case 4:
						temp.error = bToggleSelection;
						break;
					}
					products.add(temp);
				}
			} else {
				switch (iCol) {
				case 1:
					temp.info = bToggleSelection;
					break;
				case 2:
					temp.debug = bToggleSelection;
					break;
				case 3:
					temp.warning = bToggleSelection;
					break;
				case 4:
					temp.error = bToggleSelection;
					break;
				}
				products.add(temp);
			}

		}
		Collections.sort(products);
		fireTableDataChanged();
	}

	public void selectRowColumn(HashMap<String, ProductLogger> map, String strModule, int iCol) {
		products.clear();
		Iterator<ProductLogger> it = map.values().iterator();

		while (it.hasNext()) {
			ProductLogger temp = it.next();
			if (temp.label.equals(strModule)) {
				switch (iCol) {
				case 1:
					temp.info = true;
					break;
				case 2:
					temp.debug = true;
					break;
				case 3:
					temp.warning = true;
					break;
				case 4:
					temp.error = true;
					break;
				}
			}
			products.add(temp);
		}
		Collections.sort(products);
		fireTableDataChanged();
	}

	/** number of columns */
	public int getColumnCount() {
		return columnNames.length;
	}

	/** number of rows */
	public int getRowCount() {
		return products.size();
	}

	/** get column name for colimn 'col' */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/** get the value for row,col */
	public Object getValueAt(int row, int col) {
		ProductLogger product = products.get(row);
		switch (col) {
		case 0:
			return product.label;
		case 1:
			return product.info;
		case 2:
			return product.debug;
		case 3:
			return product.warning;
		case 4:
			return product.error;
		}
		return null;
	}

	/** get the class of the column 'c' */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/** is a cell editable or not? */
	public boolean isCellEditable(int row, int col) {
		return (col != 0);
	}

	/** set the value of a table cell */
	public void setValueAt(Object value, int row, int col) {
		if (col == 0)
			return;
		ProductLogger product = products.get(row);

		if (col == 1)
			product.info = (Boolean) value;
		else if (col == 2)
			product.debug = (Boolean) value;
		else if (col == 3)
			product.warning = (Boolean) value;
		else if (col == 4)
			product.error = (Boolean) value;
	}
}

//
// product data class
//
class ProductLogger implements Comparable<ProductLogger> {
	public String label = "";
	public Boolean info = false;
	public Boolean debug = false;
	public Boolean warning = false;
	public Boolean error = false;

	public ProductLogger(String label) {
		this.label = label;
	}

	public int compareTo(ProductLogger p) {
		return label.compareTo(p.label);
	}
}

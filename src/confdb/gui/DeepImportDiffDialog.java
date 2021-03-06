package confdb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.tree.*;

import confdb.data.IConfiguration;
import confdb.db.ConfDB;
import confdb.db.DatabaseException;
import confdb.diff.Diff;
import confdb.diff.DiffException;

public class DeepImportDiffDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static DefaultMutableTreeNode nNode;
	public static DefaultMutableTreeNode root;
	public static DefaultTreeModel model;
	public static MutableTreeNode node;
	public static TreePath path;
	public static JTree tree;
	public static boolean seeMoreInfo;
	public static boolean choise;
	public static String message;

	///// More fields:
	public JFrame jFrame;
	private JButton jButtonClose = new javax.swing.JButton();
	private JButton jButtonAccept = new javax.swing.JButton();
	private JEditorPane jEditorPaneDiff = new javax.swing.JEditorPane();
	/** diff tree components */
	private JTree jTreeDiff;
	private DiffTreeModel treeModel;

	//
	// construction
	//

	/** constructor with diff object */
	public DeepImportDiffDialog(Diff diff, String msg) {
		// super(true);
		setTitle("Deep Import");
		message = msg;

		// Using Modality in Dialog to make JFrames wait for the results.
		this.setModal(true);

		// initialize tree
		treeModel = new DiffTreeModel();
		jTreeDiff = new JTree(treeModel);
		jTreeDiff.setRootVisible(false);
		jTreeDiff.setEditable(false);
		jTreeDiff.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTreeDiff.setCellRenderer(new DiffTreeRenderer());
		setContentPane(initComponents());
		// register listeners
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choise = false;
				jButtonCloseActionPerformed(e);
			}
		});
		jButtonAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choise = true;
				jButtonCloseActionPerformed(e);
			}
		});
		treeModel.setDiff(diff);
		for (int i = jTreeDiff.getRowCount() - 1; i >= 0; i--)
			jTreeDiff.expandRow(i);
		jEditorPaneDiff.setText(diff.printAll());

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	}

	public boolean getResults() {
		return choise;
	}

	public void jButtonCloseActionPerformed(ActionEvent e) {
		setVisible(false);
	}

	/** initialize GUI components */
	private Container initComponents() {
		JPanel jPanel = new JPanel();
		JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
		JScrollPane jScrollPaneTree = new javax.swing.JScrollPane();
		JScrollPane jScrollPaneText = new javax.swing.JScrollPane();

		JLabel textArea = new JLabel();
		textArea.setText("<html>" + message + "</html>");
		textArea.setBackground(null);
		textArea.setPreferredSize(new Dimension(1, 1));

		jButtonClose.setText("Close");
		jButtonAccept.setText("Accept");

		jScrollPaneTree.setViewportView(jTreeDiff);
		jTabbedPane1.addTab("Tree", jScrollPaneTree);
		jScrollPaneText.setViewportView(jEditorPaneDiff);
		jTabbedPane1.addTab("Text", jScrollPaneText);

		setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
												.addComponent(textArea).addComponent(jTabbedPane1))
										.addContainerGap())
								.addGroup(layout.createSequentialGroup().addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
												.addComponent(jButtonClose, javax.swing.GroupLayout.DEFAULT_SIZE, 200,
														Short.MAX_VALUE))
										.addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
												.addComponent(jButtonAccept, javax.swing.GroupLayout.DEFAULT_SIZE, 200,
														Short.MAX_VALUE))
										.addContainerGap()))));

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(textArea)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonClose).addComponent(jButtonAccept))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addContainerGap()));

		return jPanel;
	}

}
